# Release checklist

For `v0.2.0`, confirm that the repository contains only the generic templates, documentation, packaging script, workflow, license, and public-safe site assets. It must not contain personal names, handles, emails, locations, account identifiers, private project history, transcripts, media, nested archives, credentials, or `.env` files.

Run the following checks locally:

```bash
python3 -m json.tool templates/index.json >/dev/null
python3 -m json.tool site/site.webmanifest >/dev/null
python3 - <<'PY'
import json
for line in open('templates/events.jsonl'):
    json.loads(line)
print('JSONL OK')
PY
bash scripts/package-template.sh v0.2.0
unzip -l dist/face-muse-templates-v0.2.0.zip
(cd templates && sha256sum -c checksums.sha256)
```

For the website, publish `site/` as the Cloudflare Pages build output directory. Confirm that the deployed page loads `favicon.svg`, `site.webmanifest`, and every file in `site/assets/promo/`. Replace the placeholder repository URL before publishing.

Then commit, create an annotated tag, and push the tag to the repository. GitHub Actions will attach the template ZIP to the release.
