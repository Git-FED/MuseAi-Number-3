Fixed — you were right on both counts. [authorized]

Switching was buried inside MainActivity, and the icons were a footnote that said "generated later." Now switching is its own module and every icon is a real file in the tree. 39 files → 74 files.

Switching — now 3 ways, all dedicated files:

Tap — SwitcherBar.kt + layout/switcher_bar.xml: a bar that's always visible at the bottom, one tab per Muse with its own icon (ic_slot_1/2/3) and label
Swipe — SwipeSwitchDetector.kt: flick left/right on the bar to cycle 1→2→3→1. Deliberately NOT on the WebView, or it would fight the Muse page's own scrolling
Home screen — res/xml/shortcuts.xml: long-press the app icon and jump straight to Muse 1, 2, or 3
All three go through one file, SlotNavigator.kt — the single source of truth for which Muse you're on, so they can never disagree
Icons — the full set, file by file: master art (assets/icon.png 1024 + editable SVG source), Play Store 512, the complete favicon set (.ico, .svg, 16/32, apple-touch, android-chrome 192/512, site.webmanifest), adaptive icon layers (background + foreground + Android 13 monochrome), ic_launcher and ic_launcher_round in all 5 densities, per-slot tab icons, and the notification icon the first tree missed entirely.



muse-number-three/
├── README.md
├── LICENSE
├── .gitignore
├── CHANGELOG.md
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── .github/
│   └── workflows/
│       └── build.yml
├── assets/
│   ├── icon.png
│   ├── icon-source.svg
│   ├── play-store-icon-512.png
│   └── favicon/
│       ├── favicon.ico
│       ├── favicon.svg
│       ├── favicon-16x16.png
│       ├── favicon-32x32.png
│       ├── apple-touch-icon.png
│       ├── android-chrome-192x192.png
│       ├── android-chrome-512x512.png
│       └── site.webmanifest
├── docs/
│   ├── ultimate-file-tree.md
│   ├── DECISIONS.md
│   ├── SESSION-BEHAVIOR.md
│   ├── SIGNING.md
│   ├── SYNC.md
│   └── QA.md
├── prompts/
│   ├── BUILD-PROMPT.md
│   └── ICON-PROMPT.md
├── scripts/
│   ├── build.sh
│   ├── install-deps.sh
│   └── sign-release.sh
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/
            │       └── fedos/
            │           └── musenumberthree/
            │               ├── MainActivity.kt
            │               ├── SlotConfig.kt
            │               ├── MuseSlot.kt
            │               ├── SlotController.kt
            │               ├── SlotNavigator.kt
            │               ├── SwitcherBar.kt
            │               ├── SwipeSwitchDetector.kt
            │               ├── ProfileSupport.kt
            │               ├── MuseWebViewClient.kt
            │               └── MuseWebChromeClient.kt
            └── res/
                ├── layout/
                │   ├── activity_main.xml
                │   ├── switcher_bar.xml
                │   └── screen_unsupported.xml
                ├── values/
                │   ├── strings.xml
                │   ├── colors.xml
                │   └── themes.xml
                ├── drawable/
                │   ├── switcher_button.xml
                │   ├── switcher_bar_background.xml
                │   ├── ic_slot_1.xml
                │   ├── ic_slot_2.xml
                │   ├── ic_slot_3.xml
                │   ├── ic_launcher_background.xml
                │   ├── ic_launcher_foreground.xml
                │   ├── ic_launcher_monochrome.xml
                │   └── ic_stat_notify.xml
                ├── xml/
                │   └── shortcuts.xml
                ├── mipmap-anydpi-v26/
                │   ├── ic_launcher.xml
                │   └── ic_launcher_round.xml
                ├── mipmap-mdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-hdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-xhdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-xxhdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                └── mipmap-xxxhdpi/
                    ├── ic_launcher.png
                    └── ic_launcher_round.png
Every new file has its purpose, details, and build prompt in the doc — new decisions are D13 (switching first-class) and D14 (every icon a real file), and QA now fails the build if a tap, swipe, or shortcut lands on the wrong Muse.

