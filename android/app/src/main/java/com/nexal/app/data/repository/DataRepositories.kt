package com.nexal.app.data.repository

import com.nexal.app.data.local.dao.*
import com.nexal.app.data.local.entity.*
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject
import javax.inject.Singleton
import io.github.jan.supabase.auth.Auth

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    coerceInputValues = true
}

// ─── Supabase row models ─────────────────────────────────────────────────────

@Serializable data class ProfileRow(
    val id: String? = null, val user_id: String, val name: String = "", val weight: Double = 0.0,
    val height: Double = 0.0, val age: Int = 0, val gender: String = "male",
    val activity_level: String = "moderately_active", val fitness_goals: JsonElement? = null,
    val target_weight: Double = 0.0, val interval_weeks: Int = 6, val gym_days_per_week: Int = 5,
    val workout_style: String = "muscle_group", val lifting_experience: String = "beginner",
    val training_location: String = "gym", val unit_system: String = "metric",
    val allergies: JsonElement? = null, val onboarding_done: Boolean = false
)

@Serializable data class WorkoutPlanRow(
    val id: String, val user_id: String, val interval_number: Int = 1,
    val start_date: String, val end_date: String, val weeks: Int = 6,
    val days: JsonElement, val ai_notes: String? = null, val assessment_summary: String? = null
)

@Serializable data class WorkoutLogRow(
    val id: String, val user_id: String, val date: String, val plan_id: String? = null,
    val day_id: String? = null, val day_label: String? = null, val exercises: JsonElement,
    val duration: Int? = null, val notes: String? = null
)

@Serializable data class CustomWorkoutLogRow(
    val id: String, val user_id: String, val name: String, val date: String,
    val exercises: JsonElement, val duration: Int? = null, val notes: String? = null
)

@Serializable data class MealPlanRow(
    val id: String, val user_id: String, val date: String, val meals: JsonElement,
    val daily_totals: JsonElement, val daily_targets: JsonElement? = null,
    val daily_water_intake_ml: Int? = null, val ai_notes: String? = null
)

@Serializable data class FoodLogRow(
    val id: String, val user_id: String, val date: String, val food_name: String,
    val serving_size: String = "1 serving", val quantity: Int = 1,
    val macros: JsonElement, val source: String = "manual", val barcode: String? = null
)

@Serializable data class WeightEntryRow(
    val id: String? = null, val user_id: String, val date: String, val weight: Double
)

@Serializable data class WaterLogRow(
    val id: String, val user_id: String, val date: String, val amount: Int
)

@Serializable data class CardioLogRow(
    val id: String, val user_id: String, val date: String, val type: String,
    val duration_minutes: Int, val estimated_calories_burnt: Int = 0, val notes: String? = null
)

// ─── Profile Repository ──────────────────────────────────────────────────────

