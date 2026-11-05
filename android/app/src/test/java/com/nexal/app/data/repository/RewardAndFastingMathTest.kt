package com.nexal.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RewardAndFastingMathTest {

    private val today = LocalDate.of(2026, 11, 4)

    @Test
    fun `levels start at one and cost more each time`() {
        assertEquals(1, RewardMath.levelFor(0))
        assertEquals(1, RewardMath.levelFor(99))
        assertEquals(2, RewardMath.levelFor(100))
        assertEquals(2, RewardMath.levelFor(299))
        assertEquals(3, RewardMath.levelFor(300))
    }

    @Test
    fun `level progress is the share of the current level`() {
        assertEquals(0f, RewardMath.levelProgress(100), 0.001f)
        assertEquals(0.5f, RewardMath.levelProgress(200), 0.001f)
    }

    @Test
    fun `streak grows on consecutive days and restarts after a gap`() {
        assertEquals(1, RewardMath.nextStreak(0, null, today))
        assertEquals(4, RewardMath.nextStreak(3, today.minusDays(1), today))
        assertEquals(3, RewardMath.nextStreak(3, today, today))
        assertEquals(1, RewardMath.nextStreak(3, today.minusDays(2), today))
    }

    private val plan = fastingPlans.first { it.id == "16_8" }
    private val fast = ActiveFast(plan.id, startedAtMillis = 1_000L)
    private val hour = 3_600_000L

    @Test
    fun `every plan fits in a day`() {
        assertTrue(fastingPlans.all { it.fastHours in 1..23 && it.fastHours + it.eatHours == 24 })
    }

    @Test
    fun `progress follows the clock and stops at the goal`() {
        assertEquals(0.5f, FastingMath.progress(fast, plan, 1_000L + 8 * hour), 0.001f)
        assertEquals(1f, FastingMath.progress(fast, plan, 1_000L + 20 * hour), 0.001f)
    }

    @Test
    fun `goal is reached exactly at the target`() {
        assertFalse(FastingMath.isGoalReached(fast, plan, 1_000L + 16 * hour - 1))
        assertTrue(FastingMath.isGoalReached(fast, plan, 1_000L + 16 * hour))
    }

    @Test
    fun `a clock moved backwards never shows negative time`() {
        assertEquals(0L, FastingMath.elapsedMillis(fast, 0L))
    }

    @Test
    fun `clock format pads hours minutes and seconds`() {
        assertEquals("16:05:09", FastingMath.formatClock(16 * hour + 5 * 60_000L + 9_000L))
    }
}
