#!/usr/bin/env bash
# Deploy the static promo site to Cloudflare Pages via Direct Upload.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
npx wrangler pages deploy site "$@"
