#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TASK_DIR="$ROOT/tasks"

errors=0

allowed_statuses=(TODO READY DOING DONE BLOCKED)
allowed_transitions=(
  "TODO->READY"
  "READY->DOING"
  "DOING->DONE"
  "READY->BLOCKED"
  "DOING->BLOCKED"
  "BLOCKED->READY"
)

contains() {
  local needle="$1"; shift
  for v in "$@"; do
    [[ "$v" == "$needle" ]] && return 0
  done
  return 1
}

check_task_file() {
  local file="$1"
  local required=(
    "task.id" "task.name" "task.type" "task.version" "task.status"
    "task.objective" "task.scope.modules" "task.completion.success" "task.completion.failure"
    "sub_task.id" "sub_task.name" "sub_task.type" "sub_task.status"
    "sub_task.objective" "sub_task.completion.success" "sub_task.completion.failure"
  )

  for key in "${required[@]}"; do
    if ! rg -q "$key" "$file"; then
      echo "[ERROR] missing required key '$key' in $file"
      errors=$((errors + 1))
    fi
  done

  while IFS= read -r status; do
    [[ -z "$status" ]] && continue
    if ! contains "$status" "${allowed_statuses[@]}"; then
      echo "[ERROR] invalid status '$status' in $file"
      errors=$((errors + 1))
    fi
  done < <(rg --pcre2 -o "(?:(?:task|sub_task)\.status\s*(?:::+|:)\s*)(TODO|READY|DOING|DONE|BLOCKED)" -r '$1' "$file" || true)

  while IFS= read -r pair; do
    [[ -z "$pair" ]] && continue
    pair="${pair//[[:space:]]/}"
    if ! contains "$pair" "${allowed_transitions[@]}"; then
      echo "[ERROR] disallowed status transition '$pair' in $file"
      errors=$((errors + 1))
    fi
  done < <(rg --pcre2 -o "(TODO|READY|DOING|DONE|BLOCKED)\s*->\s*(TODO|READY|DOING|DONE|BLOCKED)" "$file" || true)
}

echo "[dsl-lint] root=$ROOT"

if [[ ! -d "$TASK_DIR" ]]; then
  echo "[dsl-lint] tasks dir not found: $TASK_DIR"
  exit 1
fi

count=$(find "$TASK_DIR" -type f \( -name '*.adoc' -o -name '*.md' \) | wc -l | tr -d ' ')
if [[ "$count" -eq 0 ]]; then
  echo "[dsl-lint] no task files under $TASK_DIR, skip checks"
  echo "[dsl-lint] OK"
  exit 0
fi

while IFS= read -r f; do
  check_task_file "$f"
done < <(find "$TASK_DIR" -type f \( -name '*.adoc' -o -name '*.md' \))

if [[ "$errors" -gt 0 ]]; then
  echo "[dsl-lint] FAILED with $errors error(s)"
  exit 1
fi

echo "[dsl-lint] OK"
