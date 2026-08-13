#!/usr/bin/env python3
"""Generate or check the machine-readable Athena source snapshot."""

from __future__ import annotations

import argparse
import json
import re
import subprocess
import sys
from pathlib import Path
from typing import Any

from _shared import MANIFEST_PATH, json_dump, parse_pom


CONFIGURATION_PROPERTIES_RE = re.compile(
    r"@ConfigurationProperties\s*\(\s*(?:prefix\s*=\s*)?\"([^\"]+)\"",
    re.MULTILINE,
)
CONDITIONAL_RE = re.compile(r"@ConditionalOnProperty\s*\((.*?)\)", re.DOTALL)
PUBLIC_TYPE_RE = re.compile(
    r"\bpublic\s+(?:(?:abstract|final|sealed|non-sealed|static)\s+)*(class|interface|record|enum)\s+([A-Za-z_$][A-Za-z0-9_$]*)"
)
PACKAGE_RE = re.compile(r"^\s*package\s+([A-Za-z0-9_.]+)\s*;", re.MULTILINE)


REFERENCE_BY_FAMILY = {
    "common": ["architecture-and-boundaries.md", "common-and-web.md"],
    "web": ["common-and-web.md", "project-conventions.md"],
    "data": ["data.md"],
    "security": ["security.md", "extension-points.md"],
    "cloud": ["cloud.md"],
    "communication": ["messaging-and-communication.md"],
    "kafka": ["messaging-and-communication.md"],
    "websocket": ["websocket.md", "extension-points.md"],
    "storage": ["storage-and-observability.md"],
    "observability": ["storage-and-observability.md"],
    "test": ["testing-and-acceptance.md"],
    "ai": ["module-catalog.md"],
    "bom": ["dependency-versioning-and-repositories.md"],
    "parent": ["architecture-and-boundaries.md"],
}


def git_value(root: Path, *args: str) -> str | None:
    try:
        result = subprocess.run(
            ["git", *args], cwd=root, check=True, text=True, capture_output=True, timeout=10
        )
        return result.stdout.strip() or None
    except (OSError, subprocess.SubprocessError):
        return None


def family_for(artifact_id: str) -> str:
    if artifact_id == "framework-dependencies":
        return "bom"
    if artifact_id in {"athena-project", "athena-framework-starters"} or artifact_id.endswith("-parent"):
        return "parent"
    if artifact_id == "athena-framework-common":
        return "common"
    if "websocket" in artifact_id:
        return "websocket"
    if "security" in artifact_id:
        return "security"
    if "communication" in artifact_id:
        return "communication"
    if "kafka" in artifact_id:
        return "kafka"
    if "cloud" in artifact_id:
        return "cloud"
    if "data" in artifact_id or "datasource" in artifact_id:
        return "data"
    if artifact_id.endswith("-web"):
        return "web"
    if "minio" in artifact_id:
        return "storage"
    if artifact_id.endswith("-es") or artifact_id.endswith("-log"):
        return "observability"
    if "starter-test" in artifact_id:
        return "test"
    if artifact_id.endswith("-ai"):
        return "ai"
    return "common"


def extract_condition(block: str, source: str) -> dict[str, Any]:
    def string_value(name: str) -> str | None:
        match = re.search(rf"\b{name}\s*=\s*\"([^\"]+)\"", block)
        return match.group(1) if match else None

    def bool_value(name: str) -> bool | None:
        match = re.search(rf"\b{name}\s*=\s*(true|false)", block)
        return match.group(1) == "true" if match else None

    names_match = re.search(r"\bname\s*=\s*\{([^}]+)}", block)
    names = re.findall(r'\"([^\"]+)\"', names_match.group(1)) if names_match else []
    if not names:
        single = string_value("name")
        names = [single] if single else []
    return {
        "source": source,
        "prefix": string_value("prefix"),
        "names": names,
        "having_value": string_value("havingValue"),
        "match_if_missing": bool_value("matchIfMissing"),
    }


def direct_source_files(module_dir: Path) -> list[Path]:
    source_root = module_dir / "src" / "main" / "java"
    return sorted(source_root.rglob("*.java")) if source_root.is_dir() else []


def direct_resource_files(module_dir: Path) -> list[Path]:
    resource_root = module_dir / "src" / "main" / "resources"
    return sorted(path for path in resource_root.rglob("*") if path.is_file()) if resource_root.is_dir() else []


def direct_test_files(module_dir: Path) -> list[Path]:
    test_root = module_dir / "src" / "test" / "java"
    return sorted(test_root.rglob("*.java")) if test_root.is_dir() else []


def module_status(artifact_id: str, modules: list[str], java_count: int, resources: int, dependencies: list[dict]) -> str:
    if artifact_id == "framework-dependencies":
        return "bom"
    if modules:
        return "parent"
    if java_count:
        return "implemented"
    if artifact_id == "athena-framework-starters-ai":
        return "placeholder"
    if resources:
        return "resource-only"
    if dependencies:
        return "aggregation-only"
    return "placeholder"


