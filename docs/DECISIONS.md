# Muse Number 3 — Decisions D1–D14

Changes to a locked decision require Git-FED's explicit direction and a CHANGELOG entry.

- **D1 — Native Kotlin only.** One Android app; no web2apk, Electron, or desktop client. **Why:** three isolated WebViews need native Android control.
- **D2 — AndroidX WebKit Multi-Profile.** One named Profile (`muse-slot-1/2/3`) per slot. **Why:** each profile owns separate cookies, storage, and login state.
- **D3 — Set profile first.** `WebViewCompat.setProfile()` is called exactly once before any other WebView operation. **Why:** late assignment is invalid and can throw.
- **D4 — No silent fallback.** Unsupported Multi-Profile means show the unsupported screen and stop. **Why:** shared cookies would merge the accounts and defeat the product.
- **D5 — Switching is visibility only.** Hidden WebViews remain attached and are never destroyed, reloaded, or paused by switching. **Why:** pages, scroll positions, JavaScript, and chats must survive.
- **D6 — URLs live only in `SlotConfig.kt`.** Defaults are `https://muse.ai` pending Git-FED's exact URLs. **Why:** one safe, auditable source of truth.
- **D7 — No gate/backend in v1.** **Why:** Number 3 is a personal tool, not the paid Number Two product.
- **D8 — Sync is docs-only in v1.** Cloudflare Worker sync is described only in `docs/SYNC.md`. **Why:** no sync code is authorized until separately ordered.
- **D9 — Package and SDK.** `com.fedos.musenumberthree`, minSdk 26, compileSdk/targetSdk 34 (reconfirm at build time). **Why:** stable app identity and stated device floor.
- **D10 — Keystore never committed.** Generate once and keep off-repo; CI decodes from secrets and deletes it. **Why:** protect signing credentials and update continuity.
- **D11 — No automated tests in v1.** Manual QA is in `docs/QA.md`. **Why:** the key isolation test requires real distinct logins on a supported WebView.
- **D12 — Java 17 and declared dependencies.** Gradle wrapper is generated on the owner's machine; dependencies stay in Gradle files. **Why:** reproducible builds without agent-installed packages.
- **D13 — Switching is first-class.** Tap bar, swipe on the bar, and launcher shortcuts all call `SlotNavigator`; no parallel current-slot state. **Why:** switching is the headline interaction and all paths must agree.
- **D14 — Every icon is a real file.** Master art, source, store/favicon icons, Android adaptive/monochrome layers, density PNGs, slot icons, and notification icon are included. **Why:** no build-time missing-art hand-wave; assets stay reproducible and committed.
