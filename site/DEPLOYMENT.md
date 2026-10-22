# Static deployment

This page is plain HTML, CSS, JavaScript, and local assets. It has no server runtime, package manager, build step, database, or environment variables. All site links and local assets are relative so the bundle can be served from a project subpath as well as a domain root.

## Cloudflare Pages with GitHub as the source

Connect the repository to Cloudflare Pages and point the static project at the `site/` directory. Select the no-framework/static configuration and publish the files in that directory as-is; there is no install or build command. The site entry point is `site/index.html`. The `_headers` file supplies static response headers to Cloudflare Pages. No Worker code is required for the landing page. If using Workers with static assets instead, configure `site/` as the assets directory and route the static assets normally.

## Native GitHub Pages

The same files can be published as a Pages artifact from a GitHub Actions workflow by uploading the contents of `site/`. A deployment workflow is intentionally not included, so adding this site to a repository will not silently enable automatic publishing. GitHub Pages does not interpret Cloudflare's `_headers` file; it is harmless as a static file. `.nojekyll` is included for static-file compatibility.

## Before publishing

1. Review `CONFIGURATION.md` and set only the Discord and payment destinations that are ready to be public.
2. Confirm that the payment destination is an HTTPS hosted checkout and that the age gate meets the applicable requirements. The built-in age check is self-attestation only.
3. Set the final public title, description, social preview image URL, and canonical URL once the production domain is known. They are not guessed in the current HTML.
4. Test the site under the actual subpath or custom domain where it will be served.
