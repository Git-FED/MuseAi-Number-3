# Optional brand links

This file is separate from the core Muse memory templates. Keep these links out of the agent's durable memory unless the project owner explicitly wants them included in a branded deployment.

## Public routes supplied for the branded site

- NSFW / gated area: https://www.fedpromptly.com/nsfw
- Membership tier: https://www.fedpromptly.com/tier
- Discord: https://www.fedpromptly.com/discord
- Ko-fi: https://www.fedpromptly.com/kofi
- Bluesky: https://www.fedpromptly.com/bluesky
- Jabburr storefront: https://www.fedpromptly.com/jabburr
- GitHub: https://www.fedpromptly.com/github

## Patreon safety note

Do not commit the raw Patreon checkout URL supplied in chat. It contains OAuth-style `client_id` and `state` query parameters. Use a clean public Patreon campaign or creator URL instead, configured separately at deploy time:

```text
PATREON_PUBLIC_URL=https://www.patreon.com/<public-campaign-or-creator>
```

The raw checkout URL is intentionally not copied into this repository.

## Social cards

Use the static `site/social-cards.html` page as the starting point for carousel slides or social cards. Replace placeholders only in a branded deployment, and keep private or age-restricted routes behind the appropriate gate.