@Singleton
class ProfileRepository @Inject constructor(
    private val dao: UserProfileDao,
    private val postgrest: Postgrest,
    private val auth: Auth
) {
    private fun userId() = auth.currentUserOrNull()?.id ?: ""

    fun observeProfile(): Flow<UserProfile?> = dao.observeProfile().map { it?.toDomain() }
    suspend fun getProfile(): UserProfile? = dao.getProfile()?.toDomain()

    suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("user_profiles").upsert(ProfileRow(
                    id = profile.id, user_id = uid, name = profile.name,
                    weight = profile.weight.toDouble(), height = profile.height.toDouble(),
                    age = profile.age, gender = profile.gender.name,
                    activity_level = profile.activityLevel.name,
                    fitness_goals = json.parseToJsonElement(json.encodeToString(profile.fitnessGoals)),
                    target_weight = profile.targetWeight.toDouble(),
                    interval_weeks = profile.intervalWeeks, gym_days_per_week = profile.gymDaysPerWeek,
                    workout_style = profile.workoutStyle.name,
                    lifting_experience = profile.liftingExperience.name,
                    training_location = profile.trainingLocation.name,
                    unit_system = profile.unitSystem.name,
                    allergies = json.parseToJsonElement(json.encodeToString(profile.allergies)),
                    onboarding_done = profile.onboardingCompleted
                ))
            }
        }
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val uid = userId()
            if (uid.isBlank()) return Resource.Error("Not authenticated")
            val row = postgrest.from("user_profiles")
                .select { filter { eq("user_id", uid) } }
                .decodeSingleOrNull<ProfileRow>() ?: return Resource.Success(Unit)
            dao.upsert(rowToEntity(row))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync error")
        }
    }

    // Entity ↔ Domain mappers (keep existing)
    private fun UserProfile.toEntity() = UserProfileEntity(
        id = id, name = name, weight = weight, height = height, age = age,
        gender = gender.name, activityLevel = activityLevel.name,
        fitnessGoalsJson = json.encodeToString(fitnessGoals),
        targetWeight = targetWeight, intervalWeeks = intervalWeeks,
        gymDaysPerWeek = gymDaysPerWeek, workoutStyle = workoutStyle.name,
        allergiesJson = json.encodeToString(allergies),
        onboardingCompleted = onboardingCompleted,
        liftingExperience = liftingExperience.name,
        trainingLocation = trainingLocation.name,
        unitSystem = unitSystem.name, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id, name = name, weight = weight, height = height, age = age,
        gender = Gender.valueOf(gender), activityLevel = ActivityLevel.valueOf(activityLevel),
        fitnessGoals = json.decodeFromString(fitnessGoalsJson),
        targetWeight = targetWeight, intervalWeeks = intervalWeeks,
        gymDaysPerWeek = gymDaysPerWeek, workoutStyle = WorkoutStyle.valueOf(workoutStyle),
        allergies = json.decodeFromString(allergiesJson),
        onboardingCompleted = onboardingCompleted,
        liftingExperience = runCatching { LiftingExperience.valueOf(liftingExperience) }.getOrDefault(LiftingExperience.BEGINNER),
        trainingLocation = runCatching { TrainingLocation.valueOf(trainingLocation) }.getOrDefault(TrainingLocation.GYM),
        unitSystem = runCatching { UnitSystem.valueOf(unitSystem) }.getOrDefault(UnitSystem.METRIC),
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun rowToEntity(row: ProfileRow) = UserProfileEntity(
        id = row.id ?: "", name = row.name, weight = row.weight,
        height = row.height, age = row.age, gender = row.gender,
        activityLevel = row.activity_level,
        fitnessGoalsJson = row.fitness_goals?.toString() ?: "[]",
        targetWeight = row.target_weight,
        intervalWeeks = row.interval_weeks, gymDaysPerWeek = row.gym_days_per_week,
        workoutStyle = row.workout_style,
        allergiesJson = row.allergies?.toString() ?: "[]",
        onboardingCompleted = row.onboarding_done,
        liftingExperience = row.lifting_experience,
        trainingLocation = row.training_location,
        unitSystem = row.unit_system,
        createdAt = "", updatedAt = ""
    )
}

// ─── Workout Repository ──────────────────────────────────────────────────────

@Singleton
class WorkoutRepository @Inject constructor(
    private val planDao: WorkoutPlanDao,
    private val logDao: WorkoutLogDao,
    private val customDao: CustomWorkoutLogDao,
    private val postgrest: Postgrest,
    private val auth: Auth
) {
    private fun userId() = auth.currentUserOrNull()?.id ?: ""

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
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("workout_plans").upsert(WorkoutPlanRow(
                    id = plan.id, user_id = uid, interval_number = plan.intervalNumber,
                    start_date = plan.startDate, end_date = plan.endDate, weeks = plan.weeks,
                    days = json.parseToJsonElement(json.encodeToString(plan.days)),
                    ai_notes = plan.aiNotes, assessment_summary = plan.assessmentSummary
                ))
            }
        }
    }

    suspend fun deletePlan(id: String) {
        planDao.deleteById(id)
        runCatching { postgrest.from("workout_plans").delete { filter { eq("id", id) } } }
    }

    suspend fun saveLog(log: WorkoutLog) {
        logDao.upsert(log.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("workout_logs").upsert(WorkoutLogRow(
                    id = log.id, user_id = uid, date = log.date, plan_id = log.planId,
                    day_id = log.dayId, day_label = log.dayLabel,
                    exercises = json.parseToJsonElement(json.encodeToString(log.exercises)),
                    duration = log.duration, notes = log.notes
                ))
            }
        }
    }

    suspend fun getLogsByPlanAndDay(planId: String, dayId: String): List<WorkoutLog> =
        logDao.getByPlanAndDay(planId, dayId).map { it.toDomain() }
    suspend fun getLogsByDateRange(startDate: String, endDate: String): List<WorkoutLog> =
        logDao.getByDateRange(startDate, endDate).map { it.toDomain() }

    suspend fun saveCustomWorkout(log: CustomWorkoutLog) {
        customDao.upsert(log.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("custom_workout_logs").upsert(CustomWorkoutLogRow(
                    id = log.id, user_id = uid, name = log.name, date = log.date,
                    exercises = json.parseToJsonElement(json.encodeToString(log.exercises)),
                    duration = log.duration, notes = log.notes
                ))
            }
        }
    }

    suspend fun deleteCustomWorkout(id: String) {
        customDao.deleteById(id)
        runCatching { postgrest.from("custom_workout_logs").delete { filter { eq("id", id) } } }
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val uid = userId()
            if (uid.isBlank()) return Resource.Error("Not authenticated")

            val plans = postgrest.from("workout_plans").select { filter { eq("user_id", uid) } }.decodeList<WorkoutPlanRow>()
            planDao.deleteAll()
            planDao.insertAll(plans.map { r -> WorkoutPlanEntity(
                id = r.id, intervalNumber = r.interval_number, startDate = r.start_date,
                endDate = r.end_date, weeks = r.weeks, daysJson = r.days.toString(),
                aiNotes = r.ai_notes ?: "", assessmentSummary = r.assessment_summary,
                createdAt = ""
            )})

            val logs = postgrest.from("workout_logs").select { filter { eq("user_id", uid) } }.decodeList<WorkoutLogRow>()
            logDao.deleteAll()
            logDao.insertAll(logs.map { r -> WorkoutLogEntity(
                id = r.id, date = r.date, planId = r.plan_id ?: "", dayId = r.day_id ?: "",
                dayLabel = r.day_label ?: "", exercisesJson = r.exercises.toString(),
                duration = r.duration, notes = r.notes, createdAt = ""
            )})

            val customs = postgrest.from("custom_workout_logs").select { filter { eq("user_id", uid) } }.decodeList<CustomWorkoutLogRow>()
            customDao.deleteAll()
            customDao.insertAll(customs.map { r -> CustomWorkoutLogEntity(
                id = r.id, date = r.date, name = r.name, exercisesJson = r.exercises.toString(),
                duration = r.duration, notes = r.notes, createdAt = ""
            )})

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
        id = id, date = date, planId = planId, dayId = dayId, dayLabel = dayLabel,
        exercisesJson = json.encodeToString(exercises), duration = duration,
        notes = notes, createdAt = createdAt
    )
    private fun WorkoutLogEntity.toDomain() = WorkoutLog(
        id = id, date = date, planId = planId, dayId = dayId, dayLabel = dayLabel,
        exercises = json.decodeFromString(exercisesJson), duration = duration,
        notes = notes, createdAt = createdAt
    )
    private fun CustomWorkoutLog.toEntity() = CustomWorkoutLogEntity(
        id = id, date = date, name = name, exercisesJson = json.encodeToString(exercises),
        duration = duration, notes = notes, createdAt = createdAt
    )
    private fun CustomWorkoutLogEntity.toDomain() = CustomWorkoutLog(
        id = id, date = date, name = name, exercises = json.decodeFromString(exercisesJson),
        duration = duration, notes = notes, createdAt = createdAt
    )
}

