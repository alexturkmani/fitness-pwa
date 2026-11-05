package com.nexal.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class FastingPlan(
    val id: String,
    val name: String,
    val fastHours: Int,
    val level: String,
    val summary: String,
    val steps: List<String>,
) {
    val eatHours: Int get() = 24 - fastHours
}

/** Daily schedules only: fasts over 24 hours need medical supervision, so they aren't offered. */
val fastingPlans = listOf(
    FastingPlan(
        id = "12_12", name = "12:12", fastHours = 12, level = "Beginner",
        summary = "An even split. Most of the fast happens while you sleep.",
        steps = listOf(
            "Finish dinner by about 7pm.",
            "Have only water, black coffee or plain tea overnight.",
            "Eat breakfast from about 7am.",
        ),
    ),
    FastingPlan(
        id = "14_10", name = "14:10", fastHours = 14, level = "Beginner",
        summary = "A gentle step up that just pushes breakfast back a little.",
        steps = listOf(
            "Finish dinner by about 7pm.",
            "Skip late snacks and stay hydrated.",
            "Have your first meal at about 9am.",
        ),
    ),
    FastingPlan(
        id = "16_8", name = "16:8", fastHours = 16, level = "Intermediate",
        summary = "The most popular schedule: two or three meals in an 8-hour window.",
        steps = listOf(
            "Pick a window that suits your day, such as 12pm to 8pm.",
            "Eat balanced, protein-rich meals within that window.",
            "Stick to zero-calorie drinks for the other 16 hours.",
        ),
    ),
    FastingPlan(
        id = "18_6", name = "18:6", fastHours = 18, level = "Intermediate",
        summary = "A shorter window, usually two filling meals.",
        steps = listOf(
            "Try this only once 16:8 feels comfortable.",
            "Eat between about 1pm and 7pm.",
            "Make meals substantial so you still reach your calorie and protein goals.",
        ),
    ),
    FastingPlan(
        id = "20_4", name = "20:4", fastHours = 20, level = "Advanced",
        summary = "One main meal and a small one, inside a 4-hour window.",
        steps = listOf(
            "Build up gradually, and don't start here.",
            "Eat between about 3pm and 7pm.",
            "Avoid hard training late in the fast until you know how you respond.",
        ),
    ),
    FastingPlan(
        id = "23_1", name = "OMAD", fastHours = 23, level = "Advanced",
        summary = "One meal a day. Hard to get enough nutrition, so use it sparingly.",
        steps = listOf(
            "Plan one nutritious, complete meal at a regular time.",
            "Check you are reaching your protein and calorie goals.",
            "Stop and eat if you feel dizzy, faint or unwell.",
        ),
    ),
)

const val FASTING_SAFETY_NOTE =
    "Fasting isn't suitable for everyone. Don't fast if you are pregnant, breastfeeding, under 18, " +
        "underweight or have a history of eating disorders. If you have diabetes or take medication, " +
        "talk to your doctor first. This is general information, not medical advice."

data class ActiveFast(val planId: String, val startedAtMillis: Long)

/** Pure timer rules. Everything derives from the start time, so a fast survives the app closing. */
object FastingMath {
    private const val MILLIS_PER_HOUR = 3_600_000L

    fun goalMillis(plan: FastingPlan): Long = plan.fastHours * MILLIS_PER_HOUR

    fun elapsedMillis(fast: ActiveFast, nowMillis: Long): Long =
        (nowMillis - fast.startedAtMillis).coerceAtLeast(0L)

    fun progress(fast: ActiveFast, plan: FastingPlan, nowMillis: Long): Float =
        (elapsedMillis(fast, nowMillis).toFloat() / goalMillis(plan)).coerceIn(0f, 1f)

    fun isGoalReached(fast: ActiveFast, plan: FastingPlan, nowMillis: Long): Boolean =
        elapsedMillis(fast, nowMillis) >= goalMillis(plan)

    fun formatClock(millis: Long): String {
        val totalSeconds = millis.coerceAtLeast(0L) / 1000
        return "%02d:%02d:%02d".format(totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60)
    }
}

/** The running fast, stored on this device. */
@Singleton
class FastingRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("nexal_fasting", Context.MODE_PRIVATE)

    private val _active = MutableStateFlow(readActive())
    val active: StateFlow<ActiveFast?> = _active.asStateFlow()

    private fun readActive(): ActiveFast? {
        val planId = prefs.getString(KEY_PLAN, null) ?: return null
        val startedAt = prefs.getLong(KEY_STARTED_AT, 0L)
        return if (startedAt > 0L) ActiveFast(planId, startedAt) else null
    }

    fun start(planId: String, nowMillis: Long = System.currentTimeMillis()) {
        prefs.edit().putString(KEY_PLAN, planId).putLong(KEY_STARTED_AT, nowMillis).apply()
        _active.value = ActiveFast(planId, nowMillis)
    }

    fun end() {
        prefs.edit().remove(KEY_PLAN).remove(KEY_STARTED_AT).apply()
        _active.value = null
    }

    private companion object {
        const val KEY_PLAN = "plan_id"
        const val KEY_STARTED_AT = "started_at"
    }
}
