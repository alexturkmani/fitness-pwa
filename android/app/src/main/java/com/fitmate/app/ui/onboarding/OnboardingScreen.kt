package com.fitmate.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitmate.app.domain.model.*
import com.fitmate.app.ui.components.*
import com.fitmate.app.ui.theme.*
import com.fitmate.app.util.getCommitmentLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.completed) {
        if (uiState.completed) onComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Step ${uiState.currentStep + 1} of 8")
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
        ) {
            // Step indicator
            LinearProgressIndicator(
                progress = { (uiState.currentStep + 1) / 8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Emerald500,
            )

            // Step content
            AnimatedContent(
                targetState = uiState.currentStep,
                label = "onboarding_step"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    when (step) {
                        0 -> PersonalInfoStep(uiState, viewModel)
                        1 -> ActivityLevelStep(uiState, viewModel)
                        2 -> FitnessGoalsStep(uiState, viewModel)
                        3 -> LiftingExperienceStep(uiState, viewModel)
                        4 -> TrainingLocationStep(uiState, viewModel)
                        5 -> TargetSettingsStep(uiState, viewModel)
                        6 -> RotationExplanationStep()
                        7 -> WorkoutStyleStep(uiState, viewModel)
                    }

                    Spacer(Modifier.height(32.dp))

                    GradientButton(
                        text = if (step == 7) "Complete Setup" else "Continue",
                        onClick = {
                            if (step == 7) viewModel.completeOnboarding()
                            else viewModel.nextStep()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        loading = uiState.isSaving
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Personal Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Tell us about yourself so we can customize your plan", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = state.name,
        onValueChange = { viewModel.updateName(it) },
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
    Spacer(Modifier.height(12.dp))

    // Gender selector
    Text("Gender", style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Gender.entries.forEach { gender ->
            FilterChip(
                selected = state.gender == gender,
                onClick = { viewModel.updateGender(gender) },
                label = { Text(gender.name.lowercase().replaceFirstChar { it.uppercase() }) },
                modifier = Modifier.weight(1f)
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    // Number inputs
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = if (state.weight > 0) state.weight.toInt().toString() else "",
            onValueChange = { viewModel.updateWeight(it.toDoubleOrNull() ?: 0.0) },
            label = { Text("Weight (kg)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        OutlinedTextField(
            value = if (state.height > 0) state.height.toInt().toString() else "",
            onValueChange = { viewModel.updateHeight(it.toDoubleOrNull() ?: 0.0) },
            label = { Text("Height (cm)") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
    }
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = if (state.age > 0) state.age.toString() else "",
        onValueChange = { viewModel.updateAge(it.toIntOrNull() ?: 0) },
        label = { Text("Age") },
        modifier = Modifier.fillMaxWidth(0.5f),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )

    Spacer(Modifier.height(16.dp))

    // Unit System
    Text("Preferred Units", style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        UnitSystem.entries.forEach { unit ->
            FilterChip(
                selected = state.unitSystem == unit,
                onClick = { viewModel.updateUnitSystem(unit) },
                label = { Text(unit.label) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActivityLevelStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Activity Level", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("How active are you on a typical week?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    ActivityLevel.entries.forEach { level ->
        val selected = state.activityLevel == level
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(
                    if (selected) Modifier.border(2.dp, Emerald500, RoundedCornerShape(16.dp))
                    else Modifier
                ),
            onClick = { viewModel.updateActivityLevel(level) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(level.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(level.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (selected) {
                    Icon(Icons.Default.CheckCircle, null, tint = Emerald500)
                }
            }
        }
    }
}

@Composable
private fun FitnessGoalsStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Fitness Goals", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Select all that apply", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    FitnessGoal.entries.forEach { goal ->
        val selected = goal in state.fitnessGoals
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(if (selected) Modifier.border(2.dp, Emerald500, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.toggleGoal(goal) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(goal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Checkbox(checked = selected, onCheckedChange = { viewModel.toggleGoal(goal) })
            }
        }
    }
}

@Composable
private fun TargetSettingsStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Training Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = if (state.targetWeight > 0) state.targetWeight.toInt().toString() else "",
        onValueChange = { viewModel.updateTargetWeight(it.toDoubleOrNull() ?: 0.0) },
        label = { Text("Target Weight (kg)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
    Spacer(Modifier.height(16.dp))

    // Training interval
    Text("Training Interval", style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(6, 8).forEach { weeks ->
            FilterChip(
                selected = state.intervalWeeks == weeks,
                onClick = { viewModel.updateIntervalWeeks(weeks) },
                label = { Text("$weeks weeks") }
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    // Gym days per week
    Text("Gym Days Per Week: ${state.gymDaysPerWeek}", style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(8.dp))
    Slider(
        value = state.gymDaysPerWeek.toFloat(),
        onValueChange = { viewModel.updateGymDays(it.toInt()) },
        valueRange = 3f..7f,
        steps = 3,
        colors = SliderDefaults.colors(
            thumbColor = Emerald500,
            activeTrackColor = Emerald500
        )
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("3 days", style = MaterialTheme.typography.bodySmall)
        Text("7 days", style = MaterialTheme.typography.bodySmall)
    }

    // Commitment Meter
    Spacer(Modifier.height(16.dp))
    val commitment = getCommitmentLevel(state.gymDaysPerWeek)
    FitCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(commitment.colorHex).copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Speed,
                    contentDescription = null,
                    tint = Color(commitment.colorHex),
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(commitment.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(commitment.colorHex))
                Text(commitment.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun RotationExplanationStep() {
    Text("Why Rotate Every 6-8 Weeks?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(24.dp))

    val points = listOf(
        "Your body adapts to repetitive exercises, leading to plateaus in progress.",
        "Rotating exercises prevents overuse injuries by varying movement patterns.",
        "New stimuli force muscles to adapt, promoting continued growth and strength.",
        "It keeps your workouts fresh and mentally engaging."
    )

    points.forEachIndexed { index, point ->
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(Emerald500, Cyan500))),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                point,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LiftingExperienceStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Lifting Experience", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("This helps us tailor exercise selection and progression", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    LiftingExperience.entries.forEach { exp ->
        val selected = state.liftingExperience == exp
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(if (selected) Modifier.border(2.dp, Emerald500, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.updateLiftingExperience(exp) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exp.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(exp.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (selected) {
                    Icon(Icons.Default.CheckCircle, null, tint = Emerald500)
                }
            }
        }
    }
}

@Composable
private fun TrainingLocationStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Training Location", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Where do you primarily train? This affects exercise selection.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    TrainingLocation.entries.forEach { loc ->
        val selected = state.trainingLocation == loc
        val icon = when (loc) {
            TrainingLocation.GYM -> Icons.Default.FitnessCenter
            TrainingLocation.HOME -> Icons.Default.Home
        }
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(if (selected) Modifier.border(2.dp, Emerald500, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.updateTrainingLocation(loc) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = if (selected) Emerald500 else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(loc.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(loc.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (selected) {
                    Icon(Icons.Default.CheckCircle, null, tint = Emerald500)
                }
            }
        }
    }
}

@Composable
private fun WorkoutStyleStep(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Text("Workout Style", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Choose how you want to structure your workouts", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))

    WorkoutStyle.entries.forEach { style ->
        val selected = state.workoutStyle == style
        val description = when (style) {
            WorkoutStyle.SINGLE_MUSCLE -> "Chest Day, Back Day, Arm Day, etc. Focus on one muscle per session."
            WorkoutStyle.MUSCLE_GROUP -> "Push/Pull/Legs, Upper/Lower. Compound movements targeting muscle groups."
        }
        FitCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(if (selected) Modifier.border(2.dp, Emerald500, RoundedCornerShape(16.dp)) else Modifier),
            onClick = { viewModel.updateWorkoutStyle(style) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(style.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RadioButton(selected = selected, onClick = { viewModel.updateWorkoutStyle(style) })
            }
        }
    }
}
