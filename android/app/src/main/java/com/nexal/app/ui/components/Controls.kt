package com.nexal.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexal.app.ui.theme.AccentBright
import com.nexal.app.ui.theme.limeEdge
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** Shrinks a tappable surface slightly while it's held. */
@Composable
fun Modifier.pressScale(source: InteractionSource, pressedScale: Float = 0.975f): Modifier {
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) pressedScale else 1f, tween(120), label = "press")
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Progress ring: gradient arc, a soft halo and a bright marker at the tip.
 * [ticks] adds fine instrument marks inside the ring, lit up to the current progress.
 */
fun DrawScope.drawProgressRing(
    progress: Float,
    strokePx: Float,
    track: Color,
    start: Color,
    end: Color,
    ticks: Int = 0
) {
    val p = progress.coerceIn(0f, 1f)
    val inset = strokePx * 0.85f
    val radius = size.minDimension / 2f - inset
    val centre = Offset(size.width / 2f, size.height / 2f)
    val topLeft = Offset(centre.x - radius, centre.y - radius)
    val arcSize = Size(radius * 2f, radius * 2f)

    rotate(-90f, centre) {
        drawArc(track, 0f, 360f, false, topLeft, arcSize, style = Stroke(strokePx))
        if (ticks > 0) {
            val outer = radius - strokePx * 1.05f
            val inner = outer - strokePx * 0.42f
            repeat(ticks) { i ->
                val fraction = i / ticks.toFloat()
                val angle = Math.toRadians(fraction * 360.0)
                val dir = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                drawLine(
                    color = if (fraction <= p) end.copy(alpha = 0.8f) else track,
                    start = centre + dir * inner,
                    end = centre + dir * outer,
                    strokeWidth = strokePx * 0.12f,
                    cap = StrokeCap.Round
                )
            }
        }
        if (p > 0.001f) {
            val brush = Brush.sweepGradient(0f to start, p to end, 1f to start, center = centre)
            val sweep = 360f * p
            drawArc(
                brush, 0f, sweep, false, topLeft, arcSize, alpha = 0.22f,
                style = Stroke(strokePx * 1.6f, cap = StrokeCap.Round)
            )
            drawArc(brush, 0f, sweep, false, topLeft, arcSize, style = Stroke(strokePx, cap = StrokeCap.Round))
            val tip = Math.toRadians(sweep.toDouble())
            drawCircle(
                color = Color.White.copy(alpha = 0.92f),
                radius = strokePx * 0.26f,
                center = centre + Offset(cos(tip).toFloat(), sin(tip).toFloat()) * radius
            )
        }
    }
}

/** Rounded progress bar with a gradient fill and a bright marker at the leading edge. */
@Composable
fun GlowBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color? = null,
    height: Dp = 8.dp,
    track: Color = LocalContentColor.current.copy(alpha = 0.12f)
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), tween(800), label = "glowBar")
    val start = (color ?: AccentBright).copy(alpha = 0.5f)
    val end = color ?: AccentBright
    Canvas(modifier.fillMaxWidth().height(height)) {
        val h = size.height
        val corner = CornerRadius(h / 2f)
        drawRoundRect(track, cornerRadius = corner)
        if (animated > 0.001f) {
            val w = max(h, size.width * animated)
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(start, end), startX = 0f, endX = w),
                size = Size(w, h),
                cornerRadius = corner
            )
            drawCircle(Color.White.copy(alpha = 0.9f), radius = h * 0.2f, center = Offset(w - h / 2f, h / 2f))
        }
    }
}

/**
 * A number whose changed digits roll into place. Screen readers get the whole value at once
 * rather than one digit at a time.
 */
@Composable
fun RollingNumber(
    value: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current,
    fontWeight: FontWeight? = null
) {
    val digits = style.copy(fontFeatureSettings = "tnum")
    Row(modifier.clearAndSetSemantics { contentDescription = value }) {
        value.forEachIndexed { index, char ->
            // Counted from the right so units stay put when a number gains a digit.
            key(value.length - index) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn(tween(180))) togetherWith
                            (slideOutVertically { -it } + fadeOut(tween(180))) using SizeTransform(clip = true)
                    },
                    label = "digit"
                ) { shown ->
                    Text(shown.toString(), style = digits, color = color, fontWeight = fontWeight, maxLines = 1)
                }
            }
        }
    }
}

