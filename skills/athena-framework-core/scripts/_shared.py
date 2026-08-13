#!/usr/bin/env python3
"""Shared, dependency-free helpers for the Athena Framework core skill."""

from __future__ import annotations

import json
import re
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Iterable
from urllib.parse import urlsplit, urlunsplit


SKILL_ROOT = Path(__file__).resolve().parent.parent
MANIFEST_PATH = SKILL_ROOT / "references" / "framework-manifest.json"
TEMPLATES_ROOT = SKILL_ROOT / "assets" / "templates"
ATHENA_GROUP_ID = "org.athena"
EXCLUDED_DIRS = {
    ".git",
    ".idea",
    ".codegraph",
    ".gradle",
    ".mvn-cache",
    ".athena-skill",
    "target",
    "build",
    "node_modules",
}


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def direct_child(node: ET.Element | None, name: str) -> ET.Element | None:
    if node is None:
        return None
    for child in node:
        if local_name(child.tag) == name:
            return child
    return None


def direct_children(node: ET.Element | None, name: str) -> list[ET.Element]:
    if node is None:
        return []
    return [child for child in node if local_name(child.tag) == name]


def child_text(node: ET.Element | None, name: str, default: str | None = None) -> str | None:
    child = direct_child(node, name)
    if child is None or child.text is None:
        return default
    value = child.text.strip()
    return value if value else default


PROPERTY_RE = re.compile(r"\$\{([^}]+)}")


def resolve_properties(value: str | None, properties: dict[str, str], depth: int = 0) -> str | None:
    if value is None or depth > 10:
        return value

    def replace(match: re.Match[str]) -> str:
        key = match.group(1)
        replacement = properties.get(key)
        if replacement is None:
            return match.group(0)
        resolved = resolve_properties(replacement, properties, depth + 1)
        return resolved if resolved is not None else match.group(0)

    updated = PROPERTY_RE.sub(replace, value)
    if updated == value:
        return updated
    return resolve_properties(updated, properties, depth + 1)


@dataclass(frozen=True)
class PomDependency:
    group_id: str | None
    artifact_id: str | None
    version: str | None
    scope: str | None
    dep_type: str | None
    optional: str | None

    def as_dict(self) -> dict[str, str | None]:
        return {
            "group_id": self.group_id,
            "artifact_id": self.artifact_id,
            "version": self.version,
            "scope": self.scope,
            "type": self.dep_type,
            "optional": self.optional,
        }


@dataclass
class PomModel:
    path: Path
    group_id: str | None
    artifact_id: str | None
    version: str | None
    packaging: str
    parent: dict[str, str | None]
    properties: dict[str, str]
    modules: list[str]
    dependencies: list[PomDependency]
    managed_dependencies: list[PomDependency]
    repositories: list[dict[str, str | None]]


def _parse_dependencies(node: ET.Element | None, properties: dict[str, str]) -> list[PomDependency]:
    dependencies: list[PomDependency] = []
    for dep in direct_children(node, "dependency"):
        dependencies.append(
            PomDependency(
                resolve_properties(child_text(dep, "groupId"), properties),
                resolve_properties(child_text(dep, "artifactId"), properties),
                resolve_properties(child_text(dep, "version"), properties),
                resolve_properties(child_text(dep, "scope"), properties),
                resolve_properties(child_text(dep, "type"), properties),
                resolve_properties(child_text(dep, "optional"), properties),
            )
        )
    return dependencies


def parse_pom(path: Path) -> PomModel:
    try:
        root = ET.parse(path).getroot()
    except (ET.ParseError, OSError) as exc:
        raise ValueError(f"Cannot parse Maven POM {path}: {exc}") from exc

    parent_node = direct_child(root, "parent")
    parent = {
        "group_id": child_text(parent_node, "groupId"),
        "artifact_id": child_text(parent_node, "artifactId"),
        "version": child_text(parent_node, "version"),
        "relative_path": child_text(parent_node, "relativePath"),
    }
    group_id = child_text(root, "groupId") or parent["group_id"]
    artifact_id = child_text(root, "artifactId")
    version = child_text(root, "version") or parent["version"]

    properties: dict[str, str] = {}
    properties_node = direct_child(root, "properties")
    if properties_node is not None:
        for child in properties_node:
            if child.text and child.text.strip():
                properties[local_name(child.tag)] = child.text.strip()

    builtins = {
        "project.groupId": group_id or "",
        "project.artifactId": artifact_id or "",
        "project.version": version or "",
        "pom.groupId": group_id or "",
        "pom.artifactId": artifact_id or "",
        "pom.version": version or "",
        "revision": properties.get("revision", version or ""),
    }
    properties = {**builtins, **properties}
    group_id = resolve_properties(group_id, properties)
    artifact_id = resolve_properties(artifact_id, properties)
    version = resolve_properties(version, properties)

    modules_node = direct_child(root, "modules")
    modules = [child.text.strip() for child in direct_children(modules_node, "module") if child.text]

    dependencies_node = direct_child(root, "dependencies")
    dependencies = _parse_dependencies(dependencies_node, properties)
    management = direct_child(root, "dependencyManagement")
    managed_dependencies = _parse_dependencies(direct_child(management, "dependencies"), properties)

    repositories: list[dict[str, str | None]] = []
    repositories_node = direct_child(root, "repositories")
    for repository in direct_children(repositories_node, "repository"):
        repositories.append(
            {
                "id": child_text(repository, "id"),
                "url": sanitize_url(child_text(repository, "url")),
            }
        )

    return PomModel(
        path=path,
        group_id=group_id,
        artifact_id=artifact_id,
        version=version,
        packaging=child_text(root, "packaging", "jar") or "jar",
        parent=parent,
        properties=properties,
        modules=modules,
        dependencies=dependencies,
        managed_dependencies=managed_dependencies,
        repositories=repositories,
    )


