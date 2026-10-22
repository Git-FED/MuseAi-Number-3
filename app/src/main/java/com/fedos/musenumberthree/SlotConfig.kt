package com.fedos.musenumberthree

// SINGLE SOURCE OF TRUTH for the 3 slots. Edit URLs/labels here only. Default URLs are placeholders until Git-FED names the exact 3 Muse instances.
data class SlotDef(val id: Int, val label: String, val url: String, val profileName: String)

object SlotConfig {
    val SLOTS: List<SlotDef> = listOf(
        SlotDef(1, "Muse 1", "https://muse.ai", "muse-slot-1"),
        SlotDef(2, "Muse 2", "https://muse.ai", "muse-slot-2"),
        SlotDef(3, "Muse 3", "https://muse.ai", "muse-slot-3"),
    )
}
