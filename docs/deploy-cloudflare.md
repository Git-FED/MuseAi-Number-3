# Deploy the site through GitHub and Cloudflare Pages

The deployable output directory is `site/`. It is self-contained: HTML, CSS, favicon, manifest, and promotional images all live inside that directory. This makes the same output usable for GitHub Pages, Cloudflare Pages, or a static host.

## Cloudflare Pages with Git integration

1. Push this repository to GitHub.
2. In Cloudflare Pages, choose **Create a project** and connect the GitHub repository.
3. Use the production branch that contains the release site.
4. Set the build command to empty, or use `echo no-build`.
5. Set the build output directory to `site`.
6. Deploy and attach the custom domain in Cloudflare DNS.

No Worker is required for this static page. Add a Worker only when you need server-side logic, authentication, APIs, dynamic routing, or request processing.

## Wrangler configuration

The repository includes `wrangler.toml` with `pages_build_output_dir = "./site"` for command-line deployment. Use the Cloudflare project you intend to publish; do not commit API tokens or account secrets.

## GitHub Pages

For GitHub Pages, publish the `site/` directory using the repository's Pages settings or a Pages workflow. Because all site assets are relative to `site/index.html`, the favicon and images work from a project subpath as well as a custom domain.

## Support embeds

The page includes PayPal, Ko-fi, GitHub Sponsors, and Discord widgets. These are third-party scripts and iframes loaded in the visitor's browser. Their availability and privacy behavior are controlled by the respective providers. Keep the PayPal client ID and plan ID exactly as configured by the account owner, and never add secret API credentials to the page.

## Before publishing

Replace the placeholder repository links in `site/index.html`:

```text
https://github.com/your-org/face-muse-memory
```

Then verify that `site/index.html`, `site/favicon.svg`, `site/site.webmanifest`, and every file in `site/assets/promo/` are included in the deployed output.
