package com.fitmate.app.ui.nutrition

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitmate.app.domain.model.FoodLogEntry
import com.fitmate.app.domain.model.FoodSource
import com.fitmate.app.domain.model.CardioLogEntry
import com.fitmate.app.ui.components.*
import com.fitmate.app.ui.theme.Cyan500
import com.fitmate.app.ui.theme.Emerald500
import com.fitmate.app.util.formatWater

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onNavigateToScanner: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddModal by remember { mutableStateOf(false) }
    var showCardioModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrition") },
                actions = {
                    TextButton(onClick = onNavigateToScanner) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Scan")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = Emerald500
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // 7-Day Calendar
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.weekData) { day ->
                        DayCalendarCell(
                            dayName = day.dayName,
                            dayNumber = day.dayNumber,
                            calPercent = if (uiState.calorieTarget > 0) (day.calories.toFloat() / uiState.calorieTarget).coerceAtMost(1.2f) else 0f,
                            isSelected = day.date == uiState.selectedDate,
                            isToday = day.isToday,
                            onClick = { viewModel.selectDate(day.date) }
                        )
                    }
                }
            }

            // Macro Progress Bars
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MacroProgressRow(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "Calories",
                            current = uiState.dayTotals.calories,
                            target = uiState.calorieTarget,
                            color = Emerald500
                        )
                        MacroProgressRow(
                            icon = Icons.Default.FitnessCenter,
                            label = "Protein",
                            current = uiState.dayTotals.protein,
                            target = uiState.proteinTarget,
                            unit = "g",
                            color = Emerald500
                        )
                        MacroProgressRow(
                            icon = Icons.Default.Grain,
                            label = "Carbs",
                            current = uiState.dayTotals.carbs,
                            target = uiState.carbsTarget,
                            unit = "g",
                            color = Cyan500
                        )
                        MacroProgressRow(
                            icon = Icons.Default.WaterDrop,
                            label = "Fats",
                            current = uiState.dayTotals.fats,
                            target = uiState.fatsTarget,
                            unit = "g",
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            // Food Log header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Food Log", style = MaterialTheme.typography.titleMedium)
                    Text("${uiState.dayEntries.size} entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Water Tracker Card
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.WaterDrop, null, tint = Cyan500, modifier = Modifier.size(20.dp))
                                Text("Water Intake", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                formatWater(uiState.waterTotalMl, uiState.unitSystem) + " / " + formatWater(uiState.waterGoalMl, uiState.unitSystem),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        val waterPercent = if (uiState.waterGoalMl > 0) (uiState.waterTotalMl.toFloat() / uiState.waterGoalMl).coerceAtMost(1f) else 0f
                        LinearProgressIndicator(
                            progress = { waterPercent },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = Cyan500,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(150, 250, 350, 500).forEach { ml ->
                                OutlinedButton(
                                    onClick = { viewModel.addWater(ml) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("${ml}ml", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        // Show water entries
                        if (uiState.waterEntries.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            uiState.waterEntries.forEach { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(formatWater(entry.amount, uiState.unitSystem), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    IconButton(onClick = { viewModel.removeWaterEntry(entry.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Cardio Log
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.DirectionsRun, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Text("Cardio Log", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                            TextButton(onClick = { showCardioModal = true }) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Text("Add")
                            }
                        }
                        if (uiState.cardioCaloriesToday > 0) {
                            Text(
                                "Total burnt: ${uiState.cardioCaloriesToday} cal",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF59E0B)
                            )
                        }
                        if (uiState.cardioEntries.isEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("No cardio logged today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Spacer(Modifier.height(8.dp))
                            uiState.cardioEntries.forEach { entry ->
                                CardioEntryRow(entry = entry, onDelete = { viewModel.removeCardioEntry(entry.id) })
                            }
                        }
                    }
                }
            }

            // Food Log Entries
            if (uiState.dayEntries.isEmpty()) {
                item {
                    FitCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No entries for this day", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(uiState.dayEntries, key = { it.id }) { entry ->
                    FoodEntryCard(
                        entry = entry,
                        onDelete = { viewModel.removeEntry(entry.id) }
                    )
                }
            }

            // Bottom spacer for FAB
            item { Spacer(Modifier.height(64.dp)) }
        }

        // Add Food Modal
        if (showAddModal) {
            LaunchedEffect(Unit) { viewModel.resetAutoFillState() }
            AddFoodModal(
                onDismiss = { showAddModal = false },
                onAdd = { name, serving, cal, protein, carbs, fats ->
                    viewModel.addManualEntry(name, serving, cal, protein, carbs, fats)
                    showAddModal = false
                },
                onFieldChange = viewModel::onFoodFieldChange,
                autoFillState = uiState.autoFillState
            )
        }

        // Add Cardio Modal
        if (showCardioModal) {
            AddCardioModal(
                cardioTypes = uiState.cardioTypes,
                onDismiss = { showCardioModal = false },
                onAdd = { type, duration, notes ->
                    viewModel.addCardioEntry(type, duration, notes)
                    showCardioModal = false
                }
            )
        }
    }

    uiState.toast?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearToast()
        }
    }
}

@Composable
private fun DayCalendarCell(
    dayName: String,
    dayNumber: String,
    calPercent: Float,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Emerald500 else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isToday) Emerald500 else MaterialTheme.colorScheme.onSurfaceVariant
    val ringColor = when {
        calPercent > 1f -> MaterialTheme.colorScheme.error
        calPercent > 0f -> Emerald500
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        onClick = onClick,
        color = if (isSelected) Emerald500.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(8.dp).width(44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
            Text(dayNumber, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Canvas(modifier = Modifier.size(24.dp)) {
                val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                drawArc(color = Color(0xFF1E293B), startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
                drawArc(color = ringColor, startAngle = -90f, sweepAngle = 360f * calPercent.coerceAtMost(1f), useCenter = false, style = stroke)
            }
        }
    }
}

@Composable
private fun MacroProgressRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    current: Int,
    target: Int,
    unit: String = "",
    color: Color
) {
    val percent = if (target > 0) (current.toFloat() / target).coerceAtMost(1f) else 0f
    val barColor = if (current > target && target > 0) MaterialTheme.colorScheme.error else color

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("$current$unit / $target$unit", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun FoodEntryCard(
    entry: FoodLogEntry,
    onDelete: () -> Unit
) {
    val sourceLabel = when (entry.source) {
        FoodSource.SCANNER -> "Scanned"
        FoodSource.MEAL_PLAN -> "Meal Plan"
        FoodSource.MANUAL -> "Manual"
    }
    val sourceColor = when (entry.source) {
        FoodSource.SCANNER -> Cyan500
        FoodSource.MEAL_PLAN -> Emerald500
        FoodSource.MANUAL -> Color(0xFFF59E0B)
    }

    FitCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.foodName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Surface(color = sourceColor.copy(alpha = 0.15f), shape = MaterialTheme.shapes.extraSmall) {
                        Text(sourceLabel, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = sourceColor)
                    }
                }
                Text(
                    "${entry.macros.calories} cal | P:${entry.macros.protein}g C:${entry.macros.carbs}g F:${entry.macros.fats}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

data class AutoFillState(
    val isLoading: Boolean = false,
    val autoFilled: Boolean = false,
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fats: String = ""
)

@Composable
private fun AddFoodModal(
    onDismiss: () -> Unit,
    onAdd: (name: String, serving: String, cal: Int, protein: Int, carbs: Int, fats: Int) -> Unit,
    onFieldChange: (field: String, value: String) -> Unit,
    autoFillState: AutoFillState
) {
    var name by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    // Update fields when AI auto-fill completes
    LaunchedEffect(autoFillState.autoFilled, autoFillState.calories, autoFillState.protein, autoFillState.carbs, autoFillState.fats) {
        if (autoFillState.autoFilled) {
            calories = autoFillState.calories
            protein = autoFillState.protein
            carbs = autoFillState.carbs
            fats = autoFillState.fats
        }
    }

    FitModal(
        isOpen = true,
        title = "Add Food Entry",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onFieldChange("name", it)
                },
                label = { Text("Food Name") },
                placeholder = { Text("e.g., Chicken Breast") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = servingSize,
                onValueChange = {
                    servingSize = it
                    onFieldChange("servingSize", it)
                },
                label = { Text("Serving Size / Weight") },
                placeholder = { Text("e.g., 150g") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (autoFillState.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Emerald500)
                    Text("Estimating nutrition info...", style = MaterialTheme.typography.bodySmall, color = Emerald500)
                }
            }

            if (autoFillState.autoFilled && !autoFillState.isLoading) {
                Surface(color = Emerald500.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                    Text(
                        "Nutrition auto-filled from AI estimate. You can adjust values.",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald500
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))
            GradientButton(
                text = "Add Entry",
                onClick = {
                    if (name.isNotBlank() && calories.isNotBlank()) {
                        onAdd(name, servingSize.ifBlank { "1 serving" }, calories.toIntOrNull() ?: 0, protein.toIntOrNull() ?: 0, carbs.toIntOrNull() ?: 0, fats.toIntOrNull() ?: 0)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CardioEntryRow(entry: CardioLogEntry, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.type, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                "${entry.durationMinutes} min • ${entry.estimatedCaloriesBurnt} cal burnt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!entry.notes.isNullOrBlank()) {
                Text(entry.notes!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCardioModal(
    cardioTypes: List<String>,
    onDismiss: () -> Unit,
    onAdd: (type: String, duration: Int, notes: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(cardioTypes.firstOrNull() ?: "Running") }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    FitModal(
        isOpen = true,
        title = "Log Cardio Session",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cardio Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    cardioTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { selectedType = type; expanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            GradientButton(
                text = "Log Cardio",
                onClick = {
                    val dur = duration.toIntOrNull()
                    if (dur != null && dur > 0) {
                        onAdd(selectedType, dur, notes)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
