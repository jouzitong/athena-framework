#!/usr/bin/env bash

if [ -z "${BASH_VERSION:-}" ]; then
  exec bash "$0" "$@"
fi

set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_POM="$ROOT_DIR/pom.xml"

TARGET_VERSION=""

usage() {
  cat <<EOF
Usage:
  sh $SCRIPT_NAME -v <version>

Example:
  sh $SCRIPT_NAME -v 1.1.1
  sh $SCRIPT_NAME -v 1.1.2-SNAPSHOT
EOF
}

log() {
  printf '[%s] %s\n' "$SCRIPT_NAME" "$*"
}

die() {
  printf '[%s] ERROR: %s\n' "$SCRIPT_NAME" "$*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "Missing required command: $1"
}

ensure_git_clean() {
  if [[ -n "$(git status --porcelain)" ]]; then
    die "Git working tree is dirty. Commit or stash current changes first. / Git 工作区不干净，请先提交或暂存当前改动。"
  fi
}

parse_args() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -v|--version)
        [[ $# -ge 2 ]] || die "--version requires a value. / --version 需要传入版本号。"
        TARGET_VERSION="$2"
        shift 2
        ;;
      -h|--help)
        usage
        exit 0
        ;;
      *)
        die "Unknown option: $1 / 未知参数: $1"
        ;;
    esac
  done

  [[ -n "$TARGET_VERSION" ]] || die "Missing required option: -v <version> / 缺少必填参数: -v <version>"
}

current_version() {
  perl -0777 -ne 'if (m{<revision>\s*([^<\s]+)\s*</revision>}s) { print $1; exit 0 } exit 1' "$ROOT_POM"
}

update_revision() {
  local new_version="$1"
  perl -0777 -i -pe "s#(<revision>\\s*)[^<\\s]+(\\s*</revision>)#\${1}${new_version}\${2}#g" "$ROOT_POM"
}

is_snapshot() {
  [[ "$1" == *-SNAPSHOT ]]
}

main() {
  parse_args "$@"

  require_cmd git
  require_cmd mvn
  require_cmd perl

  cd "$ROOT_DIR"

  [[ -f "$ROOT_POM" ]] || die "Root pom not found: $ROOT_POM / 未找到根 pom 文件: $ROOT_POM"

  local before_version
  before_version="$(current_version)" || die "Cannot read <revision> from $ROOT_POM / 无法从 $ROOT_POM 读取 <revision>"
  [[ "$before_version" != "$TARGET_VERSION" ]] || die "Version is already $TARGET_VERSION / 当前版本已经是 $TARGET_VERSION"

  ensure_git_clean

  log "Current version: $before_version"
  log "Target version: $TARGET_VERSION"

  update_revision "$TARGET_VERSION"

  if git diff --quiet -- "$ROOT_POM"; then
    die "No changes detected in $ROOT_POM / 未检测到 $ROOT_POM 的版本变更"
  fi

  log "Run Maven validate"
  mvn -q -DskipTests validate

  git add -- "$ROOT_POM"
  git commit -m "chore: bump version to $TARGET_VERSION"

  local tag_name=""
  if ! is_snapshot "$TARGET_VERSION"; then
    tag_name="v$TARGET_VERSION"
    if git rev-parse -q --verify "refs/tags/$tag_name" >/dev/null 2>&1; then
      die "Git tag already exists: $tag_name / Git 标签已存在: $tag_name"
    fi
    git tag -a "$tag_name" -m "Release $TARGET_VERSION"
  fi

  local branch_name
  branch_name="$(git rev-parse --abbrev-ref HEAD)"

  printf '\n'
  log "Done."
  log "Commit: $(git rev-parse --short HEAD)"
  if [[ -n "$tag_name" ]]; then
    log "Tag: $tag_name"
  fi

  printf '\nRun these commands to push:\n'
  printf 'git push origin %s\n' "$branch_name"
  if [[ -n "$tag_name" ]]; then
    printf 'git push origin %s\n' "$tag_name"
  fi
}

main "$@"
