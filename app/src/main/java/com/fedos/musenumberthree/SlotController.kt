package com.fedos.musenumberthree

import android.app.Activity
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.webkit.ValueCallback
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// Slots are created ONCE for the activity lifetime. The navigator is the only switching state owner.
class SlotController(
    private val container: ViewGroup,
    activity: Activity,
    private val navigator: SlotNavigator,
    fileChooserLauncher: (Intent) -> Unit,
    fileCallbackHolder: (ValueCallback<Array<Uri>>?) -> Unit,
    onProgress: (Int, Int) -> Unit,
    scope: CoroutineScope = (activity as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope
        ?: throw IllegalArgumentException("Activity must be a LifecycleOwner"),
) {
    val slots: List<MuseSlot> = SlotConfig.SLOTS.mapIndexed { index, def ->
        val chrome = MuseWebChromeClient(fileChooserLauncher, fileCallbackHolder) { progress -> onProgress(index, progress) }
        MuseSlot(def, activity, MuseWebViewClient(Uri.parse(def.url).host.orEmpty()), chrome).also { slot ->
            container.addView(slot.webView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    val currentIndex: Int get() = navigator.currentIndex.value

    init {
        render(navigator.currentIndex.value)
        scope.launch { navigator.currentIndex.collect { render(it) } }
    }

    private fun render(index: Int) {
        slots.forEachIndexed { i, slot -> if (i == index) slot.show() else slot.hide() }
    }

    fun saveState(outState: android.os.Bundle) = navigator.saveState(outState)
    fun restoreState(state: android.os.Bundle?) = navigator.restoreState(state)
}
