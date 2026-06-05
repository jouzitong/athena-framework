#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPT_PATH="$ROOT_DIR/scripts/publish-github-packages.sh"

if [[ ! -x "$SCRIPT_PATH" ]]; then
  echo "missing executable script: $SCRIPT_PATH" >&2
  exit 1
fi

output="$("$SCRIPT_PATH" --dry-run)"

[[ "$output" == *"-Pgithub-packages"* ]] || {
  echo "expected github-packages profile in command" >&2
  exit 1
}

[[ "$output" == *"-Dgithub.packages.owner=jouzitong"* ]] || {
  echo "expected default owner in command" >&2
  exit 1
}

[[ "$output" == *"-Dgithub.packages.repo=athena-framework"* ]] || {
  echo "expected default repo in command" >&2
  exit 1
}

[[ "$output" == *"-Drevision=1.4.1-SNAPSHOT"* ]] || {
  echo "expected revision from root pom in command" >&2
  exit 1
}

[[ "$output" == *"clean deploy"* ]] || {
  echo "expected deploy goals in command" >&2
  exit 1
}

echo "publish-github-packages dry-run test passed"