def build_manifest(framework: Path) -> dict[str, Any]:
    framework = framework.resolve()
    root_pom_path = framework / "pom.xml"
    bom_path = framework / "athena-project" / "framework-dependencies" / "pom.xml"
    if not root_pom_path.is_file() or not bom_path.is_file():
        raise ValueError(f"Not an Athena Framework checkout: {framework}")

    root_pom = parse_pom(root_pom_path)
    bom = parse_pom(bom_path)
    revision = root_pom.properties.get("revision") or root_pom.version
    baseline = {
        "java": root_pom.properties.get("java.version"),
        "spring_boot": bom.properties.get("spring-boot-version"),
        "spring_cloud": bom.properties.get("spring-cloud-dependencies.version"),
        "spring_cloud_alibaba": bom.properties.get("spring-cloud-alibaba-dependencies.version"),
        "mybatis": bom.properties.get("mybatis.version"),
        "mybatis_plus": bom.properties.get("mybatis-plus.version"),
        "postgresql": bom.properties.get("postgresql.version"),
        "minio": bom.properties.get("minio.version"),
    }

    pom_paths = sorted((framework / "athena-project").rglob("pom.xml"))
    artifacts: dict[str, Any] = {}
    public_types: list[dict[str, str]] = []
    all_prefixes: dict[str, list[str]] = {}
    all_conditions: list[dict[str, Any]] = []

    for pom_path in pom_paths:
        model = parse_pom(pom_path)
        artifact_id = model.artifact_id
        if not artifact_id:
            continue
        module_dir = pom_path.parent
        java_files = direct_source_files(module_dir)
        resource_files = direct_resource_files(module_dir)
        test_files = direct_test_files(module_dir)
        relative_module = module_dir.relative_to(framework).as_posix()
        dependencies = [dep.as_dict() for dep in model.dependencies]
        athena_dependencies = [
            dep["artifact_id"] for dep in dependencies if dep.get("group_id") == "org.athena" and dep.get("artifact_id")
        ]

        prefixes: set[str] = set()
        conditions: list[dict[str, Any]] = []
        module_types: list[dict[str, str]] = []
        for java_file in java_files:
            text = java_file.read_text(encoding="utf-8", errors="replace")
            rel_source = java_file.relative_to(framework).as_posix()
            prefixes.update(CONFIGURATION_PROPERTIES_RE.findall(text))
            for block in CONDITIONAL_RE.findall(text):
                condition = extract_condition(block, rel_source)
                conditions.append(condition)
                all_conditions.append(condition)
            package_match = PACKAGE_RE.search(text)
            package_name = package_match.group(1) if package_match else ""
            for kind, name in PUBLIC_TYPE_RE.findall(text):
                type_item = {
                    "kind": kind,
                    "name": name,
                    "qualified_name": f"{package_name}.{name}" if package_name else name,
                    "source": rel_source,
                    "artifact_id": artifact_id,
                }
                module_types.append(type_item)
                public_types.append(type_item)

        auto_configurations: set[str] = set()
        for resource in resource_files:
            if resource.name == "org.springframework.boot.autoconfigure.AutoConfiguration.imports":
                for line in resource.read_text(encoding="utf-8", errors="replace").splitlines():
                    value = line.strip()
                    if value and not value.startswith("#"):
                        auto_configurations.add(value)
            elif resource.name == "spring.factories":
                text = resource.read_text(encoding="utf-8", errors="replace")
                auto_configurations.update(re.findall(r"org\.[A-Za-z0-9_.$]+", text))

        family = family_for(artifact_id)
        artifacts[artifact_id] = {
            "group_id": model.group_id or "org.athena",
            "family": family,
            "status": module_status(artifact_id, model.modules, len(java_files), len(resource_files), dependencies),
            "path": relative_module,
            "packaging": model.packaging,
            "source_counts": {
                "main_java": len(java_files),
                "test_java": len(test_files),
                "resources": len(resource_files),
            },
            "modules": model.modules,
            "athena_dependencies": sorted(set(athena_dependencies)),
            "configuration_prefixes": sorted(prefixes),
            "conditional_properties": conditions,
            "auto_configurations": sorted(auto_configurations),
            "public_type_count": len(module_types),
            "references": REFERENCE_BY_FAMILY.get(family, ["module-catalog.md"]),
        }
        for prefix in prefixes:
            all_prefixes.setdefault(prefix, []).append(artifact_id)

    root_modules = root_pom.modules
    manifest = {
        "schema_version": 1,
        "framework": {
            "name": "Athena Framework",
            "group_id": "org.athena",
            "revision": revision,
            "source_commit": git_value(framework, "rev-parse", "HEAD"),
            "source_commit_time": git_value(framework, "show", "-s", "--format=%cI", "HEAD"),
            "root_modules": root_modules,
            "framework_tests_aggregated_at_root": "athena-framework-test" in root_modules,
        },
        "baseline": baseline,
        "artifacts": dict(sorted(artifacts.items())),
        "configuration_prefixes": {
            key: sorted(set(values)) for key, values in sorted(all_prefixes.items())
        },
        "conditional_properties": sorted(
            all_conditions,
            key=lambda item: (item.get("prefix") or "", ",".join(item.get("names") or []), item["source"]),
        ),
        "public_types": sorted(public_types, key=lambda item: item["qualified_name"]),
        "source_policy": {
            "snapshot_only": True,
            "consumer_version_must_match_revision": True,
            "different_version_action": "Inspect the resolved JAR or matching Athena source before using APIs or defaults.",
        },
    }
    return manifest


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--framework", required=True, help="Path to the Athena Framework checkout")
    parser.add_argument("--output", default=str(MANIFEST_PATH), help="Manifest output path")
    parser.add_argument("--check", action="store_true", help="Check for drift without writing")
    args = parser.parse_args()
    try:
        manifest = build_manifest(Path(args.framework))
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2
    rendered = json_dump(manifest)
    output = Path(args.output).expanduser().resolve()
    if args.check:
        try:
            existing = output.read_text(encoding="utf-8")
        except OSError:
            print(f"drift: manifest does not exist: {output}")
            return 1
        if existing != rendered:
            print(f"drift: {output} does not match {args.framework}")
            return 1
        print(f"manifest is current: {output}")
        return 0
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(rendered, encoding="utf-8")
    print(f"wrote {output} ({len(manifest['artifacts'])} artifacts, {len(manifest['public_types'])} public types)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
