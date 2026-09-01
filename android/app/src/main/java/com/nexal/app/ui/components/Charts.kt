package com.nexal.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexal.app.ui.theme.Accent
import com.nexal.app.ui.theme.AccentBright
import com.nexal.app.ui.theme.AccentWash
import kotlin.math.max

/**
 * Weekly intake as rounded columns. Days with no data still get a column so a
 * gap in logging reads as a gap, rather than silently collapsing the week.
 */
@Composable
fun WeeklyBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 116.dp
) {
    if (data.isEmpty()) return
    val peak = max(data.maxOf { it.second }, 1)
    val todayIndex = data.lastIndex

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, (label, value) ->
            val fraction = value.toFloat() / peak
            val animated by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(600),
                label = "bar$index"
            )
            val isToday = index == todayIndex

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(barHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Track
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(13.dp))
                            .background(AccentWash)
                    )
                    // Value — minimum sliver so an empty day is still legible
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(max(animated, 0.04f))
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                if (isToday) {
                                    Brush.verticalGradient(listOf(AccentBright, Accent))
                                } else {
                                    Brush.verticalGradient(listOf(Accent, Accent))
                                }
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    color = if (isToday) Accent else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Trend line with a soft area fill. Y range is padded around the actual min/max
 * rather than anchored at zero — for weight, a 2 kg move matters and a
 * zero-based axis would flatten it into a straight line.
 */
@Composable
fun LineTrendChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList()
) {
    if (points.size < 2) return
    val lo = points.min()
    val hi = points.max()
    val pad = ((hi - lo) * 0.25f).takeIf { it > 0f } ?: 1f
    val minY = lo - pad
    val maxY = hi + pad

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val w = size.width
            val h = size.height
            fun px(i: Int) = w * i / (points.size - 1).toFloat()
            fun py(v: Float) = h - ((v - minY) / (maxY - minY)) * h

            val path = Path().apply {
                moveTo(px(0), py(points[0]))
                for (i in 1 until points.size) {
                    // Horizontal-only control points keep the curve from
                    // overshooting past real data values.
                    val cx = (px(i - 1) + px(i)) / 2f
                    cubicTo(cx, py(points[i - 1]), cx, py(points[i]), px(i), py(points[i]))
                }
            }
            val fill = Path().apply {
                addPath(path)
                lineTo(px(points.lastIndex), h)
                lineTo(px(0), h)
                close()
            }

            drawPath(
                path = fill,
                brush = Brush.verticalGradient(
                    listOf(Accent.copy(alpha = 0.28f), Accent.copy(alpha = 0f))
                )
            )
            drawPath(path = path, color = Accent, style = Stroke(width = 6f))

            points.forEachIndexed { i, v ->
                val isLast = i == points.lastIndex
                drawCircle(
                    color = if (isLast) AccentBright else Accent,
                    radius = if (isLast) 11f else 6f,
                    center = Offset(px(i), py(v))
                )
                if (isLast) {
                    drawCircle(
                        color = Color.White,
                        radius = 5f,
                        center = Offset(px(i), py(v))
                    )
                }
            }
        }
        if (labels.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Original abstract mark: concentric arcs with orbiting dots, standing in for
 * "activity in motion". Drawn rather than shipped as an asset so it scales and
 * follows the accent colour.
 */
@Composable
fun ActivityOrb(
    modifier: Modifier = Modifier,
    progress: Float = 0.68f
) {
    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f

        drawCircle(color = AccentWash, radius = r * 0.92f, center = c)

        // Outer arc carries the progress value
        val stroke = r * 0.16f
        drawArc(
            brush = Brush.sweepGradient(listOf(AccentBright, Accent, AccentBright)),
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset(c.x - r * 0.78f, c.y - r * 0.78f),
            size = Size(r * 1.56f, r * 1.56f),
            style = Stroke(width = stroke)
        )

        // Inner counter-arc for depth
        drawArc(
            color = Accent.copy(alpha = 0.28f),
            startAngle = 140f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(c.x - r * 0.5f, c.y - r * 0.5f),
            size = Size(r, r),
            style = Stroke(width = stroke * 0.5f)
        )

        // Orbiting dots
        listOf(0.0 to 0.78f, 2.1 to 0.55f, 4.3 to 0.68f).forEach { (angle, dist) ->
            val a = angle
            drawCircle(
                color = AccentBright,
                radius = r * 0.075f,
                center = Offset(
                    c.x + (r * dist * kotlin.math.cos(a)).toFloat(),
                    c.y + (r * dist * kotlin.math.sin(a)).toFloat()
                )
            )
        }
    }
}
