package com.fitmate.app.ui.workouts

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.WorkoutRepository
import com.fitmate.app.domain.model.*
import com.fitmate.app.util.generateId
import com.fitmate.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutLogUiState(
    val dayLabel: String = "",
    val exercises: List<ExerciseLog> = emptyList(),
    val restTimerSeconds: Int = 0,
    val restTimerTotal: Int = 60,
    val restDefaultSeconds: Int = 60
)

@HiltViewModel
class WorkoutLogViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutLogUiState())
    val uiState: StateFlow<WorkoutLogUiState> = _uiState.asStateFlow()

    private var planId = ""
    private var dayId = ""
    private var timerJob: Job? = null

    fun loadWorkout(planId: String, dayId: String) {
        this.planId = planId
        this.dayId = dayId

        viewModelScope.launch {
            workoutRepo.observePlans().collect { plans ->
                val plan = plans.find { it.id == planId } ?: return@collect
                val day = plan.days.find { it.id == dayId } ?: return@collect

                val exerciseLogs = day.exercises.map { exercise ->
                    ExerciseLog(
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        sets = (1..exercise.sets).map { setNum ->
                            SetLog(setNumber = setNum, weight = 0.0, reps = 0, completed = false)
                        }
                    )
                }

                _uiState.update { it.copy(dayLabel = day.dayLabel, exercises = exerciseLogs, restDefaultSeconds = day.exercises.firstOrNull()?.restSeconds ?: 60) }
            }
        }
    }

    fun updateWeight(exerciseIndex: Int, setNumber: Int, weight: Double) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.map {
                if (it.setNumber == setNumber) it.copy(weight = weight) else it
            }
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateReps(exerciseIndex: Int, setNumber: Int, reps: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.map {
                if (it.setNumber == setNumber) it.copy(reps = reps) else it
            }
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun toggleSetComplete(exerciseIndex: Int, setNumber: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.map {
                if (it.setNumber == setNumber) it.copy(completed = !it.completed) else it
            }
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun startRestTimer(seconds: Int) {
        timerJob?.cancel()
        _uiState.update { it.copy(restTimerSeconds = seconds, restTimerTotal = seconds) }
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _uiState.update { it.copy(restTimerSeconds = i) }
                delay(1000)
            }
            _uiState.update { it.copy(restTimerSeconds = 0) }
            // Vibrate on complete
            vibrate()
        }
    }

    fun cancelRestTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(restTimerSeconds = 0) }
    }

    fun saveWorkout() {
        viewModelScope.launch {
            val log = WorkoutLog(
                id = generateId(),
                date = todayFormatted(),
                planId = planId,
                dayId = dayId,
                dayLabel = _uiState.value.dayLabel,
                exercises = _uiState.value.exercises,
                createdAt = todayFormatted()
            )
            workoutRepo.saveLog(log)
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }
}
