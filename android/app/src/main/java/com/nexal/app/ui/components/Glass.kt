package com.nexal.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlin.math.max
import kotlin.math.roundToInt
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexal.app.R
import com.nexal.app.ui.theme.AccentBright
import com.nexal.app.ui.theme.HeroInk
import com.nexal.app.ui.theme.Slate950
import com.nexal.app.ui.theme.photoScreensShowing

/** Text tones for anything drawn over a photo; never theme colours, the photo is always dark. */
val GlassText = Color.White
val GlassTextMuted = Color.White.copy(alpha = 0.68f)
private val GlassFill = Color(0xFF15161A).copy(alpha = 0.78f)
private val GlassFillTop = Color(0xFF2A2C31).copy(alpha = 0.80f)

/** A thin lime line, brightest along the top and fading down the sides. */
private val GlassEdge = Brush.verticalGradient(
    listOf(AccentBright.copy(alpha = 0.62f), AccentBright.copy(alpha = 0.10f))
)
private val GlassSheen = Brush.verticalGradient(listOf(GlassFillTop, GlassFill))

/** Neutral graphite laid over every photo, so white text stays readable on bright areas. */
private val PhotoTint = Color(0xFF0A0B0D)

/** The workout photo for a day, chosen from words in its label. */
@DrawableRes
fun workoutPhotoFor(label: String): Int {
    val text = label.lowercase()
    return when {
        listOf("box", "kick", "martial", "mma").any { it in text } -> R.drawable.bg_home
        listOf("run", "cardio", "jog", "sprint", "walk", "endurance").any { it in text } -> R.drawable.bg_run
        listOf("hiit", "circuit", "conditioning", "interval").any { it in text } -> R.drawable.bg_hiit
        listOf("yoga", "stretch", "mobility", "recovery", "rest").any { it in text } -> R.drawable.bg_recovery
        listOf("push", "pull", "leg", "upper", "lower", "strength", "chest", "back", "full body").any { it in text } ->
            R.drawable.bg_strength
        else -> R.drawable.bg_gym
    }
}

/** Darkens what shows through the glass, so text keeps its contrast over bright photo areas. */
private val GlassVeil = Brush.verticalGradient(
    listOf(Color(0xFF15161A).copy(alpha = 0.46f), Color(0xFF15161A).copy(alpha = 0.68f))
)
private val GlassFrost = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.35f) })

/** A soft copy of the current photo, and where that photo sits, for cards to frost themselves with. */
@Stable
private class GlassBackdrop {
    var image by mutableStateOf<ImageBitmap?>(null)
    var origin by mutableStateOf(Offset.Zero)
    var size by mutableStateOf(Size.Zero)
}

private val LocalGlassBackdrop = staticCompositionLocalOf<GlassBackdrop?> { null }

private val GrainBrush by lazy {
    ShaderBrush(ImageShader(glassGrain, TileMode.Repeated, TileMode.Repeated))
}

/** Draws the part of the blurred photo that lies behind a card whose top-left corner is at [card]. */
private fun DrawScope.drawFrost(backdrop: GlassBackdrop, card: Offset) {
    val area = backdrop.size
    val image = backdrop.image
    if (area.width <= 0f || image == null) {
        drawRect(GlassSheen)
        return
    }
    // Same placement as the sharp photo: ContentScale.Crop, aligned to the top centre.
    val scale = max(area.width / image.width, area.height / image.height)
    val width = image.width * scale
    drawImage(
        image = image,
        dstOffset = IntOffset(
            ((area.width - width) / 2f + backdrop.origin.x - card.x).roundToInt(),
            (backdrop.origin.y - card.y).roundToInt()
        ),
        dstSize = IntSize(width.roundToInt(), (image.height * scale).roundToInt()),
        colorFilter = GlassFrost,
        filterQuality = FilterQuality.High
    )
    drawRect(GlassVeil)
    drawRect(GrainBrush)
}

/** The light on the glass: a diagonal sheen across the card and a bright rim just inside its edge. */
fun Modifier.liquidSheen(cornerRadius: Dp, strength: Float = 1f): Modifier = drawBehind {
    drawRect(
        Brush.linearGradient(
            0f to Color.White.copy(alpha = 0.17f * strength),
            0.36f to Color.Transparent,
            0.72f to Color.Transparent,
            1f to Color.White.copy(alpha = 0.06f * strength),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
    )
    val inset = 1.5.dp.toPx()
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.26f * strength), Color.Transparent),
            endY = size.height * 0.45f
        ),
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2f, size.height - inset * 2f),
        cornerRadius = CornerRadius(cornerRadius.toPx() - inset),
        style = Stroke(1.dp.toPx())
    )
}

