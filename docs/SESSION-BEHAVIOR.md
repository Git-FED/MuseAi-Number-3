# Session behavior

## Three isolated sessions

Each WebView slot gets its own AndroidX WebKit Profile: `muse-slot-1`, `muse-slot-2`, and `muse-slot-3`. Each profile owns its cookies, web storage, and login session. The phone's normal browser is a fourth, separate session.

`WebViewCompat.setProfile()` is applied to a new WebView before settings, navigation, JavaScript, or storage access. Never move this call later.

## Switching and persistence

Switching changes only visibility: the current slot becomes `GONE`, and the selected slot becomes `VISIBLE`. All WebViews remain attached. Do not destroy, reload, or pause a hidden slot. Chats and page state should remain live.

Logging out of a slot must clear data through that slot's Profile storage only. Never clear shared WebView storage to log out one slot.

Clearing app data or reinstalling removes all three profiles and logs all three accounts out; this is expected.

## Unsupported devices

If the device's WebView provider does not support `MULTI_PROFILE`, the app shows the unsupported screen before creating any WebView. There is no shared-cookie fallback and no way to continue unsafely.
