package com.nexal.app.ui.fasting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.R
import com.nexal.app.data.repository.FASTING_SAFETY_NOTE
import com.nexal.app.data.repository.FastingMath
import com.nexal.app.data.repository.FastingPlan
import com.nexal.app.data.repository.fastingPlans
import com.nexal.app.ui.components.GlassCard
import com.nexal.app.ui.components.GlassIconButton
import com.nexal.app.ui.components.GlassRing
import com.nexal.app.ui.components.GlassStat
import com.nexal.app.ui.components.GlassText
import com.nexal.app.ui.components.RollingNumber
import com.nexal.app.ui.components.GlassTextMuted
import com.nexal.app.ui.components.GradientButton
import com.nexal.app.ui.components.PhotoBackdrop
import com.nexal.app.ui.theme.AccentBright

@Composable
fun FastingScreen(
    onBack: () -> Unit,
    viewModel: FastingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PhotoBackdrop(photo = R.drawable.bg_fasting) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", onBack)
                Spacer(Modifier.width(14.dp))
                Text("Fasting", style = MaterialTheme.typography.headlineMedium, color = GlassText)
            }
            Spacer(Modifier.height(20.dp))

            val activePlan = state.activePlan
            if (activePlan != null) {
                ActiveFastContent(state, activePlan, onEnd = viewModel::endFast)
            } else {
                PlanPicker(state.selectedPlan, viewModel::selectPlan, viewModel::startFast)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActiveFastContent(state: FastingUiState, plan: FastingPlan, onEnd: () -> Unit) {
    var confirmEnd by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        GlassRing(progress = state.progress, size = 280.dp, stroke = 15.dp, ticks = 60) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (state.goalReached) "GOAL REACHED" else "FASTING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                RollingNumber(
                    FastingMath.formatClock(state.elapsedMillis),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = GlassText
                )
                Text(
                    "${(state.progress * 100).toInt()}% of ${plan.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassTextMuted
                )
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassStat(FastingMath.formatClock(state.remainingMillis), "time left", Modifier.weight(1f))
        GlassStat("${plan.fastHours} hours", "fasting goal", Modifier.weight(1f))
    }
    Spacer(Modifier.height(14.dp))
    PlanGuide(plan)
    Spacer(Modifier.height(18.dp))
    GradientButton(
        text = if (state.goalReached) "End fast and eat" else "End fast",
        onClick = { if (state.goalReached) onEnd() else confirmEnd = true },
        modifier = Modifier.fillMaxWidth()
    )

    if (confirmEnd) {
        AlertDialog(
            onDismissRequest = { confirmEnd = false },
            title = { Text("End this fast early?") },
            text = { Text("That's completely fine if you need to. You won't earn XP for this fast.") },
            confirmButton = {
                TextButton(onClick = { confirmEnd = false; onEnd() }) { Text("End fast") }
            },
            dismissButton = {
                TextButton(onClick = { confirmEnd = false }) { Text("Keep going") }
            }
        )
    }
}

@Composable
private fun PlanPicker(
    selected: FastingPlan,
    onSelect: (FastingPlan) -> Unit,
    onStart: () -> Unit
) {
    Text("Choose your fast", style = MaterialTheme.typography.titleLarge, color = GlassText)
    Spacer(Modifier.height(4.dp))
    Text(
        "Fasting hours, then eating hours. New to this? Start with 12:12.",
        style = MaterialTheme.typography.bodySmall,
        color = GlassTextMuted
    )
    Spacer(Modifier.height(14.dp))

    fastingPlans.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            row.forEach { plan ->
                PlanTile(plan, plan.id == selected.id, { onSelect(plan) }, Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    Spacer(Modifier.height(4.dp))
    PlanGuide(selected)
    Spacer(Modifier.height(18.dp))
    GradientButton(text = "Start ${selected.name} fast", onClick = onStart, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(14.dp))
    Text(
        FASTING_SAFETY_NOTE,
        style = MaterialTheme.typography.bodySmall,
        color = GlassTextMuted,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PlanTile(plan: FastingPlan, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(22.dp)
    GlassCard(
        modifier = if (selected) modifier.border(BorderStroke(2.dp, AccentBright), shape) else modifier,
        onClick = onClick,
        cornerRadius = 22.dp
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GlassText)
            Text(
                "${plan.fastHours}h fast · ${plan.eatHours}h eat",
                style = MaterialTheme.typography.labelSmall,
                color = GlassTextMuted
            )
            Spacer(Modifier.height(6.dp))
            Text(plan.level, style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
private fun PlanGuide(plan: FastingPlan) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("How ${plan.name} works", style = MaterialTheme.typography.titleMedium, color = GlassText)
            Spacer(Modifier.height(6.dp))
            Text(plan.summary, style = MaterialTheme.typography.bodySmall, color = GlassTextMuted)
            Spacer(Modifier.height(12.dp))
            plan.steps.forEachIndexed { index, step ->
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.width(22.dp)
                    )
                    Text(step, style = MaterialTheme.typography.bodySmall, color = GlassText)
                }
            }
        }
    }
}
