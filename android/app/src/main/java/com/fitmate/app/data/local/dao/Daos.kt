package com.fitmate.app.data.local.dao

import androidx.room.*
import com.fitmate.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getProfile(): UserProfileEntity?

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()
}

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plans ORDER BY createdAt DESC")
    suspend fun getAll(): List<WorkoutPlanEntity>

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getById(id: String): WorkoutPlanEntity?

    @Upsert
    suspend fun upsert(plan: WorkoutPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<WorkoutPlanEntity>)

    @Query("DELETE FROM workout_plans WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM workout_plans")
    suspend fun deleteAll()
}

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY date DESC")
    fun observeAll(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE planId = :planId ORDER BY date DESC")
    fun observeByPlanId(planId: String): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE planId = :planId AND dayId = :dayId ORDER BY date DESC")
    suspend fun getByPlanAndDay(planId: String, dayId: String): List<WorkoutLogEntity>

    @Query("SELECT * FROM workout_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<WorkoutLogEntity>

    @Upsert
    suspend fun upsert(log: WorkoutLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<WorkoutLogEntity>)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM workout_logs")
    suspend fun deleteAll()
}

@Dao
interface CustomWorkoutLogDao {
    @Query("SELECT * FROM custom_workout_logs ORDER BY date DESC")
    fun observeAll(): Flow<List<CustomWorkoutLogEntity>>

    @Upsert
    suspend fun upsert(log: CustomWorkoutLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<CustomWorkoutLogEntity>)

    @Query("DELETE FROM custom_workout_logs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM custom_workout_logs")
    suspend fun deleteAll()
}

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MealPlanEntity>>

    @Query("SELECT * FROM meal_plans ORDER BY createdAt DESC LIMIT 1")
    fun observeLatest(): Flow<MealPlanEntity?>

    @Upsert
    suspend fun upsert(plan: MealPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<MealPlanEntity>)

    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM meal_plans")
    suspend fun deleteAll()
}

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_log_entries WHERE date = :date ORDER BY createdAt DESC")
    fun observeByDate(date: String): Flow<List<FoodLogEntryEntity>>

    @Query("SELECT * FROM food_log_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, createdAt DESC")
    fun observeByDateRange(startDate: String, endDate: String): Flow<List<FoodLogEntryEntity>>

    @Query("SELECT * FROM food_log_entries ORDER BY date DESC, createdAt DESC")
    suspend fun getAll(): List<FoodLogEntryEntity>

    @Upsert
    suspend fun upsert(entry: FoodLogEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodLogEntryEntity>)

    @Query("DELETE FROM food_log_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM food_log_entries")
    suspend fun deleteAll()
}

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    suspend fun getAll(): List<WeightEntryEntity>

    @Upsert
    suspend fun upsert(entry: WeightEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<WeightEntryEntity>)

    @Query("DELETE FROM weight_entries WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("DELETE FROM weight_entries")
    suspend fun deleteAll()
}

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_log_entries WHERE date = :date ORDER BY createdAt DESC")
    fun observeByDate(date: String): Flow<List<WaterLogEntryEntity>>

    @Query("SELECT * FROM water_log_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, createdAt DESC")
    fun observeByDateRange(startDate: String, endDate: String): Flow<List<WaterLogEntryEntity>>

    @Query("SELECT * FROM water_log_entries ORDER BY date DESC, createdAt DESC")
    suspend fun getAll(): List<WaterLogEntryEntity>

    @Upsert
    suspend fun upsert(entry: WaterLogEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<WaterLogEntryEntity>)

    @Query("DELETE FROM water_log_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM water_log_entries")
    suspend fun deleteAll()
}

@Dao
interface CardioLogDao {
    @Query("SELECT * FROM cardio_log_entries WHERE date = :date ORDER BY createdAt DESC")
    fun observeByDate(date: String): Flow<List<CardioLogEntryEntity>>

    @Query("SELECT * FROM cardio_log_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, createdAt DESC")
    fun observeByDateRange(startDate: String, endDate: String): Flow<List<CardioLogEntryEntity>>

    @Query("SELECT * FROM cardio_log_entries ORDER BY date DESC, createdAt DESC")
    suspend fun getAll(): List<CardioLogEntryEntity>

    @Upsert
    suspend fun upsert(entry: CardioLogEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<CardioLogEntryEntity>)

    @Query("DELETE FROM cardio_log_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cardio_log_entries")
    suspend fun deleteAll()
}
