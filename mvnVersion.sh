#!/usr/bin/env bash

# If launched by sh, re-exec with bash.
if [ -z "${BASH_VERSION:-}" ]; then
  exec bash "$0" "$@"
fi

# mvnVersion.sh
# 核心参数:
#   --version, -v   目标版本号(必填), 例如: 1.2.0 / 1.2.0-SNAPSHOT
#   --message, -m   Git 提交信息(可选), 默认: chore: bump version to <version>
#
# 非核心参数(均可省略):
#   --old-version   当前版本号(默认自动检测: .mvn/maven.config -> root pom revision)
#   --no-commit     只改文件不提交
#   --dry-run       预览将要变更内容，不落盘
#   --skip-verify   升级后跳过 mvn validate
#   --allow-dirty   允许在脏工作区执行(默认不允许)
#   --help, -h      显示帮助

set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_CONFIG="$ROOT_DIR/.mvn/maven.config"
ROOT_POM="$ROOT_DIR/pom.xml"

TARGET_VERSION=""
COMMIT_MESSAGE=""
OLD_VERSION=""
NO_COMMIT=0
DRY_RUN=0
SKIP_VERIFY=0
ALLOW_DIRTY=0
HAS_RG=0

print_help() {
  cat <<USAGE
Usage:
  ./$SCRIPT_NAME --version <newVersion> [options]

Core options:
  -v, --version <newVersion>   Target repository version (required)
  -m, --message <message>      Git commit message (default: chore: bump version to <newVersion>)

Optional:
      --old-version <old>      Current version (default: auto-detect from .mvn/maven.config or root pom)
      --no-commit              Modify files only, do not commit
      --dry-run                Preview planned changes, do not modify files
      --skip-verify            Skip mvn validate check (\`mvn -q -DskipTests validate\`)
      --allow-dirty            Allow running on dirty git working tree
  -h, --help                   Show this help message

Examples:
  ./$SCRIPT_NAME -v 1.2.0
  ./$SCRIPT_NAME -v 1.2.0-SNAPSHOT -m "release: bump to 1.2.0-SNAPSHOT"
  ./$SCRIPT_NAME -v 1.2.0 --no-commit --skip-verify
USAGE
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

trim() {
  local input="$1"
  input="${input#${input%%[![:space:]]*}}"
  input="${input%${input##*[![:space:]]}}"
  printf '%s' "$input"
}

list_poms() {
  if [[ "$HAS_RG" -eq 1 ]]; then
    rg --files -g '**/pom.xml'
  else
    find . -type f -name 'pom.xml' | sed 's#^\./##' | sort
  fi
}

has_line_in_file() {
  local pattern="$1"
  local file="$2"
  grep -Eq "$pattern" "$file"
}

search_literal_in_poms() {
  local token="$1"
  if [[ "$HAS_RG" -eq 1 ]]; then
    rg -n -F "$token" --glob '**/pom.xml' "$ROOT_DIR" || true
  else
    local pom
    list_poms | while IFS= read -r pom; do
      grep -nF "$token" "$pom" 2>/dev/null | sed "s#^#$ROOT_DIR/$pom:#" || true
    done
  fi
}

detect_old_version() {
  local rev

  if [[ -f "$MAVEN_CONFIG" ]]; then
    rev="$(sed -n 's/^-Drevision=//p' "$MAVEN_CONFIG" | head -n1)"
    if [[ -n "$rev" ]]; then
      printf '%s' "$(trim "$rev")"
      return 0
    fi
  fi

  rev="$(perl -0777 -ne 'if (m{<revision>\s*([^<\s]+)\s*</revision>}s) { print $1; exit 0 } exit 1' "$ROOT_POM" 2>/dev/null || true)"
  if [[ -n "$rev" ]]; then
    printf '%s' "$(trim "$rev")"
    return 0
  fi

  rev="$(perl -0777 -ne 'if (m{<project\b.*?<artifactId>\s*athena-framework\s*</artifactId>.*?<version>\s*([^<\s]+)\s*</version>}s) { print $1; exit 0 } exit 1' "$ROOT_POM" 2>/dev/null || true)"
  [[ -n "$rev" ]] || die "Failed to detect current version"
  printf '%s' "$(trim "$rev")"
}

ensure_revision_property_in_root_pom() {
  if ! has_line_in_file '<revision>' "$ROOT_POM"; then
    perl -0777 -i -pe 's#(<properties>\s*)#${1}        <revision>1.0.0</revision>\n#s' "$ROOT_POM"
  fi
}

normalize_version_layout() {
  # Root version always uses ${revision}
  perl -0777 -i -pe 's#(<project\b.*?<artifactId>\s*athena-framework\s*</artifactId>.*?<version>\s*)[^<\s]+(\s*</version>)#${1}\${revision}${2}#s' "$ROOT_POM"

  # Parent version in all modules -> ${revision}
  list_poms | while IFS= read -r pom; do
    perl -0777 -i -pe 's#(<parent>\s*<groupId>\s*[^<]+\s*</groupId>\s*<artifactId>\s*[^<]+\s*</artifactId>\s*<version>\s*)[^<\s]+(\s*</version>)#${1}\${revision}${2}#s' "$pom"
  done

  # framework-dependencies import versions -> ${project.version}
  list_poms | while IFS= read -r pom; do
    perl -0777 -i -pe 's#(<artifactId>\s*framework-dependencies\s*</artifactId>\s*<groupId>\s*org\.athena\s*</groupId>\s*(?:<!--.*?-->\s*)*<version>\s*)[^<\s]+(\s*</version>)#${1}\${project.version}${2}#gs' "$pom"
  done
}

update_maven_config_revision() {
  local new="$1"

  mkdir -p "$ROOT_DIR/.mvn"
  if [[ -f "$MAVEN_CONFIG" ]] && has_line_in_file '^-Drevision=' "$MAVEN_CONFIG"; then
    sed -i.bak -E "s#^-Drevision=.*#-Drevision=${new}#g" "$MAVEN_CONFIG"
    rm -f "$MAVEN_CONFIG.bak"
  else
    {
      [[ -f "$MAVEN_CONFIG" ]] && cat "$MAVEN_CONFIG"
      printf '%s\n' "-Drevision=${new}"
    } | awk '!seen[$0]++' > "${MAVEN_CONFIG}.tmp"
    mv "${MAVEN_CONFIG}.tmp" "$MAVEN_CONFIG"
  fi
}

update_root_pom_revision() {
  local new="$1"

  ensure_revision_property_in_root_pom
  perl -0777 -i -pe "s#(<revision>\\s*)[^<\\s]+(\\s*</revision>)#\\1${new}\\2#g" "$ROOT_POM"
}

preview_matches() {
  local old="$1"
  log "Preview entries with version '$old'"
  search_literal_in_poms "<version>${old}</version>"
  search_literal_in_poms "<revision>${old}</revision>"
  grep -nE "^-Drevision=${old}$" "$MAVEN_CONFIG" 2>/dev/null || true
}

parse_args() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -v|--version)
        [[ $# -ge 2 ]] || die "--version requires a value"
        TARGET_VERSION="$(trim "$2")"
        shift 2
        ;;
      -m|--message)
        [[ $# -ge 2 ]] || die "--message requires a value"
        COMMIT_MESSAGE="$2"
        shift 2
        ;;
      --old-version)
        [[ $# -ge 2 ]] || die "--old-version requires a value"
        OLD_VERSION="$(trim "$2")"
        shift 2
        ;;
      --no-commit)
        NO_COMMIT=1
        shift
        ;;
      --dry-run)
        DRY_RUN=1
        shift
        ;;
      --skip-verify)
        SKIP_VERIFY=1
        shift
        ;;
      --allow-dirty)
        ALLOW_DIRTY=1
        shift
        ;;
      -h|--help)
        print_help
        exit 0
        ;;
      *)
        die "Unknown option: $1 (use --help)"
        ;;
    esac
  done

  [[ -n "$TARGET_VERSION" ]] || die "Missing required option: --version"
}

main() {
  parse_args "$@"

  require_cmd git
  require_cmd mvn
  require_cmd perl
  require_cmd sed
  require_cmd awk
  if command -v rg >/dev/null 2>&1; then
    HAS_RG=1
  fi

  cd "$ROOT_DIR"

  if [[ -z "$OLD_VERSION" ]]; then
    OLD_VERSION="$(detect_old_version)"
  fi

  [[ -n "$OLD_VERSION" ]] || die "Unable to determine old version"
  [[ "$OLD_VERSION" != "$TARGET_VERSION" ]] || die "old-version and target version are the same: $TARGET_VERSION"

  if [[ "$ALLOW_DIRTY" -ne 1 ]] && [[ -n "$(git status --porcelain)" ]]; then
    die "Git working tree is dirty. Commit/stash first, or pass --allow-dirty"
  fi

  if [[ -z "$COMMIT_MESSAGE" ]]; then
    COMMIT_MESSAGE="chore: bump version to $TARGET_VERSION"
  fi

  log "Root dir: $ROOT_DIR"
  log "Old version: $OLD_VERSION"
  log "Target version: $TARGET_VERSION"

  if [[ "$DRY_RUN" -eq 1 ]]; then
    preview_matches "$OLD_VERSION"
    log "Dry-run only. No file has been modified."
    exit 0
  fi

  log "Step 1/5: normalize version layout to single-source revision"
  normalize_version_layout

  log "Step 2/5: update revision source (.mvn/maven.config)"
  update_maven_config_revision "$TARGET_VERSION"

  log "Step 3/5: update root pom revision value"
  update_root_pom_revision "$OLD_VERSION" "$TARGET_VERSION"

  log "Step 4/5: verify no stale repo version remains"
  local stale_pom_lines stale_cfg_lines
  stale_pom_lines="$(
    {
      search_literal_in_poms "<version>${OLD_VERSION}</version>"
      search_literal_in_poms "<revision>${OLD_VERSION}</revision>"
    } | sed '/^$/d' || true
  )"
  stale_cfg_lines="$(grep -nE "^-Drevision=${OLD_VERSION}$" "$MAVEN_CONFIG" 2>/dev/null || true)"
  if [[ -n "$stale_pom_lines" || -n "$stale_cfg_lines" ]]; then
    [[ -n "$stale_pom_lines" ]] && printf '%s\n' "$stale_pom_lines" >&2
    [[ -n "$stale_cfg_lines" ]] && printf '%s\n' "$stale_cfg_lines" >&2
    die "Found stale version '$OLD_VERSION' after upgrade."
  fi

  if [[ "$SKIP_VERIFY" -ne 1 ]]; then
    log "Step 5/5: run maven validate (skip tests)"
    mvn -q -DskipTests validate
  else
    log "Step 5/5: skip maven validate (--skip-verify)"
  fi

  if [[ "$NO_COMMIT" -eq 1 ]]; then
    log "Done. Files updated, commit skipped (--no-commit)."
    exit 0
  fi

  log "Create git commit"
  git add -- .mvn/maven.config pom.xml
  git add -- $(list_poms)

  if git diff --cached --quiet; then
    die "No staged changes found after version upgrade"
  fi

  git commit -m "$COMMIT_MESSAGE"
  log "Done. Commit created: $COMMIT_MESSAGE"
}

main "$@"
