package com.fitmate.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Auth DTOs ───────────────────────────────────────────────────────────────

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val name: String? = null,
    val email: String,
    val trialEndsAt: String? = null,
    val subscriptionActive: Boolean = false,
    val isFreeAccount: Boolean = false,
    val hasUsedTrial: Boolean = false
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponseDto(
    val id: String,
    val email: String,
    val requiresVerification: Boolean = true
)

@Serializable
data class GoogleSignInRequestDto(
    val idToken: String
)

@Serializable
data class ForgotPasswordRequestDto(
    val email: String
)

@Serializable
data class ResetPasswordRequestDto(
    val token: String,
    val password: String
)

@Serializable
data class ChangePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class ChangeEmailRequestDto(
    val newEmail: String
)

@Serializable
data class SubscriptionActionDto(
    val action: String // "activate" or "deactivate"
)

@Serializable
data class SuccessResponseDto(
    val success: Boolean = true
)

@Serializable
data class StartTrialResponseDto(
    val success: Boolean = true,
    val trialEndsAt: String? = null
)

// ─── AI DTOs ─────────────────────────────────────────────────────────────────

@Serializable
data class GenerateWorkoutRequestDto(
    val profile: com.fitmate.app.domain.model.UserProfile,
    val previousLogs: List<com.fitmate.app.domain.model.WorkoutLog>? = null,
    val assessment: String? = null,
    val currentInterval: Int = 1,
    val workoutStyle: String = "muscle_group"
)

@Serializable
data class GenerateMealRequestDto(
    val profile: com.fitmate.app.domain.model.UserProfile,
    val allergies: List<String>? = null
)

@Serializable
data class MealSubstituteRequestDto(
    val mealName: String,
    val foodName: String,
    val reason: String,
    val currentMacros: com.fitmate.app.domain.model.MacroNutrients
)

@Serializable
data class MealSubstituteResponseDto(
    val substitutions: List<com.fitmate.app.domain.model.MealSubstitution>
)

@Serializable
data class AssessWorkoutRequestDto(
    val type: String = "workout",
    val logs: List<com.fitmate.app.domain.model.WorkoutLog>
)

@Serializable
data class AssessFoodRequestDto(
    val type: String = "food",
    val productName: String,
    val macros: com.fitmate.app.domain.model.MacroNutrients,
    val ratio: Double
)

@Serializable
data class FoodLookupRequestDto(
    val foodName: String,
    val servingSize: String
)

@Serializable
data class ExerciseSuggestionsRequestDto(
    val exercises: List<ExerciseInputDto>,
    val goals: List<String>
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
    val suggestions: List<com.fitmate.app.domain.model.ExerciseSuggestion>
)

// ─── Data Sync DTOs ──────────────────────────────────────────────────────────

@Serializable
data class SyncProfileDto(
    val profile: com.fitmate.app.domain.model.UserProfile
)

@Serializable
data class SyncWorkoutPlansDto(
    val plans: List<com.fitmate.app.domain.model.WorkoutPlan>
)

@Serializable
data class SyncWorkoutLogsDto(
    val logs: List<com.fitmate.app.domain.model.WorkoutLog>
)

@Serializable
data class SyncCustomWorkoutLogsDto(
    val logs: List<com.fitmate.app.domain.model.CustomWorkoutLog>
)

@Serializable
data class SyncMealPlansDto(
    val plans: List<com.fitmate.app.domain.model.MealPlan>
)

@Serializable
data class SyncFoodLogsDto(
    val entries: List<com.fitmate.app.domain.model.FoodLogEntry>
)

@Serializable
data class SyncWeightEntriesDto(
    val entries: List<com.fitmate.app.domain.model.WeightEntry>
)

@Serializable
data class SyncWaterLogsDto(
    val entries: List<com.fitmate.app.domain.model.WaterLogEntry>
)

@Serializable
data class SyncCardioLogsDto(
    val entries: List<com.fitmate.app.domain.model.CardioLogEntry>
)
