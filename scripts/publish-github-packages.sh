#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
POM_PATH="$ROOT_DIR/pom.xml"

DEFAULT_OWNER="jouzitong"
DEFAULT_REPO="athena-framework"

usage() {
  cat <<'EOF'
Usage: scripts/publish-github-packages.sh [options]

Options:
  --owner <value>     GitHub Packages owner, default: jouzitong
  --repo <value>      GitHub Packages repo, default: athena-framework
  --revision <value>  Maven revision, default: root pom.xml <revision>
  --dry-run           Print the Maven command without executing it
  -h, --help          Show this help message
EOF
}

read_revision() {
  awk '
    /<properties>/ { in_properties=1; next }
    /<\/properties>/ { in_properties=0 }
    in_properties && /<revision>/ {
      line=$0
      sub(/^.*<revision>[[:space:]]*/, "", line)
      sub(/[[:space:]]*<\/revision>.*$/, "", line)
      print line
      exit
    }
  ' "$POM_PATH"
}

owner="${GITHUB_PACKAGES_OWNER:-$DEFAULT_OWNER}"
repo="${GITHUB_PACKAGES_REPO:-$DEFAULT_REPO}"
revision="${REVISION:-}"
dry_run="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --owner)
      owner="${2:?missing value for --owner}"
      shift 2
      ;;
    --repo)
      repo="${2:?missing value for --repo}"
      shift 2
      ;;
    --revision)
      revision="${2:?missing value for --revision}"
      shift 2
      ;;
    --dry-run)
      dry_run="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

if [[ -z "$revision" ]]; then
  revision="$(read_revision)"
fi

if [[ -z "$revision" ]]; then
  echo "failed to resolve <revision> from $POM_PATH" >&2
  exit 1
fi

cmd=(
  mvn
  -Pgithub-packages
  -DskipTests
  "-Dgithub.packages.owner=$owner"
  "-Dgithub.packages.repo=$repo"
  "-Drevision=$revision"
  clean
  deploy
)

printf -v rendered_cmd '%q ' "${cmd[@]}"
rendered_cmd="${rendered_cmd% }"

if [[ "$dry_run" == "true" ]]; then
  printf '%s\n' "$rendered_cmd"
  exit 0
fi

printf 'Executing: %s\n' "$rendered_cmd"
(cd "$ROOT_DIR" && "${cmd[@]}")
