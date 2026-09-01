package com.nexal.app.data.repository

import com.nexal.app.BuildConfig
import com.nexal.app.data.remote.dto.*
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
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
    private val supabase: SupabaseClient
) {
    /**
     * The gateway JWT check is disabled for ES256 compatibility, so each
     * Premium Edge Function validates this user token through Supabase Auth.
     */
    private suspend fun callFunction(name: String, bodyObj: JsonObject): String {
        val url = BuildConfig.SUPABASE_URL.trimEnd('/') + "/functions/v1/$name"
        val accessToken = supabase.auth.currentSessionOrNull()?.accessToken
            ?: throw IllegalStateException("Sign in again to use Premium features.")
        val response = supabase.httpClient.post(url) {
            header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(bodyObj)
        }
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) {
            val gatewayMsg = runCatching {
                json.parseToJsonElement(text).jsonObject["message"]?.jsonPrimitive?.content
                    ?: json.parseToJsonElement(text).jsonObject["error"]?.jsonPrimitive?.content
            }.getOrNull()
            throw IllegalStateException(
                gatewayMsg?.takeIf { it.isNotBlank() }
                    ?: "AI request failed (${response.status.value})"
            )
        }
        throwIfFunctionError(text)
        return text
    }

    private fun throwIfFunctionError(text: String) {
        val trimmed = text.trim()
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            throw IllegalStateException(trimmed.ifBlank { "Empty AI response" })
        }
        runCatching {
            val element = json.parseToJsonElement(trimmed)
            val obj = element as? JsonObject ?: return
            val error = obj["error"]?.jsonPrimitive?.contentOrNull
            if (!error.isNullOrBlank() && obj.keys.size <= 3 && !obj.containsKey("days") && !obj.containsKey("meals")) {
                throw IllegalStateException(error)
            }
        }
    }

    private fun UserProfile.toAiJson(): JsonObject = buildJsonObject {
        put("id", id)
        put("name", name)
        put("weight", weight)
        put("height", height)
        put("age", age)
        put("gender", gender.name.lowercase())
        put("activityLevel", activityLevel.name.lowercase())
        put("fitnessGoals", JsonArray(fitnessGoals.map { JsonPrimitive(it.name.lowercase()) }))
        put("targetWeight", targetWeight)
        put("intervalWeeks", intervalWeeks)
        put("gymDaysPerWeek", gymDaysPerWeek)
        put("workoutStyle", workoutStyle.name.lowercase())
        put("liftingExperience", liftingExperience.name.lowercase())
        put("trainingLocation", trainingLocation.name.lowercase())
        put("unitSystem", unitSystem.name.lowercase())
        put("allergies", JsonArray(allergies.map { JsonPrimitive(it) }))
        put("onboardingCompleted", onboardingCompleted)
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
                put("profile", profile.toAiJson())
                if (previousLogs != null) put("previousLogs", json.encodeToJsonElement(previousLogs))
                if (assessment != null) put("assessment", JsonPrimitive(assessment))
                put("currentInterval", JsonPrimitive(currentInterval))
                put("workoutStyle", JsonPrimitive(workoutStyle.name.lowercase()))
            }
            val text = callFunction("ai-workout", bodyObj)
            Resource.Success(json.decodeFromString<WorkoutPlan>(text))
        } catch (e: Exception) {
            Resource.Error(friendlyAiError(e))
        }
    }

    suspend fun generateMealPlan(
        profile: UserProfile,
        allergies: List<String>? = null
    ): Resource<MealPlan> {
        return try {
            val bodyObj = buildJsonObject {
                put("profile", profile.toAiJson())
                if (allergies != null) put("allergies", json.encodeToJsonElement(allergies))
            }
            val text = callFunction("ai-meal", bodyObj)
            Resource.Success(json.decodeFromString<MealPlan>(text))
        } catch (e: Exception) {
            Resource.Error(friendlyAiError(e))
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
            Resource.Error(friendlyAiError(e))
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
            Resource.Error(friendlyAiError(e))
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
            Resource.Error(friendlyAiError(e))
        }
    }

    suspend fun lookupBarcode(barcode: String): Resource<ScannedProduct> {
        return try {
            val bodyObj = buildJsonObject {
                put("barcode", JsonPrimitive(barcode))
            }
            // Live function currently reads query params; body support ships on next deploy.
            val encoded = java.net.URLEncoder.encode(barcode, Charsets.UTF_8.name())
            val text = callFunction("nutrition-lookup?barcode=$encoded", bodyObj)
            Resource.Success(json.decodeFromString<ScannedProduct>(text))
        } catch (e: Exception) {
            Resource.Error(friendlyAiError(e))
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
            Resource.Error(friendlyAiError(e))
        }
    }

    private fun friendlyAiError(e: Exception): String {
        val raw = e.message?.trim().orEmpty()
        val cause = e.cause?.message?.trim().orEmpty()
        val combined = listOf(raw, cause).filter { it.isNotBlank() }.joinToString(" — ")
        return when {
            combined.contains("UNSUPPORTED_TOKEN_ALGORITHM", ignoreCase = true) ||
                combined.contains("ES256", ignoreCase = true) ->
                "AI auth is misconfigured on the server. Please try again shortly."
            combined.contains("GEMINI_API_KEY", ignoreCase = true) ||
                combined.contains("not configured", ignoreCase = true) ->
                "AI is not configured on the server. Please try again later."
            combined.contains("timed out", ignoreCase = true) ||
                combined.contains("Timeout", ignoreCase = true) ->
                "AI took too long. Please try again."
            combined.contains("temporarily unavailable", ignoreCase = true) ->
                "AI is temporarily unavailable. Please try again in a minute."
            combined.contains("Unable to resolve host", ignoreCase = true) ||
                combined.contains("UnknownHost", ignoreCase = true) ->
                "Couldn't reach Nexal servers. Check your connection and try again."
            combined.isNotBlank() && combined.length < 220 && !combined.contains("JsonDecoding") ->
                combined
            else -> "Couldn't generate a plan. Check your connection and try again."
        }
    }
}
