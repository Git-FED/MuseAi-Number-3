# Manual QA checklist

The critical isolation test must pass before release. If accounts collide, stop and report; do not ship around the failure.

## Install & launch
- [ ] Install the debug APK on a supported Android device.
- [ ] Launch and confirm Multi-Profile feature check passes before WebViews are created.
- [ ] Confirm exactly three tabs are visible and each slot loads its configured Muse URL.

## The isolation test — CRITICAL
- [ ] Log slot 1 into Muse account A, slot 2 into account B, and slot 3 into account C.
- [ ] Switch through all three twice and confirm every slot remains logged into its own account.
- [ ] Confirm none of the three accounts logged another slot out or adopted its session.

## Switching
- [ ] Tap each tab and confirm the matching Muse appears with its matching tab selected.
- [ ] Swipe left and right on the switcher bar through all three slots; confirm cycling wraps from slot 3 to slot 1 and back.
- [ ] Swipe and scroll inside a Muse page; confirm page scrolling and text selection still work normally.
- [ ] Long-press the app icon; confirm all three shortcuts appear and each opens the matching Muse slot.
- [ ] Activate each shortcut while the app is already open and confirm it switches to the requested slot.

## Persistence
- [ ] Start a chat in slot 2, switch away, return, and confirm the conversation remains in progress.
- [ ] Rotate the device and confirm pages do not reload.
- [ ] Kill and relaunch the app; confirm all three logins remain.

## Logout isolation
- [ ] Log out of one slot and confirm the other two remain logged in and unchanged.

## Unsupported device
- [ ] On a device/emulator whose WebView lacks Multi-Profile, confirm the unsupported screen appears before any login is possible.
- [ ] Confirm there is no continue-anyway path or merged-session fallback.

## Icons
- [ ] Confirm square and round launcher icons render correctly.
- [ ] On Android 13+, confirm the themed/monochrome icon renders.
- [ ] Confirm each switcher tab shows its own numbered icon.
- [ ] Confirm all three home-screen shortcuts appear on app-icon long-press.
