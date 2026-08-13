#!/usr/bin/env python3
"""Recommend Athena starter artifacts for requested consumer features."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path
from typing import Any

from _shared import json_dump, load_manifest, parse_feature_list, project_report, write_output


FEATURE_RULES: list[dict[str, Any]] = [
    {"aliases": {"common", "core"}, "artifacts": ["athena-framework-common"], "reason": "common context, events, exceptions, and utilities"},
    {"aliases": {"web", "rest", "http", "mvc", "api"}, "artifacts": ["athena-framework-starter-web"], "reason": "Spring MVC and Athena Web conventions"},
    {"aliases": {"jdbc", "data-api"}, "artifacts": ["athena-framework-starter-data-jdbc"], "reason": "data contracts and JDBC-level CRUD abstractions"},
    {"aliases": {"mybatis", "mybatis-crud"}, "artifacts": ["athena-framework-starter-data-mybatis"], "reason": "MyBatis-Plus persistence and Athena CRUD abstractions"},
    {"aliases": {"jpa", "jpa-crud"}, "artifacts": ["athena-framework-starter-data-jpa"], "reason": "Spring Data JPA persistence and Athena CRUD abstractions"},
    {"aliases": {"mongo", "mongodb"}, "artifacts": ["athena-framework-starter-data-mongo"], "reason": "MongoDB dependency aggregation; business repositories remain consumer-owned"},
    {"aliases": {"dynamic-datasource", "multi-datasource", "routing-datasource"}, "artifacts": ["athena-framework-starter-dynamic-datasource"], "reason": "Athena dynamic data-source routing"},
    {"aliases": {"security", "auth", "authentication"}, "artifacts": ["athena-framework-starter-security", "athena-framework-starter-security-token-jwt"], "reason": "authentication core with the framework's default JWT token mode"},
    {"aliases": {"jwt", "security-jwt"}, "artifacts": ["athena-framework-starter-security-token-jwt"], "reason": "JWT TokenManager implementation"},
    {"aliases": {"gateway", "security-gateway"}, "artifacts": ["athena-framework-starter-security-gateway"], "reason": "security plus JWT aggregation for gateway/read-only services"},
    {"aliases": {"security-jpa", "user-jpa", "rbac-jpa"}, "artifacts": ["athena-framework-starter-security-user-jpa"], "reason": "JPA-backed users, credential lookup, RBAC, menu, and audit providers"},
    {"aliases": {"security-mybatis", "user-mybatis", "rbac-mybatis"}, "artifacts": ["athena-framework-starter-security-user-mybatis"], "reason": "MyBatis-backed users, credential lookup, RBAC, menu, and audit providers"},
    {"aliases": {"authorization", "permission", "method-authorization"}, "artifacts": ["athena-framework-starter-security-authorization"], "reason": "annotation-based permission evaluation"},
    {"aliases": {"websocket", "ws"}, "artifacts": ["athena-framework-starter-websocket"], "reason": "WebSocket protocol, session, routing, resume, and extension points"},
    {"aliases": {"nacos", "discovery", "config-center"}, "artifacts": ["athena-framework-starter-cloud-nacos"], "reason": "Nacos discovery and configuration dependencies"},
    {"aliases": {"openfeign", "feign", "service-client"}, "artifacts": ["athena-framework-starter-cloud-openfeign"], "reason": "Feign scanning, headers, timeout, and error decoding"},
    {"aliases": {"seata", "distributed-transaction"}, "artifacts": ["athena-framework-starter-cloud-seata"], "reason": "Seata data-source integration"},
    {"aliases": {"kafka", "event-stream"}, "artifacts": ["athena-framework-starter-kafka"], "reason": "Kafka publisher and dynamic consumer management"},
    {"aliases": {"minio", "object-storage", "storage"}, "artifacts": ["athena-framework-starter-minio"], "reason": "MinIO-backed ObjectStorageService"},
    {"aliases": {"communication", "notification"}, "artifacts": ["athena-framework-starter-communication"], "reason": "unified communication dispatch"},
    {"aliases": {"email", "mail"}, "artifacts": ["athena-framework-starter-communication-email"], "reason": "email communication driver"},
    {"aliases": {"sms"}, "artifacts": ["athena-framework-starter-communication-sms"], "reason": "SMS communication driver"},
    {"aliases": {"wecom", "wechat-work"}, "artifacts": ["athena-framework-starter-communication-wecom"], "reason": "WeCom communication driver"},
    {"aliases": {"logging", "log"}, "artifacts": ["athena-framework-starter-log"], "reason": "Athena logging defaults"},
    {"aliases": {"elasticsearch", "es"}, "artifacts": ["athena-framework-starter-es"], "reason": "Elasticsearch dependency/configuration entry"},
    {"aliases": {"test-platform", "test-orchestration", "athena-test"}, "artifacts": ["athena-framework-starter-test"], "reason": "Athena test-plan execution core"},
    {"aliases": {"ai", "spring-ai"}, "artifacts": ["athena-framework-starters-ai"], "reason": "declared AI starter; currently a placeholder in the bundled snapshot"},
]


def transitive_athena(artifact_id: str, artifacts: dict[str, Any]) -> list[str]:
    discovered: list[str] = []
    pending = list(artifacts.get(artifact_id, {}).get("athena_dependencies", []))
    while pending:
        current = pending.pop(0)
        if current in discovered:
            continue
        discovered.append(current)
        pending.extend(artifacts.get(current, {}).get("athena_dependencies", []))
    return discovered


def build_recommendation(project: Path, features: list[str]) -> dict[str, Any]:
    report = project_report(project, redact=True)
    manifest = load_manifest()
    artifacts = manifest["artifacts"]
    existing = {item["artifact_id"] for item in report["athena"]["dependencies"] if item.get("artifact_id")}
    requested: dict[str, dict[str, Any]] = {}
    unmatched: list[str] = []

    if features:
        for feature in features:
            matches = [rule for rule in FEATURE_RULES if feature in rule["aliases"]]
            if not matches:
                unmatched.append(feature)
                continue
            for rule in matches:
                for artifact_id in rule["artifacts"]:
                    item = requested.setdefault(artifact_id, {"reasons": [], "features": []})
                    if rule["reason"] not in item["reasons"]:
                        item["reasons"].append(rule["reason"])
                    if feature not in item["features"]:
                        item["features"].append(feature)
    else:
        for artifact_id in sorted(existing):
            requested[artifact_id] = {"reasons": ["already declared in the consumer project"], "features": []}

    direct: list[dict[str, Any]] = []
    provided: dict[str, set[str]] = {}
    warnings: list[str] = []
    references: set[str] = {"module-catalog.md", "dependency-versioning-and-repositories.md"}
    for artifact_id, request in requested.items():
        metadata = artifacts.get(artifact_id)
        if metadata is None:
            warnings.append(f"{artifact_id} is not present in the bundled Athena snapshot")
            direct.append({"artifact_id": artifact_id, "status": "unknown", "already_present": artifact_id in existing, **request})
            continue
        status = metadata["status"]
        if status in {"placeholder", "resource-only", "aggregation-only"}:
            warnings.append(f"{artifact_id} is {status}; read module-catalog.md before relying on runtime behavior")
        references.update(metadata.get("references", []))
        direct.append(
            {
                "group_id": metadata["group_id"],
                "artifact_id": artifact_id,
                "status": status,
                "already_present": artifact_id in existing,
                "configuration_prefixes": metadata.get("configuration_prefixes", []),
                "reasons": request["reasons"],
                "features": request["features"],
            }
        )
        for dependency in transitive_athena(artifact_id, artifacts):
            provided.setdefault(dependency, set()).add(artifact_id)

    if unmatched:
        warnings.append("Unmatched feature names: " + ", ".join(unmatched))
    if "athena-framework-starter-security-user-jpa" in requested and "athena-framework-starter-security-user-mybatis" in requested:
        warnings.append("JPA and MyBatis security user stores cannot both be enabled in the bundled snapshot")
    if not features and not existing:
        warnings.append("No features were supplied and no Athena dependencies were detected")

    return {
        "schema_version": 1,
        "project": report["project_root"],
        "framework_snapshot_revision": manifest["framework"]["revision"],
        "requested_features": features,
        "direct_dependencies": direct,
        "transitive_athena_dependencies": [
            {"artifact_id": artifact_id, "provided_by": sorted(providers), "already_present": artifact_id in existing}
            for artifact_id, providers in sorted(provided.items())
            if artifact_id not in requested
        ],
        "references": sorted(references),
        "warnings": warnings,
    }


def render_text(result: dict[str, Any]) -> str:
    lines = [
        f"Project: {result['project']}",
        f"Athena snapshot: {result['framework_snapshot_revision']}",
        "Direct starter recommendations:",
    ]
    if not result["direct_dependencies"]:
        lines.append("  - none")
    for item in result["direct_dependencies"]:
        marker = "already present" if item["already_present"] else "add"
        lines.append(f"  - {item['artifact_id']} [{item['status']}; {marker}]")
        for reason in item["reasons"]:
            lines.append(f"      {reason}")
    if result["transitive_athena_dependencies"]:
        lines.append("Provided transitively (do not add without a separate reason):")
        for item in result["transitive_athena_dependencies"]:
            lines.append(f"  - {item['artifact_id']} via {', '.join(item['provided_by'])}")
    lines.append("Read references: " + ", ".join(result["references"]))
    if result["warnings"]:
        lines.append("Warnings:")
        lines.extend(f"  - {warning}" for warning in result["warnings"])
    return "\n".join(lines) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", default=".")
    parser.add_argument("--features", action="append", default=[], help="Comma-separated feature names; repeatable")
    parser.add_argument("--format", choices=("text", "json"), default="text")
    parser.add_argument("--output")
    args = parser.parse_args()
    features = parse_feature_list(args.features)
    try:
        result = build_recommendation(Path(args.project), features)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2
    write_output(json_dump(result) if args.format == "json" else render_text(result), args.output)
    return 1 if not result["direct_dependencies"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
