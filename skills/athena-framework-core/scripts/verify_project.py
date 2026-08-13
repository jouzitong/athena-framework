#!/usr/bin/env python3
"""Run static validation and a bounded Maven verification for an Athena consumer project."""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import time
from pathlib import Path
from typing import Any

from _shared import json_dump, write_output
from validate_project import validate


SECRET_OUTPUT_RE = re.compile(
    r"(?i)(password|passwd|secret|token|access[-_.]?key)(\s*[=:]\s*)([^\s,;]+)"
)


def redact_output(text: str) -> str:
    return SECRET_OUTPUT_RE.sub(lambda match: f"{match.group(1)}{match.group(2)}<redacted>", text)


def command_for(project: Path, args: argparse.Namespace) -> list[str]:
    wrapper = project / "mvnw"
    if wrapper.is_file():
        executable = str(wrapper)
    else:
        executable = shutil.which("mvn") or "mvn"
    goal = {"compile": "compile", "test": "test", "package": "package"}[args.mode]
    command = [executable, "--batch-mode", "--no-transfer-progress"]
    if args.quiet:
        command.append("-q")
    if args.offline:
        command.append("--offline")
    if args.module:
        command.extend(["-pl", args.module, "-am"])
    if args.mode == "compile":
        command.append("-DskipTests")
    command.append(goal)
    command.extend(args.maven_arg)
    return command


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", default=".")
    parser.add_argument("--mode", choices=("compile", "test", "package"), default="test")
    parser.add_argument("--module", help="Optional Maven -pl selector; -am is added automatically")
    parser.add_argument("--offline", action="store_true")
    parser.add_argument("--quiet", action="store_true")
    parser.add_argument("--timeout", type=int, default=900, help="Maven timeout in seconds")
    parser.add_argument("--skip-static", action="store_true")
    parser.add_argument("--continue-on-static-errors", action="store_true")
    parser.add_argument("--command-only", action="store_true")
    parser.add_argument("--maven-arg", action="append", default=[], help="Additional Maven argument; repeatable")
    parser.add_argument("--format", choices=("text", "json"), default="text")
    parser.add_argument("--output")
    args = parser.parse_args()
    project = Path(args.project).expanduser().resolve()
    if not (project / "pom.xml").is_file():
        print(f"error: no pom.xml in {project}", file=sys.stderr)
        return 2

    static_result: dict[str, Any] | None = None
    if not args.skip_static:
        try:
            static_result = validate(project)
        except ValueError as exc:
            print(f"error: {exc}", file=sys.stderr)
            return 2

    command = command_for(project, args)
    if args.command_only:
        print(" ".join(command))
        return 0

    static_errors = static_result["summary"]["error"] if static_result else 0
    if static_errors and not args.continue_on_static_errors:
        result = {
            "project": str(project),
            "mode": args.mode,
            "command": command,
            "static_validation": static_result,
            "maven": {"executed": False, "reason": "static validation contains errors"},
            "success": False,
        }
        write_output(json_dump(result) if args.format == "json" else render_text(result), args.output)
        return 1

    started = time.monotonic()
    try:
        completed = subprocess.run(
            command,
            cwd=project,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=args.timeout,
            env={**os.environ, "MAVEN_OPTS": os.environ.get("MAVEN_OPTS", "")},
        )
        duration = round(time.monotonic() - started, 3)
        output_lines = redact_output(completed.stdout or "").splitlines()
        maven_result = {
            "executed": True,
            "exit_code": completed.returncode,
            "duration_seconds": duration,
            "output_tail": output_lines[-200:],
        }
        success = completed.returncode == 0 and static_errors == 0
    except subprocess.TimeoutExpired as exc:
        duration = round(time.monotonic() - started, 3)
        raw = exc.stdout if isinstance(exc.stdout, str) else ""
        maven_result = {
            "executed": True,
            "timed_out": True,
            "duration_seconds": duration,
            "output_tail": redact_output(raw).splitlines()[-200:],
        }
        success = False
    except OSError as exc:
        maven_result = {"executed": False, "reason": str(exc)}
        success = False

    result = {
        "project": str(project),
        "mode": args.mode,
        "command": command,
        "static_validation": static_result,
        "maven": maven_result,
        "success": success,
    }
    write_output(json_dump(result) if args.format == "json" else render_text(result), args.output)
    return 0 if success else 1


def render_text(result: dict[str, Any]) -> str:
    lines = [
        f"Project: {result['project']}",
        f"Mode: {result['mode']}",
        "Command: " + " ".join(result["command"]),
    ]
    static = result.get("static_validation")
    if static:
        summary = static["summary"]
        lines.append(f"Static: {summary['error']} error(s), {summary['warning']} warning(s), {summary['info']} info")
        for issue in static["issues"]:
            lines.append(f"  [{issue['severity'].upper()}] {issue['code']}: {issue['message']}")
    maven = result["maven"]
    if not maven.get("executed"):
        lines.append("Maven: not executed - " + maven.get("reason", "unknown reason"))
    elif maven.get("timed_out"):
        lines.append(f"Maven: timed out after {maven['duration_seconds']}s")
    else:
        lines.append(f"Maven: exit {maven['exit_code']} in {maven['duration_seconds']}s")
    if maven.get("output_tail"):
        lines.append("Maven output tail:")
        lines.extend("  " + line for line in maven["output_tail"])
    lines.append("Verification result: " + ("success" if result["success"] else "failed"))
    return "\n".join(lines) + "\n"


if __name__ == "__main__":
    raise SystemExit(main())
