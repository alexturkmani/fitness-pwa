package com.fitmate.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.fitmate.app.MainActivity
import com.fitmate.app.data.repository.ProfileRepository
import com.fitmate.app.domain.model.AuthState
import com.fitmate.app.ui.auth.*
import com.fitmate.app.ui.components.LoadingScreen
import com.fitmate.app.ui.dashboard.DashboardScreen
import com.fitmate.app.ui.meals.MealsScreen
import com.fitmate.app.ui.nutrition.NutritionScreen
import com.fitmate.app.ui.onboarding.OnboardingScreen
import com.fitmate.app.ui.paywall.PaywallScreen
import com.fitmate.app.ui.profile.ProfileScreen
import com.fitmate.app.ui.progress.ProgressScreen
import com.fitmate.app.ui.scanner.ScannerScreen
import com.fitmate.app.ui.subscription.SubscriptionScreen
import com.fitmate.app.ui.theme.*
import com.fitmate.app.ui.workouts.*

@Composable
fun FitMateNavHost(
    navVm: NavViewModel = hiltViewModel()
) {
    val authRepository = navVm.authRepository
    val profileRepository = navVm.profileRepository
    val authState by authRepository.authState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    when (val state = authState) {
        is AuthState.Loading -> LoadingScreen("Starting FitMate...")
        is AuthState.Unauthenticated -> {
            AuthNavHost(navController = navController)
        }
        is AuthState.Authenticated -> {
            if (!state.hasAccess) {
                GatedFlowHost(profileRepository = profileRepository)
            } else {
                MainScaffold()
            }
        }
    }
}

/**
 * When the user is authenticated but has no access (no trial / no subscription),
 * show onboarding first (if not completed) then the paywall.
 */
@Composable
private fun GatedFlowHost(
    profileRepository: ProfileRepository
) {
    val profile by profileRepository.observeProfile().collectAsStateWithLifecycle(initialValue = null)
    val onboardingDone = profile?.onboardingCompleted == true
    // null means still loading from DB — wait briefly
    val profileLoaded = profile != null

    if (!profileLoaded) {
        // Profile loading from local Room DB — show a brief loading
        LoadingScreen("Loading...")
        return
    }

    val gatedNavController = rememberNavController()
    val startDest = if (onboardingDone) Screen.Paywall.route else Screen.Onboarding.route

    NavHost(navController = gatedNavController, startDestination = startDest) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    gatedNavController.navigate(Screen.Paywall.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Paywall.route) {
            PaywallScreen(
                onBack = { /* can't skip paywall */ },
                onSubscribed = { /* AuthState change triggers recomposition */ }
            )
        }
    }
}

@Composable
private fun AuthNavHost(navController: NavHostController) {
    // Listen for deep links from MainActivity
    LaunchedEffect(Unit) {
        MainActivity.deepLinkFlow.collect { route ->
            try {
                navController.navigate(route) {
                    launchSingleTop = true
                }
            } catch (_: Exception) { /* route not found */ }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = { /* AuthState change triggers recomposition */ }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "fitmate://app/reset-password/{token}" }
            )
        ) { backStackEntry ->
            ResetPasswordScreen(
                token = backStackEntry.arguments?.getString("token") ?: "",
                onSuccess = { navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }
    }
}

@Composable
private fun MainScaffold() {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FitMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        if (currentRoute == screen.route) return@FitMateBottomBar
                        mainNavController.navigate(screen.route) {
                            // Pop everything up to dashboard to avoid stacking
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToWorkouts = { mainNavController.navigate(Screen.Workouts.route) },
                    onNavigateToMeals = { mainNavController.navigate(Screen.Meals.route) },
                    onNavigateToOnboarding = { mainNavController.navigate(Screen.Onboarding.route) },
                    onNavigateToProfile = { mainNavController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        mainNavController.navigate(Screen.Paywall.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Workouts.route) {
                WorkoutsScreen(
                    onNavigateToLog = { planId, dayId ->
                        mainNavController.navigate(Screen.WorkoutLog.createRoute(planId, dayId))
                    },
                    onNavigateToCustom = { mainNavController.navigate(Screen.CustomWorkouts.route) }
                )
            }
            composable(
                route = Screen.WorkoutLog.route,
                arguments = listOf(
                    navArgument("planId") { type = NavType.StringType },
                    navArgument("dayId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                WorkoutLogScreen(
                    planId = backStackEntry.arguments?.getString("planId") ?: "",
                    dayId = backStackEntry.arguments?.getString("dayId") ?: "",
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(Screen.CustomWorkouts.route) {
                CustomWorkoutsScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(Screen.Meals.route) {
                MealsScreen()
            }
            composable(Screen.Nutrition.route) {
                NutritionScreen(
                    onNavigateToScanner = { mainNavController.navigate(Screen.Scanner.route) }
                )
            }
            composable(Screen.Scanner.route) {
                ScannerScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSubscription = {
                        mainNavController.navigate(Screen.Subscription.route)
                    },
                    onBack = { mainNavController.popBackStack() },
                    onSignedOut = { /* AuthState change triggers recomposition */ }
                )
            }
            composable(Screen.Subscription.route) {
                SubscriptionScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onBack = { mainNavController.popBackStack() },
                    onSubscribed = { mainNavController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun FitMateBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            if (item.isCenter) {
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(listOf(Emerald500, Cyan500))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                item.selectedIcon,
                                contentDescription = item.label,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            } else {
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald500,
                        selectedTextColor = Emerald500,
                        indicatorColor = Emerald500.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}
