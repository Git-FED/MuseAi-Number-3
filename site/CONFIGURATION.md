# Site configuration

Edit `config.js` to fill the three public settings:

- `discordServerId`: the numeric Discord server ID used by the centered widget.
- `discordInviteUrl`: an optional HTTPS invite on `discord.gg` or `discord.com`.
- `paymentUrl`: an optional HTTPS destination for an externally hosted payment page.
- `minimumAge`: defaults to 18; the page does not allow a lower threshold.

All values in `config.js` are public browser code. Do not place API keys, card details, private tokens, or payment-provider secrets there. The payment destination must be HTTPS and is only added to the page after the visitor confirms they meet the age threshold. If no valid payment URL is configured, payment controls stay hidden and the page shows only the unconfigured notice.

The age check is a self-attestation, not identity or age verification. Review the age requirement and payment flow for the jurisdictions where the site will be available; add a compliant verification service if self-attestation is not sufficient. No checkout, payment processor, or live destination is included by default.
