package com.nexal.app.domain.model

import com.nexal.app.util.NullableRoundingIntSerializer
import com.nexal.app.util.RoundingIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class MacroNutrients(
    @Serializable(with = RoundingIntSerializer::class) val calories: Int = 0,
    @Serializable(with = RoundingIntSerializer::class) val protein: Int = 0,
    @Serializable(with = RoundingIntSerializer::class) val carbs: Int = 0,
    @Serializable(with = RoundingIntSerializer::class) val fats: Int = 0,
    @Serializable(with = NullableRoundingIntSerializer::class) val fiber: Int? = null
)

@Serializable
data class Exercise(
    val id: String = "",
    val name: String = "",
    val muscleGroup: String = "",
    val sets: Int = 0,
    val reps: String = "",
    val restSeconds: Int = 0,
    val notes: String? = null
)

@Serializable
data class WorkoutDay(
    val id: String = "",
    val dayNumber: Int = 0,
    val dayLabel: String = "",
    val isRestDay: Boolean = false,
    val exercises: List<Exercise> = emptyList()
)

@Serializable
data class WorkoutPlan(
    val id: String = "",
    val intervalNumber: Int = 1,
    val startDate: String = "",
    val endDate: String = "",
    val weeks: Int = 6,
    val days: List<WorkoutDay> = emptyList(),
    val aiNotes: String = "",
    val assessmentSummary: String? = null,
    val createdAt: String = ""
)

@Serializable
data class SetLog(
    val setNumber: Int = 0,
    val weight: Double = 0.0,
    val reps: Int = 0,
    val completed: Boolean = false
)

@Serializable
data class ExerciseLog(
    val exerciseId: String = "",
    val exerciseName: String = "",
    val sets: List<SetLog> = emptyList()
)

@Serializable
data class WorkoutLog(
    val id: String = "",
    val date: String = "",
    val planId: String = "",
    val dayId: String = "",
    val dayLabel: String = "",
    val exercises: List<ExerciseLog> = emptyList(),
    val duration: Int? = null,
    val notes: String? = null,
    val createdAt: String = ""
)
