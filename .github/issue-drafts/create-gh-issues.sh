#!/usr/bin/env bash

set -euo pipefail

REPO="${1:-}"

if [[ -z "$REPO" ]]; then
  echo "usage: $0 <owner/repo>"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

for file in "$SCRIPT_DIR"/[0-9][0-9]-*.md; do
  title="$(sed -n '1s/^# //p' "$file")"
  body_file="$(mktemp)"
  sed '1d' "$file" > "$body_file"
  echo "Creating issue from $(basename "$file"): $title"
  gh issue create --repo "$REPO" --title "$title" --body-file "$body_file"
  rm -f "$body_file"
done
