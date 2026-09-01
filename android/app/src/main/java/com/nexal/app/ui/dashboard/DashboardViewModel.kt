package com.nexal.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.data.repository.WorkoutRepository
import com.nexal.app.domain.model.AuthState
import com.nexal.app.domain.model.UnitSystem
import com.nexal.app.util.Resource
import com.nexal.app.util.calculateMacroTargets
import com.nexal.app.util.calculateDailyWaterIntakeMl
import com.nexal.app.util.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val onboardingCompleted: Boolean = false,
    val workoutsThisWeek: Int = 0,
    val avgDailyCalories: Int = 0,
    val currentWeight: Double = 0.0,
    val weightChange: Double = 0.0,
    val todayWorkout: String? = null,
    val showTrialExpiredBanner: Boolean = false,
    val waterTotalMl: Int = 0,
    val waterGoalMl: Int = 2500,
    val cardioCaloriesToday: Int = 0,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val caloriesToday: Int = 0,
    val proteinToday: Int = 0,
    val carbsToday: Int = 0,
    val fatsToday: Int = 0,
    val calorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val carbsGoal: Int = 200,
    val fatsGoal: Int = 65,
    val hasWorkoutPlan: Boolean = false,
    val hasMealPlan: Boolean = false,
    val isGeneratingPlans: Boolean = false,
    val firstWinMessage: String? = null,
    val promptPlayReview: Boolean = false,
    // Last 7 days of intake, oldest first, as (single-letter day, calories).
    val weeklyCalories: List<Pair<String, Int>> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val workoutRepo: WorkoutRepository,
    private val nutritionRepo: NutritionRepository,
    private val authRepo: AuthRepository,
    private val aiRepo: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            authRepo.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    _uiState.update {
                        it.copy(
                            showTrialExpiredBanner = !state.subscriptionActive,
                            // Prefer profile name; fall back to auth display name
                            userName = it.userName.ifBlank { state.name.orEmpty() }
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            profileRepo.observeProfile().collect { profile ->
                if (profile != null) {
                    val waterGoal = calculateDailyWaterIntakeMl(profile.weight, profile.activityLevel)
                    val macros = calculateMacroTargets(profile)
                    _uiState.update {
                        val authName = (authRepo.authState.value as? AuthState.Authenticated)?.name
                        it.copy(
                            userName = profile.name.ifBlank { authName.orEmpty() },
                            onboardingCompleted = profile.onboardingCompleted,
                            currentWeight = profile.weight,
                            waterGoalMl = waterGoal,
                            unitSystem = profile.unitSystem,
                            calorieGoal = macros.calories,
                            proteinGoal = macros.protein,
                            carbsGoal = macros.carbs,
                            fatsGoal = macros.fats
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            workoutRepo.observePlans().collect { plans ->
                val todayDayOfWeek = LocalDate.now().dayOfWeek.value
                val latestPlan = plans.firstOrNull()
                val todayWorkout = latestPlan?.days?.find { it.dayNumber == todayDayOfWeek }
                _uiState.update {
                    it.copy(
                        hasWorkoutPlan = latestPlan != null,
                        todayWorkout = when {
                            latestPlan == null -> null
                            todayWorkout?.isRestDay == true -> "Rest Day"
                            else -> todayWorkout?.dayLabel
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            nutritionRepo.observeMealPlans().collect { plans ->
                _uiState.update { it.copy(hasMealPlan = plans.isNotEmpty()) }
            }
        }

        viewModelScope.launch {
            workoutRepo.observeLogs().collect { logs ->
                val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
                val count = logs.count {
                    try {
                        LocalDate.parse(it.date) >= startOfWeek
                    } catch (_: Exception) {
                        false
                    }
                }
                _uiState.update { it.copy(workoutsThisWeek = count) }
            }
        }

        viewModelScope.launch {
            nutritionRepo.observeRecentWeightEntries(30).collect { entries ->
                if (entries.size >= 2) {
                    val latest = entries.first().weight
                    val oldest = entries.last().weight
                    _uiState.update {
                        it.copy(
                            currentWeight = latest,
                            weightChange = ((latest - oldest) * 10).toInt() / 10.0
                        )
                    }
                } else if (entries.size == 1) {
                    _uiState.update { it.copy(currentWeight = entries.first().weight) }
                }
            }
        }

        viewModelScope.launch {
            val today = LocalDate.now()
            val sevenDaysAgo = today.minusDays(6)
            nutritionRepo.observeFoodLogByDateRange(formatDate(sevenDaysAgo), formatDate(today)).collect { entries ->
                if (entries.isNotEmpty()) {
                    val totalCalories = entries.sumOf { it.macros.calories * it.quantity }
                    val days = entries.map { it.date }.distinct().size.coerceAtLeast(1)
                    _uiState.update { it.copy(avgDailyCalories = totalCalories / days) }
                }
                // Bucket by day so the chart always shows all 7 columns, including
                // zero days — a gap is information, not something to omit.
                val byDate = entries.groupBy { it.date }
                    .mapValues { (_, e) -> e.sumOf { it.macros.calories * it.quantity } }
                val series = (0..6).map { offset ->
                    val day = sevenDaysAgo.plusDays(offset.toLong())
                    val label = day.dayOfWeek
                        .getDisplayName(java.time.format.TextStyle.NARROW, Locale.getDefault())
                    label to (byDate[formatDate(day)] ?: 0)
                }
                _uiState.update { it.copy(weeklyCalories = series) }
            }
        }

        viewModelScope.launch {
            val todayStr = formatDate(LocalDate.now())
            nutritionRepo.observeFoodLogByDate(todayStr).collect { entries ->
                val calories = entries.sumOf { it.macros.calories * it.quantity }
                val protein = entries.sumOf { it.macros.protein * it.quantity }
                val carbs = entries.sumOf { it.macros.carbs * it.quantity }
                val fats = entries.sumOf { it.macros.fats * it.quantity }
                _uiState.update {
                    it.copy(
                        caloriesToday = calories,
                        proteinToday = protein,
                        carbsToday = carbs,
                        fatsToday = fats
                    )
                }
            }
        }

        viewModelScope.launch {
            val todayStr = formatDate(LocalDate.now())
            nutritionRepo.observeWaterLogByDate(todayStr).collect { entries ->
                _uiState.update { it.copy(waterTotalMl = entries.sumOf { e -> e.amount }) }
            }
        }

        viewModelScope.launch {
            val todayStr = formatDate(LocalDate.now())
            nutritionRepo.observeCardioLogByDate(todayStr).collect { entries ->
                _uiState.update {
                    it.copy(cardioCaloriesToday = entries.sumOf { e -> e.estimatedCaloriesBurnt })
                }
            }
        }
    }

    fun generateStarterPlans() {
        viewModelScope.launch {
            val auth = authRepo.authState.value as? AuthState.Authenticated
            if (auth?.isPremium != true) {
                _uiState.update { it.copy(firstWinMessage = "Premium unlocks personalized AI plans") }
                return@launch
            }
            val profile = profileRepo.getProfile()
            if (profile == null || !profile.onboardingCompleted) {
                _uiState.update { it.copy(firstWinMessage = "Complete onboarding first") }
                return@launch
            }
            _uiState.update {
                it.copy(isGeneratingPlans = true, firstWinMessage = "Creating your AI workout & meal plans…")
            }
            var anySaved = false
            coroutineScope {
                val workoutJob = async {
                    aiRepo.generateWorkoutPlan(profile = profile, workoutStyle = profile.workoutStyle)
                }
                val mealJob = async {
                    aiRepo.generateMealPlan(profile = profile, allergies = profile.allergies)
                }
                when (val w = workoutJob.await()) {
                    is Resource.Success -> {
                        workoutRepo.savePlan(w.data)
                        anySaved = true
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
                when (val m = mealJob.await()) {
                    is Resource.Success -> {
                        nutritionRepo.saveMealPlan(m.data)
                        anySaved = true
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }
            _uiState.update {
                it.copy(
                    isGeneratingPlans = false,
                    firstWinMessage = "Your plans are ready — open Workouts & Meals",
                    promptPlayReview = anySaved
                )
            }
        }
    }

    fun clearFirstWinMessage() {
        _uiState.update { it.copy(firstWinMessage = null) }
    }

    fun markReviewPrompted() {
        _uiState.update { it.copy(promptPlayReview = false) }
    }
}
