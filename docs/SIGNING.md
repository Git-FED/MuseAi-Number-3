# Release signing

## One keystore, kept private

Generate the release keystore once, back it up somewhere outside the repository, and store its password in a password manager. Never commit it, place it in a script, or put passwords in a file. Losing the keystore means future updates install as a brand-new app and existing users must start over.

Generate it locally in an ignored build directory (never in the repo's tracked files):

```sh
mkdir -p build/keys
keytool -genkeypair -v -keystore build/keys/release.keystore -alias musenumberthree -keyalg RSA -keysize 2048 -validity 10000
```

## CI signing

Store the base64 keystore as the GitHub Actions secret `ANDROID_KEYSTORE_BASE64`. Configure `MUSE3_KEYSTORE_PASS`, `MUSE3_KEY_ALIAS`, and `MUSE3_KEY_PASS` as secrets too. The tag workflow materializes `app/release.keystore` for the release build and removes it in an `always()` cleanup step. Never print secret values. CI uploads a draft release for a human to publish.

## Local signing

Build an unsigned release APK, set `MUSE3_KEYSTORE` to the local keystore path, then run:

```sh
scripts/sign-release.sh app/build/outputs/apk/release/app-release-unsigned.apk
```

`apksigner` prompts for the password. Do not pass a password on the command line.
