#!/usr/bin/env bash
# Local debug build only — see docs/SIGNING.md for releases.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
./gradlew assembleDebug
echo "APK: $ROOT/app/build/outputs/apk/debug/app-debug.apk"
