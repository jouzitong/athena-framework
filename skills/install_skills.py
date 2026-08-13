#!/usr/bin/env python3
"""Install the Athena Framework Codex skill distribution into a Codex home."""

from __future__ import annotations

import argparse
import json
import shutil
import sys
from datetime import datetime, timezone
from pathlib import Path


ROOT = Path(__file__).resolve().parent
REGISTRY_PATH = ROOT / "registry.json"


def load_registry() -> dict:
    try:
        return json.loads(REGISTRY_PATH.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SystemExit(f"Cannot read {REGISTRY_PATH}: {exc}") from exc


def dependency_order(registry: dict, requested: list[str]) -> list[str]:
    entries = registry["skills"]
    selected: list[str] = []

    def visit(name: str) -> None:
        if name in selected:
            return
        if name not in entries:
            raise SystemExit(f"Unknown skill: {name}")
        for dependency in entries[name].get("dependencies", []):
            visit(dependency)
        selected.append(name)

    for name in requested:
        visit(name)
    return selected


def validate_source(source: Path, name: str) -> None:
    if not source.is_dir() or not (source / "SKILL.md").is_file():
        raise SystemExit(f"Invalid skill source for {name}: {source} (SKILL.md is required)")


def install_one(source: Path, target_root: Path, name: str, force: bool) -> Path:
    target = target_root / name
    validate_source(source, name)
    if target.exists():
        if not force:
            raise SystemExit(f"Target already exists: {target}; use --force to create a timestamped backup")
        stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
        backup_root = target_root.parent / "skill-backups"
        backup_root.mkdir(parents=True, exist_ok=True)
        backup = backup_root / f"{name}-{stamp}"
        shutil.move(str(target), str(backup))
        print(f"Backed up {target} to {backup}")
    shutil.copytree(source, target, ignore=shutil.ignore_patterns("__pycache__", "*.pyc"))
    return target


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--list", action="store_true", help="List installable skills")
    parser.add_argument("--all", action="store_true", help="Install all skills")
    parser.add_argument("--skill", action="append", default=[], help="Install a skill and its dependencies; repeatable")
    parser.add_argument("--project", default=".", help="Consumer project root (default: current directory)")
    parser.add_argument("--target", help="Skill target directory (default: <project>/.codex/skills)")
    parser.add_argument("--force", action="store_true", help="Back up existing targets before replacing them")
    args = parser.parse_args()
    registry = load_registry()
    entries = registry["skills"]

    if args.list:
        for name, item in entries.items():
            dependencies = ", ".join(item.get("dependencies", [])) or "none"
            print(f"{name}\tdependencies: {dependencies}")
        return 0

    requested = list(entries) if args.all else args.skill
    if not requested:
        parser.error("choose --list, --all, or at least one --skill")
    names = dependency_order(registry, requested)
    project_root = Path(args.project).expanduser().resolve()
    if not project_root.is_dir():
        raise SystemExit(f"Consumer project directory does not exist: {project_root}")
    target_root = Path(args.target).expanduser().resolve() if args.target else project_root / ".codex" / "skills"
    target_root.mkdir(parents=True, exist_ok=True)
    for name in names:
        target = install_one(ROOT / entries[name]["path"], target_root, name, args.force)
        print(f"Installed {name} -> {target}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
