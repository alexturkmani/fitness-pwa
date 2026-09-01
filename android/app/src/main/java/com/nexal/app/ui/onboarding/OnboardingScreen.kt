package com.nexal.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.domain.model.*
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val metrics = rememberAdaptiveMetrics()

    LaunchedEffect(uiState.completed) {
        if (uiState.completed) onComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Step ${uiState.currentStep + 1} of 3", fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    if (uiState.currentStep > 0) {
                        IconButton(onClick = { viewModel.previousStep() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            LinearProgressIndicator(
                progress = { (uiState.currentStep + 1) / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = BrandBlue,
                trackColor = Emerald50
            )

            AnimatedContent(
                targetState = uiState.currentStep,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut())
                    }
                },
                label = "onboarding_step"
            ) { step ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = metrics.horizontalPadding,
                                vertical = metrics.verticalPadding
                            )
                    ) {
                        when (step) {
                            0 -> PersonalInfoStep(uiState, viewModel, metrics)
                            1 -> GoalsAndExperienceStep(uiState, viewModel, metrics)
                            2 -> TrainingSetupStep(uiState, viewModel, metrics)
                        }
                        // Bottom breathing room so last field isn't flush against CTA
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Surface(
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(
                                    horizontal = metrics.horizontalPadding,
                                    vertical = if (metrics.isCompactHeight) 10.dp else 14.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (uiState.generatingPlans || uiState.statusMessage.isNotBlank()) {
                                Text(
                                    uiState.statusMessage.ifBlank { "Building your personalized plans…" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandBlue,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            GradientButton(
                                text = when {
                                    uiState.generatingPlans -> "Creating your plans…"
                                    step == 2 && uiState.isPremium -> "Create my plans"
                                    step == 2 -> "Start tracking"
                                    else -> "Continue"
                                },
                                onClick = {
                                    if (step == 2) viewModel.completeOnboarding()
                                    else viewModel.nextStep()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                loading = uiState.isSaving || uiState.generatingPlans,
                                enabled = !uiState.isSaving && !uiState.generatingPlans
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonalInfoStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
    metrics: AdaptiveMetrics
) {
    FadeSlideIn {
        Column {
            Text("Personal Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Tell us about yourself so we can customize your plan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    ScalePopIn(delayMs = 60) {
        Column {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(metrics.fieldSpacing))

            Text("Gender", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Gender.entries.forEach { gender ->
                    FilterChip(
                        selected = state.gender == gender,
                        onClick = { viewModel.updateGender(gender) },
                        label = {
                            Text(
                                gender.name.lowercase().replaceFirstChar { it.uppercase() },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald50,
                            selectedLabelColor = BrandBlue
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = if (state.weight > 0) state.weight.toInt().toString() else "",
                    onValueChange = { viewModel.updateWeight(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text(if (state.unitSystem == UnitSystem.IMPERIAL) "Weight (lb)" else "Weight (kg)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = if (state.height > 0) state.height.toInt().toString() else "",
                    onValueChange = { viewModel.updateHeight(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text(if (state.unitSystem == UnitSystem.IMPERIAL) "Height (in)" else "Height (cm)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }
            Spacer(modifier = Modifier.height(metrics.fieldSpacing))
            OutlinedTextField(
                value = if (state.age > 0) state.age.toString() else "",
                onValueChange = { viewModel.updateAge(it.toIntOrNull() ?: 0) },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Preferred Units", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UnitSystem.entries.forEach { unit ->
                    FilterChip(
                        selected = state.unitSystem == unit,
                        onClick = { viewModel.updateUnitSystem(unit) },
                        label = { Text(unit.label, maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald50,
                            selectedLabelColor = BrandBlue
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsAndExperienceStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
    metrics: AdaptiveMetrics
) {
    FadeSlideIn {
        Column {
            Text("Goals & Experience", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Help us tailor your workout and nutrition plans",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text("Activity Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    ActivityLevel.entries.forEachIndexed { index, level ->
        FadeSlideIn(delayMs = if (metrics.useCompactOptions) 0 else 40 + index * 30) {
            SelectableOptionCard(
                title = level.label,
                description = level.description,
                selected = state.activityLevel == level,
                onClick = { viewModel.updateActivityLevel(level) },
                compact = metrics.useCompactOptions
            )
        }
    }

    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text("Fitness Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Text("Select all that apply", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(8.dp))
    FitnessGoal.entries.forEach { goal ->
        val selected = goal in state.fitnessGoals
        SelectableOptionCard(
            title = goal.label,
            description = goal.description,
            selected = selected,
            onClick = { viewModel.toggleGoal(goal) },
            compact = metrics.useCompactOptions,
            trailing = {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { viewModel.toggleGoal(goal) },
                    colors = CheckboxDefaults.colors(checkedColor = BrandBlue)
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text("Lifting Experience", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    LiftingExperience.entries.forEach { exp ->
        SelectableOptionCard(
            title = exp.label,
            description = exp.description,
            selected = state.liftingExperience == exp,
            onClick = { viewModel.updateLiftingExperience(exp) },
            compact = metrics.useCompactOptions
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainingSetupStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
    metrics: AdaptiveMetrics
) {
    FadeSlideIn {
        Column {
            Text("Training Setup", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Configure your training preferences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text("Where do you train?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    TrainingLocation.entries.forEach { loc ->
        val selected = state.trainingLocation == loc
        val icon = when (loc) {
            TrainingLocation.GYM -> Icons.Default.FitnessCenter
            TrainingLocation.HOME -> Icons.Default.Home
        }
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .then(if (selected) Modifier.border(2.dp, BrandBlue, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.updateTrainingLocation(loc) }
        ) {
            Row(
                modifier = Modifier.padding(if (metrics.useCompactOptions) 10.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (selected) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (metrics.useCompactOptions) 24.dp else 28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(loc.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    if (!metrics.useCompactOptions || selected) {
                        Text(
                            loc.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (metrics.useCompactOptions) 2 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (selected) Icon(Icons.Default.CheckCircle, null, tint = BrandBlue)
            }
        }
    }

    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text("Workout Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    WorkoutStyle.entries.forEach { style ->
        val selected = state.workoutStyle == style
        val description = when (style) {
            WorkoutStyle.SINGLE_MUSCLE -> "Chest Day, Back Day, Arm Day - one muscle per session"
            WorkoutStyle.MUSCLE_GROUP -> "Push/Pull/Legs, Upper/Lower - compound movements"
        }
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .then(if (selected) Modifier.border(2.dp, BrandBlue, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.updateWorkoutStyle(style) }
        ) {
            Row(
                modifier = Modifier.padding(if (metrics.useCompactOptions) 10.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(style.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    if (!metrics.useCompactOptions || selected) {
                        Text(
                            description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (metrics.useCompactOptions) 2 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                RadioButton(
                    selected = selected,
                    onClick = { viewModel.updateWorkoutStyle(style) },
                    colors = RadioButtonDefaults.colors(selectedColor = BrandBlue)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

    Text(
        "Gym Days Per Week: ${state.gymDaysPerWeek}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Slider(
        value = state.gymDaysPerWeek.toFloat(),
        onValueChange = { viewModel.updateGymDays(it.toInt()) },
        valueRange = 3f..7f,
        steps = 3,
        colors = SliderDefaults.colors(thumbColor = BrandBlue, activeTrackColor = BrandBlue)
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("3 days", style = MaterialTheme.typography.bodySmall)
        Text("7 days", style = MaterialTheme.typography.bodySmall)
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = if (state.targetWeight > 0) state.targetWeight.toInt().toString() else "",
        onValueChange = { viewModel.updateTargetWeight(it.toDoubleOrNull() ?: 0.0) },
        label = {
            Text(
                if (state.unitSystem == UnitSystem.IMPERIAL) "Target Weight (lb)" else "Target Weight (kg)"
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text("Training Cycle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(6, 8).forEach { weeks ->
            FilterChip(
                selected = state.intervalWeeks == weeks,
                onClick = { viewModel.updateIntervalWeeks(weeks) },
                label = { Text("$weeks weeks") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Emerald50,
                    selectedLabelColor = BrandBlue
                )
            )
        }
    }
}

@Composable
private fun SelectableOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
    trailing: (@Composable () -> Unit)? = null
) {
    FitCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (compact) 2.dp else 3.dp)
            .then(if (selected) Modifier.border(2.dp, BrandBlue, RoundedCornerShape(16.dp)) else Modifier),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 10.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!compact || selected) {
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (compact) 2 else 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (trailing != null) {
                trailing()
            } else if (selected) {
                Icon(Icons.Default.CheckCircle, null, tint = BrandBlue)
            }
        }
    }
}
