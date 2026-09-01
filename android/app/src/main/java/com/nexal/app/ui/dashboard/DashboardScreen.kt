package com.nexal.app.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*
import com.nexal.app.util.PlayReviewPrompt
import com.nexal.app.util.formatWater
import com.nexal.app.util.formatWeight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun DashboardScreen(
    onNavigateToWorkouts: () -> Unit,
    onNavigateToMeals: () -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onUpgrade: () -> Unit,
    isPremium: Boolean,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.promptPlayReview) {
        if (uiState.promptPlayReview) {
            context.findActivity()?.let { PlayReviewPrompt.maybeRequest(it) }
            viewModel.markReviewPrompted()
        }
    }

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
                    onClick = onUpgrade,
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
                            Text(
                                "Subscribe to unlock AI plans",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Get personalized workouts, meals, and barcode scanning",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Dark hero: the one high-contrast surface on the screen, so the
        // greeting anchors the page instead of competing with the cards.
        FadeSlideIn {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = HeroInk,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)) {
                    Text(
                        dateLabel.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentBright
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Hi, ${uiState.userName.ifBlank { "Athlete" }}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Let's keep the streak going.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.62f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!uiState.hasWorkoutPlan || !uiState.hasMealPlan) {
            FadeSlideIn {
                FitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ActivityOrb(modifier = Modifier.size(76.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Get your first win",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    uiState.firstWinMessage
                                        ?: if (isPremium) {
                                            "Generate a personalized workout and meal plan in one tap."
                                        } else {
                                            "Unlock personalized AI workout and meal plans."
                                        },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        GradientButton(
                            text = when {
                                uiState.isGeneratingPlans -> "Creating plans…"
                                isPremium -> "Create my plans"
                                else -> "Unlock AI plans"
                            },
                            onClick = {
                                if (isPremium) viewModel.generateStarterPlans() else onUpgrade()
                            },
                            loading = uiState.isGeneratingPlans,
                            enabled = !uiState.isGeneratingPlans,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Website-matched daily progress hero: near-black surface, lime ring,
        // connected nutrition metrics and a clear entry into the diary.
        ScalePopIn(delayMs = 80) {
            Surface(
                onClick = onNavigateToDiary,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = HeroInk
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TODAY'S PROGRESS", style = MaterialTheme.typography.labelSmall, color = AccentBright)
                            Spacer(Modifier.height(5.dp))
                            Text("On track", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        }
                        Surface(color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(10.dp)) {
                            Text("View diary  →", modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CalorieRing(
                            consumed = uiState.caloriesToday,
                            goal = uiState.calorieGoal,
                            size = 138.dp,
                            stroke = 12.dp,
                            ringColor = AccentBright,
                            valueColor = Color.White,
                            labelColor = Color.White.copy(alpha = 0.55f)
                        )
                        Spacer(Modifier.width(22.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            DarkMacroRow("PROTEIN", uiState.proteinToday, uiState.proteinGoal, MacroProtein)
                            DarkMacroRow("CARBS", uiState.carbsToday, uiState.carbsGoal, MacroCarbs)
                            DarkMacroRow("FATS", uiState.fatsToday, uiState.fatsGoal, MacroFat)
                        }
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
                    tint = Accent
                )
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Water",
                    value = formatWater(uiState.waterTotalMl, uiState.unitSystem),
                    hint = "of ${formatWater(uiState.waterGoalMl, uiState.unitSystem)}",
                    icon = Icons.Default.WaterDrop,
                    tint = Accent
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
                    tint = Accent
                )
                MiniStat(
                    modifier = Modifier.weight(1f),
                    label = "Cardio",
                    value = "${uiState.cardioCaloriesToday}",
                    hint = "cal burnt",
                    icon = Icons.Default.DirectionsRun,
                    tint = Accent
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (uiState.weeklyCalories.isNotEmpty()) {
            FadeSlideIn(delayMs = 200) {
                Text(
                    "THIS WEEK",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                FitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "${uiState.avgDailyCalories}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "cal / day avg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        WeeklyBarChart(data = uiState.weeklyCalories)
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        FadeSlideIn(delayMs = 220) {
            Text(
                "TODAY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                            .background(AccentWash),
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
            Text(
                "QUICK ADD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
private fun DarkMacroRow(label: String, current: Int, goal: Int, color: Color) {
    val fraction = if (goal > 0) (current.toFloat() / goal).coerceIn(0f, 1f) else 0f
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.54f))
            Text("${current}g", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)).background(Color.White.copy(alpha = 0.1f))) {
            Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().clip(RoundedCornerShape(5.dp)).background(color))
        }
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
                    .background(AccentWash),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BrandBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}
