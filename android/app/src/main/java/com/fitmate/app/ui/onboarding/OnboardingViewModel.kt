package com.fitmate.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.ProfileRepository
import com.fitmate.app.domain.model.*
import com.fitmate.app.util.generateId
import com.fitmate.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0,
    val name: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val age: Int = 0,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val fitnessGoals: List<FitnessGoal> = emptyList(),
    val targetWeight: Double = 0.0,
    val intervalWeeks: Int = 6,
    val gymDaysPerWeek: Int = 4,
    val workoutStyle: WorkoutStyle = WorkoutStyle.MUSCLE_GROUP,
    val liftingExperience: LiftingExperience = LiftingExperience.BEGINNER,
    val trainingLocation: TrainingLocation = TrainingLocation.GYM,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val isSaving: Boolean = false,
    val completed: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() { _uiState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(7)) } }
    fun previousStep() { _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) } }
    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateWeight(weight: Double) { _uiState.update { it.copy(weight = weight) } }
    fun updateHeight(height: Double) { _uiState.update { it.copy(height = height) } }
    fun updateAge(age: Int) { _uiState.update { it.copy(age = age) } }
    fun updateGender(gender: Gender) { _uiState.update { it.copy(gender = gender) } }
    fun updateActivityLevel(level: ActivityLevel) { _uiState.update { it.copy(activityLevel = level) } }
    fun updateTargetWeight(weight: Double) { _uiState.update { it.copy(targetWeight = weight) } }
    fun updateIntervalWeeks(weeks: Int) { _uiState.update { it.copy(intervalWeeks = weeks) } }
    fun updateGymDays(days: Int) { _uiState.update { it.copy(gymDaysPerWeek = days) } }
    fun updateWorkoutStyle(style: WorkoutStyle) { _uiState.update { it.copy(workoutStyle = style) } }
    fun updateLiftingExperience(exp: LiftingExperience) { _uiState.update { it.copy(liftingExperience = exp) } }
    fun updateTrainingLocation(loc: TrainingLocation) { _uiState.update { it.copy(trainingLocation = loc) } }
    fun updateUnitSystem(unit: UnitSystem) { _uiState.update { it.copy(unitSystem = unit) } }

    fun toggleGoal(goal: FitnessGoal) {
        _uiState.update { state ->
            val current = state.fitnessGoals.toMutableList()
            if (goal in current) current.remove(goal) else current.add(goal)
            state.copy(fitnessGoals = current)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value
            val now = todayFormatted()
            val profile = UserProfile(
                id = generateId(),
                name = state.name,
                weight = state.weight,
                height = state.height,
                age = state.age,
                gender = state.gender,
                activityLevel = state.activityLevel,
                fitnessGoals = state.fitnessGoals,
                targetWeight = state.targetWeight,
                intervalWeeks = state.intervalWeeks,
                gymDaysPerWeek = state.gymDaysPerWeek,
                workoutStyle = state.workoutStyle,
                liftingExperience = state.liftingExperience,
                trainingLocation = state.trainingLocation,
                unitSystem = state.unitSystem,
                onboardingCompleted = true,
                createdAt = now,
                updatedAt = now
            )
            profileRepository.saveProfile(profile)
            _uiState.update { it.copy(isSaving = false, completed = true) }
        }
    }
}
