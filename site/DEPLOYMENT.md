# Static deployment

This page is plain HTML, CSS, JavaScript, and local assets. It has no server runtime, package manager, build step, database, or environment variables. All site links and local assets are relative so the bundle can be served from a project subpath as well as a domain root.

The archive includes a top-level `index.html` that forwards to `site/`. That makes the promo page discoverable when a static host publishes the repository root. The actual page and its assets remain in `site/`.

## Cloudflare Pages with GitHub as the source

Set the Pages project to use:

- **Build command:** `exit 0`
- **Build output directory:** `site`

There is no install or build step. Pages can publish the checked-in files directly. Do **not** use `npx wrangler deploy` here; that is the Worker deployment command and produces the “Missing entry-point to Worker script or to assets directory” error for this repository. The `_headers` file inside `site/` supplies static response headers.

For a direct upload from a configured Wrangler session, run:

```sh
./scripts/deploy-pages.sh
# equivalent:
npx wrangler pages deploy site
```

The first direct upload may ask for the Cloudflare Pages project name and production branch. No Worker code is required for the landing page.

## Native GitHub Pages

For branch-based publishing from the repository root, the top-level `index.html` forwards to the working page in `site/`; `.nojekyll` is at the repository root. If you instead publish a Pages artifact from GitHub Actions, upload the contents of `site/` directly. A deployment workflow is intentionally not included, so adding this site to a repository will not silently enable automatic publishing. GitHub Pages ignores Cloudflare's `_headers` files.

## Before publishing

1. Review `CONFIGURATION.md` and set only the Discord and payment destinations that are ready to be public.
2. Confirm that the payment destination is an HTTPS hosted checkout and that the age gate meets the applicable requirements. The built-in age check is self-attestation only.
3. Set the final public title, description, social preview image URL, and canonical URL once the production domain is known. They are not guessed in the current HTML.
4. Test the site under the actual subpath or custom domain where it will be served.
