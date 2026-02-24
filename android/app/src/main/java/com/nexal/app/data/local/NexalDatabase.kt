package com.nexal.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexal.app.data.local.dao.*
import com.nexal.app.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutPlanEntity::class,
        WorkoutLogEntity::class,
        CustomWorkoutLogEntity::class,
        MealPlanEntity::class,
        FoodLogEntryEntity::class,
        WeightEntryEntity::class,
        WaterLogEntryEntity::class,
        CardioLogEntryEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class NexalDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun customWorkoutLogDao(): CustomWorkoutLogDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun cardioLogDao(): CardioLogDao
}
