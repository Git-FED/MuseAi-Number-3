package com.fedos.musenumberthree

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class MuseWebChromeClient(
    private val fileChooserLauncher: (Intent) -> Unit,
    private val fileCallbackHolder: (ValueCallback<Array<Uri>>?) -> Unit,
    private val onProgress: (Int) -> Unit,
) : WebChromeClient() {
    // Without onShowFileChooser, Muse's file-upload button does nothing inside a WebView.
    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams,
    ): Boolean {
        fileCallbackHolder(filePathCallback)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = fileChooserParams.acceptTypes.firstOrNull { it.isNotBlank() } ?: "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, fileChooserParams.mode == FileChooserParams.MODE_OPEN_MULTIPLE)
            putExtra(Intent.EXTRA_MIME_TYPES, fileChooserParams.acceptTypes.filter { it.isNotBlank() }.toTypedArray())
        }
        return try { fileChooserLauncher(intent); true } catch (_: Exception) { fileCallbackHolder(null); false }
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) { onProgress(newProgress) }
}
