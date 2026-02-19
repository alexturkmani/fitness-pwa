package com.fitmate.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val weight: Double,
    val height: Double,
    val age: Int,
    val gender: String,
    val activityLevel: String,
    val fitnessGoalsJson: String, // JSON array of goals
    val targetWeight: Double,
    val intervalWeeks: Int,
    val gymDaysPerWeek: Int,
    val workoutStyle: String,
    val liftingExperience: String = "BEGINNER",
    val trainingLocation: String = "GYM",
    val unitSystem: String = "METRIC",
    val allergiesJson: String, // JSON array of allergies
    val onboardingCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey val id: String,
    val intervalNumber: Int,
    val startDate: String,
    val endDate: String,
    val weeks: Int,
    val daysJson: String, // JSON array of WorkoutDay
    val aiNotes: String,
    val assessmentSummary: String?,
    val createdAt: String
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey val id: String,
    val date: String,
    val planId: String,
    val dayId: String,
    val dayLabel: String = "",
    val exercisesJson: String, // JSON array of ExerciseLog
    val duration: Int?,
    val notes: String?,
    val createdAt: String
)

@Entity(tableName = "custom_workout_logs")
data class CustomWorkoutLogEntity(
    @PrimaryKey val id: String,
    val date: String,
    val name: String,
    val exercisesJson: String, // JSON array of CustomExerciseLog
    val duration: Int?,
    val notes: String?,
    val createdAt: String
)

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey val id: String,
    val date: String,
    val mealsJson: String, // JSON array of Meal
    val dailyTotalsJson: String, // JSON MacroNutrients
    val dailyTargetsJson: String, // JSON MacroNutrients
    val aiNotes: String,
    val dailyWaterIntakeMl: Int? = null,
    val createdAt: String
)

@Entity(tableName = "food_log_entries")
data class FoodLogEntryEntity(
    @PrimaryKey val id: String,
    val date: String,
    val foodName: String,
    val servingSize: String,
    val quantity: Int,
    val macrosJson: String, // JSON MacroNutrients
    val source: String,
    val barcode: String?,
    val createdAt: String
)

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey val date: String,
    val weight: Double
)

@Entity(tableName = "water_log_entries")
data class WaterLogEntryEntity(
    @PrimaryKey val id: String,
    val date: String,
    val amount: Int, // ml
    val createdAt: String
)

@Entity(tableName = "cardio_log_entries")
data class CardioLogEntryEntity(
    @PrimaryKey val id: String,
    val date: String,
    val type: String,
    val durationMinutes: Int,
    val estimatedCaloriesBurnt: Int,
    val notes: String?,
    val createdAt: String
)
