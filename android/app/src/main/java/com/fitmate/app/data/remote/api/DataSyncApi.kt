package com.fitmate.app.data.remote.api

import com.fitmate.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DataSyncApi {

    // ─── Profile ─────────
    @GET("api/data/profile")
    suspend fun getProfile(): Response<SyncProfileDto>

    @POST("api/data/profile")
    suspend fun saveProfile(@Body body: SyncProfileDto): Response<Unit>

    // ─── Workout Plans ───
    @GET("api/data/workout-plans")
    suspend fun getWorkoutPlans(): Response<SyncWorkoutPlansDto>

    @POST("api/data/workout-plans")
    suspend fun saveWorkoutPlans(@Body body: SyncWorkoutPlansDto): Response<Unit>

    // ─── Workout Logs ────
    @GET("api/data/workout-logs")
    suspend fun getWorkoutLogs(): Response<SyncWorkoutLogsDto>

    @POST("api/data/workout-logs")
    suspend fun saveWorkoutLogs(@Body body: SyncWorkoutLogsDto): Response<Unit>

    // ─── Custom Workouts ─
    @GET("api/data/custom-workouts")
    suspend fun getCustomWorkouts(): Response<SyncCustomWorkoutLogsDto>

    @POST("api/data/custom-workouts")
    suspend fun saveCustomWorkouts(@Body body: SyncCustomWorkoutLogsDto): Response<Unit>

    // ─── Meal Plans ──────
    @GET("api/data/meal-plans")
    suspend fun getMealPlans(): Response<SyncMealPlansDto>

    @POST("api/data/meal-plans")
    suspend fun saveMealPlans(@Body body: SyncMealPlansDto): Response<Unit>

    // ─── Food Logs ───────
    @GET("api/data/food-logs")
    suspend fun getFoodLogs(): Response<SyncFoodLogsDto>

    @POST("api/data/food-logs")
    suspend fun saveFoodLogs(@Body body: SyncFoodLogsDto): Response<Unit>

    // ─── Weight Entries ──
    @GET("api/data/weight-entries")
    suspend fun getWeightEntries(): Response<SyncWeightEntriesDto>

    @POST("api/data/weight-entries")
    suspend fun saveWeightEntries(@Body body: SyncWeightEntriesDto): Response<Unit>

    // ─── Water Logs ──────
    @GET("api/data/water-logs")
    suspend fun getWaterLogs(): Response<SyncWaterLogsDto>

    @POST("api/data/water-logs")
    suspend fun saveWaterLogs(@Body body: SyncWaterLogsDto): Response<Unit>

    // ─── Cardio Logs ─────
    @GET("api/data/cardio-logs")
    suspend fun getCardioLogs(): Response<SyncCardioLogsDto>

    @POST("api/data/cardio-logs")
    suspend fun saveCardioLogs(@Body body: SyncCardioLogsDto): Response<Unit>
}
