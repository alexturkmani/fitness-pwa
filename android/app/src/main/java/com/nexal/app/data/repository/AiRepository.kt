package com.nexal.app.data.repository

import com.nexal.app.data.remote.dto.*
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import io.github.jan.supabase.functions.Functions
import io.ktor.client.call.body
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}

@Singleton
class AiRepository @Inject constructor(
    private val functions: Functions
) {
    private suspend fun callFunction(name: String, bodyObj: JsonObject): String {
        val response = functions.invoke(name, body = bodyObj)
        return response.body<String>()
    }

    suspend fun generateWorkoutPlan(
        profile: UserProfile,
        previousLogs: List<WorkoutLog>? = null,
        assessment: String? = null,
        currentInterval: Int = 1,
        workoutStyle: WorkoutStyle = WorkoutStyle.MUSCLE_GROUP
    ): Resource<WorkoutPlan> {
        return try {
            val bodyObj = buildJsonObject {
                put("profile", json.encodeToJsonElement(profile))
                if (previousLogs != null) put("previousLogs", json.encodeToJsonElement(previousLogs))
                if (assessment != null) put("assessment", JsonPrimitive(assessment))
                put("currentInterval", JsonPrimitive(currentInterval))
                put("workoutStyle", JsonPrimitive(workoutStyle.name.lowercase()))
            }
            val text = callFunction("ai-workout", bodyObj)
            Resource.Success(json.decodeFromString<WorkoutPlan>(text))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun generateMealPlan(
        profile: UserProfile,
        allergies: List<String>? = null
    ): Resource<MealPlan> {
        return try {
            val bodyObj = buildJsonObject {
                put("profile", json.encodeToJsonElement(profile))
                if (allergies != null) put("allergies", json.encodeToJsonElement(allergies))
            }
            val text = callFunction("ai-meal", bodyObj)
            Resource.Success(json.decodeFromString<MealPlan>(text))
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
            val bodyObj = buildJsonObject {
                put("mealName", JsonPrimitive(mealName))
                put("foodName", JsonPrimitive(foodName))
                put("reason", JsonPrimitive(reason))
                put("currentMacros", json.encodeToJsonElement(currentMacros))
            }
            val text = callFunction("ai-meal-substitute", bodyObj)
            val result = json.decodeFromString<MealSubstituteResponseDto>(text)
            Resource.Success(result.substitutions)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun lookupFoodMacros(
        foodName: String,
        servingSize: String
    ): Resource<MacroNutrients> {
        return try {
            val bodyObj = buildJsonObject {
                put("foodName", JsonPrimitive(foodName))
                put("servingSize", JsonPrimitive(servingSize))
            }
            val text = callFunction("ai-food-lookup", bodyObj)
            Resource.Success(json.decodeFromString<MacroNutrients>(text))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getExerciseSuggestions(
        exercises: List<CustomExerciseLog>,
        goals: List<String>
    ): Resource<List<ExerciseSuggestion>> {
        return try {
            val bodyObj = buildJsonObject {
                put("exercises", json.encodeToJsonElement(
                    exercises.map { ex ->
                        ExerciseInputDto(
                            name = ex.name,
                            muscleGroup = ex.muscleGroup,
                            sets = ex.sets.map { ExerciseSetInputDto(it.weight, it.reps) }
                        )
                    }
                ))
                put("goals", json.encodeToJsonElement(goals))
            }
            val text = callFunction("ai-exercise-suggestions", bodyObj)
            val result = json.decodeFromString<ExerciseSuggestionsResponseDto>(text)
            Resource.Success(result.suggestions)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun lookupBarcode(barcode: String): Resource<ScannedProduct> {
        return try {
            val bodyObj = buildJsonObject {
                put("barcode", JsonPrimitive(barcode))
            }
            val text = callFunction("nutrition-lookup", bodyObj)
            Resource.Success(json.decodeFromString<ScannedProduct>(text))
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
            val bodyObj = buildJsonObject {
                put("type", JsonPrimitive("food"))
                put("productName", JsonPrimitive(productName))
                put("macros", json.encodeToJsonElement(macros))
                put("ratio", JsonPrimitive(ratio))
            }
            val text = callFunction("ai-assess", bodyObj)
            val body = json.parseToJsonElement(text).jsonObject
            val assessment = body["assessment"]?.jsonPrimitive?.content ?: ""
            val alternatives = body["alternatives"]?.jsonArray?.map { elem ->
                val obj = elem.jsonObject
                FoodAlternative(
                    name = obj["name"]?.jsonPrimitive?.content ?: "",
                    reason = obj["reason"]?.jsonPrimitive?.content ?: ""
                )
            } ?: emptyList()
            Resource.Success(FoodAssessment(assessment, alternatives))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
