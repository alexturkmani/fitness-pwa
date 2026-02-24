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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*
import com.nexal.app.util.formatWater

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWorkouts: () -> Unit,
    onNavigateToMeals: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.onboardingCompleted) {
        EmptyState(
            icon = Icons.Default.FitnessCenter,
            title = "Welcome to Nexal!",
            description = "Let's set up your profile so we can create your personalized fitness plan.",
            actionLabel = "Get Started",
            onAction = onNavigateToOnboarding
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Trial expiry banner
        if (uiState.showTrialExpiredBanner) {
            Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trial Expired", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Subscribe to continue using premium features", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        } else if (uiState.trialDaysLeft in 1..3) {
            Surface(
                color = Color(0xFFF59E0B).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trial ending soon", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("${uiState.trialDaysLeft} day${if (uiState.trialDaysLeft != 1) "s" else ""} left — subscribe to keep access", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Greeting
        Text(
            "Welcome back, ${uiState.userName}! 💪",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Here's your fitness overview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        // Stats Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Workouts\nThis Week",
                value = uiState.workoutsThisWeek.toString(),
                icon = Icons.Default.FitnessCenter
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Avg Daily\nCalories",
                value = uiState.avgDailyCalories.toString(),
                icon = Icons.Default.LocalFireDepartment
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Current\nWeight",
                value = "${uiState.currentWeight} kg",
                icon = Icons.Default.MonitorWeight
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Weight\nChange",
                value = "${if (uiState.weightChange >= 0) "+" else ""}${uiState.weightChange} kg",
                icon = Icons.Default.TrendingUp
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Water\nIntake",
                value = formatWater(uiState.waterTotalMl, uiState.unitSystem),
                icon = Icons.Default.WaterDrop
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Cardio\nBurnt",
                value = "${uiState.cardioCaloriesToday} cal",
                icon = Icons.Default.DirectionsRun
            )
        }
        Spacer(Modifier.height(24.dp))

        // Today's Workout Preview
        if (uiState.todayWorkout != null) {
            Text("Today's Workout", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            FitCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToWorkouts
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        uiState.todayWorkout!!,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tap to view exercises →",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald500
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Quick Actions
        Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Workouts",
                icon = Icons.Default.FitnessCenter,
                onClick = onNavigateToWorkouts
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Meals",
                icon = Icons.Default.Restaurant,
                onClick = onNavigateToMeals
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Profile",
                icon = Icons.Default.Person,
                onClick = onNavigateToProfile
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Settings",
                icon = Icons.Default.Settings,
                onClick = onNavigateToProfile
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: ImageVector
) {
    FitCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Emerald500, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FitCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Emerald500.copy(alpha = 0.15f), Cyan500.copy(alpha = 0.15f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Emerald500)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}
