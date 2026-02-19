package com.fitmate.app.data.remote.api

import com.fitmate.app.data.remote.dto.*
import com.fitmate.app.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface AiApi {

    @POST("api/ai/workout")
    suspend fun generateWorkoutPlan(
        @Body request: GenerateWorkoutRequestDto
    ): Response<WorkoutPlan>

    @POST("api/ai/meal")
    suspend fun generateMealPlan(
        @Body request: GenerateMealRequestDto
    ): Response<MealPlan>

    @POST("api/ai/meal-substitute")
    suspend fun getMealSubstitutions(
        @Body request: MealSubstituteRequestDto
    ): Response<MealSubstituteResponseDto>

    @POST("api/ai/assess")
    suspend fun assessWorkout(
        @Body request: AssessWorkoutRequestDto
    ): Response<Map<String, kotlinx.serialization.json.JsonElement>>

    @POST("api/ai/assess")
    suspend fun assessFood(
        @Body request: AssessFoodRequestDto
    ): Response<Map<String, kotlinx.serialization.json.JsonElement>>

    @POST("api/ai/food-lookup")
    suspend fun lookupFoodMacros(
        @Body request: FoodLookupRequestDto
    ): Response<MacroNutrients>

    @POST("api/ai/exercise-suggestions")
    suspend fun getExerciseSuggestions(
        @Body request: ExerciseSuggestionsRequestDto
    ): Response<ExerciseSuggestionsResponseDto>
}
