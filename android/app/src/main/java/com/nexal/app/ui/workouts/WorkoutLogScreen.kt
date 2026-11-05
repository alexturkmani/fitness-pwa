package com.nexal.app.ui.workouts

import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.domain.model.SetLog
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLogScreen(
    planId: String,
    dayId: String,
    onBack: () -> Unit,
    viewModel: WorkoutLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(planId, dayId) {
        viewModel.loadWorkout(planId, dayId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.dayLabel, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.exercises.isEmpty()) {
            LoadingScreen("Loading workout...")
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Rest Timer (shown when active)
                if (uiState.restTimerSeconds > 0) {
                    RestTimer(
                        remainingSeconds = uiState.restTimerSeconds,
                        totalSeconds = uiState.restTimerTotal,
                        onCancel = { viewModel.cancelRestTimer() }
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    uiState.exercises.forEachIndexed { exIndex, exercise ->
                        item(key = "header_$exIndex") {
                            FadeSlideIn(delayMs = exIndex * 50) {
                                Text(
                                    exercise.exerciseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        items(exercise.sets, key = { "set_${exIndex}_${it.setNumber}" }) { set ->
                            FadeSlideIn(delayMs = 40 + exIndex * 40) {
                                SetRow(
                                    set = set,
                                    onWeightChange = { viewModel.updateWeight(exIndex, set.setNumber, it) },
                                    onRepsChange = { viewModel.updateReps(exIndex, set.setNumber, it) },
                                    onToggleComplete = { viewModel.toggleSetComplete(exIndex, set.setNumber) },
                                    onRestTimer = { viewModel.startRestTimer(uiState.restDefaultSeconds) }
                                )
                            }
                        }
                    }
                }

                ScalePopIn(delayMs = 100) {
                    GradientButton(
                        text = "Save Workout",
                        onClick = {
                            viewModel.saveWorkout()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SetRow(
    set: SetLog,
    onWeightChange: (Double) -> Unit,
    onRepsChange: (Int) -> Unit,
    onToggleComplete: () -> Unit,
    onRestTimer: () -> Unit
) {
    var weightText by remember(set.weight) { mutableStateOf(if (set.weight > 0) set.weight.toString().removeSuffix(".0") else "") }
    var repsText by remember(set.reps) { mutableStateOf(if (set.reps > 0) set.reps.toString() else "") }

    FitCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Set number
            Text(
                "Set ${set.setNumber}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(34.dp)
            )

            // Weight input with steppers
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Weight (kg)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                StepperField(
                    text = weightText,
                    onTextChange = { v ->
                        weightText = v
                        v.toDoubleOrNull()?.let { onWeightChange(it) }
                    },
                    onMinus = {
                        val newW = (set.weight - 2.5).coerceAtLeast(0.0)
                        weightText = newW.toString().removeSuffix(".0")
                        onWeightChange(newW)
                    },
                    onPlus = {
                        val newW = set.weight + 2.5
                        weightText = newW.toString().removeSuffix(".0")
                        onWeightChange(newW)
                    },
                    label = "weight, set ${set.setNumber}",
                    keyboardType = KeyboardType.Decimal
                )
            }

            // Reps input with steppers
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Reps", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                StepperField(
                    text = repsText,
                    onTextChange = { v ->
                        repsText = v
                        v.toIntOrNull()?.let { onRepsChange(it) }
                    },
                    onMinus = {
                        val newR = (set.reps - 1).coerceAtLeast(0)
                        repsText = if (newR > 0) newR.toString() else ""
                        onRepsChange(newR)
                    },
                    onPlus = {
                        val newR = set.reps + 1
                        repsText = newR.toString()
                        onRepsChange(newR)
                    },
                    label = "reps, set ${set.setNumber}",
                    keyboardType = KeyboardType.Number
                )
            }

            // Complete checkbox + rest timer
            RoundCheck(
                checked = set.completed,
                onCheckedChange = {
                    onToggleComplete()
                    if (!set.completed) onRestTimer()
                },
                description = "Set ${set.setNumber} complete"
            )
        }
    }
}

@Composable
private fun RestTimer(
    remainingSeconds: Int,
    totalSeconds: Int,
    onCancel: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = remainingSeconds.toFloat() / totalSeconds,
        label = "timer_progress"
    )

    FitCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rest Timer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            // Circular timer
            val ringTrack = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawProgressRing(progress, 8.dp.toPx(), ringTrack, AccentBright.copy(alpha = 0.45f), AccentBright)
                }
                RollingNumber(
                    "${remainingSeconds}s",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onCancel) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
