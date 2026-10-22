#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
(
  cd "$ROOT/templates"
  find . -type f ! -name 'checksums.sha256' -print0 | sort -z | xargs -0 sha256sum > checksums.sha256
)
echo "templates/checksums.sha256 updated"
