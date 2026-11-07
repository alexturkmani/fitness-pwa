package com.nexal.app.ui.paywall

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class PaywallCopyTest {
    @Test
    fun headlineUsesDaysAndGoal() {
        assertEquals("Build your 4-day muscle gain plan", setupHeadline(4, "Muscle Gain"))
    }

    @Test
    fun headlineCopesWithMissingAnswers() {
        assertEquals("Build your 3-day training plan", setupHeadline(3, null))
        assertEquals("Build your strength plan", setupHeadline(0, "Strength"))
    }

    @Test
    fun savingsRoundDown() {
        assertEquals(29, yearlySavingsPercent(12_990_000, 109_990_000))
    }

    @Test
    fun noSavingsClaimWithoutARealSaving() {
        assertNull(yearlySavingsPercent(10_000_000, 120_000_000))
        assertNull(yearlySavingsPercent(0, 109_990_000))
    }

    @Test
    fun perMonthUsesPlayCurrency() {
        assertEquals("$9.17", yearlyPerMonthText(109_990_000, "USD", Locale.US))
    }
}