// ─── Nutrition Repository ────────────────────────────────────────────────────

@Singleton
class NutritionRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao,
    private val foodLogDao: FoodLogDao,
    private val weightDao: WeightEntryDao,
    private val waterLogDao: WaterLogDao,
    private val cardioLogDao: CardioLogDao,
    private val postgrest: Postgrest,
    private val auth: Auth
) {
    private fun userId() = auth.currentUserOrNull()?.id ?: ""

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
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("meal_plans").upsert(MealPlanRow(
                    id = plan.id, user_id = uid, date = plan.date,
                    meals = json.parseToJsonElement(json.encodeToString(plan.meals)),
                    daily_totals = json.parseToJsonElement(json.encodeToString(plan.dailyTotals)),
                    daily_targets = json.parseToJsonElement(json.encodeToString(plan.dailyTargets)),
                    daily_water_intake_ml = plan.dailyWaterIntakeMl, ai_notes = plan.aiNotes
                ))
            }
        }
    }

    suspend fun deleteMealPlan(id: String) {
        mealPlanDao.deleteById(id)
        runCatching { postgrest.from("meal_plans").delete { filter { eq("id", id) } } }
    }

    suspend fun addFoodLogEntry(entry: FoodLogEntry) {
        foodLogDao.upsert(entry.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("food_log_entries").upsert(FoodLogRow(
                    id = entry.id, user_id = uid, date = entry.date, food_name = entry.foodName,
                    serving_size = entry.servingSize, quantity = entry.quantity,
                    macros = json.parseToJsonElement(json.encodeToString(entry.macros)),
                    source = entry.source.name, barcode = entry.barcode
                ))
            }
        }
    }

    suspend fun deleteFoodLogEntry(id: String) {
        foodLogDao.deleteById(id)
        runCatching { postgrest.from("food_log_entries").delete { filter { eq("id", id) } } }
    }

    suspend fun logWeight(entry: WeightEntry) {
        weightDao.upsert(WeightEntryEntity(date = entry.date, weight = entry.weight))
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("weight_entries").upsert(WeightEntryRow(
                    user_id = uid, date = entry.date, weight = entry.weight.toDouble()
                ))
            }
        }
    }

    // ─── Water Logs ──────────────────────────────────────────────────────────────
    fun observeWaterLogByDate(date: String): Flow<List<WaterLogEntry>> =
        waterLogDao.observeByDate(date).map { list -> list.map { it.toDomain() } }
    fun observeWaterLogByDateRange(start: String, end: String): Flow<List<WaterLogEntry>> =
        waterLogDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }

    suspend fun addWaterLogEntry(entry: WaterLogEntry) {
        waterLogDao.upsert(entry.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("water_log_entries").upsert(WaterLogRow(
                    id = entry.id, user_id = uid, date = entry.date, amount = entry.amount
                ))
            }
        }
    }

    suspend fun deleteWaterLogEntry(id: String) {
        waterLogDao.deleteById(id)
        runCatching { postgrest.from("water_log_entries").delete { filter { eq("id", id) } } }
    }

    // ─── Cardio Logs ─────────────────────────────────────────────────────────────
    fun observeCardioLogByDate(date: String): Flow<List<CardioLogEntry>> =
        cardioLogDao.observeByDate(date).map { list -> list.map { it.toDomain() } }
    fun observeCardioLogByDateRange(start: String, end: String): Flow<List<CardioLogEntry>> =
        cardioLogDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }

    suspend fun addCardioLogEntry(entry: CardioLogEntry) {
        cardioLogDao.upsert(entry.toEntity())
        runCatching {
            val uid = userId()
            if (uid.isNotBlank()) {
                postgrest.from("cardio_log_entries").upsert(CardioLogRow(
                    id = entry.id, user_id = uid, date = entry.date, type = entry.type,
                    duration_minutes = entry.durationMinutes,
                    estimated_calories_burnt = entry.estimatedCaloriesBurnt, notes = entry.notes
                ))
            }
        }
    }

    suspend fun deleteCardioLogEntry(id: String) {
        cardioLogDao.deleteById(id)
        runCatching { postgrest.from("cardio_log_entries").delete { filter { eq("id", id) } } }
    }

    suspend fun syncFromServer(): Resource<Unit> {
        return try {
            val uid = userId()
            if (uid.isBlank()) return Resource.Error("Not authenticated")

            val meals = postgrest.from("meal_plans").select { filter { eq("user_id", uid) } }.decodeList<MealPlanRow>()
            mealPlanDao.deleteAll()
            mealPlanDao.insertAll(meals.map { r -> MealPlanEntity(
                id = r.id, date = r.date, mealsJson = r.meals.toString(),
                dailyTotalsJson = r.daily_totals.toString(),
                dailyTargetsJson = r.daily_targets?.toString() ?: "{}",
                dailyWaterIntakeMl = r.daily_water_intake_ml, aiNotes = r.ai_notes ?: "",
                createdAt = ""
            )})

            val foods = postgrest.from("food_log_entries").select { filter { eq("user_id", uid) } }.decodeList<FoodLogRow>()
            foodLogDao.deleteAll()
            foodLogDao.insertAll(foods.map { r -> FoodLogEntryEntity(
                id = r.id, date = r.date, foodName = r.food_name, servingSize = r.serving_size,
                quantity = r.quantity, macrosJson = r.macros.toString(),
                source = r.source, barcode = r.barcode, createdAt = ""
            )})

            val weights = postgrest.from("weight_entries").select { filter { eq("user_id", uid) } }.decodeList<WeightEntryRow>()
            weightDao.deleteAll()
            weightDao.insertAll(weights.map { WeightEntryEntity(date = it.date, weight = it.weight) })

            val water = postgrest.from("water_log_entries").select { filter { eq("user_id", uid) } }.decodeList<WaterLogRow>()
            waterLogDao.deleteAll()
            waterLogDao.insertAll(water.map { WaterLogEntryEntity(id = it.id, date = it.date, amount = it.amount, createdAt = "") })

            val cardio = postgrest.from("cardio_log_entries").select { filter { eq("user_id", uid) } }.decodeList<CardioLogRow>()
            cardioLogDao.deleteAll()
            cardioLogDao.insertAll(cardio.map { CardioLogEntryEntity(
                id = it.id, date = it.date, type = it.type,
                durationMinutes = it.duration_minutes,
                estimatedCaloriesBurnt = it.estimated_calories_burnt, notes = it.notes,
                createdAt = ""
            )})

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
    private fun WaterLogEntry.toEntity() = WaterLogEntryEntity(id = id, date = date, amount = amount, createdAt = createdAt)
    private fun WaterLogEntryEntity.toDomain() = WaterLogEntry(id = id, date = date, amount = amount, createdAt = createdAt)
    private fun CardioLogEntry.toEntity() = CardioLogEntryEntity(
        id = id, date = date, type = type, durationMinutes = durationMinutes,
        estimatedCaloriesBurnt = estimatedCaloriesBurnt, notes = notes, createdAt = createdAt
    )
    private fun CardioLogEntryEntity.toDomain() = CardioLogEntry(
        id = id, date = date, type = type, durationMinutes = durationMinutes,
        estimatedCaloriesBurnt = estimatedCaloriesBurnt, notes = notes, createdAt = createdAt
    )
}
