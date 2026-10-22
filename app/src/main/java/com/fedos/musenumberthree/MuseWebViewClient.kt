package com.fedos.musenumberthree

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError

class MuseWebViewClient(private val slotHost: String) : WebViewClient() {
    private var failedMainFrameUrl: String? = null

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (request.url.scheme == "muse3retry") {
            failedMainFrameUrl?.let(view::loadUrl)
            return true
        }
        val host = request.url.host.orEmpty().lowercase()
        // Keep the configured Muse host and Muse AI auth redirects inside this slot's profile.
        if (host == slotHost.lowercase() || host == "muse.ai" || host.endsWith(".muse.ai")) return false
        // Send unrelated links to the regular browser, which is a separate session.
        return try {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (_: ActivityNotFoundException) { true }
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
