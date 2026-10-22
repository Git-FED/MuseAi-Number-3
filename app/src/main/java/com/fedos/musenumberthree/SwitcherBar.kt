package com.fedos.musenumberthree

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SwitcherBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var tabRow: LinearLayout? = null
    private var swipeDetector: SwipeSwitchDetector? = null
    private val tabs = mutableListOf<LinearLayout>()
    private val labels = mutableListOf<TextView>()
    private val icons = mutableListOf<ImageView>()

    fun bind(navigator: SlotNavigator, scope: CoroutineScope) {
        // The layout is inflated by activity_main.xml; this bar is ALWAYS visible as the app's main control.
        tabRow = findViewById(R.id.switcher_tab_row)
        val row = tabRow ?: return
        swipeDetector = SwipeSwitchDetector(context, navigator)
        row.removeAllViews(); tabs.clear(); labels.clear(); icons.clear()
        SlotConfig.SLOTS.forEachIndexed { index, def ->
            val tab = LinearLayout(context).apply {
                orientation = VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setPadding(8, 7, 8, 7)
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            }
            val icon = ImageView(context).apply {
                setImageResource(when (index) { 0 -> R.drawable.ic_slot_1; 1 -> R.drawable.ic_slot_2; else -> R.drawable.ic_slot_3 })
                layoutParams = LayoutParams(24.dp, 24.dp)
                contentDescription = def.label
            }
            val label = TextView(context).apply {
                text = def.label
                gravity = Gravity.CENTER
                textSize = 12f
            }
            tab.addView(icon); tab.addView(label)
            tab.setOnClickListener { navigator.switchTo(index) }
            tab.setOnTouchListener { _, event -> swipeDetector?.onTouchEvent(event) ?: false }
            row.addView(tab)
            tabs += tab; labels += label; icons += icon
        }
        setOnTouchListener { _, event -> swipeDetector?.onTouchEvent(event) ?: false }
        scope.launch { navigator.currentIndex.collect { updateSelected(it) } }
    }

    private fun updateSelected(selected: Int) {
        tabs.forEachIndexed { index, tab ->
            val active = index == selected
            val bg = GradientDrawable().apply {
                cornerRadii = floatArrayOf(8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 8.dp.toFloat(), 0f, 0f, 0f, 0f)
                setColor(ContextCompat.getColor(context, if (active) R.color.muse_orange else R.color.muse_cream))
            }
            tab.background = bg
            labels[index].setTypeface(null, if (active) Typeface.BOLD else Typeface.NORMAL)
            labels[index].setTextColor(ContextCompat.getColor(context, R.color.muse_ink))
            icons[index].setColorFilter(ContextCompat.getColor(context, if (active) R.color.muse_orange_dark else R.color.muse_ink))
            tab.isSelected = active
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