/** Icon in a soft tinted circle, used at the top of stat tiles. */
@Composable
fun IconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 34.dp
) {
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(tint.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

/** Selectable pill. Filled with the brand colour when on, a quiet outline when off. */
@Composable
fun PillChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val edge = limeEdge()
    val fill by animateColorAsState(
        if (selected) edge.copy(alpha = 0.12f) else scheme.onSurface.copy(alpha = 0.06f), tween(180), label = "chipFill"
    )
    val line by animateColorAsState(
        if (selected) edge else scheme.onSurface.copy(alpha = 0.14f), tween(180), label = "chipLine"
    )
    val ink = scheme.onSurface
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .pressScale(source, 0.95f)
            .heightIn(min = 42.dp)
            .clip(CircleShape)
            .background(scheme.surface.copy(alpha = 0.6f))
            .background(fill)
            .border(
                BorderStroke(if (selected) 1.5.dp else 1.dp, line),
                CircleShape
            )
            .selectable(selected, source, ripple(), onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides ink) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge.copy(color = ink), label)
        }
    }
}

/** Round tick that fills in when done. Replaces the square checkbox. */
@Composable
fun RoundCheck(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val scheme = MaterialTheme.colorScheme
    val fill by animateColorAsState(if (checked) scheme.primary else Color.Transparent, tween(160), label = "checkFill")
    val scale by animateFloatAsState(if (checked) 1f else 0.4f, tween(160), label = "checkScale")
    Box(
        modifier = modifier
            .size(44.dp)
            .toggleable(checked, role = Role.Checkbox, onValueChange = onCheckedChange)
            .then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(fill)
                .border(BorderStroke(1.5.dp, if (checked) scheme.primary else scheme.onSurfaceVariant), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Default.Check, null, tint = scheme.onPrimary,
                    modifier = Modifier.size(20.dp).graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        }
    }
}

/** On/off switch in the brand colours, with a tick on the thumb when on. */
@Composable
fun BrandSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = if (checked) {
            { Icon(Icons.Default.Check, null, tint = scheme.primary, modifier = Modifier.size(14.dp)) }
        } else {
            null
        },
        colors = SwitchDefaults.colors(
            checkedTrackColor = scheme.primary,
            checkedThumbColor = scheme.onPrimary,
            checkedBorderColor = Color.Transparent,
            uncheckedTrackColor = scheme.onSurface.copy(alpha = 0.1f),
            uncheckedThumbColor = scheme.onSurfaceVariant,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

/** Thick stepped slider with a dot on each step and the step values printed underneath. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepSlider(value: Int, onValueChange: (Int) -> Unit, range: IntRange, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val first = range.first
    val span = (range.last - first).coerceAtLeast(1)
    val fraction = (value - first).toFloat() / span
    val thumbSize = 30.dp
    Column(modifier.fillMaxWidth()) {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(Math.round(it)) },
            valueRange = first.toFloat()..range.last.toFloat(),
            steps = span - 1,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(thumbSize)
                        .clip(CircleShape)
                        .background(scheme.surface)
                        .border(BorderStroke(4.dp, limeEdge()), CircleShape)
                )
            },
            track = {
                val trackColor = scheme.onSurface.copy(alpha = 0.12f)
                val dotOn = Color.Black.copy(alpha = 0.45f)
                val dotOff = scheme.onSurface.copy(alpha = 0.35f)
                Canvas(Modifier.fillMaxWidth().height(12.dp)) {
                    val h = size.height
                    // The thumb's centre travels between these two points.
                    val inset = thumbSize.toPx() / 2f
                    val usable = size.width - inset * 2f
                    val corner = CornerRadius(h / 2f)
                    drawRoundRect(trackColor, cornerRadius = corner)
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(AccentBright.copy(alpha = 0.5f), AccentBright)),
                        size = Size(inset + usable * fraction, h),
                        cornerRadius = corner
                    )
                    for (step in 0..span) {
                        val on = step / span.toFloat() <= fraction
                        drawCircle(
                            if (on) dotOn else dotOff,
                            radius = h * 0.14f,
                            center = Offset(inset + usable * step / span, h / 2f)
                        )
                    }
                }
            }
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in range) {
                Text(
                    "$step",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (step == value) FontWeight.Bold else FontWeight.Normal,
                    color = if (step == value) scheme.primary else scheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StepButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .pressScale(source, 0.88f)
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
            .clickable(source, ripple(), onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
    }
}

/** Number field with minus and plus buttons, all in one pill. */
@Composable
fun StepperField(
    text: String,
    onTextChange: (String) -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    label: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(CircleShape)
            .background(scheme.onSurface.copy(alpha = 0.07f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepButton(Icons.Default.Remove, "Decrease $label", onMinus)
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f).semantics { contentDescription = label },
            textStyle = MaterialTheme.typography.titleSmall.copy(
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum"
            ),
            singleLine = true,
            cursorBrush = SolidColor(scheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        StepButton(Icons.Default.Add, "Increase $label", onPlus)
    }
}

