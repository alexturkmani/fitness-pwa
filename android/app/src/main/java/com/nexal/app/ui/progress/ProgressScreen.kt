package com.nexal.app.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showWeightModal by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { showWeightModal = true }) {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null, modifier = Modifier.size(18.dp), tint = BrandBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Weight", color = BrandBlue)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(padding)
        ) {
            item {
                FadeSlideIn {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            icon = Icons.Default.FitnessCenter,
                            iconColor = BrandBlue,
                            label = "Workouts This Week",
                            value = "${uiState.weeklyWorkouts}",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.LocalFireDepartment,
                            iconColor = MacroCalorie,
                            label = "Avg Daily Calories",
                            value = "${uiState.avgDailyCalories}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item {
                ScalePopIn(delayMs = 60) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            icon = Icons.Default.MonitorWeight,
                            iconColor = Cyan500,
                            label = "Current Weight",
                            value = "${uiState.currentWeight} kg",
                            modifier = Modifier.weight(1f)
                        )
                        val changeColor = when {
                            uiState.weightChange < 0 -> SuccessGreen
                            uiState.weightChange > 0 -> WarningAmber
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val changeIcon = when {
                            uiState.weightChange < 0 -> Icons.Default.TrendingDown
                            uiState.weightChange > 0 -> Icons.Default.TrendingUp
                            else -> Icons.Default.Remove
                        }
                        StatCard(
                            icon = changeIcon,
                            iconColor = changeColor,
                            label = "Weight Change",
                            value = "${if (uiState.weightChange > 0) "+" else ""}${"%.1f".format(uiState.weightChange)} kg",
                            valueColor = changeColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 100) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Weight Trend", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.weightData.isEmpty()) {
                                Text(
                                    "Log your weight to see trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                                )
                            } else {
                                uiState.weightData.takeLast(10).forEach { (date, weight) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$weight kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    }
                                }
                                if (uiState.targetWeight > 0) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Target", style = MaterialTheme.typography.bodySmall, color = BrandBlue)
                                        Text("${uiState.targetWeight} kg", style = MaterialTheme.typography.bodySmall, color = BrandBlue, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 140) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Workout Volume (kg × reps)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.volumeData.isEmpty()) {
                                Text(
                                    "Complete workouts to see volume trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 32.dp)
                                )
                            } else {
                                uiState.volumeData.forEach { (week, volume, workouts) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(week, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text("$volume kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                            Text("$workouts sessions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 180) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Daily Calories & Protein", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            uiState.calorieData.forEach { (day, calories, protein) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text("$calories cal", style = MaterialTheme.typography.bodySmall, color = MacroCalorie)
                                        Text("${protein}g protein", style = MaterialTheme.typography.bodySmall, color = MacroProtein)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 200) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Avg. Daily Macro Split", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            MacroSplitRow("Protein", uiState.avgProtein, "g", MacroProtein)
                            MacroSplitRow("Carbs", uiState.avgCarbs, "g", MacroCarbs)
                            MacroSplitRow("Fats", uiState.avgFats, "g", MacroFat)
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 220) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Daily Water Intake", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.waterData.isEmpty() || uiState.waterData.all { it.second == 0 }) {
                                Text(
                                    "Log your water intake to see trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                uiState.waterData.forEach { (day, ml) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${ml} ml", style = MaterialTheme.typography.bodySmall, color = Cyan500)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Avg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${uiState.avgDailyWater} ml/day", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = Cyan500)
                                }
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 240) {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Daily Cardio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.cardioData.isEmpty() || uiState.cardioData.all { it.second == 0 }) {
                                Text(
                                    "Log cardio sessions to see trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                uiState.cardioData.forEach { (day, calories, duration) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Text("$calories cal", style = MaterialTheme.typography.bodySmall, color = MacroFat)
                                            Text("${duration} min", style = MaterialTheme.typography.bodySmall, color = Cyan500)
                                        }
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Avg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${uiState.avgCardioCalories} cal/day", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MacroFat)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showWeightModal) {
            FitModal(
                isOpen = true,
                onDismiss = { showWeightModal = false },
                title = "Log Weight"
            ) {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("${uiState.currentWeight}") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                GradientButton(
                    text = "Save Weight",
                    onClick = {
                        newWeight.toDoubleOrNull()?.let {
                            viewModel.addWeight(it)
                            newWeight = ""
                            showWeightModal = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    FitCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun MacroSplitRow(label: String, value: Int, unit: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = color, shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.size(12.dp)) {}
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("$value$unit", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
