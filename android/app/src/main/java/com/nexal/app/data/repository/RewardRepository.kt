package com.nexal.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

enum class RewardType(val xp: Int, val title: String, val message: String) {
    WORKOUT(50, "Workout complete", "That's another session in the bank."),
    FAST(40, "Fast complete", "You reached your fasting goal."),
    CARDIO(25, "Cardio logged", "Every session adds up."),
    MEAL(10, "Meal logged", "Tracking is what makes the plan work."),
    WATER(15, "Water goal hit", "Fully hydrated for today."),
}

data class RewardProgress(val xp: Int = 0, val streakDays: Int = 0) {
    val level: Int get() = RewardMath.levelFor(xp)
    val levelProgress: Float get() = RewardMath.levelProgress(xp)
}

data class RewardEvent(
    val type: RewardType,
    val xpGained: Int,
    val progress: RewardProgress,
    val leveledUp: Boolean,
)

/** Pure rules, kept apart from storage so they can be unit tested. */
object RewardMath {
    private const val XP_PER_LEVEL_STEP = 100

    /** Total XP needed to reach [level]; each level costs 100 XP more than the last. */
    fun xpForLevel(level: Int): Int = XP_PER_LEVEL_STEP * (level - 1) * level / 2

    fun levelFor(xp: Int): Int {
        var level = 1
        while (xp >= xpForLevel(level + 1)) level++
        return level
    }

    fun levelProgress(xp: Int): Float {
        val level = levelFor(xp)
        val start = xpForLevel(level)
        return (xp - start).toFloat() / (xpForLevel(level + 1) - start)
    }

    /** A streak grows on consecutive days, holds within a day and restarts after a gap. */
    fun nextStreak(current: Int, lastActive: LocalDate?, today: LocalDate): Int = when {
        lastActive == null -> 1
        lastActive == today -> current.coerceAtLeast(1)
        lastActive == today.minusDays(1) -> current + 1
        else -> 1
    }
}

/**
 * XP and streaks, stored on this device. Each reward type pays out once per
 * [dedupeKey], so repeating an action cannot farm XP or re-trigger the pop-up.
 */
@Singleton
class RewardRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("nexal_rewards", Context.MODE_PRIVATE)

    private val _progress = MutableStateFlow(
        RewardProgress(prefs.getInt(KEY_XP, 0), prefs.getInt(KEY_STREAK, 0))
    )
    val progress: StateFlow<RewardProgress> = _progress.asStateFlow()

    private val _pending = MutableStateFlow<RewardEvent?>(null)
    val pending: StateFlow<RewardEvent?> = _pending.asStateFlow()

    @Synchronized
    fun award(type: RewardType, dedupeKey: String = LocalDate.now().toString()) {
        val claimKey = "claimed_${type.name}"
        if (prefs.getString(claimKey, null) == dedupeKey) return

        val today = LocalDate.now()
        val before = _progress.value
        val lastActive = prefs.getString(KEY_LAST_ACTIVE, null)?.let(LocalDate::parse)
        val after = RewardProgress(
            xp = before.xp + type.xp,
            streakDays = RewardMath.nextStreak(before.streakDays, lastActive, today)
        )
        prefs.edit()
            .putInt(KEY_XP, after.xp)
            .putInt(KEY_STREAK, after.streakDays)
            .putString(KEY_LAST_ACTIVE, today.toString())
            .putString(claimKey, dedupeKey)
            .apply()

        _progress.value = after
        _pending.value = RewardEvent(type, type.xp, after, after.level > before.level)
    }

    fun dismiss() {
        _pending.value = null
    }

    private companion object {
        const val KEY_XP = "xp"
        const val KEY_STREAK = "streak"
        const val KEY_LAST_ACTIVE = "last_active"
    }
}
