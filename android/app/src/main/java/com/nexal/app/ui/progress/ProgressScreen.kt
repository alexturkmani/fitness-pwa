package com.nexal.app.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
                ScalePopIn {
                    MomentumHero(
                        workouts = uiState.weeklyWorkouts,
                        averageCalories = uiState.avgDailyCalories,
                        weightChange = uiState.weightChange
                    )
                }
            }
            item {
                FadeSlideIn {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            icon = Icons.Default.FitnessCenter,
                            iconColor = Accent,
                            label = "Workouts This Week",
                            value = "${uiState.weeklyWorkouts}",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.LocalFireDepartment,
                            iconColor = Accent,
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
                            iconColor = Accent,
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
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "WEIGHT TREND",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.weightData.isEmpty()) {
                                Text(
                                    "Log your weight to see trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                                )
                            } else {
                                val recent = uiState.weightData.takeLast(12)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "${recent.last().second} kg",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val delta = recent.last().second - recent.first().second
                                    Text(
                                        (if (delta >= 0) "+" else "") + String.format("%.1f", delta) + " kg",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (delta <= 0) SuccessGreen else WarningAmber,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                LineTrendChart(
                                    points = recent.map { it.second.toFloat() },
                                    labels = listOf(recent.first().first, recent.last().first)
                                )
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
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "WORKOUT VOLUME (KG × REPS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.volumeData.isEmpty()) {
                                Text(
                                    "Complete workouts to see volume trends",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 32.dp)
                                )
                            } else {
                                val vols = uiState.volumeData.takeLast(7)
                                Text(
                                    "${vols.sumOf { it.second }} kg lifted",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                WeeklyBarChart(data = vols.map { it.first to it.second })
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 180) {
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "DAILY CALORIES & PROTEIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.calorieData.isEmpty()) {
                                Text(
                                    "Log meals to see daily intake",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 32.dp)
                                )
                            } else {
                                val cals = uiState.calorieData.takeLast(7)
                                Text(
                                    "${cals.sumOf { it.second } / cals.size} cal / day",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                WeeklyBarChart(data = cals.map { it.first to it.second })
                            }
                        }
                    }
                }
            }

            item {
                FadeSlideIn(delayMs = 200) {
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "AVG. DAILY MACRO SPLIT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "DAILY WATER INTAKE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "DAILY CARDIO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
private fun MomentumHero(
    workouts: Int,
    averageCalories: Int,
    weightChange: Double
) {
    val momentum = (workouts * 20 + (if (averageCalories > 0) 18 else 0) + (if (weightChange != 0.0) 12 else 0))
        .coerceIn(8, 100)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = HeroInk
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text("YOUR MOMENTUM", style = MaterialTheme.typography.labelSmall, color = AccentBright)
            Spacer(Modifier.height(6.dp))
            Text("Progress, made visible.", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Text(
                "Your daily wins connect into one clear picture.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.62f)
            )
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(118.dp)) {
                    ActivityOrb(modifier = Modifier.fillMaxSize(), progress = momentum / 100f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(color = AccentBright, shape = RoundedCornerShape(999.dp)) {
                            Text(
                                "$momentum%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = HeroInk,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        Text("MOMENTUM", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                }
                Spacer(Modifier.width(20.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MomentumMetric("WORKOUTS", "$workouts this week")
                    MomentumMetric("NUTRITION", if (averageCalories > 0) "$averageCalories cal daily" else "Start logging")
                    MomentumMetric("WEIGHT TREND", if (weightChange == 0.0) "Building baseline" else "${if (weightChange > 0) "+" else ""}${"%.1f".format(weightChange)} kg")
                }
            }
        }
    }
}

@Composable
private fun MomentumMetric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = AccentBright)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
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
