package com.nexal.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nexal.app.MainActivity
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.domain.model.AuthState
import com.nexal.app.ui.auth.*
import com.nexal.app.ui.components.LoadingScreen
import com.nexal.app.ui.dashboard.DashboardScreen
import com.nexal.app.ui.meals.MealsScreen
import com.nexal.app.ui.nutrition.NutritionScreen
import com.nexal.app.ui.onboarding.OnboardingScreen
import com.nexal.app.ui.paywall.PaywallScreen
import com.nexal.app.ui.profile.ProfileScreen
import com.nexal.app.ui.progress.ProgressScreen
import com.nexal.app.ui.scanner.ScannerScreen
import com.nexal.app.ui.subscription.SubscriptionScreen
import com.nexal.app.ui.theme.*
import com.nexal.app.ui.workouts.*

@Composable
fun NexalNavHost(
    navVm: NavViewModel = hiltViewModel()
) {
    val authRepository = navVm.authRepository
    val profileRepository = navVm.profileRepository
    val authState by authRepository.authState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    when (val state = authState) {
        is AuthState.Loading -> LoadingScreen("Starting Nexal...")
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

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { pushEnter() },
        exitTransition = { pushExit() },
        popEnterTransition = { popEnter() },
        popExitTransition = { popExit() }
    ) {
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
                navDeepLink { uriPattern = "nexal://app/reset-password/{token}" }
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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                NexalBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        if (currentRoute == screen.route) return@NexalBottomBar
                        mainNavController.navigate(screen.route) {
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
            modifier = Modifier.padding(paddingValues),
            enterTransition = { tabEnterTransition() },
            exitTransition = { tabExitTransition() },
            popEnterTransition = { tabEnterTransition() },
            popExitTransition = { tabExitTransition() }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToWorkouts = { mainNavController.navigate(Screen.Workouts.route) },
                    onNavigateToMeals = { mainNavController.navigate(Screen.Meals.route) },
                    onNavigateToDiary = { mainNavController.navigate(Screen.Nutrition.route) },
                    onNavigateToOnboarding = { mainNavController.navigate(Screen.Onboarding.route) },
                    onNavigateToProfile = { mainNavController.navigate(Screen.Profile.route) }
                )
            }
            composable(
                route = Screen.Onboarding.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
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
                ),
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) { backStackEntry ->
                WorkoutLogScreen(
                    planId = backStackEntry.arguments?.getString("planId") ?: "",
                    dayId = backStackEntry.arguments?.getString("dayId") ?: "",
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(
                route = Screen.CustomWorkouts.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
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
            composable(
                route = Screen.Scanner.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
                ScannerScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen()
            }
            composable(
                route = Screen.Profile.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
                ProfileScreen(
                    onNavigateToSubscription = {
                        mainNavController.navigate(Screen.Subscription.route)
                    },
                    onBack = { mainNavController.popBackStack() },
                    onSignedOut = { /* AuthState change triggers recomposition */ }
                )
            }
            composable(
                route = Screen.Subscription.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
                SubscriptionScreen(
                    onBack = { mainNavController.popBackStack() }
                )
            }
            composable(
                route = Screen.Paywall.route,
                enterTransition = { pushEnter() },
                exitTransition = { pushExit() },
                popEnterTransition = { popEnter() },
                popExitTransition = { popExit() }
            ) {
                PaywallScreen(
                    onBack = { mainNavController.popBackStack() },
                    onSubscribed = { mainNavController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun NexalBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
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
                                .clip(CircleShape)
                                .background(BrandBlue),
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
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
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
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandBlue,
                        selectedTextColor = BrandBlue,
                        indicatorColor = Emerald100
                    )
                )
            }
        }
    }
}
