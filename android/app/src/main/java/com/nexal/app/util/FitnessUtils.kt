package com.nexal.app.util

import com.nexal.app.domain.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToInt

/** Mifflin-St Jeor BMR × activity multiplier */
fun calculateTDEE(profile: UserProfile): Int {
    val bmr = when (profile.gender) {
        Gender.MALE -> 10 * profile.weight + 6.25 * profile.height - 5 * profile.age + 5
        Gender.FEMALE -> 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 161
        Gender.OTHER -> 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 78
    }
    return (bmr * profile.activityLevel.multiplier).roundToInt()
}

/** Goal-adjusted calories + protein/carb/fat split */
fun calculateMacroTargets(profile: UserProfile): MacroNutrients {
    val tdee = calculateTDEE(profile)
    val goals = profile.fitnessGoals.ifEmpty { listOf(FitnessGoal.GENERAL_FITNESS) }

    data class GoalMacros(val calAdjust: Int, val proteinRatio: Double, val fatRatio: Double)

    val goalMacros = mapOf(
        FitnessGoal.WEIGHT_LOSS to GoalMacros(-500, 0.35, 0.25),
        FitnessGoal.MUSCLE_GAIN to GoalMacros(300, 0.30, 0.25),
        FitnessGoal.STRENGTH to GoalMacros(0, 0.30, 0.30),
        FitnessGoal.ENDURANCE to GoalMacros(200, 0.20, 0.25),
        FitnessGoal.GENERAL_FITNESS to GoalMacros(0, 0.25, 0.30),
    )

    var totalCalAdjust = 0
    var totalProteinRatio = 0.0
    var totalFatRatio = 0.0
    val count = goals.size.coerceAtLeast(1)

    for (goal in goals) {
        val g = goalMacros[goal] ?: goalMacros[FitnessGoal.GENERAL_FITNESS]!!
        totalCalAdjust += g.calAdjust
        totalProteinRatio += g.proteinRatio
        totalFatRatio += g.fatRatio
    }

    val calories = tdee + (totalCalAdjust / count)
    val proteinRatio = totalProteinRatio / count
    val fatRatio = totalFatRatio / count

    val protein = ((calories * proteinRatio) / 4).roundToInt()
    val fats = ((calories * fatRatio) / 9).roundToInt()
    val carbs = ((calories - protein * 4 - fats * 9).toDouble() / 4).roundToInt()

    return MacroNutrients(
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats
    )
}

fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun todayFormatted(): String = formatDate(LocalDate.now())

fun generateId(): String =
    UUID.randomUUID().toString().replace("-", "").take(20)

fun getProteinCalorieRatio(macros: MacroNutrients): Double {
    if (macros.calories == 0) return 0.0
    return (macros.protein.toDouble() / macros.calories) * 100
}

data class RatioRating(val label: String, val colorHex: Long)

fun getRatioRating(ratio: Double): RatioRating = when {
    ratio >= 10 -> RatioRating("Excellent", 0xFF10b981)
    ratio >= 5 -> RatioRating("Moderate", 0xFFf59e0b)
    else -> RatioRating("Poor", 0xFFef4444)
}

// ─── Unit Conversion ─────────────────────────────────────────────────────────

fun kgToLbs(kg: Double): Double = kg * 2.20462
fun lbsToKg(lbs: Double): Double = lbs / 2.20462
fun cmToInches(cm: Double): Double = cm / 2.54
fun inchesToCm(inches: Double): Double = inches * 2.54
fun mlToOz(ml: Double): Double = ml / 29.5735
fun ozToMl(oz: Double): Double = oz * 29.5735

fun formatWeight(kg: Double, unit: UnitSystem): String = when (unit) {
    UnitSystem.METRIC -> "${"%.1f".format(kg)} kg"
    UnitSystem.IMPERIAL -> "${"%.1f".format(kgToLbs(kg))} lbs"
}

fun formatHeight(cm: Double, unit: UnitSystem): String = when (unit) {
    UnitSystem.METRIC -> "${"%.0f".format(cm)} cm"
    UnitSystem.IMPERIAL -> {
        val totalInches = cmToInches(cm)
        val feet = (totalInches / 12).toInt()
        val inches = (totalInches % 12).roundToInt()
        "${feet}'${inches}\""
    }
}

fun formatWater(ml: Int, unit: UnitSystem): String = when (unit) {
    UnitSystem.METRIC -> "${ml} ml"
    UnitSystem.IMPERIAL -> "${"%.1f".format(mlToOz(ml.toDouble()))} oz"
}

// ─── Water Intake Recommendation ─────────────────────────────────────────────

/**
 * Calculates recommended daily water intake in ml based on body weight and activity level.
 * Base: 35ml per kg body weight, adjusted by activity multiplier (1.0–1.4).
 */
fun calculateDailyWaterIntakeMl(weightKg: Double, activityLevel: ActivityLevel): Int {
    val basePerKg = 35.0 // ml per kg
    val multiplier = when (activityLevel) {
        ActivityLevel.SEDENTARY -> 1.0
        ActivityLevel.LIGHTLY_ACTIVE -> 1.1
        ActivityLevel.MODERATELY_ACTIVE -> 1.2
        ActivityLevel.VERY_ACTIVE -> 1.3
        ActivityLevel.EXTREMELY_ACTIVE -> 1.4
    }
    return (weightKg * basePerKg * multiplier).roundToInt()
}

// ─── Cardio Calorie Estimation ───────────────────────────────────────────────

private val cardioMetValues = mapOf(
    "running" to 9.8,
    "jogging" to 7.0,
    "cycling" to 7.5,
    "swimming" to 6.0,
    "walking" to 3.5,
    "elliptical" to 5.0,
    "rowing" to 7.0,
    "jump rope" to 12.3,
    "hiit" to 8.0,
    "stair climbing" to 9.0,
    "dancing" to 5.5,
    "hiking" to 6.0,
    "boxing" to 7.8,
    "kickboxing" to 10.3
)

/**
 * Estimates calories burnt from a cardio session using MET (Metabolic Equivalent of Task).
 * Formula: calories = MET × weight(kg) × duration(hours)
 */
fun estimateCardioCalories(type: String, durationMinutes: Int, weightKg: Double): Int {
    val met = cardioMetValues[type.lowercase()] ?: 5.0
    return (met * weightKg * (durationMinutes / 60.0)).roundToInt()
}

/** Returns list of known cardio types for dropdown/selector */
fun getCardioTypes(): List<String> = listOf(
    "Running", "Jogging", "Cycling", "Swimming", "Walking",
    "Elliptical", "Rowing", "Jump Rope", "HIIT", "Stair Climbing",
    "Dancing", "Hiking", "Boxing", "Kickboxing"
)

// ─── Commitment Meter ────────────────────────────────────────────────────────

data class CommitmentLevel(val label: String, val description: String, val colorHex: Long)

fun getCommitmentLevel(gymDays: Int): CommitmentLevel = when (gymDays) {
    in 1..2 -> CommitmentLevel("Light", "Easy start", 0xFF3b82f6)
    3 -> CommitmentLevel("Moderate", "Balanced routine", 0xFF10b981)
    in 4..5 -> CommitmentLevel("Dedicated", "Serious commitment", 0xFFf59e0b)
    in 6..7 -> CommitmentLevel("Beast Mode", "Maximum effort", 0xFFef4444)
    else -> CommitmentLevel("None", "Set your gym days", 0xFF6b7280)
}
