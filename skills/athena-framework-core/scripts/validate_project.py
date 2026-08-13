#!/usr/bin/env python3
"""Statically validate an Athena consumer project's dependency and configuration choices."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from typing import Any

from _shared import canonical_key, is_sensitive_key, json_dump, load_manifest, project_report, write_output


def transitive_closure(direct: set[str], artifacts: dict[str, Any]) -> set[str]:
    closure = set(direct)
    pending = list(direct)
    while pending:
        current = pending.pop()
        for dependency in artifacts.get(current, {}).get("athena_dependencies", []):
            if dependency not in closure:
                closure.add(dependency)
                pending.append(dependency)
    return closure


def value_for(values: dict[str, list[dict[str, str]]], key: str) -> str | None:
    entries = values.get(canonical_key(key), [])
    return entries[-1]["value"] if entries else None


def bool_value(value: str | None, default: bool = False) -> bool:
    if value is None:
        return default
    return value.strip().lower() in {"true", "1", "yes", "on"}


def externalized(value: str) -> bool:
    stripped = value.strip()
    return "${" in stripped or stripped.startswith(("@", "vault:", "secret:"))


def add_issue(issues: list[dict[str, str]], severity: str, code: str, message: str) -> None:
    item = {"severity": severity, "code": code, "message": message}
    if item not in issues:
        issues.append(item)


def validate(project: Path) -> dict[str, Any]:
    report = project_report(project, redact=False)
    manifest = load_manifest()
    artifacts = manifest["artifacts"]
    issues: list[dict[str, str]] = []
    direct = {item["artifact_id"] for item in report["athena"]["dependencies"] if item.get("artifact_id")}
    available = transitive_closure(direct, artifacts)
    bom_imports = report["athena"]["bom_imports"]
    values = report["configuration"]["values"]

    for error in report["parse_errors"]:
        add_issue(issues, "error", "POM_PARSE", error)

    if direct and not bom_imports:
        add_issue(issues, "warning", "ATHENA_BOM_MISSING", "Athena dependencies are declared without importing org.athena:framework-dependencies")
    if bom_imports:
        for dependency in report["athena"]["dependencies"]:
            if dependency.get("version"):
                add_issue(issues, "warning", "DIRECT_VERSION_WITH_BOM", f"{dependency['artifact_id']} declares a direct version while the Athena BOM is imported")

    explicit_versions = {
        item["version"]
        for item in [*bom_imports, *report["athena"]["dependencies"]]
        if item.get("version") and "${" not in str(item["version"])
    }
    if len(explicit_versions) > 1:
        add_issue(issues, "error", "ATHENA_VERSION_SPLIT", "Multiple explicit Athena versions are present: " + ", ".join(sorted(explicit_versions)))
    snapshot_revision = manifest["framework"]["revision"]
    for version in explicit_versions:
        if version != snapshot_revision:
            add_issue(issues, "warning", "SNAPSHOT_MISMATCH", f"Consumer Athena version {version} differs from the bundled skill snapshot {snapshot_revision}; inspect matching JAR/source before using APIs")

    java_values = report["build"]["java_versions"]
    for key, value in java_values.items():
        match = re.search(r"\d+", value)
        if match and int(match.group()) < int(manifest["baseline"]["java"]):
            add_issue(issues, "error", "JAVA_BASELINE", f"{key}={value} is below Athena's Java {manifest['baseline']['java']} baseline")

    for artifact_id in sorted(direct):
        metadata = artifacts.get(artifact_id)
        if metadata is None:
            add_issue(issues, "warning", "UNKNOWN_ARTIFACT", f"{artifact_id} is absent from the bundled snapshot")
            continue
        status = metadata["status"]
        if status == "placeholder":
            add_issue(issues, "error", "PLACEHOLDER_ARTIFACT", f"{artifact_id} is a placeholder in the bundled snapshot and supplies no runtime capability")
        elif status in {"aggregation-only", "resource-only"}:
            add_issue(issues, "info", "NON_CODE_ARTIFACT", f"{artifact_id} is {status}; rely only on its declared dependencies/resources")

    if "athena-framework-starter-security" in available:
        token_type = (value_for(values, "athena.security.token.type") or "jwt").lower()
        if token_type == "jwt" and "athena-framework-starter-security-token-jwt" not in available:
            add_issue(issues, "error", "JWT_MODULE_MISSING", "athena.security.token.type defaults to jwt, but athena-framework-starter-security-token-jwt is not available")
        elif token_type == "redis":
            add_issue(issues, "error", "REDIS_TOKEN_UNAVAILABLE", "The bundled snapshot declares redis token configuration but contains no Redis TokenManager artifact")
        elif token_type not in {"jwt", "local", "redis"}:
            add_issue(issues, "error", "TOKEN_TYPE_UNSUPPORTED", f"Unsupported athena.security.token.type={token_type}")

        jpa_enabled = bool_value(value_for(values, "athena.security.user.jpa.enabled"))
        mybatis_enabled = bool_value(value_for(values, "athena.security.user.mybatis.enabled"))
        if jpa_enabled and mybatis_enabled:
            add_issue(issues, "error", "SECURITY_USER_STORE_CONFLICT", "JPA and MyBatis security user stores cannot both be enabled")
        if jpa_enabled and "athena-framework-starter-security-user-jpa" not in available:
            add_issue(issues, "error", "SECURITY_JPA_MODULE_MISSING", "JPA security user store is enabled but its starter is absent")
        if mybatis_enabled and "athena-framework-starter-security-user-mybatis" not in available:
            add_issue(issues, "error", "SECURITY_MYBATIS_MODULE_MISSING", "MyBatis security user store is enabled but its starter is absent")
        if bool_value(value_for(values, "athena.security.authorization.enabled")) and "athena-framework-starter-security-authorization" not in available:
            add_issue(issues, "error", "AUTHORIZATION_MODULE_MISSING", "Authorization is enabled but athena-framework-starter-security-authorization is absent")

        jwt_secret = value_for(values, "athena.security.token.jwt.secret")
        if token_type == "jwt" and jwt_secret is None:
            add_issue(issues, "warning", "JWT_DEFAULT_SECRET", "JWT secret is not configured; the bundled implementation otherwise uses an insecure development default")

    activation_rules = {
        "athena-framework-starter-minio": ("athena.minio.enabled", ["athena.minio.endpoint", "athena.minio.access-key", "athena.minio.secret-key", "athena.minio.bucket"]),
        "athena-framework-starter-kafka": ("athena.kafka.enabled", ["athena.kafka.bootstrap-servers", "athena.kafka.consumer-group-id"]),
        "athena-framework-starter-dynamic-datasource": ("athena.datasource.dynamic.enabled", []),
        "athena-framework-starter-security-authorization": ("athena.security.authorization.enabled", []),
        "athena-framework-starter-communication-email": ("athena.communication.email.enabled", []),
        "athena-framework-starter-communication-sms": ("athena.communication.sms.enabled", ["athena.communication.sms.access-key-id", "athena.communication.sms.access-key-secret"]),
        "athena-framework-starter-communication-wecom": ("athena.communication.wecom.enabled", ["athena.communication.wecom.corp-id", "athena.communication.wecom.corp-secret", "athena.communication.wecom.agent-id"]),
    }
    for artifact_id, (enabled_key, required_keys) in activation_rules.items():
        if artifact_id not in available:
            continue
        enabled = bool_value(value_for(values, enabled_key))
        if not enabled:
            add_issue(issues, "warning", "AUTO_CONFIG_DISABLED", f"{artifact_id} is present but {enabled_key}=true is not configured")
            continue
        for required_key in required_keys:
            if value_for(values, required_key) in {None, ""}:
                add_issue(issues, "error", "REQUIRED_CONFIG_MISSING", f"{required_key} is required when {enabled_key}=true")

    for key, entries in values.items():
        if is_sensitive_key(key):
            for entry in entries:
                value = entry["value"]
                if value and value != "<redacted>" and not externalized(value):
                    add_issue(issues, "warning", "LITERAL_SECRET", f"Sensitive key {key} is a literal in {entry['file']}; use environment or secret-manager injection")
        if key == "spring.jpa.hibernate.ddl-auto":
            for entry in entries:
                if entry["value"].lower() in {"update", "create", "create-drop"}:
                    severity = "error" if "prod" in entry["file"].lower() else "warning"
                    add_issue(issues, severity, "DESTRUCTIVE_DDL", f"spring.jpa.hibernate.ddl-auto={entry['value']} in {entry['file']} is unsafe for production")

    severity_order = {"error": 0, "warning": 1, "info": 2}
    issues.sort(key=lambda item: (severity_order[item["severity"]], item["code"], item["message"]))
    summary = {
        severity: sum(1 for issue in issues if issue["severity"] == severity)
        for severity in ("error", "warning", "info")
    }
    return {
        "schema_version": 1,
        "project": report["project_root"],
        "framework_snapshot_revision": snapshot_revision,
        "athena_detected": report["athena"]["detected"],
        "direct_artifacts": sorted(direct),
        "effective_artifacts": sorted(available),
        "summary": summary,
        "issues": issues,
    }


def render_text(result: dict[str, Any]) -> str:
    summary = result["summary"]
    lines = [
        f"Project: {result['project']}",
        f"Athena snapshot: {result['framework_snapshot_revision']}",
        f"Result: {summary['error']} error(s), {summary['warning']} warning(s), {summary['info']} info",
    ]
    for issue in result["issues"]:
        lines.append(f"[{issue['severity'].upper()}] {issue['code']}: {issue['message']}")
    if not result["issues"]:
        lines.append("No static Athena issues found.")
    lines.append("Static validation does not replace compilation, tests, or runtime dependency checks.")
    return "\n".join(lines) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", default=".")
    parser.add_argument("--format", choices=("text", "json"), default="text")
    parser.add_argument("--output")
    parser.add_argument("--strict", action="store_true", help="Return failure when warnings are present")
    args = parser.parse_args()
    try:
        result = validate(Path(args.project))
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2
    write_output(json_dump(result) if args.format == "json" else render_text(result), args.output)
    if result["summary"]["error"] or (args.strict and result["summary"]["warning"]):
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
