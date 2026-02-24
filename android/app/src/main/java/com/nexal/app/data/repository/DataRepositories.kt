package com.nexal.app.data.repository

import com.nexal.app.data.local.dao.*
import com.nexal.app.data.local.entity.*
import com.nexal.app.data.remote.api.DataSyncApi
import com.nexal.app.data.remote.dto.*
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}

@Singleton
class ProfileRepository @Inject constructor(
    private val dao: UserProfileDao,
    private val syncApi: DataSyncApi
) {
    fun observeProfile(): Flow<UserProfile?> = dao.observeProfile().map { it?.toDomain() }

    suspend fun getProfile(): UserProfile? = dao.getProfile()?.toDomain()

    suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
        // Background sync to server (best-effort)
        runCatching { syncApi.saveProfile(SyncProfileDto(profile)) }
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val response = syncApi.getProfile()
            if (response.isSuccessful && response.body() != null) {
                dao.upsert(response.body()!!.profile.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to sync profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync error")
        }
    }

    private fun UserProfile.toEntity() = UserProfileEntity(
        id = id,
        name = name,
        weight = weight,
        height = height,
        age = age,
        gender = gender.name,
        activityLevel = activityLevel.name,
        fitnessGoalsJson = json.encodeToString(fitnessGoals),
        targetWeight = targetWeight,
        intervalWeeks = intervalWeeks,
        gymDaysPerWeek = gymDaysPerWeek,
        workoutStyle = workoutStyle.name,
        allergiesJson = json.encodeToString(allergies),
        onboardingCompleted = onboardingCompleted,
        liftingExperience = liftingExperience.name,
        trainingLocation = trainingLocation.name,
        unitSystem = unitSystem.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id,
        name = name,
        weight = weight,
        height = height,
        age = age,
        gender = Gender.valueOf(gender),
        activityLevel = ActivityLevel.valueOf(activityLevel),
        fitnessGoals = json.decodeFromString(fitnessGoalsJson),
        targetWeight = targetWeight,
        intervalWeeks = intervalWeeks,
        gymDaysPerWeek = gymDaysPerWeek,
        workoutStyle = WorkoutStyle.valueOf(workoutStyle),
        allergies = json.decodeFromString(allergiesJson),
        onboardingCompleted = onboardingCompleted,
        liftingExperience = runCatching { LiftingExperience.valueOf(liftingExperience) }.getOrDefault(LiftingExperience.BEGINNER),
        trainingLocation = runCatching { TrainingLocation.valueOf(trainingLocation) }.getOrDefault(TrainingLocation.GYM),
        unitSystem = runCatching { UnitSystem.valueOf(unitSystem) }.getOrDefault(UnitSystem.METRIC),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

@Singleton
class WorkoutRepository @Inject constructor(
    private val planDao: WorkoutPlanDao,
    private val logDao: WorkoutLogDao,
    private val customDao: CustomWorkoutLogDao,
    private val syncApi: DataSyncApi
) {
    fun observePlans(): Flow<List<WorkoutPlan>> =
        planDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLogs(): Flow<List<WorkoutLog>> =
        logDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLogsByPlan(planId: String): Flow<List<WorkoutLog>> =
        logDao.observeByPlanId(planId).map { list -> list.map { it.toDomain() } }

    fun observeCustomWorkouts(): Flow<List<CustomWorkoutLog>> =
        customDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun savePlan(plan: WorkoutPlan) {
        planDao.upsert(plan.toEntity())
        runCatching {
            val all = planDao.getAll().map { it.toDomain() }
            syncApi.saveWorkoutPlans(SyncWorkoutPlansDto(all))
        }
    }

    suspend fun deletePlan(id: String) {
        planDao.deleteById(id)
        runCatching {
            val all = planDao.getAll().map { it.toDomain() }
            syncApi.saveWorkoutPlans(SyncWorkoutPlansDto(all))
        }
    }

    suspend fun saveLog(log: WorkoutLog) {
        logDao.upsert(log.toEntity())
        runCatching {
            val all = logDao.observeAll() // sync best-effort
        }
    }

    suspend fun getLogsByPlanAndDay(planId: String, dayId: String): List<WorkoutLog> =
        logDao.getByPlanAndDay(planId, dayId).map { it.toDomain() }

    suspend fun getLogsByDateRange(startDate: String, endDate: String): List<WorkoutLog> =
        logDao.getByDateRange(startDate, endDate).map { it.toDomain() }

    suspend fun saveCustomWorkout(log: CustomWorkoutLog) {
        customDao.upsert(log.toEntity())
    }

    suspend fun deleteCustomWorkout(id: String) {
        customDao.deleteById(id)
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val plansResp = syncApi.getWorkoutPlans()
            if (plansResp.isSuccessful && plansResp.body() != null) {
                planDao.deleteAll()
                planDao.insertAll(plansResp.body()!!.plans.map { it.toEntity() })
            }
            val logsResp = syncApi.getWorkoutLogs()
            if (logsResp.isSuccessful && logsResp.body() != null) {
                logDao.deleteAll()
                logDao.insertAll(logsResp.body()!!.logs.map { it.toEntity() })
            }
            val customResp = syncApi.getCustomWorkouts()
            if (customResp.isSuccessful && customResp.body() != null) {
                customDao.deleteAll()
                customDao.insertAll(customResp.body()!!.logs.map { it.toEntity() })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync error")
        }
    }

    // Entity ↔ Domain mappers
    private fun WorkoutPlan.toEntity() = WorkoutPlanEntity(
        id = id, intervalNumber = intervalNumber, startDate = startDate,
        endDate = endDate, weeks = weeks, daysJson = json.encodeToString(days),
        aiNotes = aiNotes, assessmentSummary = assessmentSummary, createdAt = createdAt
    )
    private fun WorkoutPlanEntity.toDomain() = WorkoutPlan(
        id = id, intervalNumber = intervalNumber, startDate = startDate,
        endDate = endDate, weeks = weeks, days = json.decodeFromString(daysJson),
        aiNotes = aiNotes, assessmentSummary = assessmentSummary, createdAt = createdAt
    )
    private fun WorkoutLog.toEntity() = WorkoutLogEntity(
        id = id, date = date, planId = planId, dayId = dayId,
        dayLabel = dayLabel,
        exercisesJson = json.encodeToString(exercises), duration = duration,
        notes = notes, createdAt = createdAt
    )
    private fun WorkoutLogEntity.toDomain() = WorkoutLog(
        id = id, date = date, planId = planId, dayId = dayId,
        dayLabel = dayLabel,
        exercises = json.decodeFromString(exercisesJson), duration = duration,
        notes = notes, createdAt = createdAt
    )
    private fun CustomWorkoutLog.toEntity() = CustomWorkoutLogEntity(
        id = id, date = date, name = name,
        exercisesJson = json.encodeToString(exercises), duration = duration,
        notes = notes, createdAt = createdAt
    )
    private fun CustomWorkoutLogEntity.toDomain() = CustomWorkoutLog(
        id = id, date = date, name = name,
        exercises = json.decodeFromString(exercisesJson), duration = duration,
        notes = notes, createdAt = createdAt
    )
}

@Singleton
class NutritionRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val foodLogDao: FoodLogDao,
    private val weightDao: WeightEntryDao,
    private val waterLogDao: WaterLogDao,
    private val cardioLogDao: CardioLogDao,
    private val syncApi: DataSyncApi
) {
    fun observeMealPlans(): Flow<List<MealPlan>> =
        mealPlanDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLatestMealPlan(): Flow<MealPlan?> =
        mealPlanDao.observeLatest().map { it?.toDomain() }

    fun observeFoodLogByDate(date: String): Flow<List<FoodLogEntry>> =
        foodLogDao.observeByDate(date).map { list -> list.map { it.toDomain() } }

    fun observeFoodLogByDateRange(start: String, end: String): Flow<List<FoodLogEntry>> =
        foodLogDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }

    fun observeWeightEntries(): Flow<List<WeightEntry>> =
        weightDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeRecentWeightEntries(limit: Int): Flow<List<WeightEntry>> =
        weightDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    suspend fun saveMealPlan(plan: MealPlan) {
        mealPlanDao.upsert(plan.toEntity())
    }

    suspend fun deleteMealPlan(id: String) {
        mealPlanDao.deleteById(id)
    }

    suspend fun addFoodLogEntry(entry: FoodLogEntry) {
        foodLogDao.upsert(entry.toEntity())
    }

    suspend fun deleteFoodLogEntry(id: String) {
        foodLogDao.deleteById(id)
    }

    suspend fun logWeight(entry: WeightEntry) {
        weightDao.upsert(WeightEntryEntity(date = entry.date, weight = entry.weight))
    }

    // ─── Water Logs ──────────────────────────────────────────────────────────────
    fun observeWaterLogByDate(date: String): Flow<List<WaterLogEntry>> =
        waterLogDao.observeByDate(date).map { list -> list.map { it.toDomain() } }

    fun observeWaterLogByDateRange(start: String, end: String): Flow<List<WaterLogEntry>> =
        waterLogDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }

    suspend fun addWaterLogEntry(entry: WaterLogEntry) {
        waterLogDao.upsert(entry.toEntity())
    }

    suspend fun deleteWaterLogEntry(id: String) {
        waterLogDao.deleteById(id)
    }

    // ─── Cardio Logs ─────────────────────────────────────────────────────────────
    fun observeCardioLogByDate(date: String): Flow<List<CardioLogEntry>> =
        cardioLogDao.observeByDate(date).map { list -> list.map { it.toDomain() } }

    fun observeCardioLogByDateRange(start: String, end: String): Flow<List<CardioLogEntry>> =
        cardioLogDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }

    suspend fun addCardioLogEntry(entry: CardioLogEntry) {
        cardioLogDao.upsert(entry.toEntity())
    }

    suspend fun deleteCardioLogEntry(id: String) {
        cardioLogDao.deleteById(id)
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val mealResp = syncApi.getMealPlans()
            if (mealResp.isSuccessful && mealResp.body() != null) {
                mealPlanDao.deleteAll()
                mealPlanDao.insertAll(mealResp.body()!!.plans.map { it.toEntity() })
            }
            val foodResp = syncApi.getFoodLogs()
            if (foodResp.isSuccessful && foodResp.body() != null) {
                foodLogDao.deleteAll()
                foodLogDao.insertAll(foodResp.body()!!.entries.map { it.toEntity() })
            }
            val weightResp = syncApi.getWeightEntries()
            if (weightResp.isSuccessful && weightResp.body() != null) {
                weightDao.deleteAll()
                weightDao.insertAll(weightResp.body()!!.entries.map {
                    WeightEntryEntity(date = it.date, weight = it.weight)
                })
            }
            val waterResp = syncApi.getWaterLogs()
            if (waterResp.isSuccessful && waterResp.body() != null) {
                waterLogDao.deleteAll()
                waterLogDao.insertAll(waterResp.body()!!.entries.map { it.toEntity() })
            }
            val cardioResp = syncApi.getCardioLogs()
            if (cardioResp.isSuccessful && cardioResp.body() != null) {
                cardioLogDao.deleteAll()
                cardioLogDao.insertAll(cardioResp.body()!!.entries.map { it.toEntity() })
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync error")
        }
    }

    // Entity ↔ Domain mappers
    private fun MealPlan.toEntity() = MealPlanEntity(
        id = id, date = date, mealsJson = json.encodeToString(meals),
        dailyTotalsJson = json.encodeToString(dailyTotals),
        dailyTargetsJson = json.encodeToString(dailyTargets),
        aiNotes = aiNotes, dailyWaterIntakeMl = dailyWaterIntakeMl, createdAt = createdAt
    )
    private fun MealPlanEntity.toDomain() = MealPlan(
        id = id, date = date, meals = json.decodeFromString(mealsJson),
        dailyTotals = json.decodeFromString(dailyTotalsJson),
        dailyTargets = json.decodeFromString(dailyTargetsJson),
        aiNotes = aiNotes, dailyWaterIntakeMl = dailyWaterIntakeMl, createdAt = createdAt
    )
    private fun FoodLogEntry.toEntity() = FoodLogEntryEntity(
        id = id, date = date, foodName = foodName, servingSize = servingSize,
        quantity = quantity, macrosJson = json.encodeToString(macros),
        source = source.name, barcode = barcode, createdAt = createdAt
    )
    private fun FoodLogEntryEntity.toDomain() = FoodLogEntry(
        id = id, date = date, foodName = foodName, servingSize = servingSize,
        quantity = quantity, macros = json.decodeFromString(macrosJson),
        source = FoodSource.valueOf(source), barcode = barcode, createdAt = createdAt
    )
    private fun WeightEntryEntity.toDomain() = WeightEntry(date = date, weight = weight)

    // Water / Cardio mappers
    private fun WaterLogEntry.toEntity() = WaterLogEntryEntity(
        id = id, date = date, amount = amount, createdAt = createdAt
    )
    private fun WaterLogEntryEntity.toDomain() = WaterLogEntry(
        id = id, date = date, amount = amount, createdAt = createdAt
    )
    private fun CardioLogEntry.toEntity() = CardioLogEntryEntity(
        id = id, date = date, type = type, durationMinutes = durationMinutes,
        estimatedCaloriesBurnt = estimatedCaloriesBurnt, notes = notes, createdAt = createdAt
    )
    private fun CardioLogEntryEntity.toDomain() = CardioLogEntry(
        id = id, date = date, type = type, durationMinutes = durationMinutes,
        estimatedCaloriesBurnt = estimatedCaloriesBurnt, notes = notes, createdAt = createdAt
    )
}
