#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?usage: package-template.sh vX.Y.Z}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/dist/face-muse-templates-${VERSION}.zip"

mkdir -p "$ROOT/dist"
rm -f "$OUT"

(
  cd "$ROOT/templates"
  find . -type f ! -name 'checksums.sha256' -print0 | sort -z | xargs -0 sha256sum > checksums.sha256
)

(
  cd "$ROOT"
  zip -qr "$OUT" templates -x 'templates/checksums.sha256.tmp'
)

echo "built $OUT"
