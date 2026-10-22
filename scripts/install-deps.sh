#!/usr/bin/env bash
# Prerequisite check only — this script does NOT download or install anything.
set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
missing=0

if command -v java >/dev/null 2>&1; then
  version="$(java -version 2>&1 | sed -n '1s/.*version "\([0-9]*\).*/\1/p')"
  if [[ "$version" =~ ^[0-9]+$ ]] && (( version >= 17 )); then echo "OK Java $version"; else echo "MISSING Java 17+ (found: ${version:-unknown})"; missing=1; fi
else echo "MISSING Java 17+"; missing=1; fi

sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "$sdk" && -f "$ROOT/local.properties" ]]; then sdk="$(sed -n 's/^sdk\.dir=//p' "$ROOT/local.properties" | head -n1 | sed 's/\\\\/:/g')"; fi
if [[ -n "$sdk" && -d "$sdk" ]]; then echo "OK Android SDK: $sdk"; else echo "MISSING Android SDK (set ANDROID_HOME/ANDROID_SDK_ROOT or local.properties sdk.dir)"; missing=1; fi
if [[ -x "$ROOT/gradlew" && -f "$ROOT/gradle/wrapper/gradle-wrapper.jar" && -f "$ROOT/gradle/wrapper/gradle-wrapper.properties" ]]; then echo "OK Gradle wrapper files"; else echo "MISSING generated Gradle wrapper files (create with gradle wrapper or Android Studio)"; missing=1; fi
exit "$missing"
