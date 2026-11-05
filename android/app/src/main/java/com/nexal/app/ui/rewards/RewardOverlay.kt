package com.nexal.app.ui.rewards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.data.repository.RewardEvent
import com.nexal.app.data.repository.RewardRepository
import com.nexal.app.data.repository.RewardType
import com.nexal.app.ui.components.GlassProgressBar
import com.nexal.app.ui.components.GlassText
import com.nexal.app.ui.components.GlassTextMuted
import com.nexal.app.ui.components.GradientButton
import com.nexal.app.ui.theme.AccentBright
import com.nexal.app.ui.theme.Slate300
import com.nexal.app.ui.theme.Cyan500
import com.nexal.app.ui.theme.Emerald300
import com.nexal.app.ui.theme.HeroInk
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val rewards: RewardRepository
) : ViewModel() {
    val pending = rewards.pending
    val progress = rewards.progress
    fun dismiss() = rewards.dismiss()
}

private fun RewardType.icon(): ImageVector = when (this) {
    RewardType.WORKOUT -> Icons.Default.FitnessCenter
    RewardType.FAST -> Icons.Default.Timer
    RewardType.CARDIO -> Icons.Default.DirectionsRun
    RewardType.MEAL -> Icons.Default.Restaurant
    RewardType.WATER -> Icons.Default.WaterDrop
}

/** Hosted once above the main navigation, so a reward shows on whichever screen comes next. */
@Composable
fun RewardOverlay(viewModel: RewardViewModel = hiltViewModel()) {
    val event by viewModel.pending.collectAsStateWithLifecycle()
    event?.let { RewardDialog(it, onDismiss = viewModel::dismiss) }
}

@Composable
private fun RewardDialog(event: RewardEvent, onDismiss: () -> Unit) {
    val pop = remember(event) { Animatable(0.6f) }
    LaunchedEffect(event) {
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Confetti(Modifier.fillMaxSize())
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = HeroInk,
                modifier = Modifier.fillMaxWidth().scale(pop.value)
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(AccentBright),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (event.leveledUp) Icons.Default.EmojiEvents else event.type.icon(),
                            contentDescription = null,
                            tint = HeroInk,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        if (event.leveledUp) "Level ${event.progress.level}!" else event.type.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = GlassText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        event.type.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GlassTextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RewardFigure("+${event.xpGained}", "XP earned")
                        RewardFigure("${event.progress.streakDays}", "day streak")
                        RewardFigure("${event.progress.level}", "level")
                    }
                    Spacer(Modifier.height(16.dp))
                    GlassProgressBar(event.progress.levelProgress)
                    Spacer(Modifier.height(20.dp))
                    GradientButton(text = "Keep going", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun RewardFigure(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AccentBright)
        Text(label, style = MaterialTheme.typography.labelSmall, color = GlassTextMuted)
    }
}

private class Piece(val x: Float, val speed: Float, val drift: Float, val size: Float, val color: Color)

/** One short burst of brand-coloured pieces falling past the card. */
@Composable
private fun Confetti(modifier: Modifier) {
    val pieces = remember {
        val colors = listOf(AccentBright, Color.White, Slate300, AccentBright)
        List(70) {
            Piece(
                x = Random.nextFloat(),
                speed = 0.6f + Random.nextFloat() * 0.8f,
                drift = (Random.nextFloat() - 0.5f) * 0.2f,
                size = 6f + Random.nextFloat() * 10f,
                color = colors.random()
            )
        }
    }
    val time = remember { Animatable(0f) }
    LaunchedEffect(Unit) { time.animateTo(1f, tween(2400, easing = LinearEasing)) }

    Canvas(modifier) {
        val t = time.value
        pieces.forEach { piece ->
            drawRect(
                color = piece.color.copy(alpha = (1f - t).coerceIn(0f, 1f)),
                topLeft = Offset(
                    x = (piece.x + piece.drift * t) * size.width,
                    y = (t * piece.speed * 1.3f - 0.1f) * size.height
                ),
                size = Size(piece.size, piece.size * 1.6f)
            )
        }
    }
}
