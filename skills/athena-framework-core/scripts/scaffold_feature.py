#!/usr/bin/env python3
"""Render reviewed Athena consumer templates; dry-run is the default."""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Any

from _shared import TEMPLATES_ROOT, load_manifest


PLACEHOLDER_RE = re.compile(r"\{\{([A-Z0-9_]+)}}")
JAVA_IDENTIFIER_RE = re.compile(r"[A-Za-z][A-Za-z0-9_]*")
JAVA_PACKAGE_RE = re.compile(r"[A-Za-z][A-Za-z0-9_]*(?:\.[A-Za-z][A-Za-z0-9_]*)*")
MAVEN_GROUP_RE = re.compile(r"[A-Za-z0-9_]+(?:[.-][A-Za-z0-9_]+)*")
MAVEN_ARTIFACT_RE = re.compile(r"[A-Za-z0-9][A-Za-z0-9_.-]*")
MAVEN_VERSION_RE = re.compile(r"[A-Za-z0-9][A-Za-z0-9_.+\-]*")
TABLE_NAME_RE = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")


def load_catalog() -> dict[str, Any]:
    path = TEMPLATES_ROOT / "catalog.json"
    try:
        return json.loads(path.read_text(encoding="utf-8"))["templates"]
    except (OSError, json.JSONDecodeError, KeyError) as exc:
        raise ValueError(f"Cannot load template catalog {path}: {exc}") from exc


def parse_sets(values: list[str]) -> dict[str, str]:
    result: dict[str, str] = {}
    for value in values:
        if "=" not in value:
            raise ValueError(f"--set must use KEY=VALUE: {value}")
        key, raw = value.split("=", 1)
        key = key.strip().upper()
        if not re.fullmatch(r"[A-Z][A-Z0-9_]*", key):
            raise ValueError(f"Invalid template variable: {key}")
        result[key] = raw
    return result


def lower_camel(name: str) -> str:
    return name[:1].lower() + name[1:] if name else name


def default_variables(args: argparse.Namespace) -> dict[str, str]:
    manifest = load_manifest()
    package = args.package or "com.example.athena"
    name = args.name or "Demo"
    return {
        "PACKAGE": package,
        "PACKAGE_PATH": package.replace(".", "/"),
        "NAME": name,
        "NAME_LOWER": lower_camel(name),
        "GROUP_ID": args.group_id or package.rsplit(".", 1)[0],
        "ARTIFACT_ID": args.artifact_id or re.sub(r"[^a-z0-9-]", "-", lower_camel(name).lower()),
        "ATHENA_VERSION": args.athena_version or manifest["framework"]["revision"],
        "SPRING_BOOT_VERSION": manifest["baseline"]["spring_boot"],
        "JAVA_VERSION": manifest["baseline"]["java"],
        "TABLE_NAME": args.table_name or re.sub(r"(?<!^)(?=[A-Z])", "_", name).lower(),
    }


def render(value: str, variables: dict[str, str]) -> str:
    rendered = PLACEHOLDER_RE.sub(lambda match: variables.get(match.group(1), match.group(0)), value)
    unresolved = sorted(set(PLACEHOLDER_RE.findall(rendered)))
    if unresolved:
        raise ValueError("Unresolved template variables: " + ", ".join(unresolved))
    return rendered


def validate_variables(variables: dict[str, str]) -> None:
    checks = {
        "PACKAGE": (JAVA_PACKAGE_RE, "a dot-separated Java package"),
        "NAME": (JAVA_IDENTIFIER_RE, "a Java class-name stem"),
        "GROUP_ID": (MAVEN_GROUP_RE, "a Maven groupId"),
        "ARTIFACT_ID": (MAVEN_ARTIFACT_RE, "a Maven artifactId"),
        "ATHENA_VERSION": (MAVEN_VERSION_RE, "a literal Maven version"),
        "SPRING_BOOT_VERSION": (MAVEN_VERSION_RE, "a literal Maven version"),
        "JAVA_VERSION": (MAVEN_VERSION_RE, "a Java version"),
        "TABLE_NAME": (TABLE_NAME_RE, "an unquoted SQL identifier"),
    }
    for key, (pattern, description) in checks.items():
        value = variables.get(key, "")
        if not pattern.fullmatch(value):
            raise ValueError(f"{key} must be {description}: {value!r}")


