package com.fedos.musenumberthree

import android.content.Context
import android.view.View
import android.webkit.WebView

class MuseSlot(
    val def: SlotDef,
    context: Context,
    client: MuseWebViewClient,
    chromeClient: MuseWebChromeClient,
) {
    val webView: WebView
    var loaded: Boolean = false
        private set

    init {
        // Required order: create, assign profile immediately, configure, attach clients, load once.
        webView = WebView(context)
        ProfileSupport.applyProfile(webView, def.profileName)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webViewClient = client
        webView.webChromeClient = chromeClient
        webView.loadUrl(def.url)
        loaded = true
    }

    // NEVER destroy, reload, or onPause a hidden slot — visibility-only switching preserves page state.
    fun show() { webView.visibility = View.VISIBLE }
    fun hide() { webView.visibility = View.GONE }
}
