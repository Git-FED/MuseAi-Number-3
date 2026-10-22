#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 1 ]]; then echo "Usage: $0 <unsigned.apk>" >&2; exit 2; fi
input="$1"
if [[ ! -f "$input" ]]; then echo "Error: APK not found: $input" >&2; exit 1; fi
if [[ -z "${MUSE3_KEYSTORE:-}" || ! -f "${MUSE3_KEYSTORE:-}" ]]; then echo "Error: set MUSE3_KEYSTORE to an existing local keystore file." >&2; exit 1; fi
command -v zipalign >/dev/null 2>&1 || { echo "Error: zipalign not found on PATH (install Android Build Tools through Android Studio)." >&2; exit 1; }
command -v apksigner >/dev/null 2>&1 || { echo "Error: apksigner not found on PATH (install Android Build Tools through Android Studio)." >&2; exit 1; }
dir="$(cd "$(dirname "$input")" && pwd)"
base="$(basename "$input" .apk)"
aligned="$dir/${base}-aligned.apk"
out="$dir/${base}-signed.apk"
zipalign -p -f 4 "$input" "$aligned"
apksigner sign --ks "$MUSE3_KEYSTORE" --ks-key-alias musenumberthree --out "$out" "$aligned"
apksigner verify --print-certs "$out"
rm -f "$aligned"
echo "Signed APK: $out"
