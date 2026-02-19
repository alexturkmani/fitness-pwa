package com.fitmate.app.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.WorkoutRepository
import com.fitmate.app.domain.model.CustomExerciseLog
import com.fitmate.app.domain.model.CustomWorkoutLog
import com.fitmate.app.util.generateId
import com.fitmate.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomWorkoutsUiState(
    val logs: List<CustomWorkoutLog> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CustomWorkoutsViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomWorkoutsUiState())
    val uiState: StateFlow<CustomWorkoutsUiState> = _uiState.asStateFlow()

    private val commonExercises = listOf(
        "Bench Press", "Incline Bench Press", "Decline Bench Press", "Dumbbell Fly",
        "Squat", "Front Squat", "Leg Press", "Leg Curl", "Leg Extension",
        "Deadlift", "Romanian Deadlift", "Sumo Deadlift",
        "Overhead Press", "Arnold Press", "Lateral Raise", "Front Raise",
        "Barbell Row", "Dumbbell Row", "Cable Row", "Lat Pulldown",
        "Pull Up", "Chin Up", "Dip", "Push Up",
        "Bicep Curl", "Hammer Curl", "Preacher Curl",
        "Tricep Pushdown", "Skull Crusher", "Tricep Kickback",
        "Face Pull", "Rear Delt Fly", "Cable Crossover",
        "Calf Raise", "Hip Thrust", "Bulgarian Split Squat", "Lunge", "Step Up",
        "Plank", "Russian Twist", "Hanging Leg Raise", "Cable Crunch"
    )

    init {
        viewModelScope.launch {
            workoutRepo.observeCustomWorkouts().collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
    }

    fun saveCustomWorkout(name: String, exercises: List<CustomExerciseLog>) {
        viewModelScope.launch {
            val log = CustomWorkoutLog(
                id = generateId(),
                name = name,
                date = todayFormatted(),
                exercises = exercises,
                createdAt = todayFormatted()
            )
            workoutRepo.saveCustomWorkout(log)
        }
    }

    fun deleteLog(id: String) {
        viewModelScope.launch {
            workoutRepo.deleteCustomWorkout(id)
        }
    }

    fun getExerciseSuggestions(query: String) {
        if (query.length < 2) return
        val filtered = commonExercises.filter { it.contains(query, ignoreCase = true) }
        _uiState.update { it.copy(suggestions = filtered) }
    }
}
