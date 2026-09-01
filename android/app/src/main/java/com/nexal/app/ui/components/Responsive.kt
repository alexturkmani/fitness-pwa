package com.nexal.app.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Adaptive spacing/sizing for phones of different heights/widths and font scales.
 * Pixel-class phones (~400×780–900dp) should scroll cleanly; short/landscape
 * devices get tighter spacing; tablets get a centered content column.
 */
@Immutable
data class AdaptiveMetrics(
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val fontScale: Float,
    val isCompactHeight: Boolean,
    val isCompactWidth: Boolean,
    val isLandscape: Boolean,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val sectionSpacing: Dp,
    val fieldSpacing: Dp,
    val heroSize: Dp,
    val contentMaxWidth: Dp,
    val useCompactOptions: Boolean
)

val LocalAdaptiveMetrics = staticCompositionLocalOf {
    AdaptiveMetrics(
        screenWidthDp = 411,
        screenHeightDp = 891,
        fontScale = 1f,
        isCompactHeight = false,
        isCompactWidth = false,
        isLandscape = false,
        horizontalPadding = 24.dp,
        verticalPadding = 24.dp,
        sectionSpacing = 20.dp,
        fieldSpacing = 12.dp,
        heroSize = 72.dp,
        contentMaxWidth = 520.dp,
        useCompactOptions = false
    )
}

@Composable
fun rememberAdaptiveMetrics(): AdaptiveMetrics {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val width = config.screenWidthDp
    val height = config.screenHeightDp
    val fontScale = density.fontScale
    val landscape = width > height
    val compactHeight = height < 720 || landscape || fontScale >= 1.3f
    val compactWidth = width < 360
    val veryShort = height < 640 || (landscape && height < 480)

    return remember(width, height, fontScale, landscape) {
        AdaptiveMetrics(
            screenWidthDp = width,
            screenHeightDp = height,
            fontScale = fontScale,
            isCompactHeight = compactHeight,
            isCompactWidth = compactWidth,
            isLandscape = landscape,
            horizontalPadding = when {
                compactWidth -> 16.dp
                width >= 600 -> 32.dp
                else -> 24.dp
            },
            verticalPadding = when {
                veryShort -> 12.dp
                compactHeight -> 16.dp
                else -> 24.dp
            },
            sectionSpacing = when {
                veryShort -> 12.dp
                compactHeight -> 16.dp
                else -> 24.dp
            },
            fieldSpacing = if (compactHeight) 10.dp else 12.dp,
            heroSize = when {
                veryShort -> 48.dp
                compactHeight -> 56.dp
                else -> 72.dp
            },
            contentMaxWidth = if (width >= 600) 520.dp else Dp.Unspecified,
            useCompactOptions = compactHeight || fontScale >= 1.15f || height < 800
        )
    }
}

/**
 * Full-screen scrollable column that respects system bars, IME, and max content width.
 */
@Composable
fun AdaptiveScrollScreen(
    modifier: Modifier = Modifier,
    applyStatusBars: Boolean = true,
    applyNavigationBars: Boolean = true,
    applyImePadding: Boolean = true,
    content: @Composable ColumnScope.(AdaptiveMetrics) -> Unit
) {
    val metrics = rememberAdaptiveMetrics()
    CompositionLocalProvider(LocalAdaptiveMetrics provides metrics) {
        val scroll = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .then(if (applyStatusBars) Modifier.statusBarsPadding() else Modifier)
                .then(if (applyNavigationBars) Modifier.navigationBarsPadding() else Modifier)
                .then(if (applyImePadding) Modifier.imePadding() else Modifier)
                .verticalScroll(scroll)
                .padding(
                    horizontal = metrics.horizontalPadding,
                    vertical = metrics.verticalPadding
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (metrics.contentMaxWidth != Dp.Unspecified) {
                            Modifier.widthIn(max = metrics.contentMaxWidth)
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = { content(metrics) }
            )
        }
    }
}

/**
 * Constrains child content to a readable max width on large screens.
 */
@Composable
fun AdaptiveContentWidth(
    modifier: Modifier = Modifier,
    content: @Composable (AdaptiveMetrics) -> Unit
) {
    val metrics = LocalAdaptiveMetrics.current
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxW = if (metrics.contentMaxWidth != Dp.Unspecified) {
            metrics.contentMaxWidth
        } else {
            maxWidth
        }
        Column(
            modifier = Modifier
                .widthIn(max = maxW)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(metrics)
        }
    }
}

@Composable
fun AdaptiveMetrics.scaledSp(base: Int) = (base / fontScale.coerceAtLeast(1f)).sp

@Composable
fun Modifier.adaptiveMinTouch(): Modifier = this.heightIn(min = 48.dp)
