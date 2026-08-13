#!/usr/bin/env python3
"""Inspect an Athena consumer project without changing it."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from _shared import json_dump, project_report, write_output


def render_text(report: dict) -> str:
    athena = report["athena"]
    lines = [
        f"Project: {report['project_root']}",
        f"Maven POMs: {report['pom_count']}",
        f"Maven wrapper: {'yes' if report['build']['maven_wrapper'] else 'no'}",
        f"Athena detected: {'yes' if athena['detected'] else 'no'}",
    ]
    java_versions = report["build"]["java_versions"]
    lines.append("Java baseline: " + (", ".join(f"{key}={value}" for key, value in java_versions.items()) or "not declared"))
    if athena["bom_imports"]:
        lines.append("Athena BOM imports:")
        for item in athena["bom_imports"]:
            lines.append(f"  - {item['artifact_id']}:{item.get('version') or '<inherited>'} ({item['pom']})")
    if athena["dependencies"]:
        lines.append("Athena dependencies:")
        for item in athena["dependencies"]:
            suffix = f":{item['version']}" if item.get("version") else ""
            lines.append(f"  - {item['artifact_id']}{suffix} ({item['pom']})")
    if report["configuration"]["files"]:
        lines.append("Configuration files:")
        for item in report["configuration"]["files"]:
            athena_keys = [key for key in item["entries"] if key.startswith(("athena.", "lib."))]
            lines.append(f"  - {item['path']} ({len(athena_keys)} Athena/lib keys)")
    if report["parse_errors"]:
        lines.append("POM parse errors:")
        lines.extend(f"  - {error}" for error in report["parse_errors"])
    lines.append("Sensitive configuration values are redacted.")
    return "\n".join(lines) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", default=".", help="Consumer project path (default: current directory)")
    parser.add_argument("--format", choices=("text", "json"), default="text")
    parser.add_argument("--output", help="Optional output file; stdout is used by default")
    args = parser.parse_args()
    try:
        report = project_report(Path(args.project), redact=True)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2
    text = json_dump(report) if args.format == "json" else render_text(report)
    write_output(text, args.output)
    return 1 if report["parse_errors"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
