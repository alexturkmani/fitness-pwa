package com.nexal.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Auth
    data object Landing : Screen("landing")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object ResetPassword : Screen("reset_password/{token}") {
        fun createRoute(token: String) = "reset_password/$token"
    }

    // Main
    data object Dashboard : Screen("dashboard")
    data object Onboarding : Screen("onboarding")
    data object Workouts : Screen("workouts")
    data object WorkoutLog : Screen("workout_log/{planId}/{dayId}") {
        fun createRoute(planId: String, dayId: String) = "workout_log/$planId/$dayId"
    }
    data object CustomWorkouts : Screen("custom_workouts")
    data object Meals : Screen("meals")
    data object Nutrition : Screen("nutrition")
    data object Scanner : Screen("scanner")
    data object Progress : Screen("progress")
    data object Profile : Screen("profile")
    data object Subscription : Screen("subscription")
    data object Paywall : Screen("paywall")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isCenter: Boolean = false
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Meals, "Meals", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
    BottomNavItem(Screen.Workouts, "Workouts", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, isCenter = true),
    BottomNavItem(Screen.Nutrition, "Nutrition", Icons.Filled.Spa, Icons.Outlined.Spa),
    BottomNavItem(Screen.Progress, "Progress", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
)
