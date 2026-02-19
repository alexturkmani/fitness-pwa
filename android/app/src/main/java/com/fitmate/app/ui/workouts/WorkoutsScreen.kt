package com.fitmate.app.ui.workouts

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitmate.app.domain.model.WorkoutDay
import com.fitmate.app.domain.model.WorkoutLog
import com.fitmate.app.ui.components.*
import com.fitmate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    onNavigateToLog: (planId: String, dayId: String) -> Unit,
    onNavigateToCustom: () -> Unit,
    viewModel: WorkoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workouts", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onNavigateToCustom) {
                        Text("Custom", color = Emerald500)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.currentPlan == null && !uiState.isGenerating) {
            EmptyState(
                icon = Icons.Default.FitnessCenter,
                title = "No Workout Plan",
                description = "Generate an AI-powered workout plan tailored to your goals.",
                actionLabel = "Generate Plan",
                onAction = { viewModel.generatePlan() },
                modifier = Modifier.padding(paddingValues)
            )
        } else if (uiState.isGenerating) {
            LoadingScreen("Generating your personalized workout plan...")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // AI Notes
                if (uiState.aiNotes.isNotBlank()) {
                    item {
                        FitCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("AI Coach Notes", style = MaterialTheme.typography.labelMedium, color = Emerald500)
                                Spacer(Modifier.height(4.dp))
                                Text(uiState.aiNotes, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Day selector tabs
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.days) { day ->
                            val selected = day.id == uiState.selectedDayId
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectDay(day.id) },
                                label = {
                                    Text(
                                        if (day.isRestDay) "Rest" else day.dayLabel,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald500,
                                    selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Selected day exercises
                val selectedDay = uiState.days.find { it.id == uiState.selectedDayId }
                if (selectedDay != null) {
                    if (selectedDay.isRestDay) {
                        item {
                            EmptyState(
                                icon = Icons.Default.SelfImprovement,
                                title = "Rest Day 🧘",
                                description = "Recovery is just as important as training. Take it easy today!"
                            )
                        }
                    } else {
                        item {
                            Text(
                                selectedDay.dayLabel,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        items(selectedDay.exercises) { exercise ->
                            FitCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        exercise.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text(
                                            "${exercise.sets} sets × ${exercise.reps}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Rest: ${exercise.restSeconds}s",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (!exercise.notes.isNullOrBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            exercise.notes!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Emerald500
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        item {
                            Spacer(Modifier.height(8.dp))
                            GradientButton(
                                text = "Start Workout",
                                onClick = {
                                    uiState.currentPlan?.let { plan ->
                                        onNavigateToLog(plan.id, selectedDay.id)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    FitButton(
                        text = "Delete Plan",
                        onClick = { viewModel.deletePlan() },
                        variant = ButtonVariant.GHOST,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Delete
                    )
                }

                // Workout History section
                if (uiState.workoutLogs.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Workout History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(uiState.workoutLogs.take(20)) { log ->
                        val label = log.dayLabel.ifBlank {
                            uiState.days.find { it.id == log.dayId }?.dayLabel ?: "Workout"
                        }
                        WorkoutLogCard(
                            log = log,
                            dayLabel = label,
                            onClick = { viewModel.showLogDetail(log) }
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }

        // Log detail modal
        if (uiState.showLogDetail && uiState.selectedLog != null) {
            val detailLabel = uiState.selectedLog!!.dayLabel.ifBlank {
                uiState.days.find { it.id == uiState.selectedLog!!.dayId }?.dayLabel ?: "Workout"
            }
            WorkoutLogDetailModal(
                log = uiState.selectedLog!!,
                dayLabel = detailLabel,
                onDismiss = { viewModel.dismissLogDetail() }
            )
        }
    }
}

@Composable
private fun WorkoutLogCard(
    log: WorkoutLog,
    dayLabel: String,
    onClick: () -> Unit
) {
    FitCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = Emerald500, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(dayLabel, fontWeight = FontWeight.SemiBold)
                Text(
                    "${log.exercises.size} exercises • ${log.exercises.sumOf { it.sets.size }} sets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    log.date.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.duration != null && log.duration > 0) {
                    Text(
                        "${log.duration}min",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald500
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WorkoutLogDetailModal(
    log: WorkoutLog,
    dayLabel: String,
    onDismiss: () -> Unit
) {
    FitModal(
        isOpen = true,
        onDismiss = onDismiss,
        title = "$dayLabel — ${log.date.take(10)}"
    ) {
        if (log.duration != null && log.duration > 0) {
            Text("Duration: ${log.duration} minutes", style = MaterialTheme.typography.bodyMedium, color = Emerald500)
            Spacer(Modifier.height(12.dp))
        }
        log.exercises.forEach { exercise ->
            Text(exercise.exerciseName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            exercise.sets.forEach { set ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Set ${set.setNumber}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${set.weight}kg × ${set.reps} reps",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    if (set.completed) {
                        Icon(Icons.Default.Check, null, tint = Emerald500, modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        if (!log.notes.isNullOrBlank()) {
            Text("Notes", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Text(log.notes, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        FitButton(
            text = "Close",
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.SECONDARY
        )
    }
}
