package com.fedos.musenumberthree

import android.webkit.WebView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

object ProfileSupport {
    fun isMultiProfileSupported(): Boolean =
        WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)

    /**
     * MUST be the first call made on a new WebView — before loadUrl, before evaluateJavascript,
     * before any storage access. Calling it late throws IllegalStateException. There is deliberately
     * NO fallback: if Multi-Profile is unsupported the app stops at the unsupported screen rather
     * than merging sessions.
     */
    fun applyProfile(webView: WebView, profileName: String) {
        WebViewCompat.setProfile(webView, profileName)
    }
}
