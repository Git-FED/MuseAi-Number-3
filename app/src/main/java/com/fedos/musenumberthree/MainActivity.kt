package com.fedos.musenumberthree

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    private var pendingFileCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var filePicker: ActivityResultLauncher<Intent>
    private lateinit var navigator: SlotNavigator
    private lateinit var slotController: SlotController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val uris = if (result.resultCode == RESULT_OK && data != null) {
                val clip = data.clipData
                if (clip != null) Array(clip.itemCount) { i -> clip.getItemAt(i).uri }
                else data.data?.let { arrayOf(it) }
            } else null
            pendingFileCallback?.onReceiveValue(uris)
            pendingFileCallback = null
        }

        // If Multi-Profile is unsupported we stop here on purpose — a merged session is worse than no app.
        setContentView(R.layout.activity_main)
        if (!ProfileSupport.isMultiProfileSupported()) {
            setContentView(R.layout.screen_unsupported)
            return
        }

        val initialIndex = intent.targetSlotIndex() ?: savedInstanceState?.getInt("nav_current_slot") ?: 0
        navigator = SlotNavigator(initialIndex)
        navigator.restoreState(savedInstanceState)
        intent.targetSlotIndex()?.let(navigator::switchTo)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val bar = findViewById<SwitcherBar>(R.id.switcher_bar)
        bar.bind(navigator, lifecycleScope)
        slotController = SlotController(
            container = findViewById<FrameLayout>(R.id.slot_container),
            activity = this,
            navigator = navigator,
            fileChooserLauncher = { filePicker.launch(it) },
            fileCallbackHolder = { pendingFileCallback = it },
            onProgress = { slotIndex, value ->
                if (slotIndex == navigator.currentIndex.value) {
                    progress.progress = value
                    progress.visibility = if (value >= 100) View.GONE else View.VISIBLE
                }
            },
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val current = slotController.slots.getOrNull(navigator.currentIndex.value)?.webView
                if (current?.canGoBack() == true) current.goBack() else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::navigator.isInitialized) intent.targetSlotIndex()?.let(navigator::switchTo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::navigator.isInitialized) navigator.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun Intent.targetSlotIndex(): Int? {
        val uri = data ?: return getIntExtra("extra_target_slot", -1).takeIf { it in SlotConfig.SLOTS.indices }
        if (uri.scheme != "muse3" || uri.host != "slot") return null
        return uri.lastPathSegment?.toIntOrNull()?.minus(1)?.takeIf { it in SlotConfig.SLOTS.indices }
    }
}
