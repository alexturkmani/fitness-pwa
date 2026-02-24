package com.nexal.app.ui.workouts

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.domain.model.CustomExerciseLog
import com.nexal.app.domain.model.CustomSet
import com.nexal.app.domain.model.CustomWorkoutLog
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.Emerald500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutsScreen(
    onBack: () -> Unit,
    viewModel: CustomWorkoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Workouts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogSheet = true },
                containerColor = Emerald500
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Workout", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        if (uiState.logs.isEmpty() && !showLogSheet) {
            EmptyState(
                icon = Icons.Default.FitnessCenter,
                title = "No Custom Workouts",
                description = "Log your own workouts with custom exercises, sets, and reps.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(uiState.logs, key = { it.id }) { log ->
                    CustomWorkoutCard(
                        log = log,
                        onDelete = { viewModel.deleteLog(log.id) }
                    )
                }
            }
        }

        if (showLogSheet) {
            CustomWorkoutSheet(
                onDismiss = { showLogSheet = false },
                onSave = { name, exercises ->
                    viewModel.saveCustomWorkout(name, exercises)
                    showLogSheet = false
                },
                onGetSuggestions = { query -> viewModel.getExerciseSuggestions(query) },
                suggestions = uiState.suggestions
            )
        }
    }
}

@Composable
fun CustomWorkoutCard(
    log: CustomWorkoutLog,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    FitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = log.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle details"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Text(
                text = "${log.exercises.size} exercises",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    log.exercises.forEach { exercise ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(exercise.name, style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(4.dp))
                                exercise.sets.forEachIndexed { i, set ->
                                    Text(
                                        "Set ${i + 1}: ${set.weight}kg × ${set.reps} reps",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutSheet(
    onDismiss: () -> Unit,
    onSave: (String, List<CustomExerciseLog>) -> Unit,
    onGetSuggestions: (String) -> Unit,
    suggestions: List<String>
) {
    var workoutName by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf(listOf(EditableExercise())) }

    FitModal(
        isOpen = true,
        onDismiss = onDismiss,
        title = "Log Custom Workout"
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            items(exercises.size) { index ->
                ExerciseEditor(
                    exercise = exercises[index],
                    onUpdate = { updated ->
                        exercises = exercises.toMutableList().also { it[index] = updated }
                    },
                    onRemove = {
                        if (exercises.size > 1) {
                            exercises = exercises.toMutableList().also { it.removeAt(index) }
                        }
                    },
                    onSearchExercise = onGetSuggestions,
                    suggestions = suggestions
                )
            }

            item {
                TextButton(
                    onClick = { exercises = exercises + EditableExercise() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Add Exercise")
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                GradientButton(
                    text = "Save Workout",
                    onClick = {
                        val logs = exercises.filter { it.name.isNotBlank() }.map { ex ->
                            CustomExerciseLog(
                                name = ex.name,
                                sets = ex.sets.filter { it.reps > 0 }.map { s ->
                                    CustomSet(weight = s.weight, reps = s.reps)
                                }
                            )
                        }
                        if (logs.isNotEmpty()) onSave(workoutName.ifBlank { "Custom Workout" }, logs)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private data class EditableExercise(
    val name: String = "",
    val sets: List<EditableSet> = listOf(EditableSet())
)

private data class EditableSet(
    val weight: Double = 0.0,
    val reps: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseEditor(
    exercise: EditableExercise,
    onUpdate: (EditableExercise) -> Unit,
    onRemove: () -> Unit,
    onSearchExercise: (String) -> Unit,
    suggestions: List<String>
) {
    var showSuggestions by remember { mutableStateOf(false) }

    FitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = showSuggestions && suggestions.isNotEmpty(),
                    onExpandedChange = { showSuggestions = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = exercise.name,
                        onValueChange = { name ->
                            onUpdate(exercise.copy(name = name))
                            if (name.length >= 2) {
                                onSearchExercise(name)
                                showSuggestions = true
                            }
                        },
                        label = { Text("Exercise") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    ExposedDropdownMenu(
                        expanded = showSuggestions && suggestions.isNotEmpty(),
                        onDismissRequest = { showSuggestions = false }
                    ) {
                        suggestions.take(5).forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    onUpdate(exercise.copy(name = suggestion))
                                    showSuggestions = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Remove exercise")
                }
            }

            Spacer(Modifier.height(8.dp))

            exercise.sets.forEachIndexed { setIndex, set ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Set ${setIndex + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(40.dp)
                    )
                    OutlinedTextField(
                        value = if (set.weight > 0) set.weight.toString() else "",
                        onValueChange = { v ->
                            val w = v.toDoubleOrNull() ?: 0.0
                            val newSets = exercise.sets.toMutableList()
                            newSets[setIndex] = set.copy(weight = w)
                            onUpdate(exercise.copy(sets = newSets))
                        },
                        label = { Text("kg") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = if (set.reps > 0) set.reps.toString() else "",
                        onValueChange = { v ->
                            val r = v.toIntOrNull() ?: 0
                            val newSets = exercise.sets.toMutableList()
                            newSets[setIndex] = set.copy(reps = r)
                            onUpdate(exercise.copy(sets = newSets))
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        singleLine = true
                    )
                }
            }

            TextButton(
                onClick = {
                    onUpdate(exercise.copy(sets = exercise.sets + EditableSet()))
                }
            ) {
                Text("+ Add Set")
            }
        }
    }
}
