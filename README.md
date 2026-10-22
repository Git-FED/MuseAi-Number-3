# Muse Number 3

Muse Number 3 is **almost 1 app with 3 different apps inside**: three Muse AI instances held in separate WebView slots in one Android app. Only one is visible at a time; switching slots does not close or reload the other pages.

## How the three slots work

Each slot has its own AndroidX WebKit Multi-Profile profile and login session. The three profiles keep their cookies and site storage separate from each other and from the phone's regular browser. Slot URLs and labels are configured only in `app/src/main/java/com/fedos/musenumberthree/SlotConfig.kt`.

Switch using the always-visible tab bar, swipe left/right on that bar, or use a shortcut from the app icon's long-press menu. All three paths use `SlotNavigator`.

If the Android System WebView provider does not support Multi-Profile, the app stops at an unsupported-device screen; it never falls back to shared cookies.

## Build

Open this project in Android Studio after generating the Gradle wrapper (see below), or run `scripts/build.sh` once the wrapper and Android SDK are available. `scripts/install-deps.sh` only checks prerequisites; it installs nothing.

The wrapper scripts and wrapper JAR are intentionally not included in this initial archive because the specification requires these generated files to be created by `gradle wrapper` or Android Studio, never hand-written. From a configured Android Studio/Gradle environment, run `gradle wrapper` in the repository root and commit `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`, and `gradle/wrapper/gradle-wrapper.jar`.

Release signing rules are in [docs/SIGNING.md](docs/SIGNING.md). Do not publish or claim a public download until a signed release exists.

## Status

Personal project scaffold. No release has been built. Three distinct account sessions and all switching routes require the manual checks in [docs/QA.md](docs/QA.md) on a supported device before shipping.

## Promo website

The static Muse Number 3 landing page is in [`site/`](site/). It includes its favicon, portable comic SVG artwork, looping header ticker, centered Discord placeholder, and an age gate that keeps payment links hidden until configured and confirmed. See [site/DEPLOYMENT.md](site/DEPLOYMENT.md) and [site/CONFIGURATION.md](site/CONFIGURATION.md) before publishing. The site has no build step or server dependency.

## Support

museai@fedpromptly.com