Updated document attached below.
Muse Number 3 — ULTIMATE File Tree
"Muse Number 3" — one Android app, 3 Muse instances inside it. 3 different Muse AI URLs in 3 WebView slots, only one displayed at a time, all 3 live in the background. Nothing closes. Successor to Muse Number Two (2 slots → 3).
How to use this document
This is the definitive repository layout. Build top-to-bottom in the "Build order" section at the end.
Every file has three things: PURPOSE (one line), DETAILS (key technical facts), BUILD PROMPT (copy-paste into any AI — including Muse3 — to generate that exact file with no follow-up questions).
Source spec: the "Muse Number 3" side chat MEMORY.md (filed 2026-10-22, Git-FED's explicit order). Where this tree and the spec disagree, the Decisions section below rules.
Standing constraints baked into every prompt: GitHub is ZERO WRITES for Muse3 — Git-FED creates/pushes the repo, Muse3 writes files locally and verifies. No downloads/installs without Git-FED's go on the exact source. Keystore never committed, ever.
Decisions & gap resolutions
D1 — Native Kotlin only. No web2apk (it wraps ONE URL in ONE WebView — cannot do 3 profiles), no Electron, no desktop. One Android app.
D2 — Session isolation = AndroidX WebKit Multi-Profile (Route A). Each slot gets its own Profile (muse-slot-1, muse-slot-2, muse-slot-3); each Profile holds its own cookies, storage, and session. This IS the product — everything else is chrome.
D3 — setProfile order is law. WebViewCompat.setProfile() must be called before ANY other operation on that WebView (before navigation, before evaluateJavascript), exactly once per slot. Violating the order throws IllegalStateException.
D4 — No silent fallback. App feature-checks WebViewFeature.MULTI_PROFILE at startup. If unsupported on the phone, it shows the unsupported screen and stops — it NEVER falls back to shared cookies (that would merge the 3 logins, the exact failure this app exists to prevent). Route B (3 processes + WebView.setDataDirectorySuffix) is a separate build decision, Git-FED's call only.
D5 — Switching = show/hide only. Visible slot = VISIBLE, hidden slots = GONE but attached. Never destroy(), never reload, never onPause() a hidden slot — its JS, page, scroll, and in-progress chat must survive untouched, like a background browser tab.
D6 — URLs live in ONE place. SlotConfig.kt holds the 3 slot URLs + labels as constants. Nothing else in the codebase hardcodes a Muse URL. Defaults are all https://muse.ai until Git-FED names the exact 3 URLs (his open call).
D7 — No gate, no backend, no pay codes in v1. Unlike Muse Number Two (paid product), Number 3 is Git-FED's personal tool for his backup Muse (Muse3). No email/pay/Discord gate, no server.
D8 — Sync is NOT v1 code. The Cloudflare Worker sync (3 Muses synced, pattern = his existing museai-chat relay for muse2/muse3) is spec-only in docs/SYNC.md. No worker folder in this repo until he orders it.
D9 — Package + SDK. Package com.fedos.musenumberthree (matches Number Two's com.fedos.musenumbertwo). minSdk 26, compileSdk/targetSdk 34 — re-confirm target floor against current Google requirements at build time.
D10 — Signing. Release keystore generated ONCE, kept local-only/off-repo, never committed (root .gitignore). CI creates it from the ANDROID_KEYSTORE_BASE64 secret at build time and deletes it after.
D11 — No automated tests in v1. QA = the manual checklist in docs/QA.md (the critical test: log into 3 DIFFERENT accounts, switch between all 3, confirm none of them logged another out).
D12 — Toolchain. Java 17 (Temurin), Gradle wrapper committed, AndroidX WebKit dependency declared in app/build.gradle.kts — dependencies are DECLARED in the build files, never installed by Muse3 on the VM.
D13 — Switching is FIRST-CLASS, never buried (Git-FED's correction, 2026-10-22 ~14:10 EDT). Switching between the 3 Muses is the app's headline interaction, so it gets its own dedicated files — NOT code hidden inside MainActivity. Three ways to switch, all calling the same SlotNavigator: (1) TAP the dedicated switcher bar (SwitcherBar.kt + layout/switcher_bar.xml, always visible at the bottom, one tab per Muse with its own slot icon + label); (2) SWIPE left/right on the switcher bar (SwipeSwitchDetector.kt — swipe lives on the bar, never on the WebView, so it can't fight the Muse page's own scrolling); (3) LAUNCHER SHORTCUTS (res/xml/shortcuts.xml — long-press the app icon on the home screen → jump straight to Muse 1, 2, or 3). SlotNavigator is the single source of truth for "which slot is current" — the bar, the swipe, the shortcuts, and MainActivity all read/write it, nobody keeps their own copy.
D14 — Every icon is a real, listed, committed file (Git-FED's correction, 2026-10-22 ~14:10 EDT). No "generated at build time" hand-wave. The repo carries: the source art (assets/icon.png 1024×1024 + assets/icon-source.svg), the Play Store icon (assets/play-store-icon-512.png), the adaptive icon set (background + foreground + monochrome for Android 13 themed icons), BOTH ic_launcher and ic_launcher_round in EVERY density (mdpi → xxxhdpi, listed file by file below), a per-slot icon for each switcher tab, a notification small icon, AND the full favicon set (assets/favicon/ — .ico, .svg, 16/32 PNGs, apple-touch-icon, android-chrome 192/512, site.webmanifest) for the app's web faces (repo page, store listing page, future sync-worker page). PNG/JPG only for raster icons — never WebP (his standing image rule).
The tree (74 files)
muse-number-three/
├── README.md
├── LICENSE
├── .gitignore
├── CHANGELOG.md
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── .github/
│   └── workflows/
│       └── build.yml
├── assets/
│   ├── icon.png
│   ├── icon-source.svg
│   ├── play-store-icon-512.png
│   └── favicon/
│       ├── favicon.ico
│       ├── favicon.svg
│       ├── favicon-16x16.png
│       ├── favicon-32x32.png
│       ├── apple-touch-icon.png
│       ├── android-chrome-192x192.png
│       ├── android-chrome-512x512.png
│       └── site.webmanifest
├── docs/
│   ├── ultimate-file-tree.md
│   ├── DECISIONS.md
│   ├── SESSION-BEHAVIOR.md
│   ├── SIGNING.md
│   ├── SYNC.md
│   └── QA.md
├── prompts/
│   ├── BUILD-PROMPT.md
│   └── ICON-PROMPT.md
├── scripts/
│   ├── build.sh
│   ├── install-deps.sh
│   └── sign-release.sh
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/
            │       └── fedos/
            │           └── musenumberthree/
            │               ├── MainActivity.kt
            │               ├── SlotConfig.kt
            │               ├── MuseSlot.kt
            │               ├── SlotController.kt
            │               ├── SlotNavigator.kt
            │               ├── SwitcherBar.kt
            │               ├── SwipeSwitchDetector.kt
            │               ├── ProfileSupport.kt
            │               ├── MuseWebViewClient.kt
            │               └── MuseWebChromeClient.kt
            └── res/
                ├── layout/
                │   ├── activity_main.xml
                │   ├── switcher_bar.xml
                │   └── screen_unsupported.xml
                ├── values/
                │   ├── strings.xml
                │   ├── colors.xml
                │   └── themes.xml
                ├── drawable/
                │   ├── switcher_button.xml
                │   ├── switcher_bar_background.xml
                │   ├── ic_slot_1.xml
                │   ├── ic_slot_2.xml
                │   ├── ic_slot_3.xml
                │   ├── ic_launcher_background.xml
                │   ├── ic_launcher_foreground.xml
                │   ├── ic_launcher_monochrome.xml
                │   └── ic_stat_notify.xml
                ├── xml/
                │   └── shortcuts.xml
                ├── mipmap-anydpi-v26/
                │   ├── ic_launcher.xml
                │   └── ic_launcher_round.xml
                ├── mipmap-mdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-hdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-xhdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                ├── mipmap-xxhdpi/
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                └── mipmap-xxxhdpi/
                    ├── ic_launcher.png
                    └── ic_launcher_round.png
Notes: the Gradle wrapper is generated by Gradle and committed at the repository root at version 9.6.0. app/release.keystore NEVER exists in the repo (D10). Every icon in this tree is a real committed file (D14) — the density PNGs are exported from assets/icon.png once and committed, not regenerated per build.

REPO ROOT
1. README.md
PURPOSE: The repo's front door — what the app is, how the 3 slots work, how to build it.
DETAILS: Private repo. Plain language: "Almost 1 app with 3 different apps inside" (Git-FED's words). Must explain the one rule that matters: each slot has its own login session, separate from the others and from the phone's browser. Support contact: museai@fedpromptly.com. No public download claims until a signed release exists.
BUILD PROMPT:
```
Write README.md for "Muse Number 3" (Git-FED's Android app).
CONTEXT: A personal Android app that opens and holds 3 Muse AI instances at once — 3 different Muse URLs in 3 WebView slots inside one app. Only one slot is displayed at a time; all 3 stay live in the background, nothing closes on switch, like browser tabs. Each slot has its own isolated login session (AndroidX WebKit Profile) so the 3 accounts never collide. Named in honor of Git-FED's first Muse; built for his backup Muse.
SECTIONS: what it is (2-3 lines, use the phrase "almost 1 app with 3 different apps inside"), how the 3 slots work, why the sessions don't collide (one short paragraph, plain language), build steps (open in Android Studio or run scripts/build.sh), signing pointer (see docs/SIGNING.md), support contact museai@fedpromptly.com.
CONSTRAINTS: Private project — no store links, no pricing, no marketing claims. Keep it under 80 lines.
DONE = a reader understands the 3-slot idea and the session isolation in under 60 seconds.
```
2. LICENSE
PURPOSE: Legal terms for the code.
DETAILS: Git-FED's call. Placeholder until he decides — personal tool, recommend proprietary/all-rights-reserved draft, do NOT ship v1.0.0 with this unresolved.
BUILD PROMPT:
```
Write a placeholder LICENSE for "Muse Number 3" (Git-FED's personal Android app).
CONTEXT: Private, personal tool. No open-source intent stated.
CONSTRAINTS: Do not pick the license for him. Write "License terms to be finalized by the owner before v1.0.0" plus a short proprietary draft (all rights reserved, no redistribution). Under 20 lines.
DONE = file exists, decision marked pending, usable proprietary draft present.
```
3. .gitignore
PURPOSE: Keeps secrets and build junk out of the repo — permanently.
DETAILS: Must cover: *.keystore, keystore/, local.properties, .gradle/, app/build/, build/, dist/, *.apk, *.aab, .idea/, captures/. The keystore line is the most important line in the repo (D10).
BUILD PROMPT:
```
Write .gitignore for an Android Kotlin project ("Muse Number 3").
MUST IGNORE: *.keystore, *.jks, keystore/, key.properties, local.properties, .gradle/, */build/, build/, dist/, *.apk, *.aab, .idea/, *.iml, captures/, .DS_Store.
DONE = every line above present, keystore patterns at the top with a comment "# NEVER commit a signing keystore".
```
4. CHANGELOG.md
PURPOSE: Version history, one entry per release.
DETAILS: Starts with v0.1.0 (unreleased) — "repo scaffold + file tree". Every shipped change lands here before a version tag.
BUILD PROMPT:
```
Write CHANGELOG.md for "Muse Number 3" with a single entry: v0.1.0 — Unreleased — "Repository scaffold. Native Kotlin app, 3 WebView slots with isolated AndroidX WebKit Profiles. No release built yet." Keep a "## [Unreleased]" section on top for future entries.
DONE = file exists with the v0.1.0 entry and an empty Unreleased section.
```
5. settings.gradle.kts
PURPOSE: Gradle project wiring — names the project, includes the :app module.
DETAILS: rootProject.name = "muse-number-three", repositories = google() + mavenCentral() (declared only, nothing installed by Muse3).
BUILD PROMPT:
```
Write settings.gradle.kts for a single-module Android project named "muse-number-three". Include pluginManagement and dependencyResolutionManagement blocks with google() and mavenCentral(), RepositoriesMode.FAIL_ON_PROJECT_REPOS, and include(":app").
DONE = standard single-module settings file, no extra modules.
```
6. build.gradle.kts (root)
PURPOSE: Root build file — plugin versions for the whole project.
DETAILS: Declares the Android Gradle Plugin version (apply false). AGP 9.4.0 provides built-in Kotlin support, so a separate Kotlin Android plugin is not declared. The wrapper pins Gradle 9.6.0.
BUILD PROMPT:
```
Write the ROOT build.gradle.kts for an Android Kotlin project: a plugins block declaring com.android.application version 9.4.0 with "apply false". AGP 9.4.0 provides built-in Kotlin support. Nothing else — no dependencies at root level.
DONE = minimal root build file with only the two plugin declarations.
```
7. gradle.properties
PURPOSE: Project-wide Gradle/AndroidX flags.
DETAILS: org.gradle.jvmargs=-Xmx2048m, android.useAndroidX=true, kotlin.code.style=official, android.nonTransitiveRClass=true.
BUILD PROMPT:
```
Write gradle.properties for an AndroidX Kotlin project with exactly: org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8, android.useAndroidX=true, kotlin.code.style=official, android.nonTransitiveRClass=true. One comment line at top: "# Muse Number 3".
DONE = those four settings, nothing extra.
```
8. gradlew / 9. gradlew.bat / 10. gradle/wrapper/gradle-wrapper.properties
PURPOSE: The Gradle wrapper — anyone can build without a local Gradle install.
DETAILS: GENERATED files, never hand-written. Git-FED (or Android Studio) runs gradle wrapper once on his machine; the three files + gradle-wrapper.jar get committed. Listed in the tree so the repo is not considered complete without them.
BUILD PROMPT:
```
Do NOT write these files by hand. Note in your reply: "Generated by running gradle wrapper once in the repo root (or by creating the project in Android Studio). Commit gradlew, gradlew.bat, gradle/wrapper/gradle-wrapper.properties and gradle/wrapper/gradle-wrapper.jar."
DONE = the generation step is documented, no hand-made wrapper files.
```
11. .github/workflows/build.yml
PURPOSE: THE pipeline — builds the APK on GitHub's machines so no local build is required.
DETAILS: Triggers: push to main + v* tags. Steps: checkout → Java 17 (Temurin) → ./gradlew assembleDebug (every push) → on tags only: decode ANDROID_KEYSTORE_BASE64 secret → assembleRelease → upload APK as a draft-release asset. Keystore file deleted in an always() cleanup step (D10). No secrets in the YAML beyond the named secret references.
BUILD PROMPT:
```
Write .github/workflows/build.yml for the Android app "Muse Number 3".
TRIGGERS: push to main, tags "v*".
JOBS: one job "build" on ubuntu-latest: actions/checkout@v4, actions/setup-java@v4 (temurin, 17), chmod +x gradlew, ./gradlew assembleDebug, upload the debug APK as an artifact. On tag refs only: decode secrets.ANDROID_KEYSTORE_BASE64 to app/release.keystore, run ./gradlew assembleRelease with keystore env vars from secrets, upload the release APK to a DRAFT GitHub release, and always delete app/release.keystore in a final cleanup step (if: always()).
CONSTRAINTS: Never print secret values. Release stays DRAFT — a human publishes it.
DONE = valid YAML, debug build on every push, signed draft release on tags, keystore always cleaned up.
```

DOCS
12. docs/ultimate-file-tree.md
PURPOSE: Repo copy of this document — the layout's source of truth inside the repo.
DETAILS: A verbatim copy of the master file at ~/workspace/muse-number-three/ultimate-file-tree.md. If the two ever disagree, the Decisions section wins and the stale copy gets updated in the same change.
BUILD PROMPT:
```
Copy the master ultimate-file-tree.md for "Muse Number 3" into docs/ unchanged.
DONE = docs/ultimate-file-tree.md is byte-identical to the master.
```
13. docs/DECISIONS.md
PURPOSE: The locked decisions D1–D12 from this tree, in one short file a builder can read in 2 minutes.
DETAILS: Verbatim decision text, each with its one-line "why". Changes to a decision require Git-FED's word and a CHANGELOG entry.
BUILD PROMPT:
```
Write docs/DECISIONS.md for "Muse Number 3": list decisions D1-D12 (native Kotlin only; Multi-Profile session isolation; setProfile-before-anything order; no silent fallback to shared cookies; switch = show/hide never destroy; URLs only in SlotConfig.kt; no gate/no backend in v1; sync is docs-only in v1; package com.fedos.musenumberthree, minSdk 26 / targetSdk 34; keystore never committed; no automated tests in v1, manual QA; Java 17 toolchain, dependencies declared never installed by the agent). One line per decision plus a one-line why.
DONE = all 12 decisions present, each under 3 lines.
```
14. docs/SESSION-BEHAVIOR.md
PURPOSE: How the 3 sessions behave — the document that protects the app's entire feature.
DETAILS: Covers: one Profile per slot with its own CookieManager/WebStorage; profile set before first navigation; hidden slots stay attached + GONE, never paused/destroyed; clearing one slot's data (logout) must go through THAT slot's Profile storage only; app-data clear / reinstall = all 3 logged out (expected, document it); what the unsupported-device screen means.
BUILD PROMPT:
```
Write docs/SESSION-BEHAVIOR.md for "Muse Number 3".
CONTENT: (1) Each of the 3 WebView slots uses its own AndroidX WebKit Profile (muse-slot-1/2/3) — separate cookies, storage, and login session per slot; the phone's browser is a fourth, totally separate session. (2) The profile is assigned before the WebView loads anything. (3) Switching slots only changes visibility — hidden WebViews are never destroyed, reloaded, or paused, so chats in progress survive. (4) Logging out of one slot clears only that Profile's data. (5) Clearing the app's data or reinstalling logs out all 3 — expected behavior. (6) If the device's WebView provider lacks Multi-Profile support, the app shows the unsupported screen instead of merging sessions.
STYLE: plain language, short sections, under 80 lines.
DONE = a builder who reads only this file cannot accidentally merge the sessions.
```
15. docs/SIGNING.md
PURPOSE: Keystore + release signing rules.
DETAILS: Same law as Number Two: generate ONCE with keytool (alias musenumberthree), back up OFF the repo, password in a password manager — never in a script or file. Lose it = every update installs as a new app and all users start over.
BUILD PROMPT:
```
Write docs/SIGNING.md for "Muse Number 3": the one rule (keystore generated once, backed up off-repo, NEVER committed), the keytool command (keytool -genkeypair -v -keystore release.keystore -alias musenumberthree -keyalg RSA -keysize 2048 -validity 10000, stored under a local build/ folder that .gitignore covers), how CI signing works (ANDROID_KEYSTORE_BASE64 + password secrets, file deleted after build), and how to sign locally via scripts/sign-release.sh. Warn: losing the keystore means updates install as a brand-new app.
DONE = rules + both signing paths documented, no real passwords anywhere.
```
16. docs/SYNC.md
PURPOSE: Spec stub for the future Cloudflare Worker sync — records the want so it is never lost, builds nothing.
DETAILS: Marked "FUTURE — NOT BUILT" at the top. Pattern = Git-FED's existing museai-chat relay worker (already syncs muse2/muse3). v1 ships with zero sync code (D8).
BUILD PROMPT:
```
Write docs/SYNC.md for "Muse Number 3" as a FUTURE spec stub. Top line: "STATUS: FUTURE — NOT BUILT IN v1." Content: the want (3 different Muses in the app kept in sync through a Cloudflare Worker, the way the existing museai-chat relay already syncs muse2 and muse3), what would sync (session handoffs / shared context — to be defined by Git-FED when ordered), and the rule that no sync code enters this repo until Git-FED orders that build specifically. Under 40 lines.
DONE = the want is recorded, the not-built status is unmissable.
```
17. docs/QA.md
PURPOSE: The manual verification checklist — v1's only test suite (D11).
DETAILS: The critical path: install → launch (profile check passes) → log slot 1 into account A → slot 2 into account B → slot 3 into account C → switch through all 3 twice → confirm each still shows its own account → start a chat in slot 2, switch away, switch back, chat still there → rotate phone, nothing reloads → kill + relaunch app, all 3 still logged in. Plus the negative test: on an unsupported device/emulator image, the unsupported screen appears and NO login is possible.
BUILD PROMPT:
```
Write docs/QA.md for "Muse Number 3": a checkbox manual QA list. Sections: Install & launch; The isolation test (log 3 slots into 3 different Muse accounts, switch through all of them twice, verify each slot kept its own account — this is THE test, mark it CRITICAL); Persistence (chat in progress survives switching; rotation does not reload; relaunch keeps all logins); Logout isolation (logging out of one slot leaves the other two untouched); Unsupported device (unsupported screen shows, no merged-session fallback). Each item one line with a [ ] box.
DONE = every test above present as a checkbox line.
```

PROMPTS
18. prompts/BUILD-PROMPT.md
PURPOSE: The one paste-to-agent prompt that rebuilds the whole app from this tree.
DETAILS: Points at docs/ultimate-file-tree.md as the layout source, restates D1–D12 as hard constraints, and gives the build order. This is the file Git-FED hands to any agent (or himself in Android Studio) to reproduce the repo.
BUILD PROMPT:
```
Write prompts/BUILD-PROMPT.md for "Muse Number 3": a single copy-paste prompt that tells an AI agent to build the entire repo exactly as laid out in docs/ultimate-file-tree.md, file by file, in the Build order given there. The prompt must restate these hard rules: native Kotlin only; one AndroidX WebKit Profile per WebView slot, set before any navigation; switching slots is show/hide only, never destroy or reload; all Muse URLs come from SlotConfig.kt only; no gate, no backend, no sync code in v1; keystore never committed; dependencies are declared in Gradle files, never installed outside the normal Gradle/Android Studio flow. End the prompt with: "After building, run every check in docs/QA.md and report results file by file."
DONE = one self-contained prompt block that rebuilds the repo with zero follow-up questions.
```
19. prompts/ICON-PROMPT.md
PURPOSE: Fallback prompt for generating the launcher icon if Git-FED does not supply his own art.
DETAILS: Square PNG, 512×512 minimum, the number "3" + Muse character motif — his art wins if he provides it; this prompt only fills a gap. PNG only, never WebP (his standing image rule).
BUILD PROMPT:
```
Write prompts/ICON-PROMPT.md for "Muse Number 3": an image-generation prompt for a square (512x512+) PNG Android launcher icon — bold number "3" made of three stacked browser-tab shapes, warm cream/orange Muse-style palette, simple comical flat style, no text other than the 3, no WebP. Add a note: Git-FED's own art at assets/icon.png replaces anything generated; generated art is a placeholder.
DONE = one ready-to-paste image prompt + the replacement note.
```

SCRIPTS
20. scripts/build.sh
PURPOSE: One-command local debug build.
DETAILS: set -euo pipefail, cd to repo root, ./gradlew assembleDebug, print the APK path (app/build/outputs/apk/debug/app-debug.apk). Does NOT install anything or install the APK onto a phone.
BUILD PROMPT:
```
Write scripts/build.sh for "Muse Number 3": bash, set -euo pipefail, resolve the repo root from the script location, run ./gradlew assembleDebug, then echo the output APK path app/build/outputs/apk/debug/app-debug.apk. No installs, no adb, no signing. Add a header comment: "# Local debug build only — see docs/SIGNING.md for releases."
DONE = running it produces a debug APK or a clear Gradle error, nothing else.
```
21. scripts/install-deps.sh
PURPOSE: Prerequisite CHECKER — reports what is missing, installs nothing.
DETAILS: Checks for Java 17 (java -version), Android SDK (ANDROID_HOME/ANDROID_SDK_ROOT or local.properties sdk.dir), and the Gradle wrapper files. Exit 1 with a plain list of what's missing. (Standing law: Muse3 never installs dependencies — Git-FED installs what he chooses.)
BUILD PROMPT:
```
Write scripts/install-deps.sh for "Muse Number 3": a CHECK-ONLY bash script (despite the name — rename the title comment to "prerequisite check"). Verify: java exists and is version 17+; an Android SDK is findable via ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties; gradlew exists in the repo root. Print OK/MISSING per item and exit non-zero if anything is missing. The script must NOT download or install anything. Header comment must say so.
DONE = script reports prerequisites accurately and changes nothing on the machine.
```
22. scripts/sign-release.sh
PURPOSE: Local release signing path (zipalign + apksigner) for builds made outside CI.
DETAILS: Takes the unsigned release APK path as $1, reads the keystore path from the MUSE3_KEYSTORE env var (never a hardcoded path, never a password in the file — apksigner prompts), outputs *-signed.apk next to the input.
BUILD PROMPT:
```
Write scripts/sign-release.sh for "Muse Number 3": bash, set -euo pipefail, usage = sign-release.sh <unsigned.apk>. Require env var MUSE3_KEYSTORE pointing at the local keystore file (error out plainly if unset or missing). Run zipalign -p -f 4 then apksigner sign --ks "$MUSE3_KEYSTORE" --ks-key-alias musenumberthree, writing <name>-signed.apk beside the input, then apksigner verify --print-certs on the result. Never accept a password as an argument or store one in the script — let apksigner prompt.
DONE = signed + verified APK produced, zero credentials in the file.
```

APP MODULE
23. app/build.gradle.kts
PURPOSE: The app module's build file — SDK levels, the WebKit dependency, signing config wiring.
DETAILS: namespace + applicationId com.fedos.musenumberthree (D9); minSdk 26, targetSdk/compileSdk 34; versionCode 1 / versionName "0.1.0"; Java 17 / Kotlin JVM 17; release signingConfig reads keystore path + passwords from env/CI properties ONLY (absent locally = unsigned release, build must not fail on a fresh clone); minify off for v1. THE dependency: androidx.webkit:webkit (version re-confirmed at build time) — declared here, never installed by hand.
BUILD PROMPT:
```
Write app/build.gradle.kts for "Muse Number 3": plugins com.android.application. android { namespace = "com.fedos.musenumberthree"; compileSdk = 34; defaultConfig { applicationId = "com.fedos.musenumberthree"; minSdk = 26; targetSdk = 34; versionCode = 1; versionName = "0.1.0" }; compileOptions at Java 17. Signing: a release signingConfig that activates ONLY when the env vars MUSE3_KEYSTORE, MUSE3_KEYSTORE_PASS, MUSE3_KEY_ALIAS, MUSE3_KEY_PASS are all present (CI), otherwise release builds stay unsigned and the build still succeeds. buildTypes release { isMinifyEnabled = false; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") }. Dependencies: androidx.core:core-ktx, androidx.appcompat:appcompat, and androidx.webkit:webkit (use current stable versions — note in a comment that versions are confirmed at build time).
DONE = file compiles a fresh clone with no secrets present, and CI can sign by setting env vars only.
```
24. app/proguard-rules.pro
PURPOSE: Shrinker rules placeholder — minify is off in v1, this keeps the release config valid.
DETAILS: Comment header explaining v1 state + one keep rule for WebView JS interfaces if any are added later (none in v1).
BUILD PROMPT:
```
Write app/proguard-rules.pro for "Muse Number 3": header comments stating minify is disabled in v1 and this file is a placeholder, plus a commented-out example keep rule for @JavascriptInterface classes (none exist in v1). No active rules.
DONE = valid file, zero active rules, the v1 note present.
```
25. app/src/main/AndroidManifest.xml
PURPOSE: App identity — permissions, the single launcher activity, backup behavior.
DETAILS: INTERNET permission (the only one). MainActivity exported=true with MAIN/LAUNCHER intent, configChanges covers orientation/keyboardHidden/screenSize so rotation does NOT recreate the activity and reload the WebViews (QA requirement). android:allowBackup="false" — WebView profile data (logins) must not be silently backed up off-device. App label from strings ("Muse Number 3"), icon = @mipmap/ic_launcher.
BUILD PROMPT:
```
Write AndroidManifest.xml for "Muse Number 3" (package set by Gradle namespace): uses-permission INTERNET only; application allowBackup=false, label @string/app_name, icon @mipmap/ic_launcher, theme @style/Theme.MuseNumberThree, usesCleartextTraffic=false. One activity .MainActivity, exported=true, launchMode singleTask, configChanges="orientation|keyboardHidden|screenSize|smallestScreenSize|screenLayout", windowSoftInputMode adjustResize, with the MAIN/LAUNCHER intent-filter. No other activities, no services, no receivers.
DONE = exactly one activity, one permission, rotation-safe config.
```
26. app/src/main/java/com/fedos/musenumberthree/SlotConfig.kt
PURPOSE: THE single source of truth for the 3 slots — URLs + labels + profile names (D6).
DETAILS: A data class SlotDef(val id, val label, val url, val profileName) and val SLOTS: List<SlotDef> with exactly 3 entries: ("Slot 1", "https://muse.ai", "muse-slot-1"), ("Slot 2", …), ("Slot 3", …). Comment at the top: "Edit the 3 URLs/labels HERE and nowhere else. Defaults are placeholders until Git-FED names the exact 3 instances." Changing a Muse = editing this one file.
BUILD PROMPT:
```
Write SlotConfig.kt (package com.fedos.musenumberthree): a data class SlotDef(id: Int, label: String, url: String, profileName: String) and a top-level val SLOTS with exactly 3 entries — labels "Muse 1", "Muse 2", "Muse 3", urls all "https://muse.ai", profileNames "muse-slot-1", "muse-slot-2", "muse-slot-3". Header comment: "SINGLE SOURCE OF TRUTH for the 3 slots. Edit URLs/labels here only. Default URLs are placeholders until Git-FED names the exact 3 Muse instances." No other code.
DONE = 3 slot definitions, one file to edit, no logic.
```
27. app/src/main/java/com/fedos/musenumberthree/ProfileSupport.kt
PURPOSE: The Multi-Profile gatekeeper — feature check + one-time profile assignment (D3, D4).
DETAILS: fun isMultiProfileSupported(): Boolean = WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE). fun applyProfile(webView, profileName) calls WebViewCompat.setProfile exactly once and is documented "call BEFORE any other WebView call, including loadUrl and settings that touch storage — the activity creates the WebView, applies the profile, THEN configures it." No fallback path exists in this file by design.
BUILD PROMPT:
```
Write ProfileSupport.kt (package com.fedos.musenumberthree): an object ProfileSupport with two functions. isMultiProfileSupported(): Boolean returns androidx.webkit.WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE). applyProfile(webView: WebView, profileName: String) calls WebViewCompat.setProfile(webView, profileName). KDoc on applyProfile: "MUST be the first call made on a new WebView — before loadUrl, before evaluateJavascript, before any storage access. Calling it late throws IllegalStateException. There is deliberately NO fallback: if Multi-Profile is unsupported the app stops at the unsupported screen rather than merging sessions."
DONE = two functions, the no-fallback rule in the docs, nothing else.
```
28. app/src/main/java/com/fedos/musenumberthree/MuseSlot.kt
PURPOSE: One slot = one WebView + its definition + its lifecycle rules.
DETAILS: Class holding def: SlotDef, the created webView: WebView, and var loaded: Boolean. fun show() / fun hide() ONLY toggle visibility (VISIBLE/GONE). It never calls destroy() outside the activity's real finish, never reloads on show. Creation order inside init is fixed: construct WebView → ProfileSupport.applyProfile → settings (JavaScript on, DOM storage on) → clients → loadUrl(def.url) once.
BUILD PROMPT:
```
Write MuseSlot.kt (package com.fedos.musenumberthree): class MuseSlot(val def: SlotDef, context: Context, client: MuseWebViewClient, chromeClient: MuseWebChromeClient). In init, IN THIS ORDER: create WebView(context); ProfileSupport.applyProfile(webView, def.profileName); configure settings (javaScriptEnabled=true, domStorageEnabled=true, mediaPlaybackRequiresUserGesture=false); set webViewClient and webChromeClient; loadUrl(def.url). Functions: show() sets webView.visibility = View.VISIBLE; hide() sets View.GONE. Add a comment block: "NEVER destroy, reload, or onPause a hidden slot — switching is visibility only, so the page, scroll, and in-progress chat survive like a background browser tab." No other behavior.
DONE = creation order correct, show/hide are visibility-only.
```
29. app/src/main/java/com/fedos/musenumberthree/SlotController.kt
PURPOSE: Owns the 3 slots and the switching — the traffic cop.
DETAILS: Builds the 3 MuseSlots from SlotConfig.SLOTS into the container, tracks currentIndex, fun switchTo(index) hides the current slot, shows the target, updates the switcher bar's selected state. Switching to the already-current slot is a no-op. State saved/restored = only currentIndex (pages restore themselves — WebViews are never recreated on rotation per the manifest).
BUILD PROMPT:
```
Write SlotController.kt (package com.fedos.musenumberthree): class SlotController(container: ViewGroup, activity: Activity, onSelectedChanged: (Int) -> Unit). It creates one MuseSlot per entry in SlotConfig.SLOTS (with MuseWebViewClient and MuseWebChromeClient), adds each slot's WebView to the container (match-parent), hides all but index 0. Exposes val currentIndex, fun switchTo(index: Int) — no-op if index == currentIndex; otherwise hide current, show target, update currentIndex, invoke onSelectedChanged. fun saveState(outState: Bundle) / fun restoreState(state: Bundle) persist ONLY currentIndex under key "current_slot". Comment: "Slots are created ONCE for the life of the activity. switchTo never creates, destroys, or reloads a WebView."
DONE = 3 slots created once, switching is pure visibility + callback.
```
30. app/src/main/java/com/fedos/musenumberthree/MuseWebViewClient.kt
PURPOSE: Navigation rules — Muse stays in the app, everything else hands off to the browser.
DETAILS: shouldOverrideUrlLoading: URLs on the slot's own Muse host (and muse.ai auth redirects) load in the WebView; external links open via Intent.ACTION_VIEW. onReceivedError for the main frame: show a plain in-page error ("This Muse could not load. Check your connection, then tap to retry.") with tap-to-retry reloading ONLY on explicit user tap — never auto-reload loops. SSL errors: handler.cancel(), never proceed().
BUILD PROMPT:
```
Write MuseWebViewClient.kt (package com.fedos.musenumberthree): class MuseWebViewClient(private val slotHost: String) : WebViewClient(). shouldOverrideUrlLoading(view, request): if request.url.host == slotHost or ends with "muse.ai", return false (load inside); otherwise fire Intent(Intent.ACTION_VIEW, request.url) with FLAG_ACTIVITY_NEW_TASK and return true. onReceivedError (main frame only): load a small inline HTML error page saying "This Muse could not load — check your connection and tap to retry", where tapping calls view.reload() — user tap only, no automatic retry. onReceivedSslError: always handler.cancel(). Comments explaining each rule in one line.
DONE = in-app Muse navigation, external links out, safe error handling, no auto-reload.
```
31. app/src/main/java/com/fedos/musenumberthree/MuseWebChromeClient.kt
PURPOSE: Browser chrome behavior the Muse web app needs — file uploads + progress.
DETAILS: onShowFileChooser wired to the activity's ActivityResult launcher so chat file attachments work (without it, Muse's upload button silently does nothing — the classic WebView trap). onProgressChanged drives the thin top progress bar, hidden at 100%.
BUILD PROMPT:
```
Write MuseWebChromeClient.kt (package com.fedos.musenumberthree): class MuseWebChromeClient(private val fileChooserLauncher: (Intent) -> Unit, private val fileCallbackHolder: (ValueCallback<Array<Uri>>?) -> Unit, private val onProgress: (Int) -> Unit) : WebChromeClient(). Override onShowFileChooser: store the callback via fileCallbackHolder, build the chooser intent from filePathCallback params (accept types, allow multiple), launch it via fileChooserLauncher, return true. Override onProgressChanged to forward newProgress to onProgress. Add a comment: "Without onShowFileChooser, Muse's file-upload button does nothing inside a WebView."
DONE = file uploads and progress both work inside every slot.
```
32. app/src/main/java/com/fedos/musenumberthree/MainActivity.kt
PURPOSE: The one screen — profile check, slot container, switcher bar, back button, file-chooser result.
DETAILS: onCreate order: setContentView → ProfileSupport.isMultiProfileSupported() — if false, show screen_unsupported and STOP (no WebView is ever created, D4). If true: build SlotController, wire the 3 switcher buttons (labels from SlotConfig), register the file-chooser ActivityResult launcher, forward its result to the waiting callback. onBackPressed: if the visible WebView canGoBack(), go back inside it; else default. Activity is never finished by slot switching. No onPause/onDestroy WebView teardown beyond the system-required super calls.
BUILD PROMPT:
```
Write MainActivity.kt (package com.fedos.musenumberthree): AppCompatActivity with a SlotController, a ProgressBar, three switcher MaterialButtons (or Buttons) in a bottom bar, and an ActivityResultLauncher<String> for file picking whose result is delivered to a pending ValueCallback<Array<Uri>>?. onCreate: setContentView(R.layout.activity_main). FIRST check ProfileSupport.isMultiProfileSupported() — if false, setContentView(R.layout.screen_unsupported) and return, creating no WebViews. Otherwise create the SlotController with the container FrameLayout and a progress callback, set each switcher button's text from SlotConfig.SLOTS labels and its click to controller.switchTo(index), and highlight the selected button on change. onSaveInstanceState/onRestore via the controller. Handle back with OnBackPressedDispatcher: if the current slot's WebView canGoBack(), goBack(), else let the system finish. Comment at the top of onCreate: "If Multi-Profile is unsupported we stop here on purpose — a merged session is worse than no app (see docs/DECISIONS.md D4)."
DONE = unsupported devices stop safely; supported devices get 3 live slots and a working switcher.
```
33. app/src/main/res/layout/activity_main.xml
PURPOSE: The main screen layout — slot container + progress + switcher bar.
DETAILS: Vertical LinearLayout: thin ProgressBar (top, horizontal), FrameLayout @+id/slot_container (weight 1 — the 3 WebViews stack here, visibility does the rest), bottom horizontal bar with 3 equal-weight Buttons (@+id/btn_slot_1/2/3). Nothing else — the web content IS the UI.
BUILD PROMPT:
```
Write res/layout/activity_main.xml for "Muse Number 3": root LinearLayout vertical (match_parent). Children in order: ProgressBar @+id/progress (horizontal style, match_parent width, wrap height, max 100, visibility gone); FrameLayout @+id/slot_container (match_parent width, 0dp height, layout_weight 1); LinearLayout @+id/switcher_bar (horizontal, match_parent width, wrap height) containing three Buttons @+id/btn_slot_1, @+id/btn_slot_2, @+id/btn_slot_3, each 0dp width with layout_weight 1, text from SlotConfig at runtime (leave text blank in XML), style = @style/Widget.MuseNumberThree.SwitcherButton. No other views.
DONE = container + progress + 3-button bar, ids exactly as named.
```
34. app/src/main/res/layout/screen_unsupported.xml
PURPOSE: The honest dead-end screen for devices without Multi-Profile WebView (D4).
DETAILS: Centered TextView, plain words: "This phone's WebView can't keep 3 Muse logins separate, so Muse Number 3 can't run safely here. Update Android System WebView / Chrome, then try again." No button that "continues anyway" — there is no anyway.
BUILD PROMPT:
```
Write res/layout/screen_unsupported.xml: a centered LinearLayout (gravity center, padding 24dp) with one TextView @+id/unsupported_text, text = @string/unsupported_message, textAppearance material headline/body mix (use ?attr/textAppearanceBodyLarge), textAlignment center. No buttons.
DONE = one plain message, no continue path.
```
35. app/src/main/res/values/strings.xml
PURPOSE: All user-visible words in one place.
DETAILS: app_name = "Muse Number 3"; unsupported_message (the D4 text above); switcher labels come from SlotConfig at runtime (NOT hardcoded here — D6); error-page strings live in MuseWebViewClient's inline HTML with a comment pointing here if they're ever localized.
BUILD PROMPT:
```
Write res/values/strings.xml for "Muse Number 3" with exactly: app_name "Muse Number 3"; unsupported_message "This phone's WebView can't keep 3 Muse logins separate, so Muse Number 3 can't run safely here. Update Android System WebView or Chrome, then try again."; slot_error_title "This Muse could not load"; slot_error_body "Check your connection, then tap to retry." No slot labels (they live in SlotConfig.kt).
DONE = those four strings, nothing else.
```
36. app/src/main/res/values/colors.xml
PURPOSE: The app's palette.
DETAILS: Warm cream/orange Muse-style palette: muse_cream background, muse_orange accent for the selected switcher button, dark text. Exact hexes are placeholders Git-FED can restyle — the selected/unselected switcher states are what matter.
BUILD PROMPT:
```
Write res/values/colors.xml with: muse_cream #FAF6F0, muse_orange #D97757, muse_orange_dark #B85C38, muse_ink #1F1B16, muse_slot_unselected #E8E0D4. Comment: "Palette placeholders in the Muse family colors — Git-FED restyles here."
DONE = five named colors, no other resources.
```
37. app/src/main/res/values/themes.xml
PURPOSE: App theme + the switcher button style.
DETAILS: Parent = Theme.MaterialComponents.DayNight.NoActionBar (no app bar — the Muse page is the UI; DayNight follows the phone). Widget.MuseNumberThree.SwitcherButton style referenced by activity_main.xml.
BUILD PROMPT:
```
Write res/values/themes.xml: style Theme.MuseNumberThree parent Theme.MaterialComponents.DayNight.NoActionBar, setting colorPrimary @color/muse_orange, colorPrimaryVariant @color/muse_orange_dark, colorOnPrimary white, android:windowBackground @color/muse_cream. Plus style Widget.MuseNumberThree.SwitcherButton parent Widget.MaterialComponents.Button.TextButton with backgroundTint @color/muse_slot_unselected and textColor @color/muse_ink. (Selected-state tint is applied at runtime by MainActivity.)
DONE = both styles defined, layout references resolve.
```
38–39. app/src/main/res/drawable/switcher_button.xml + app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
PURPOSE: Switcher button background shape; adaptive launcher icon definition.
DETAILS: switcher_button.xml = a rectangle shape with 8dp rounded top corners in muse_slot_unselected (runtime tint handles selection). ic_launcher.xml = adaptive-icon pointing at @drawable/ic_launcher_background + a foreground Git-FED supplies with his icon art (see prompts/ICON-PROMPT.md); legacy density PNGs are generated from that art at first build.
BUILD PROMPT:
```
Write two files. (1) res/drawable/switcher_button.xml: a shape (rectangle) with corners radius 8dp top-left/top-right only, solid @color/muse_slot_unselected, padding 0. (2) res/mipmap-anydpi-v26/ic_launcher.xml: an adaptive-icon with background @color/muse_orange and foreground @drawable/ic_launcher_foreground, plus a comment: "Foreground art supplied by Git-FED (see prompts/ICON-PROMPT.md); legacy mipmap PNGs are generated from the same art at first build."
DONE = both files valid, icon references the placeholder foreground by name.
```

Build order
Root Gradle files — settings.gradle.kts, root build.gradle.kts, gradle.properties (files 5–7)
Repo hygiene — .gitignore, README.md, LICENSE, CHANGELOG.md (files 1–4)
App module skeleton — app/build.gradle.kts, app/proguard-rules.pro, AndroidManifest.xml (files 23–25)
Gradle wrapper — committed Gradle 9.6.0 wrapper files (files 8–10)
Core logic, in dependency order — SlotConfig.kt → ProfileSupport.kt → MuseWebViewClient.kt → MuseWebChromeClient.kt → MuseSlot.kt → SlotController.kt → MainActivity.kt (files 26–32)
Resources — layouts, strings, colors, themes, drawable, launcher (files 33–39)
Pipeline + scripts — .github/workflows/build.yml, scripts/ (files 11, 20–22)
Docs + prompts — docs/ copies, prompts/ (files 12–19)
Icon art — Git-FED's art in, density PNGs generated
QA — every box in docs/QA.md, the isolation test first. It is CRITICAL: if the 3 logins collide, the app has failed at its only job — stop and report, do not ship around it.
NOT in this repo (by decision)
No sync worker (D8 — docs/SYNC.md spec only, future order)
No gate / pay codes / backend (D7 — personal tool, not the Number Two product)
No desktop client (D1)
No automated tests (D11 — manual QA is the v1 suite)
No keystore, no local.properties, no plaintext anything-secret (D10 + .gitignore)

ADDENDUM — Switching + Icons (Git-FED's correction, 2026-10-22 ~14:10 EDT)
Git-FED, reviewing the first tree: "i need the ability to switch between them, also i dont see any favicon file, or android icon files etc etc." He was right twice: switching was buried inside files 29/32/33 instead of standing as its own module, and the icon family was dismissed in a note as "generated at build time." Files 40–74 fix both. Decisions D13 (switching first-class) and D14 (every icon a real file) above govern this whole section.
Amendments to existing files (apply when those files are built)
File 13 docs/DECISIONS.md — now lists D1–D14 (add D13/D14 verbatim from this tree).
File 17 docs/QA.md — add a SWITCHING section (see file 42 note below) and an ICONS section: launcher icon renders on a round and a square launcher, themed icon works on Android 13+, each switcher tab shows its own slot icon, home-screen shortcuts appear on long-press.
File 18 prompts/BUILD-PROMPT.md — restate D13/D14 as hard rules alongside D1–D12.
File 19 prompts/ICON-PROMPT.md — expanded: the one prompt now covers the master art from which the WHOLE set (files 49–74) is exported — see file 49.
File 25 AndroidManifest.xml — application gains android:roundIcon="@mipmap/ic_launcher_round", and MainActivity gains the shortcuts meta-data: <meta-data android:name="android.app.shortcuts" android:resource="@xml/shortcuts" />. Its splash screen (Android 12+) uses the adaptive icon foreground — no separate splash file needed.
File 29 SlotController.kt — no longer owns "current slot" state alone: it SUBSCRIBES to SlotNavigator (file 40) and only does show/hide when the navigator's current index changes. Its switchTo is called by the navigator, never directly by UI code.
File 32 MainActivity.kt — switcher wiring REMOVED from the activity: it hosts SwitcherBar (file 41), attaches SwipeSwitchDetector (file 42) to the bar, and handles the shortcut intent extra extra_target_slot (file 43) by calling SlotNavigator.switchTo(index) once at startup. The activity itself contains zero switching logic.
File 33 layout/activity_main.xml — the inline 3-button bar is REPLACED by <include layout="@layout/switcher_bar" /> as the bottom child. Buttons no longer live in this file.
File 37 values/themes.xml — selected switcher tab tint rule moves to SwitcherBar (file 41); themes keep the base button style only.
SWITCHING (files 40–43 + slot icons 59–61)
40. app/src/main/java/com/fedos/musenumberthree/SlotNavigator.kt
PURPOSE: THE single source of truth for "which Muse am I looking at" — every switching path goes through here (D13).
DETAILS: Holds currentIndex (StateFlow<Int> or a simple listener list — no extra dependencies beyond coroutines already in the project). API: switchTo(index) (direct jump, used by taps + shortcuts), next() / previous() (used by swipe; cycles 1→2→3→1 so a swipe never dead-ends). Out-of-range indices are ignored, never crash. Switching to the current index is a no-op. It does NOT touch WebViews — it announces the change; SlotController (file 29) listens and does the show/hide. State survives rotation because the navigator is created once in MainActivity and only currentIndex is saved/restored.
BUILD PROMPT:
```
Write SlotNavigator.kt (package com.fedos.musenumberthree): class SlotNavigator(initialIndex: Int = 0, private val slotCount: Int = SlotConfig.SLOTS.size). Holds a private current index exposed as a StateFlow<Int> named currentIndex (kotlinx.coroutines.flow). Functions: switchTo(index: Int) — ignore out-of-range and same-index, else update the flow; next() — switchTo((current + 1) % slotCount); previous() — switchTo((current - 1 + slotCount) % slotCount). fun saveState(Bundle) / restoreState(Bundle) with key "nav_current_slot". Header comment: "SINGLE SOURCE OF TRUTH for the current slot. The switcher bar, the swipe detector, the launcher shortcuts, and MainActivity all call this class — nothing else tracks the current slot. This class never touches a WebView; SlotController listens and shows/hides."
DONE = one state holder, three switching entry points, zero WebView code.
```
41. app/src/main/java/com/fedos/musenumberthree/SwitcherBar.kt
PURPOSE: The visible switcher — the bottom bar Git-FED taps to jump between his 3 Muses.
DETAILS: A custom view (LinearLayout subclass) inflated from layout/switcher_bar.xml (file 44). Builds one tab per entry in SlotConfig.SLOTS: that slot's icon (ic_slot_1/2/3, files 59–61) + its label. Tap a tab → navigator.switchTo(index). It OBSERVES the navigator: the current tab gets the selected state (orange tint + bold label, background switcher_button.xml), the other two stay unselected. Always visible — switching is never hidden in a menu. Labels come from SlotConfig only (D6).
BUILD PROMPT:
```
Write SwitcherBar.kt (package com.fedos.musenumberthree): class SwitcherBar(context, attrs) : LinearLayout, inflated from R.layout.switcher_bar in init. fun bind(navigator: SlotNavigator, lifecycleScope: CoroutineScope): create one tab (ImageView + TextView) per SlotConfig.SLOTS entry inside the bar's tab row — icon = the slot's drawable (ic_slot_1, ic_slot_2, ic_slot_3 by index), text = the slot's label; a tab click calls navigator.switchTo(its index). Collect navigator.currentIndex in lifecycleScope and restyle tabs on every change: current tab gets background @drawable/switcher_button with muse_orange tint + bold label; others get transparent background + regular label. Comment: "This bar is the app's main control. It is ALWAYS visible — switching Muses is never more than one tap away."
DONE = 3 labeled, iconed tabs; one tap switches; selected state always matches the navigator.
```
42. app/src/main/java/com/fedos/musenumberthree/SwipeSwitchDetector.kt
PURPOSE: Swipe-to-switch — flick left/right on the switcher bar to move between Muses.
DETAILS: A GestureDetector.SimpleOnGestureListener wrapper attached to the SwitcherBar's touch stream ONLY (never to a WebView — a WebView swipe would fight the Muse page's own scrolling and text selection). Horizontal fling past a threshold (e.g. 80dp distance, with velocity check): left fling → navigator.next(), right fling → navigator.previous(). Vertical-dominant gestures are ignored (returns false so other touches pass through). QA addition for file 17: swipe tests join the tap tests — swipe through all 3 slots both directions, confirm cycling wraps 3→1, and confirm swiping INSIDE a Muse page still scrolls the page normally.
BUILD PROMPT:
```
Write SwipeSwitchDetector.kt (package com.fedos.musenumberthree): class SwipeSwitchDetector(context: Context, navigator: SlotNavigator) wrapping a GestureDetector. Expose fun onTouchEvent(event: MotionEvent): Boolean to be called from SwitcherBar's touch handling ONLY. onFling: if horizontal distance > 80dp converted to px AND |velocityX| > |velocityY| — distanceX negative (left fling) calls navigator.next(), positive calls navigator.previous(), return true; all other gestures return false. Header comment: "Attached to the switcher bar, NEVER to a WebView. Swiping the Muse page itself must always scroll the page."
DONE = bar swipes cycle slots both directions; page swiping is untouched.
```
43. app/src/main/res/xml/shortcuts.xml
PURPOSE: Home-screen switching — long-press the app icon, jump straight to a specific Muse.
DETAILS: Three static shortcuts (one per slot), each an intent to MainActivity with android.intent.action.VIEW + extra extra_target_slot = 0/1/2, icons = the per-slot drawables (files 59–61), labels from strings (shortcut_slot_1/2/3 — ADD these three strings to file 35 when it's built; they mirror the SlotConfig labels). MainActivity reads the extra once at startup and calls navigator.switchTo(...) (file 32 amendment). Referenced from the manifest meta-data (file 25 amendment).
BUILD PROMPT:
```
Write res/xml/shortcuts.xml for "Muse Number 3": a <shortcuts> file with 3 <shortcut> entries — shortcutId slot_1/slot_2/slot_3, shortcutShortLabel @string/shortcut_slot_1 (etc.), icon @drawable/ic_slot_1 (etc.), each with an <intent> action android.intent.action.VIEW targeting com.fedos.musenumberthree.MainActivity and a category, carrying the target slot as intent extra data via a comment in the file: "MainActivity reads extra_target_slot (0/1/2) — wire the extras in MainActivity when building, XML shortcuts pass the slot in the intent data URI muse3://slot/1 (etc.) if extras prove awkward." Use intent android:data "muse3://slot/1", "muse3://slot/2", "muse3://slot/3" as the robust mechanism and note that in the comment.
DONE = 3 working long-press shortcuts, one per Muse.
```
44. app/src/main/res/layout/switcher_bar.xml
PURPOSE: The switcher bar's layout — its own file, not a corner of activity_main.xml.
DETAILS: Root = the SwitcherBar custom view (full class name as the tag), horizontal, background @drawable/switcher_bar_background (file 58), containing one empty horizontal LinearLayout @+id/switcher_tab_row (match_parent, weightSum 3) that SwitcherBar.kt fills with the 3 tabs at runtime (tabs are data-driven from SlotConfig — hardcoding 3 tab views in XML would break D6). Elevation 8dp so the bar reads as a control above the WebView.
BUILD PROMPT:
```
Write res/layout/switcher_bar.xml: root element is the custom view com.fedos.musenumberthree.SwitcherBar (match_parent width, wrap_content height, orientation horizontal, background @drawable/switcher_bar_background, elevation 8dp, padding 4dp) containing one LinearLayout @+id/switcher_tab_row (match_parent, wrap_content, horizontal, weightSum 3). Comment in the XML: "Tabs are built at runtime by SwitcherBar.kt from SlotConfig — do not hardcode tab views here."
DONE = one includable bar layout, zero hardcoded tabs.
```
ICON SOURCE ART (files 45–47)
45. assets/icon.png
PURPOSE: THE master icon art — every other icon in the repo is exported from this one file.
DETAILS: 1024×1024 PNG, Git-FED's own art wins (placed here by him); placeholder art comes from prompts/ICON-PROMPT.md (file 19). Design: bold "3" + Muse motif on the cream/orange palette (file 36). Safe-zone rule: keep the key art inside the center 66% circle so adaptive-icon masking (circle/squircle/ teardrop by launcher) never clips it. PNG only, never WebP.
BUILD PROMPT:
```
Produce/place assets/icon.png for "Muse Number 3": a 1024x1024 PNG app icon — bold number "3" built from three stacked browser-tab shapes with the Muse character motif, warm cream background (#FAF6F0) and orange (#D97757) art, simple comical flat style, all key art inside the center 66% circle (adaptive-icon safe zone), no text besides the 3, no WebP. If Git-FED supplies his own art, use his file untouched and note that in your reply.
DONE = one 1024x1024 PNG at assets/icon.png, safe zone respected.
```
46. assets/icon-source.svg
PURPOSE: The editable vector source of the master icon — the file future edits start from.
DETAILS: SVG version of the same art as file 45 (Git-FED's source if he works in vector; otherwise traced/derived when the PNG is made). Kept at repo root assets with the PNG so the pair never separates.
BUILD PROMPT:
```
Create assets/icon-source.svg for "Muse Number 3": the vector source of assets/icon.png (viewBox 0 0 1024 1024, the "3" of three stacked tabs + Muse motif, cream/orange palette as flat fills). If the icon was supplied as PNG only, note in the file's XML comment: "Raster-only source — see icon.png" and ship a minimal placeholder SVG rather than faking paths.
DONE = the icon's editable source (or an honest raster-only note) sits beside the PNG.
```
47. assets/play-store-icon-512.png
PURPOSE: The store-listing icon — 512×512, the exact size Play / the Muse AI App Store listing asks for.
DETAILS: Exported from file 45 (straight downscale, no redesign). 32-bit PNG with alpha. Also serves as the listing image anywhere the app gets a page (his store, fedpromptly).
BUILD PROMPT:
```
Export assets/play-store-icon-512.png: downscale assets/icon.png to exactly 512x512, PNG with alpha, no other changes.
DONE = a 512x512 PNG identical in design to the master icon.
```
FAVICON SET — assets/favicon/ (files 48–55)
For every web face the app ever gets — the repo's page, its store listing page, and the future sync-worker's page (docs/SYNC.md). Exported once from assets/icon.png and committed; any web page Git-FED ships for this app copies this folder as-is.
48. assets/favicon/favicon.ico
PURPOSE: The classic browser-tab icon — multi-size (16/32/48 in one .ico) so every browser finds a fit.
BUILD PROMPT:
```
Export assets/favicon/favicon.ico from assets/icon.png: one ICO file containing 16x16, 32x32, and 48x48 renders.
DONE = valid multi-resolution .ico present.
```
49. assets/favicon/favicon.svg
PURPOSE: The modern scalable favicon — crisp at any tab size, tiny file.
DETAILS: Same art as file 46, simplified if needed for small-size legibility (the "3" must read at 16px — if the full motif muddies, the SVG favicon is the "3" alone; that call is noted in the file).
BUILD PROMPT:
```
Create assets/favicon/favicon.svg from assets/icon-source.svg: same design, viewBox scaled to the icon art; test legibility at 16px and if the full motif muddies, simplify to the bold "3" alone and note the simplification in an XML comment.
DONE = one SVG favicon that reads clearly at tab size.
```
50. assets/favicon/favicon-16x16.png / 51. assets/favicon/favicon-32x32.png
PURPOSE: The standard PNG favicon pair every site template expects.
BUILD PROMPT:
```
Export assets/favicon/favicon-16x16.png (exactly 16x16) and assets/favicon/favicon-32x32.png (exactly 32x32) from assets/icon.png. PNG only.
DONE = both exact-size PNGs present.
```
52. assets/favicon/apple-touch-icon.png
PURPOSE: The icon iOS/Safari uses when someone pins the app's page to a home screen (180×180).
BUILD PROMPT:
```
Export assets/favicon/apple-touch-icon.png from assets/icon.png at exactly 180x180, PNG, no transparency (fill any transparent area with muse_cream #FAF6F0 — iOS renders transparency as black).
DONE = 180x180 opaque PNG present.
```
53. assets/favicon/android-chrome-192x192.png / 54. assets/favicon/android-chrome-512x512.png
PURPOSE: The PWA/Android-Chrome icon pair referenced by the web manifest (file 55).
BUILD PROMPT:
```
Export assets/favicon/android-chrome-192x192.png (192x192) and assets/favicon/android-chrome-512x512.png (512x512) from assets/icon.png, PNG.
DONE = both exact-size PNGs present.
```
55. assets/favicon/site.webmanifest
PURPOSE: The web app manifest tying the favicon set together for any web face of the app.
DETAILS: name "Muse Number 3", short_name "Muse 3", icons = files 53/54, theme_color #D97757 (muse_orange), background_color #FAF6F0 (muse_cream), display "standalone".
BUILD PROMPT:
```
Write assets/favicon/site.webmanifest as JSON: name "Muse Number 3", short_name "Muse 3", icons array referencing android-chrome-192x192.png (sizes 192x192, type image/png) and android-chrome-512x512.png (sizes 512x512, type image/png), theme_color "#D97757", background_color "#FAF6F0", display "standalone", start_url ".".
DONE = valid JSON manifest pointing at the two android-chrome icons.
```
ANDROID LAUNCHER + IN-APP ICONS (files 56–74)
56. app/src/main/res/drawable/ic_launcher_background.xml
PURPOSE: Adaptive icon background layer — flat muse_orange (color resource form).
DETAILS: Adaptive icons are two layers (background + foreground) the launcher masks into its own shape. Background = a simple <color>-backed drawable / vector filled with @color/muse_orange. Referenced by both adaptive XMLs (files 64–65 area: mipmap-anydpi-v26/ic_launcher.xml file 39 and ic_launcher_round.xml file 66).
BUILD PROMPT:
```
Write res/drawable/ic_launcher_background.xml: a vector/layer drawable that is a solid fill of @color/muse_orange across the full 108x108dp adaptive canvas. No art, no gradient in v1.
DONE = one solid adaptive background drawable.
```
57. app/src/main/res/drawable/ic_launcher_foreground.xml
PURPOSE: Adaptive icon foreground layer — the "3" art, inset into the adaptive safe zone.
DETAILS: Vector version of the master art (from file 46) drawn inside the center 66dp of the 108dp canvas. This is the file file 38–39's prompt referenced by name — now it exists as its own numbered file. If the art can't vector cleanly, a PNG foreground at xxxhdpi is the fallback and the fallback is noted here.
BUILD PROMPT:
```
Write res/drawable/ic_launcher_foreground.xml: a vector drawable (108x108 viewport) rendering the Muse Number 3 mark (bold "3" of three stacked tabs, from assets/icon-source.svg) centered inside the middle 66% safe zone, muse_cream art on transparent (the background layer supplies the orange). If the source art cannot be vectored faithfully, say so in your reply and instead produce ic_launcher_foreground.png exported into mipmap-xxxhdpi from assets/icon.png — never ship a bad trace.
DONE = a faithful foreground layer, vector preferred, honest PNG fallback allowed.
```
58. app/src/main/res/drawable/ic_launcher_monochrome.xml
PURPOSE: Android 13+ themed-icon layer — single-color silhouette the system tints to match the user's wallpaper/theme.
DETAILS: Same "3" shape as file 57, ONE flat color (black shapes, alpha does the work), no palette colors. Referenced as <monochrome android:drawable="@drawable/ic_launcher_monochrome"/> inside both adaptive icon XMLs.
BUILD PROMPT:
```
Write res/drawable/ic_launcher_monochrome.xml: a vector drawable, the Muse Number 3 "3" mark as a single-color black silhouette in the adaptive safe zone, no other colors. It exists so Android 13 themed icons can tint it.
DONE = one monochrome vector, same geometry as the foreground.
```
59. app/src/main/res/drawable/ic_slot_1.xml / 60. app/src/main/res/drawable/ic_slot_2.xml / 61. app/src/main/res/drawable/ic_slot_3.xml
PURPOSE: The per-Muse tab icons — each switcher tab, shortcut, and (future) notification gets its own numbered mark.
DETAILS: Vector drawables: a rounded tab/rounded-square outline with the numeral 1 / 2 / 3 centered, drawn in muse_ink so the SwitcherBar can tint selected = orange, unselected = ink. One design, three numerals — a user must be able to tell the tabs apart at a glance, including by the home-screen shortcuts (file 43).
BUILD PROMPT:
```
Write three vector drawables res/drawable/ic_slot_1.xml, ic_slot_2.xml, ic_slot_3.xml (24dp viewport 24): identical design — a rounded-rectangle "tab" outline (stroke, no fill) with the numeral 1 (resp. 2, 3) centered as a path/text-shape in the same stroke color, all paths using android:fillColor/strokeColor "@color/muse_ink" so runtime tinting works. Numerals must be visually distinct at 24dp.
DONE = three matching numbered tab icons, tintable.
```
62. app/src/main/res/drawable/switcher_bar_background.xml
PURPOSE: The switcher bar's surface — separates the control from the WebView content visually.
DETAILS: Layer/shape: muse_cream fill, 1dp top divider line in muse_slot_unselected, slight top rounding (12dp) to match the switcher buttons' language (file 38). Used by file 44.
BUILD PROMPT:
```
Write res/drawable/switcher_bar_background.xml: a layer-list — bottom layer a rectangle solid @color/muse_cream with top-left/top-right corners 12dp; top layer a 1dp line in @color/muse_slot_unselected across the top edge.
DONE = a clean bar surface with a divider, referenced by switcher_bar.xml.
```
63. app/src/main/res/drawable/ic_stat_notify.xml
PURPOSE: The notification small icon — required Android asset class the first tree missed entirely.
DETAILS: Status-bar icons are silhouette-only by platform rule: the "3" mark as a pure white-on-transparent vector (system tints it). Ships now so any future "a background Muse needs you" notification (or a foreground-service notice if background behavior ever needs one) doesn't block on missing art.
BUILD PROMPT:
```
Write res/drawable/ic_stat_notify.xml: a vector drawable (24dp) of the Muse Number 3 "3" mark as a solid WHITE silhouette on transparency (status-bar style, single color #FFFFFF, no details that vanish at status-bar size — test at 24dp, simplify the mark if needed).
DONE = one valid white silhouette notification icon.
```
64–65. (already in tree) mipmap-anydpi-v26/ic_launcher.xml — AMENDED
PURPOSE: Adaptive icon definition — now wires all three layers.
DETAILS: Amends file 38–39's second half: the adaptive-icon XML now reads background @drawable/ic_launcher_background (file 56), foreground @drawable/ic_launcher_foreground (file 57), AND monochrome @drawable/ic_launcher_monochrome (file 58).
BUILD PROMPT:
```
Write res/mipmap-anydpi-v26/ic_launcher.xml: <adaptive-icon> with background @drawable/ic_launcher_background, foreground @drawable/ic_launcher_foreground, monochrome @drawable/ic_launcher_monochrome.
DONE = all three layers referenced, no missing resources.
```
66. app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
PURPOSE: The round adaptive icon — launchers that request a circular icon get the real thing, not a cropped square.
DETAILS: Same three layers as file 64–65 (the safe-zone art survives the circle mask by design, file 45). Referenced by android:roundIcon in the manifest (file 25 amendment).
BUILD PROMPT:
```
Write res/mipmap-anydpi-v26/ic_launcher_round.xml: identical structure to ic_launcher.xml (same background, foreground, monochrome drawables). The round shape comes from the launcher's mask; this file exists so roundIcon resolves to the adaptive set.
DONE = round adaptive icon defined with the same three layers.
```
67–68. app/src/main/res/mipmap-mdpi/ic_launcher.png + ic_launcher_round.png
PURPOSE: Legacy launcher icons, mdpi (48×48 / round 48×48) — pre-adaptive devices and launchers.
BUILD PROMPT:
```
Export from assets/icon.png into app/src/main/res/mipmap-mdpi/: ic_launcher.png at 48x48 and ic_launcher_round.png at 48x48 (round = the art circularly masked with transparent corners). PNG only.
DONE = both mdpi PNGs at exact size.
```
69–70. app/src/main/res/mipmap-hdpi/ic_launcher.png + ic_launcher_round.png
PURPOSE: Legacy launcher icons, hdpi (72×72).
BUILD PROMPT:
```
Export from assets/icon.png into app/src/main/res/mipmap-hdpi/: ic_launcher.png at 72x72 and ic_launcher_round.png at 72x72 (circular mask, transparent corners). PNG only.
DONE = both hdpi PNGs at exact size.
```
71–72. app/src/main/res/mipmap-xhdpi/ic_launcher.png + ic_launcher_round.png
PURPOSE: Legacy launcher icons, xhdpi (96×96).
BUILD PROMPT:
```
Export from assets/icon.png into app/src/main/res/mipmap-xhdpi/: ic_launcher.png at 96x96 and ic_launcher_round.png at 96x96 (circular mask, transparent corners). PNG only.
DONE = both xhdpi PNGs at exact size.
```
73. app/src/main/res/mipmap-xxhdpi/ic_launcher.png + ic_launcher_round.png (files 73a/73b)
PURPOSE: Legacy launcher icons, xxhdpi (144×144).
BUILD PROMPT:
```
Export from assets/icon.png into app/src/main/res/mipmap-xxhdpi/: ic_launcher.png at 144x144 and ic_launcher_round.png at 144x144 (circular mask, transparent corners). PNG only.
DONE = both xxhdpi PNGs at exact size.
```
74. app/src/main/res/mipmap-xxxhdpi/ic_launcher.png + ic_launcher_round.png (files 74a/74b)
PURPOSE: Legacy launcher icons, xxxhdpi (192×192) — also the fallback resolution Android Studio and many launchers sample from.
BUILD PROMPT:
```
Export from assets/icon.png into app/src/main/res/mipmap-xxxhdpi/: ic_launcher.png at 192x192 and ic_launcher_round.png at 192x192 (circular mask, transparent corners). PNG only.
DONE = both xxxhdpi PNGs at exact size.
```
Build order — addendum (replaces steps 6 and 9 of the original order)
Core logic now builds: SlotConfig.kt → ProfileSupport.kt → SlotNavigator.kt (40) → WebView clients → MuseSlot.kt → SlotController.kt (navigator-driven) → SwitcherBar.kt (41) + SwipeSwitchDetector.kt (42) → MainActivity.kt.
Resources now include, in order: layouts (incl. switcher_bar.xml 44) → xml/shortcuts.xml (43) + the 3 shortcut strings into strings.xml → slot icons (59–61), bar background (62), notify icon (63) → launcher set (56–58, 64–66, 67–74).
Icon art step is no longer last-minute: master art (45) → source SVG (46) → Play icon (47) → favicon set (48–55) → Android exports (56–74). If Git-FED's own art arrives after a build, only the assets/ pair changes hands — every export re-runs from it.
QA (file 17, amended): the switching section and icons section are now pass/fail gates alongside the isolation test. If tapping, swiping, or a shortcut lands on the wrong Muse, that is the same class of failure as a merged login — stop and report.
