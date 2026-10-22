```text
Build the complete Muse Number 3 repository exactly as described in docs/ultimate-file-tree.md, file by file, in its updated Build order. This is an Android-native Kotlin app with three persistent Muse WebView slots. Do not add unlisted product scope. Do not perform GitHub writes. Do not install or download tools, SDKs, packages, dependencies, or assets; dependencies must be declared in Gradle files and used through the normal owner-controlled Gradle/Android Studio flow. Do not generate wrapper files by hand; the owner generates them with `gradle wrapper` or Android Studio.

Hard rules D1–D14:
1. Native Kotlin Android app only.
2. One AndroidX WebKit Profile per WebView slot, with independent cookies and storage.
3. Call WebViewCompat.setProfile exactly once before any other operation on each new WebView.
4. If Multi-Profile is unsupported, show the unsupported screen and stop; never fall back to shared cookies.
5. Switching slots only toggles visibility; never destroy, reload, or pause hidden slots.
6. All three Muse URLs and labels live only in SlotConfig.kt.
7. No gate, pay codes, backend, or payment code in v1.
8. No sync implementation in v1; docs/SYNC.md is spec-only.
9. Use package com.fedos.musenumberthree, minSdk 26, compileSdk/targetSdk 34 (reconfirm SDK floor at build time).
10. Never commit or expose a signing keystore or passwords.
11. No automated tests in v1; use the manual QA checklist.
12. Java 17; declare dependencies in Gradle; do not install them outside the normal Gradle/Android Studio flow.
13. Switching is first-class: tap bar, swipe on the bar only, and static launcher shortcuts all call SlotNavigator; no other code owns current-slot state.
14. Every specified icon is a real file in the tree; export raster variants from assets/icon.png and commit them. Never use WebP.

Use docs/ultimate-file-tree.md as the repository layout source of truth. Preserve the setProfile-before-use creation order, navigator-driven show/hide lifecycle, launcher shortcut routing, and exact file paths. Do not add a network backend, extra permissions, or a silent fallback. Do not claim device QA has passed unless it was run on a supported device with three separate accounts.

After building, run every check in docs/QA.md and report results file by file.
```