def sanitize_url(value: str | None) -> str | None:
    if not value:
        return value
    try:
        parts = urlsplit(value)
        if "@" not in parts.netloc:
            return value
        host = parts.netloc.rsplit("@", 1)[-1]
        return urlunsplit((parts.scheme, host, parts.path, parts.query, parts.fragment))
    except ValueError:
        return re.sub(r"(?<=://)[^/@]+@", "<redacted>@", value)


def is_excluded(path: Path) -> bool:
    return any(part in EXCLUDED_DIRS for part in path.parts)


def scan_poms(project_root: Path) -> list[Path]:
    return sorted(path for path in project_root.rglob("pom.xml") if not is_excluded(path.relative_to(project_root)))


def find_project_root(path: Path) -> Path:
    candidate = path.resolve()
    if candidate.is_file():
        candidate = candidate.parent
    if (candidate / "pom.xml").is_file():
        return candidate
    for parent in [candidate, *candidate.parents]:
        if (parent / "pom.xml").is_file():
            return parent
    raise ValueError(f"No pom.xml found at or above {path}")


def canonical_key(key: str) -> str:
    segments: list[str] = []
    for segment in key.strip().split("."):
        segment = re.sub(r"([a-z0-9])([A-Z])", r"\1-\2", segment)
        segment = segment.replace("_", "-").lower()
        segments.append(segment)
    return ".".join(segments)


SENSITIVE_SEGMENTS = {
    "password",
    "passwd",
    "secret",
    "secret-key",
    "access-key",
    "access-key-id",
    "access-key-secret",
    "corp-secret",
    "private-key",
    "client-secret",
    "credential",
    "credentials",
}

SENSITIVE_TOKEN_LEAVES = {
    "token",
    "access-token",
    "api-token",
    "auth-token",
    "bearer-token",
    "id-token",
    "personal-access-token",
    "refresh-token",
    "registration-token",
    "security-token",
    "service-token",
    "session-token",
    "token-value",
}


def is_sensitive_key(key: str) -> bool:
    canonical = canonical_key(key)
    segments = canonical.split(".")
    if any(segment in SENSITIVE_SEGMENTS or segment.endswith("-secret") for segment in segments):
        return True
    leaf = segments[-1] if segments else ""
    return leaf in SENSITIVE_TOKEN_LEAVES


def _strip_inline_comment(value: str) -> str:
    quote: str | None = None
    for index, char in enumerate(value):
        if char in {"'", '"'}:
            if quote is None:
                quote = char
            elif quote == char:
                quote = None
        elif char == "#" and quote is None and (index == 0 or value[index - 1].isspace()):
            return value[:index].rstrip()
    return value.strip()


def parse_simple_yaml(text: str) -> dict[str, str]:
    """Flatten common Spring YAML without executing tags or requiring PyYAML."""
    flattened: dict[str, str] = {}
    stack: list[tuple[int, str]] = []
    for raw_line in text.splitlines():
        if not raw_line.strip() or raw_line.lstrip().startswith(("#", "---")):
            continue
        if raw_line.lstrip().startswith("-"):
            continue
        match = re.match(r"^(\s*)([A-Za-z0-9_.-]+)\s*:\s*(.*)$", raw_line)
        if not match:
            continue
        indent = len(match.group(1).replace("\t", "    "))
        key = match.group(2)
        value = _strip_inline_comment(match.group(3))
        while stack and indent <= stack[-1][0]:
            stack.pop()
        full_key = canonical_key(".".join([item[1] for item in stack] + [key]))
        if value:
            flattened[full_key] = value.strip().strip("'\"")
        else:
            stack.append((indent, key))
    return flattened


def parse_properties(text: str) -> dict[str, str]:
    flattened: dict[str, str] = {}
    logical = ""
    for raw_line in text.splitlines():
        line = raw_line.strip()
        if not line or line.startswith(("#", "!")):
            continue
        logical += line
        if logical.endswith("\\"):
            logical = logical[:-1]
            continue
        match = re.match(r"([^:=\s]+)\s*[:=]\s*(.*)$", logical)
        if match:
            flattened[canonical_key(match.group(1))] = match.group(2).strip()
        logical = ""
    return flattened


