package com.fedos.musenumberthree

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs

// Attached to the switcher bar only, NEVER to a WebView; page scrolling must remain untouched.
class SwipeSwitchDetector(context: Context, private val navigator: SlotNavigator) {
    private val threshold = 80f * context.resources.displayMetrics.density
    private var consumedFling = false
    private val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val start = e1 ?: return false
            val dx = e2.x - start.x
            if (abs(dx) > threshold && abs(velocityX) > abs(velocityY)) {
                if (dx < 0) navigator.next() else navigator.previous()
                consumedFling = true
                return true
            }
            return false
        }
    })

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) consumedFling = false
        detector.onTouchEvent(event)
        val handled = event.actionMasked == MotionEvent.ACTION_UP && consumedFling
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) consumedFling = false
        return handled
    }
}
