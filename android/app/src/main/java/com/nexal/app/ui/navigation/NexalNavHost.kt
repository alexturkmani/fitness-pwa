package com.nexal.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
            AuthenticatedFlowHost(
                profileRepository = profileRepository,
                isPremium = state.isPremium
            )
        }
    }
}

@Composable
private fun AuthenticatedFlowHost(
    profileRepository: ProfileRepository,
    isPremium: Boolean
) {
    val profile by profileRepository.observeProfile().collectAsStateWithLifecycle(initialValue = null)
    val onboardingDone = profile?.onboardingCompleted == true

    if (onboardingDone) {
        MainScaffold(isPremium = isPremium)
    } else {
        OnboardingScreen(onComplete = { /* Profile flow opens the core app. */ })
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
private fun MainScaffold(isPremium: Boolean) {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }
    val navigateTopLevel: (Screen) -> Unit = { screen ->
        if (screen == Screen.Dashboard) {
            // Home is a destination, not a saved tab stack. Always collapse any
            // meal/workout/detail history back to the real dashboard instance.
            if (!mainNavController.popBackStack(Screen.Dashboard.route, inclusive = false) &&
                currentRoute != Screen.Dashboard.route
            ) {
                mainNavController.navigate(Screen.Dashboard.route) {
                    launchSingleTop = true
                }
            }
        } else if (currentRoute != screen.route) {
            mainNavController.navigate(screen.route) {
                popUpTo(Screen.Dashboard.route) {
                    inclusive = false
                    saveState = false
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

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
                    onNavigate = navigateTopLevel
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
                    onNavigateToWorkouts = { navigateTopLevel(Screen.Workouts) },
                    onNavigateToMeals = { navigateTopLevel(Screen.Meals) },
                    onNavigateToDiary = { navigateTopLevel(Screen.Nutrition) },
                    onNavigateToOnboarding = { mainNavController.navigate(Screen.Onboarding.route) },
                    onNavigateToProfile = { mainNavController.navigate(Screen.Profile.route) },
                    onUpgrade = { mainNavController.navigate(Screen.Paywall.route) },
                    isPremium = isPremium
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
                        mainNavController.popBackStack()
                    }
                )
            }
            composable(Screen.Workouts.route) {
                WorkoutsScreen(
                    onNavigateToLog = { planId, dayId ->
                        mainNavController.navigate(Screen.WorkoutLog.createRoute(planId, dayId))
                    },
                    onNavigateToCustom = { mainNavController.navigate(Screen.CustomWorkouts.route) },
                    onUpgrade = { mainNavController.navigate(Screen.Paywall.route) },
                    isPremium = isPremium
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
                MealsScreen(
                    isPremium = isPremium,
                    onUpgrade = { mainNavController.navigate(Screen.Paywall.route) }
                )
            }
            composable(Screen.Nutrition.route) {
                NutritionScreen(
                    onNavigateToScanner = {
                        mainNavController.navigate(
                            if (isPremium) Screen.Scanner.route else Screen.Paywall.route
                        )
                    }
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
                    onBack = { mainNavController.popBackStack() }
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
    // Floating pill bar on a dark surface — one persistent high-contrast
    // element, so the accent reads as "the action" everywhere in the app.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .navigationBarsPadding()
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = HeroInk,
            shadowElevation = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    if (item.isCenter) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AccentBright)
                                .clickable { onNavigate(item.screen) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                item.selectedIcon,
                                contentDescription = item.label,
                                tint = HeroInk,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onNavigate(item.screen) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (selected) AccentBright else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(23.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) AccentBright else Color.White.copy(alpha = 0.5f),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}
