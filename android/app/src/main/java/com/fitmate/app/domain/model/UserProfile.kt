package com.fitmate.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val name: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val age: Int = 0,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val fitnessGoals: List<FitnessGoal> = emptyList(),
    val targetWeight: Double = 0.0,
    val intervalWeeks: Int = 6,
    val gymDaysPerWeek: Int = 3,
    val workoutStyle: WorkoutStyle = WorkoutStyle.MUSCLE_GROUP,
    val liftingExperience: LiftingExperience = LiftingExperience.BEGINNER,
    val trainingLocation: TrainingLocation = TrainingLocation.GYM,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val allergies: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class Gender { MALE, FEMALE, OTHER }

@Serializable
enum class ActivityLevel {
    SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTREMELY_ACTIVE;

    val label: String
        get() = when (this) {
            SEDENTARY -> "Sedentary"
            LIGHTLY_ACTIVE -> "Lightly Active"
            MODERATELY_ACTIVE -> "Moderately Active"
            VERY_ACTIVE -> "Very Active"
            EXTREMELY_ACTIVE -> "Extremely Active"
        }

    val description: String
        get() = when (this) {
            SEDENTARY -> "Little or no exercise, desk job"
            LIGHTLY_ACTIVE -> "Light exercise 1-3 days/week"
            MODERATELY_ACTIVE -> "Moderate exercise 3-5 days/week"
            VERY_ACTIVE -> "Hard exercise 6-7 days/week"
            EXTREMELY_ACTIVE -> "Very hard exercise, physical job"
        }

    val multiplier: Double
        get() = when (this) {
            SEDENTARY -> 1.2
            LIGHTLY_ACTIVE -> 1.375
            MODERATELY_ACTIVE -> 1.55
            VERY_ACTIVE -> 1.725
            EXTREMELY_ACTIVE -> 1.9
        }
}

@Serializable
enum class FitnessGoal {
    WEIGHT_LOSS, MUSCLE_GAIN, STRENGTH, ENDURANCE, GENERAL_FITNESS;

    val label: String
        get() = when (this) {
            WEIGHT_LOSS -> "Weight Loss"
            MUSCLE_GAIN -> "Muscle Gain"
            STRENGTH -> "Strength"
            ENDURANCE -> "Endurance"
            GENERAL_FITNESS -> "General Fitness"
        }

    val description: String
        get() = when (this) {
            WEIGHT_LOSS -> "Burn fat and reduce body weight"
            MUSCLE_GAIN -> "Build lean muscle mass"
            STRENGTH -> "Increase max lifting capacity"
            ENDURANCE -> "Improve cardiovascular fitness"
            GENERAL_FITNESS -> "Overall health and wellness"
        }
}

@Serializable
enum class WorkoutStyle {
    SINGLE_MUSCLE, MUSCLE_GROUP;

    val label: String
        get() = when (this) {
            SINGLE_MUSCLE -> "Single Muscle Isolation"
            MUSCLE_GROUP -> "Muscle Group Split"
        }
}

@Serializable
enum class LiftingExperience {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT;

    val label: String
        get() = when (this) {
            BEGINNER -> "Beginner"
            INTERMEDIATE -> "Intermediate"
            ADVANCED -> "Advanced"
            EXPERT -> "Expert"
        }

    val description: String
        get() = when (this) {
            BEGINNER -> "New to lifting or < 6 months experience"
            INTERMEDIATE -> "6 months to 2 years of consistent lifting"
            ADVANCED -> "2-5 years of consistent, structured training"
            EXPERT -> "5+ years with deep knowledge of programming"
        }
}

@Serializable
enum class TrainingLocation {
    GYM, HOME;

    val label: String
        get() = when (this) {
            GYM -> "Gym"
            HOME -> "Home"
        }

    val description: String
        get() = when (this) {
            GYM -> "Full gym with machines, barbells & dumbbells"
            HOME -> "Home setup with limited or no equipment"
        }
}

@Serializable
enum class UnitSystem {
    METRIC, IMPERIAL;

    val label: String
        get() = when (this) {
            METRIC -> "Metric (kg, cm)"
            IMPERIAL -> "Imperial (lbs, ft)"
        }
}
