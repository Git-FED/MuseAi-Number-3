package com.fedos.musenumberthree

import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError

class MuseWebViewClient : WebViewClient() {
    private var failedMainFrameUrl: String? = null

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (request.url.scheme == "muse3retry") {
            failedMainFrameUrl?.let(view::loadUrl)
            return true
        }
        // Keep every web URL inside this slot. Muse may use auth, asset, or redirect
        // hosts outside the configured starting host; sending those URLs to the
        // regular browser would break the slot's isolated session.
        if (request.url.scheme == "http" || request.url.scheme == "https") return false

        // Do not hand custom schemes to another app. The only custom scheme owned by
        // this app is muse3retry, handled above.
        return true
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        if (!request.isForMainFrame) return
        failedMainFrameUrl = request.url.toString()
        // Inline localized-ready error page retries only after the user taps; no automatic loop.
        val html = """<!doctype html><meta name='viewport' content='width=device-width,initial-scale=1'><body style='font-family:sans-serif;padding:24px;background:#FAF6F0;color:#1F1B16'><h2>This Muse could not load</h2><p>Check your connection, then tap to retry.</p><button onclick="location.href='muse3retry://again'">Retry</button></body>"""
        view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        // Never bypass certificate errors.
        handler.cancel()
    }
}
