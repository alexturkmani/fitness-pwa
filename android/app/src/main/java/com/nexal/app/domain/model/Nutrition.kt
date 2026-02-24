package com.nexal.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val servingSize: String = "",
    val macros: MacroNutrients = MacroNutrients()
)

@Serializable
data class Meal(
    val id: String = "",
    val name: String = "",
    val foods: List<FoodItem> = emptyList(),
    val totalMacros: MacroNutrients = MacroNutrients()
)

@Serializable
data class MealPlan(
    val id: String = "",
    val date: String = "",
    val meals: List<Meal> = emptyList(),
    val dailyTotals: MacroNutrients = MacroNutrients(),
    val dailyTargets: MacroNutrients = MacroNutrients(),
    val dailyWaterIntakeMl: Int? = null,
    val aiNotes: String = "",
    val createdAt: String = ""
)

@Serializable
data class FoodLogEntry(
    val id: String = "",
    val date: String = "",
    val foodName: String = "",
    val servingSize: String = "",
    val quantity: Int = 1,
    val macros: MacroNutrients = MacroNutrients(),
    val source: FoodSource = FoodSource.MANUAL,
    val barcode: String? = null,
    val createdAt: String = ""
)

@Serializable
enum class FoodSource { MANUAL, SCANNER, MEAL_PLAN }

@Serializable
data class ScannedProduct(
    val barcode: String = "",
    val name: String = "",
    val brand: String? = null,
    val servingSize: String = "",
    val macros: MacroNutrients = MacroNutrients(),
    val proteinCalorieRatio: Double = 0.0,
    val imageUrl: String? = null
)

@Serializable
data class WeightEntry(
    val date: String = "",
    val weight: Double = 0.0
)

@Serializable
data class WaterLogEntry(
    val id: String = "",
    val date: String = "",
    val amount: Int = 0, // ml
    val createdAt: String = ""
)

@Serializable
data class CardioLogEntry(
    val id: String = "",
    val date: String = "",
    val type: String = "",
    val durationMinutes: Int = 0,
    val estimatedCaloriesBurnt: Int = 0,
    val notes: String? = null,
    val createdAt: String = ""
)

@Serializable
data class CustomExerciseLog(
    val id: String = "",
    val name: String = "",
    val muscleGroup: String = "",
    val sets: List<CustomSet> = emptyList()
)

@Serializable
data class CustomSet(
    val weight: Double = 0.0,
    val reps: Int = 0
)

@Serializable
data class CustomWorkoutLog(
    val id: String = "",
    val date: String = "",
    val name: String = "",
    val exercises: List<CustomExerciseLog> = emptyList(),
    val duration: Int? = null,
    val notes: String? = null,
    val createdAt: String = ""
)

@Serializable
data class ExerciseSuggestion(
    val exerciseName: String = "",
    val assessment: String = "",
    val improvementTips: List<String> = emptyList(),
    val alternatives: List<ExerciseAlternative> = emptyList()
)

@Serializable
data class ExerciseAlternative(
    val name: String = "",
    val reason: String = ""
)

@Serializable
data class MealSubstitution(
    val name: String = "",
    val servingSize: String = "",
    val macros: MacroNutrients = MacroNutrients(),
    val reason: String = ""
)

@Serializable
data class FoodAssessment(
    val assessment: String = "",
    val alternatives: List<FoodAlternative> = emptyList()
)

@Serializable
data class FoodAlternative(
    val name: String = "",
    val reason: String = "",
    val macros: MacroNutrients? = null
)
