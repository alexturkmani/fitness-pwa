package com.nexal.app.data.remote.dto

import kotlinx.serialization.Serializable

// ─── AI DTOs (used by AiRepository → Edge Functions) ─────────────────────────

@Serializable
data class MealSubstituteResponseDto(
    val substitutions: List<com.nexal.app.domain.model.MealSubstitution>
)

@Serializable
data class ExerciseInputDto(
    val name: String,
    val muscleGroup: String,
    val sets: List<ExerciseSetInputDto>
)

@Serializable
data class ExerciseSetInputDto(
    val weight: Double,
    val reps: Int
)

@Serializable
data class ExerciseSuggestionsResponseDto(
    val suggestions: List<com.nexal.app.domain.model.ExerciseSuggestion>
)
