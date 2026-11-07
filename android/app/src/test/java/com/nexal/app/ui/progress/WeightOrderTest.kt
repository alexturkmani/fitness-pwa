package com.nexal.app.ui.progress

import com.nexal.app.domain.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class WeightOrderTest {
    @Test
    fun newestEntryComesLastWhateverOrderTheDatabaseUses() {
        val fromDatabase = listOf(
            WeightEntry("2026-11-07", 72.0),
            WeightEntry("2026-10-20", 73.1),
            WeightEntry("2026-10-01", 74.2),
        )
        val ordered = oldestFirst(fromDatabase)
        assertEquals(72.0, ordered.last().weight, 0.0)
        assertEquals(-2.2, ordered.last().weight - ordered.first().weight, 0.001)
    }
}
