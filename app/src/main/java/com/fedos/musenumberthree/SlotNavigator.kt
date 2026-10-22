package com.fedos.musenumberthree

import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// SINGLE SOURCE OF TRUTH for the current slot. The switcher bar, swipe detector, launcher shortcuts,
// and MainActivity all call this class — nothing else tracks the current slot. It never touches a WebView.
class SlotNavigator(initialIndex: Int = 0, private val slotCount: Int = SlotConfig.SLOTS.size) {
    private val _currentIndex = MutableStateFlow(initialIndex.coerceIn(0, (slotCount - 1).coerceAtLeast(0)))
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun switchTo(index: Int) {
        if (index !in 0 until slotCount || index == _currentIndex.value) return
        _currentIndex.value = index
    }

    fun next() { if (slotCount > 0) switchTo((_currentIndex.value + 1) % slotCount) }
    fun previous() { if (slotCount > 0) switchTo((_currentIndex.value - 1 + slotCount) % slotCount) }

    fun saveState(outState: Bundle) { outState.putInt(KEY, _currentIndex.value) }
    fun restoreState(state: Bundle?) {
        state?.getInt(KEY, _currentIndex.value)?.let(::switchTo)
    }

    companion object { private const val KEY = "nav_current_slot" }
}