CONFIG_NAME_RE = re.compile(
    r"^(application|bootstrap)(-[^.]+)?\.(ya?ml|properties)$|^(jdbc|jpa)\.properties$",
    re.IGNORECASE,
)


def collect_config(project_root: Path, redact: bool = True) -> dict[str, Any]:
    files: list[dict[str, Any]] = []
    combined: dict[str, list[dict[str, str]]] = {}
    for path in sorted(project_root.rglob("*")):
        if not path.is_file() or not CONFIG_NAME_RE.match(path.name):
            continue
        rel = path.relative_to(project_root)
        if is_excluded(rel):
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            continue
        entries = parse_simple_yaml(text) if path.suffix.lower() in {".yml", ".yaml"} else parse_properties(text)
        safe_entries: dict[str, str] = {}
        for key, value in sorted(entries.items()):
            safe_value = "<redacted>" if redact and is_sensitive_key(key) and value else value
            safe_entries[key] = safe_value
            combined.setdefault(key, []).append({"file": rel.as_posix(), "value": safe_value})
        files.append({"path": rel.as_posix(), "entries": safe_entries})
    return {"files": files, "keys": sorted(combined), "values": combined}


def load_manifest(path: Path = MANIFEST_PATH) -> dict[str, Any]:
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise ValueError(f"Cannot load Athena manifest {path}: {exc}") from exc


def project_report(project: Path, redact: bool = True) -> dict[str, Any]:
    project_root = find_project_root(project)
    pom_paths = scan_poms(project_root)
    models: list[PomModel] = []
    parse_errors: list[str] = []
    for path in pom_paths:
        try:
            models.append(parse_pom(path))
        except ValueError as exc:
            parse_errors.append(str(exc))

    athena_dependencies: list[dict[str, Any]] = []
    bom_imports: list[dict[str, Any]] = []
    repositories: list[dict[str, str | None]] = []
    java_versions: dict[str, str] = {}
    for model in models:
        rel_pom = model.path.relative_to(project_root).as_posix()
        for key in ("java.version", "maven.compiler.source", "maven.compiler.target", "maven.compiler.release"):
            value = model.properties.get(key)
            if value and key not in java_versions:
                java_versions[key] = resolve_properties(value, model.properties) or value
        for repo in model.repositories:
            item = {"pom": rel_pom, **repo}
            if item not in repositories:
                repositories.append(item)
        for dep in model.managed_dependencies:
            if dep.group_id == ATHENA_GROUP_ID and dep.artifact_id == "framework-dependencies" and dep.scope == "import":
                bom_imports.append({"pom": rel_pom, **dep.as_dict()})
        for dep in model.dependencies:
            if dep.group_id == ATHENA_GROUP_ID:
                athena_dependencies.append({"pom": rel_pom, **dep.as_dict()})

    source_counts = {"main_java": 0, "test_java": 0, "resource_files": 0}
    for path in project_root.rglob("*"):
        if not path.is_file() or is_excluded(path.relative_to(project_root)):
            continue
        rel = path.relative_to(project_root).as_posix()
        if "/src/main/java/" in f"/{rel}" and path.suffix == ".java":
            source_counts["main_java"] += 1
        elif "/src/test/java/" in f"/{rel}" and path.suffix == ".java":
            source_counts["test_java"] += 1
        elif "/src/main/resources/" in f"/{rel}":
            source_counts["resource_files"] += 1

    version_candidates = sorted(
        {
            dep.get("version")
            for dep in [*bom_imports, *athena_dependencies]
            if dep.get("version") and "${" not in str(dep.get("version"))
        }
    )

    return {
        "schema_version": 1,
        "project_root": str(project_root),
        "root_pom": str(project_root / "pom.xml"),
        "pom_count": len(models),
        "parse_errors": parse_errors,
        "build": {
            "maven_wrapper": (project_root / "mvnw").is_file(),
            "java_versions": java_versions,
        },
        "athena": {
            "detected": bool(bom_imports or athena_dependencies),
            "bom_imports": bom_imports,
            "dependencies": athena_dependencies,
            "version_candidates": version_candidates,
        },
        "repositories": repositories,
        "configuration": collect_config(project_root, redact=redact),
        "source_counts": source_counts,
    }


def json_dump(data: Any) -> str:
    return json.dumps(data, ensure_ascii=False, indent=2, sort_keys=True) + "\n"


def write_output(text: str, output: str | None) -> None:
    if output:
        path = Path(output).expanduser().resolve()
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text, encoding="utf-8")
    else:
        sys.stdout.write(text)


def parse_feature_list(values: Iterable[str]) -> list[str]:
    features: list[str] = []
    for value in values:
        for item in value.split(","):
            normalized = canonical_key(item.strip()).replace(".", "-")
            if normalized and normalized not in features:
                features.append(normalized)
    return features
