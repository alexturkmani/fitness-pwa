package com.nexal.app.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.data.repository.WorkoutRepository
import com.nexal.app.domain.model.WorkoutDay
import com.nexal.app.domain.model.WorkoutLog
import com.nexal.app.domain.model.WorkoutPlan
import com.nexal.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutsUiState(
    val currentPlan: WorkoutPlan? = null,
    val days: List<WorkoutDay> = emptyList(),
    val selectedDayId: String = "",
    val aiNotes: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
    val workoutLogs: List<WorkoutLog> = emptyList(),
    val selectedLog: WorkoutLog? = null,
    val showLogDetail: Boolean = false
)

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepository,
    private val profileRepo: ProfileRepository,
    private val aiRepo: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsUiState())
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            workoutRepo.observePlans().collect { plans ->
                val latest = plans.firstOrNull()
                _uiState.update {
                    it.copy(
                        currentPlan = latest,
                        days = latest?.days ?: emptyList(),
                        selectedDayId = it.selectedDayId.ifBlank { latest?.days?.firstOrNull()?.id ?: "" },
                        aiNotes = latest?.aiNotes ?: ""
                    )
                }
            }
        }
        viewModelScope.launch {
            workoutRepo.observeLogs().collect { logs ->
                _uiState.update { it.copy(workoutLogs = logs.sortedByDescending { l -> l.createdAt }) }
            }
        }
    }

    fun showLogDetail(log: WorkoutLog) {
        _uiState.update { it.copy(selectedLog = log, showLogDetail = true) }
    }

    fun dismissLogDetail() {
        _uiState.update { it.copy(selectedLog = null, showLogDetail = false) }
    }

    fun selectDay(dayId: String) {
        _uiState.update { it.copy(selectedDayId = dayId) }
    }

    fun generatePlan() {
        viewModelScope.launch {
            val profile = profileRepo.getProfile() ?: return@launch
            _uiState.update { it.copy(isGenerating = true, error = null) }
            when (val result = aiRepo.generateWorkoutPlan(
                profile = profile,
                workoutStyle = profile.workoutStyle
            )) {
                is Resource.Success -> {
                    workoutRepo.savePlan(result.data)
                    _uiState.update { it.copy(isGenerating = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isGenerating = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deletePlan() {
        viewModelScope.launch {
            val plan = _uiState.value.currentPlan ?: return@launch
            workoutRepo.deletePlan(plan.id)
        }
    }
}
