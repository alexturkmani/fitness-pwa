package com.fitmate.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.AuthRepository
import com.fitmate.app.data.repository.NutritionRepository
import com.fitmate.app.data.repository.ProfileRepository
import com.fitmate.app.data.repository.WorkoutRepository
import com.fitmate.app.domain.model.AuthState
import com.fitmate.app.domain.model.UnitSystem
import com.fitmate.app.util.formatDate
import com.fitmate.app.util.calculateDailyWaterIntakeMl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val onboardingCompleted: Boolean = false,
    val workoutsThisWeek: Int = 0,
    val avgDailyCalories: Int = 0,
    val currentWeight: Double = 0.0,
    val weightChange: Double = 0.0,
    val todayWorkout: String? = null,
    val trialDaysLeft: Int = -1,   // -1 = no trial / not applicable, 0 = expired
    val showTrialExpiredBanner: Boolean = false,
    // Water
    val waterTotalMl: Int = 0,
    val waterGoalMl: Int = 2500,
    // Cardio
    val cardioCaloriesToday: Int = 0,
    // Units
    val unitSystem: UnitSystem = UnitSystem.METRIC
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val workoutRepo: WorkoutRepository,
    private val nutritionRepo: NutritionRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        // Check trial status
        viewModelScope.launch {
            authRepo.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    val now = Instant.now()
                    val trialEnd = state.trialEndsAt?.let {
                        try { Instant.parse(it) } catch (_: Exception) { null }
                    }
                    val daysLeft = if (trialEnd != null && trialEnd.isAfter(now)) {
                        ChronoUnit.DAYS.between(now, trialEnd).toInt() + 1
                    } else if (trialEnd != null) 0 else -1

                    val showExpired = !state.subscriptionActive && state.hasUsedTrial &&
                            (trialEnd == null || !trialEnd.isAfter(now))

                    _uiState.update {
                        it.copy(trialDaysLeft = daysLeft, showTrialExpiredBanner = showExpired)
                    }
                }
            }
        }

        viewModelScope.launch {
            // Observe profile
            profileRepo.observeProfile().collect { profile ->
                if (profile != null) {
                    val waterGoal = calculateDailyWaterIntakeMl(profile.weight, profile.activityLevel)
                    _uiState.update {
                        it.copy(
                            userName = profile.name,
                            onboardingCompleted = profile.onboardingCompleted,
                            currentWeight = profile.weight,
                            waterGoalMl = waterGoal,
                            unitSystem = profile.unitSystem
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            // Observe workout plans for today's workout
            workoutRepo.observePlans().collect { plans ->
                val today = LocalDate.now()
                val todayDayOfWeek = today.dayOfWeek.value // 1=Monday..7=Sunday
                val latestPlan = plans.firstOrNull()
                if (latestPlan != null) {
                    val todayWorkout = latestPlan.days.find { it.dayNumber == todayDayOfWeek }
                    _uiState.update {
                        it.copy(todayWorkout = if (todayWorkout?.isRestDay == true) "Rest Day 🧘" else todayWorkout?.dayLabel)
                    }
                }
            }
        }

        viewModelScope.launch {
            // Count this week's workout logs
            workoutRepo.observeLogs().collect { logs ->
                val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
                val count = logs.count {
                    try {
                        LocalDate.parse(it.date) >= startOfWeek
                    } catch (_: Exception) { false }
                }
                _uiState.update { it.copy(workoutsThisWeek = count) }
            }
        }

        viewModelScope.launch {
            // Weight change from weight entries
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
            // Average daily calories (last 7 days)
            val today = LocalDate.now()
            val sevenDaysAgo = today.minusDays(6)
            nutritionRepo.observeFoodLogByDateRange(formatDate(sevenDaysAgo), formatDate(today)).collect { entries ->
                if (entries.isNotEmpty()) {
                    val totalCalories = entries.sumOf { it.macros.calories * it.quantity }
                    val days = entries.map { it.date }.distinct().size.coerceAtLeast(1)
                    _uiState.update { it.copy(avgDailyCalories = totalCalories / days) }
                }
            }
        }

        // Water intake today
        viewModelScope.launch {
            val todayStr = formatDate(LocalDate.now())
            nutritionRepo.observeWaterLogByDate(todayStr).collect { entries ->
                val total = entries.sumOf { it.amount }
                _uiState.update { it.copy(waterTotalMl = total) }
            }
        }

        // Cardio calories today
        viewModelScope.launch {
            val todayStr = formatDate(LocalDate.now())
            nutritionRepo.observeCardioLogByDate(todayStr).collect { entries ->
                val total = entries.sumOf { it.estimatedCaloriesBurnt }
                _uiState.update { it.copy(cardioCaloriesToday = total) }
            }
        }
    }
}