/**
 * A full-bleed photo with a scrim that deepens towards the bottom, so glass
 * cards stay readable wherever the content scrolls to.
 */
@Composable
fun PhotoBackdrop(
    @DrawableRes photo: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val sharp = ImageBitmap.imageResource(photo)
    val backdrop = remember(photo) { GlassBackdrop() }
    DisposableEffect(Unit) {
        photoScreensShowing.intValue++
        onDispose { photoScreensShowing.intValue-- }
    }
    LaunchedEffect(photo) {
        backdrop.image = withContext(Dispatchers.Default) { frostedCopy(photo, sharp) }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .onGloballyPositioned {
                backdrop.origin = it.positionInRoot()
                backdrop.size = it.size.toSize()
            }
    ) {
        Image(
            painter = painterResource(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            // Muted so the photos sit behind the interface rather than colouring it.
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.35f) }),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to PhotoTint.copy(alpha = 0.62f),
                        0.4f to PhotoTint.copy(alpha = 0.74f),
                        1f to Slate950.copy(alpha = 0.97f)
                    )
                )
        )
        val scheme = MaterialTheme.colorScheme.copy(
            onBackground = GlassText,
            onSurface = GlassText,
            onSurfaceVariant = GlassTextMuted,
            surface = HeroInk,
            background = Slate950,
            primary = GlassText,
            onPrimary = HeroInk,
        )
        MaterialTheme(colorScheme = scheme) {
            CompositionLocalProvider(
                LocalContentColor provides GlassText,
                LocalGlassBackdrop provides backdrop
            ) { content() }
        }
    }
}

/** Liquid glass card: the photo blurred behind it, light across the surface and a lime edge. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val source = remember { MutableInteractionSource() }
    val backdrop = LocalGlassBackdrop.current
    // Kept out of composition: scrolling only redraws the card, it doesn't recompose it.
    val position = remember { mutableStateOf(Offset.Zero) }
    Column(
        modifier = modifier
            .then(if (onClick != null) Modifier.pressScale(source) else Modifier)
            .shadow(14.dp, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .onGloballyPositioned { position.value = it.positionInRoot() }
            .drawBehind {
                if (backdrop != null) {
                    drawFrost(backdrop, position.value)
                } else {
                    drawRect(GlassSheen)
                }
            }
            .liquidSheen(cornerRadius)
            .border(BorderStroke(1.dp, GlassEdge), shape)
            .then(if (onClick != null) Modifier.clickable(source, ripple(), onClick = onClick) else Modifier),
        content = content
    )
}

/** Round glass button for back, home and other single-icon actions. */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(GlassSheen)
            .border(BorderStroke(1.dp, GlassEdge), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = GlassText, modifier = Modifier.size(22.dp))
    }
}

/** One compact metric for glass rows. */
@Composable
fun GlassStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    GlassCard(modifier = modifier, cornerRadius = 22.dp) {
        Column(Modifier.padding(14.dp)) {
            if (icon != null) {
                IconBadge(icon, tint = GlassText, size = 32.dp)
                Box(Modifier.height(10.dp))
            }
            RollingNumber(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GlassText
            )
            Text(label, style = MaterialTheme.typography.labelSmall, color = GlassTextMuted, maxLines = 1)
        }
    }
}

/** Circular progress in the brand lime, with whatever sits at its centre. */
@Composable
fun GlassRing(
    progress: Float,
    size: Dp,
    stroke: Dp,
    modifier: Modifier = Modifier,
    ticks: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(700), label = "glassRing")
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawProgressRing(animated, stroke.toPx(), Color.White.copy(alpha = 0.13f), AccentBright.copy(alpha = 0.45f), AccentBright, ticks)
        }
        content()
    }
}

/** Lime progress line used inside glass cards. */
@Composable
fun GlassProgressBar(progress: Float, modifier: Modifier = Modifier) {
    GlowBar(progress, modifier, track = Color.White.copy(alpha = 0.13f))
}
