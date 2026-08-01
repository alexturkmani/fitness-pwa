package com.nexal.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

@Composable
fun FadeSlideIn(
    delayMs: Int = 0,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(16f) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        alpha.animateTo(1f, tween(360, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        offsetY.animateTo(
            0f,
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
            .graphicsLayer {
                this.alpha = alpha.value
                translationY = offsetY.value
            },
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}

@Composable
fun ScalePopIn(
    delayMs: Int = 0,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0.96f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        alpha.animateTo(1f, tween(260))
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
            },
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}

@Composable
fun PulsingAlpha(
    content: @Composable (alpha: Float) -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val alpha by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    content(alpha)
}

@Composable
fun rememberStaggeredVisible(itemCount: Int, stepMs: Int = 55): List<Boolean> {
    var visibleCount by remember { mutableStateOf(0) }
    LaunchedEffect(itemCount) {
        visibleCount = 0
        repeat(itemCount) {
            delay(stepMs.toLong())
            visibleCount = it + 1
        }
    }
    return List(itemCount) { index -> index < visibleCount }
}
