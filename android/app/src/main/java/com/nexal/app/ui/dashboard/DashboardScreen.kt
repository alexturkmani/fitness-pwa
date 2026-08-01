package com.nexal.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*
import com.nexal.app.util.formatWater
import com.nexal.app.util.formatWeight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToWorkouts: () -> Unit,
    onNavigateToMeals: () -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.onboardingCompleted) {
        EmptyState(
            icon = Icons.Default.FitnessCenter,
            title = "Welcome to Nexal",
            description = "Set up your profile so we can build a personalized diary and training plan.",
            actionLabel = "Get Started",
            onAction = onNavigateToOnboarding
        )
        return
    }

    val dateLabel = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (uiState.showTrialExpiredBanner) {
            FadeSlideIn {
                Surface(
                    color = ErrorRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = ErrorRed, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Trial expired", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Subscribe to keep premium AI plans",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        FadeSlideIn {
            Text(
                "Hi, ${uiState.userName.ifBlank { "Athlete" }}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (!uiState.hasWorkoutPlan || !uiState.hasMealPlan) {
            FadeSlideIn {
                FitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Get your first win",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            uiState.firstWinMessage
                                ?: "Generate a personalized workout and meal plan in one tap.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        GradientButton(
                            text = if (uiState.isGeneratingPlans) "Creating plans…" else "Create my plans",
                            onClick = { viewModel.generateStarterPlans() },
                            loading = uiState.isGeneratingPlans,
                            enabled = !uiState.isGeneratingPlans,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Diary hero — calorie ring + macros
        ScalePopIn(delayMs = 80) {
            FitCard(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToDiary) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Calories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CalorieRing(
                        consumed = uiState.caloriesToday,
                        goal = uiState.calorieGoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${uiState.caloriesToday} eaten · ${uiState.calorieGoal} goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroChip("Protein", uiState.proteinToday, uiState.proteinGoal, MacroProtein)
                        MacroChip("Carbs", uiState.carbsToday, uiState.carbsGoal, MacroCarbs)
                        MacroChip("Fat", uiState.fatsToday, uiState.fatsGoal, MacroFat)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FadeSlideIn(delayMs = 140) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Workouts",
                    value = "${uiState.workoutsThisWeek}",
                    hint = "this week",
                    icon = Icons.Default.FitnessCenter,
                    tint = BrandBlue
                )
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Water",
                    value = formatWater(uiState.waterTotalMl, uiState.unitSystem),
                    hint = "of ${formatWater(uiState.waterGoalMl, uiState.unitSystem)}",
                    icon = Icons.Default.WaterDrop,
                    tint = Cyan500
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FadeSlideIn(delayMs = 180) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Weight",
                    value = formatWeight(uiState.currentWeight, uiState.unitSystem),
                    hint = run {
                        val change = formatWeight(kotlin.math.abs(uiState.weightChange), uiState.unitSystem)
                        "${if (uiState.weightChange >= 0) "+" else "-"}$change"
                    },
                    icon = Icons.Default.MonitorWeight,
                    tint = MacroCarbs
                )
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Cardio",
                    value = "${uiState.cardioCaloriesToday}",
                    hint = "cal burnt",
                    icon = Icons.Default.DirectionsRun,
                    tint = MacroFat
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        FadeSlideIn(delayMs = 220) {
            Text("Today", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            FitCard(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToWorkouts) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Emerald50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, null, tint = BrandBlue)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Workout", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            uiState.todayWorkout ?: "No plan yet — generate one",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        FadeSlideIn(delayMs = 260) {
            Text("Quick add", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(Modifier.weight(1f), "Log food", Icons.Default.Restaurant, onNavigateToDiary)
                QuickAction(Modifier.weight(1f), "Train", Icons.Default.FitnessCenter, onNavigateToWorkouts)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(Modifier.weight(1f), "Profile", Icons.Default.Person, onNavigateToProfile)
                QuickAction(Modifier.weight(1f), "Goals", Icons.Default.Flag, onNavigateToProfile)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MiniStat(
    modifier: Modifier,
    label: String,
    value: String,
    hint: String,
    icon: ImageVector,
    tint: Color
) {
    FitCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Text(hint, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FitCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Emerald50),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BrandBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}
