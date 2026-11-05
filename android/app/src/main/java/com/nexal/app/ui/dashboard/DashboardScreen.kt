package com.nexal.app.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.nexal.app.R
import com.nexal.app.data.repository.FastingMath
import com.nexal.app.ui.components.*
import com.nexal.app.ui.fasting.FastingViewModel
import com.nexal.app.ui.rewards.RewardViewModel
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
    onNavigateToSubscription: () -> Unit,
    onNavigateToFasting: () -> Unit,
    onUpgrade: () -> Unit,
    isPremium: Boolean,
    viewModel: DashboardViewModel = hiltViewModel(),
    fastingViewModel: FastingViewModel = hiltViewModel(),
    rewardViewModel: RewardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fasting by fastingViewModel.uiState.collectAsStateWithLifecycle()
    val rewardProgress by rewardViewModel.progress.collectAsStateWithLifecycle()
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

    PhotoBackdrop(photo = R.drawable.bg_home) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        dateLabel.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Hi, ${uiState.userName.ifBlank { "Athlete" }}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = GlassText
                    )
                }
                GlassIconButton(Icons.Default.Person, "Profile", onNavigateToProfile)
            }

            // Leaves the athlete visible before the cards begin.
            Spacer(Modifier.height(170.dp))

            if (uiState.showTrialExpiredBanner) {
                GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onUpgrade) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(Icons.Default.WorkspacePremium, tint = GlassText)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Subscribe to unlock AI plans", fontWeight = FontWeight.Bold, color = GlassText)
                            Text(
                                "Get personalized workouts, meals, and barcode scanning",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextMuted
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            FadeSlideIn {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(30.dp).clip(CircleShape).background(AccentBright),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${rewardProgress.level}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = HeroInk
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("Level ${rewardProgress.level}", style = MaterialTheme.typography.titleMedium, color = GlassText)
                            }
                            Text(
                                "${rewardProgress.streakDays} day streak · ${rewardProgress.xp} XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlassTextMuted
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        GlassProgressBar(rewardProgress.levelProgress)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            if (!uiState.hasWorkoutPlan || !uiState.hasMealPlan) {
                FadeSlideIn {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Get your first win", style = MaterialTheme.typography.titleLarge, color = GlassText)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                uiState.firstWinMessage
                                    ?: if (isPremium) {
                                        "Generate a personalized workout and meal plan in one tap."
                                    } else {
                                        "Unlock personalized AI workout and meal plans."
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = GlassTextMuted
                            )
                            Spacer(Modifier.height(14.dp))
                            GradientButton(
                                text = when {
                                    uiState.isGeneratingPlans -> "Creating plans…"
                                    isPremium -> "Create my plans"
                                    else -> "Unlock AI plans"
                                },
                                onClick = { if (isPremium) viewModel.generateStarterPlans() else onUpgrade() },
                                loading = uiState.isGeneratingPlans,
                                enabled = !uiState.isGeneratingPlans,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            FadeSlideIn(delayMs = 60) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.FitnessCenter,
                        label = "Today's workout",
                        value = uiState.todayWorkout ?: "No plan yet",
                        onClick = onNavigateToWorkouts
                    )
                    ActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        label = if (fasting.activeFast != null) "Fasting" else "Fasting timer",
                        value = if (fasting.activeFast != null) {
                            FastingMath.formatClock(fasting.elapsedMillis)
                        } else {
                            "Start a fast"
                        },
                        onClick = onNavigateToFasting
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            ScalePopIn(delayMs = 80) {
                GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToDiary) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        CalorieRing(
                            consumed = uiState.caloriesToday,
                            goal = uiState.calorieGoal,
                            size = 118.dp,
                            stroke = 10.dp,
                            ringColor = AccentBright,
                            valueColor = GlassText,
                            labelColor = GlassTextMuted
                        )
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            DarkMacroRow("PROTEIN", uiState.proteinToday, uiState.proteinGoal, MacroProtein)
                            DarkMacroRow("CARBS", uiState.carbsToday, uiState.carbsGoal, MacroCarbs)
                            DarkMacroRow("FATS", uiState.fatsToday, uiState.fatsGoal, MacroFat)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            FadeSlideIn(delayMs = 140) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassStat("${uiState.workoutsThisWeek}", "workouts this week", Modifier.weight(1f), Icons.Default.FitnessCenter)
                        GlassStat(
                            formatWater(uiState.waterTotalMl, uiState.unitSystem),
                            "of ${formatWater(uiState.waterGoalMl, uiState.unitSystem)} water",
                            Modifier.weight(1f),
                            Icons.Default.WaterDrop
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassStat(
                            formatWeight(uiState.currentWeight, uiState.unitSystem),
                            run {
                                val change = formatWeight(kotlin.math.abs(uiState.weightChange), uiState.unitSystem)
                                "${if (uiState.weightChange >= 0) "+" else "-"}$change weight"
                            },
                            Modifier.weight(1f),
                            Icons.Default.MonitorWeight
                        )
                        GlassStat("${uiState.cardioCaloriesToday}", "cardio cal burnt", Modifier.weight(1f), Icons.Default.DirectionsRun)
                    }
                }
            }

            if (uiState.weeklyCalories.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                FadeSlideIn(delayMs = 200) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("${uiState.avgDailyCalories}", style = MaterialTheme.typography.headlineMedium, color = GlassText)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "cal / day this week",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassTextMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            WeeklyBarChart(data = uiState.weeklyCalories)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToSubscription) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = GlassText, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Premium AI", fontWeight = FontWeight.SemiBold, color = GlassText, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = GlassTextMuted)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ActionTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    GlassCard(modifier = modifier, onClick = onClick, cornerRadius = 22.dp) {
        Column(Modifier.padding(16.dp)) {
            IconBadge(icon, tint = GlassText)
            Spacer(Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = GlassTextMuted)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = GlassText,
                maxLines = 1
            )
        }
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
        GlowBar(fraction, color = color, height = 6.dp, track = Color.White.copy(alpha = 0.12f))
    }
}
