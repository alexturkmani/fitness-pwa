package com.nexal.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

private const val ENTER_MS = 280
private const val EXIT_MS = 180

/**
 * Tab switches: quick fade-out then fade-in (minimal ghost overlap).
 * Enter is slightly delayed so outgoing text clears first.
 */
fun tabEnterTransition(): EnterTransition =
    fadeIn(
        animationSpec = tween(
            durationMillis = ENTER_MS,
            delayMillis = 90,
            easing = FastOutSlowInEasing
        )
    )

fun tabExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(EXIT_MS, easing = FastOutSlowInEasing))

/** Forward push for nested screens */
fun AnimatedContentTransitionScope<NavBackStackEntry>.pushEnter(): EnterTransition =
    fadeIn(tween(ENTER_MS, easing = FastOutSlowInEasing)) +
        slideInHorizontally(
            animationSpec = tween(ENTER_MS, easing = FastOutSlowInEasing)
        ) { it / 10 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.pushExit(): ExitTransition =
    fadeOut(tween(EXIT_MS)) +
        slideOutHorizontally(
            animationSpec = tween(EXIT_MS, easing = FastOutSlowInEasing)
        ) { -it / 16 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnter(): EnterTransition =
    fadeIn(tween(ENTER_MS, easing = FastOutSlowInEasing)) +
        slideInHorizontally(
            animationSpec = tween(ENTER_MS, easing = FastOutSlowInEasing)
        ) { -it / 10 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition =
    fadeOut(tween(EXIT_MS)) +
        slideOutHorizontally(
            animationSpec = tween(EXIT_MS, easing = FastOutSlowInEasing)
        ) { it / 16 }
