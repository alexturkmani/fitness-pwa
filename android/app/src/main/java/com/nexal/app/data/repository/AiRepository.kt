package com.nexal.app.data.repository

import com.nexal.app.data.remote.api.AiApi
import com.nexal.app.data.remote.api.NutritionApi
import com.nexal.app.data.remote.dto.*
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val aiApi: AiApi,
    private val nutritionApi: NutritionApi
) {
    suspend fun generateWorkoutPlan(
        profile: UserProfile,
        previousLogs: List<WorkoutLog>? = null,
        assessment: String? = null,
        currentInterval: Int = 1,
        workoutStyle: WorkoutStyle = WorkoutStyle.MUSCLE_GROUP
    ): Resource<WorkoutPlan> {
        return try {
            val response = aiApi.generateWorkoutPlan(
                GenerateWorkoutRequestDto(
                    profile = profile,
                    previousLogs = previousLogs,
                    assessment = assessment,
                    currentInterval = currentInterval,
                    workoutStyle = workoutStyle.name.lowercase()
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to generate workout plan. Please try again.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun generateMealPlan(
        profile: UserProfile,
        allergies: List<String>? = null
    ): Resource<MealPlan> {
        return try {
            val response = aiApi.generateMealPlan(
                GenerateMealRequestDto(profile = profile, allergies = allergies)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to generate meal plan. Please try again.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getMealSubstitutions(
        mealName: String,
        foodName: String,
        reason: String,
        currentMacros: MacroNutrients
    ): Resource<List<MealSubstitution>> {
        return try {
            val response = aiApi.getMealSubstitutions(
                MealSubstituteRequestDto(mealName, foodName, reason, currentMacros)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.substitutions)
            } else {
                Resource.Error("Failed to get substitutions")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun lookupFoodMacros(
        foodName: String,
        servingSize: String
    ): Resource<MacroNutrients> {
        return try {
            val response = aiApi.lookupFoodMacros(
                FoodLookupRequestDto(foodName, servingSize)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to look up food")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getExerciseSuggestions(
        exercises: List<CustomExerciseLog>,
        goals: List<String>
    ): Resource<List<ExerciseSuggestion>> {
        return try {
            val response = aiApi.getExerciseSuggestions(
                ExerciseSuggestionsRequestDto(
                    exercises = exercises.map { ex ->
                        ExerciseInputDto(
                            name = ex.name,
                            muscleGroup = ex.muscleGroup,
                            sets = ex.sets.map { ExerciseSetInputDto(it.weight, it.reps) }
                        )
                    },
                    goals = goals
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.suggestions)
            } else {
                Resource.Error("Failed to get suggestions")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun lookupBarcode(barcode: String): Resource<ScannedProduct> {
        return try {
            val response = nutritionApi.lookupBarcode(barcode)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Product not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun assessFood(
        productName: String,
        macros: MacroNutrients,
        ratio: Double
    ): Resource<FoodAssessment> {
        return try {
            val response = aiApi.assessFood(
                AssessFoodRequestDto(productName = productName, macros = macros, ratio = ratio)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val assessment = body["assessment"]?.jsonPrimitive?.content ?: ""
                val alternatives = body["alternatives"]?.jsonArray?.map { elem ->
                    val obj = elem.jsonObject
                    FoodAlternative(
                        name = obj["name"]?.jsonPrimitive?.content ?: "",
                        reason = obj["reason"]?.jsonPrimitive?.content ?: ""
                    )
                } ?: emptyList()
                Resource.Success(FoodAssessment(assessment, alternatives))
            } else {
                Resource.Error("Failed to assess food")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