def build_plan(template_id: str, template: dict[str, Any], project: Path, variables: dict[str, str]) -> list[dict[str, Any]]:
    plan: list[dict[str, Any]] = []
    templates_root = TEMPLATES_ROOT.resolve()
    project_root = project.resolve()
    for item in template.get("files", []):
        source = (templates_root / item["source"]).resolve()
        try:
            source.relative_to(templates_root)
        except ValueError as exc:
            raise ValueError(f"Template source escapes the template root: {item['source']}") from exc
        if not source.is_file():
            raise ValueError(f"Template source does not exist: {source}")
        destination_name = render(item["destination"], variables)
        destination = (project_root / destination_name).resolve()
        try:
            destination.relative_to(project_root)
        except ValueError as exc:
            raise ValueError(f"Rendered destination escapes the project: {destination_name}") from exc
        content = render(source.read_text(encoding="utf-8"), variables)
        plan.append(
            {
                "template": template_id,
                "source": source,
                "destination": destination,
                "content": content,
                "exists": destination.exists(),
            }
        )
    return plan


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--list", action="store_true", help="List available templates")
    parser.add_argument("--template", help="Template id from --list")
    parser.add_argument("--project", default=".")
    parser.add_argument("--package")
    parser.add_argument("--name")
    parser.add_argument("--group-id")
    parser.add_argument("--artifact-id")
    parser.add_argument("--athena-version")
    parser.add_argument("--table-name")
    parser.add_argument("--set", action="append", default=[], metavar="KEY=VALUE")
    parser.add_argument("--write", action="store_true", help="Write rendered files; otherwise dry-run")
    parser.add_argument("--force", action="store_true", help="Allow overwriting existing files")
    parser.add_argument("--show-content", action="store_true", help="Print rendered content during dry-run")
    args = parser.parse_args()
    try:
        catalog = load_catalog()
        if args.list:
            for template_id, item in sorted(catalog.items()):
                artifacts = ", ".join(item.get("required_artifacts", [])) or "none"
                print(f"{template_id}: {item.get('description', '')}")
                print(f"  required artifacts: {artifacts}")
            return 0
        if not args.template:
            parser.error("--template is required unless --list is used")
        template = catalog.get(args.template)
        if template is None:
            raise ValueError(f"Unknown template: {args.template}; use --list")
        variables = default_variables(args)
        variables.update(parse_sets(args.set))
        variables["PACKAGE_PATH"] = variables["PACKAGE"].replace(".", "/")
        variables["NAME_LOWER"] = lower_camel(variables["NAME"])
        validate_variables(variables)
        project = Path(args.project).expanduser().resolve()
        if args.write:
            project.mkdir(parents=True, exist_ok=True)
        elif not project.exists():
            raise ValueError(f"Project path does not exist in dry-run mode: {project}")
        plan = build_plan(args.template, template, project, variables)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2

    print(f"Template: {args.template}")
    print("Required artifacts: " + (", ".join(template.get("required_artifacts", [])) or "none"))
    print("Mode: " + ("write" if args.write else "dry-run"))
    blocked = False
    for item in plan:
        state = "exists" if item["exists"] else "new"
        print(f"  - [{state}] {item['destination']}")
        if item["exists"] and not args.force:
            blocked = True
        if args.show_content and not args.write:
            print("-----")
            print(item["content"], end="" if item["content"].endswith("\n") else "\n")
            print("-----")
    if blocked:
        print("Refusing to overwrite existing files; choose new names or pass --force explicitly.", file=sys.stderr)
        return 1
    if not args.write:
        print("Dry-run only. Re-run with --write after reviewing destinations and content.")
        return 0
    for item in plan:
        destination: Path = item["destination"]
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_text(item["content"], encoding="utf-8")
    print(f"Wrote {len(plan)} file(s). Review and adapt all business placeholders before building.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
