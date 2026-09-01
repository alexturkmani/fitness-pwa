package com.nexal.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.data.repository.WorkoutRepository
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import com.nexal.app.util.generateId
import com.nexal.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val isPremium: Boolean = false,
    val isSaving: Boolean = false,
    val generatingPlans: Boolean = false,
    val statusMessage: String = "",
    val completed: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val aiRepository: AiRepository,
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        // Prefill display name from signup metadata when onboarding name is still empty
        viewModelScope.launch {
            authRepository.authState.collect { auth ->
                if (auth is AuthState.Authenticated) {
                    val authName = auth.name?.trim().orEmpty()
                    _uiState.update { state ->
                        state.copy(
                            name = if (state.name.isBlank() && authName.isNotEmpty()) authName else state.name,
                            isPremium = auth.isPremium
                        )
                    }
                }
            }
        }
    }

    fun nextStep() { _uiState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(2)) } }
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
            _uiState.update {
                it.copy(
                    isSaving = true,
                    generatingPlans = true,
                    statusMessage = "Saving your profile…"
                )
            }
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

            if (!state.isPremium) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        generatingPlans = false,
                        statusMessage = "",
                        completed = true
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(statusMessage = "Creating your AI workout & meal plans…") }

            coroutineScope {
                val workoutDeferred = async {
                    aiRepository.generateWorkoutPlan(profile = profile, workoutStyle = profile.workoutStyle)
                }
                val mealDeferred = async {
                    aiRepository.generateMealPlan(profile = profile, allergies = emptyList())
                }
                when (val workout = workoutDeferred.await()) {
                    is Resource.Success -> workoutRepository.savePlan(workout.data)
                    is Resource.Error -> { /* continue; user can regenerate */ }
                    is Resource.Loading -> {}
                }
                when (val meal = mealDeferred.await()) {
                    is Resource.Success -> nutritionRepository.saveMealPlan(meal.data)
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            _uiState.update {
                it.copy(
                    isSaving = false,
                    generatingPlans = false,
                    statusMessage = "",
                    completed = true
                )
            }
        }
    }
}
