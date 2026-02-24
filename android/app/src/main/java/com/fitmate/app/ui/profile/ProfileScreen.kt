package com.fitmate.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitmate.app.domain.model.ActivityLevel
import com.fitmate.app.domain.model.FitnessGoal
import com.fitmate.app.domain.model.Gender
import com.fitmate.app.domain.model.LiftingExperience
import com.fitmate.app.domain.model.TrainingLocation
import com.fitmate.app.domain.model.UnitSystem
import com.fitmate.app.ui.components.*
import com.fitmate.app.ui.theme.Cyan500
import com.fitmate.app.ui.theme.Emerald500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSubscription: () -> Unit,
    onBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGoalModal by remember { mutableStateOf(false) }
    var showActivityModal by remember { mutableStateOf(false) }
    var showPasswordModal by remember { mutableStateOf(false) }
    var showEmailModal by remember { mutableStateOf(false) }
    var showLiftingExpModal by remember { mutableStateOf(false) }
    var showTrainingLocModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!uiState.editing) {
                        TextButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit")
                        }
                    } else {
                        TextButton(onClick = { viewModel.saveProfile() }) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // Success banner
            if (uiState.saved) {
                item {
                    Surface(color = Emerald500.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Emerald500, modifier = Modifier.size(18.dp))
                            Text("Profile saved successfully!", style = MaterialTheme.typography.bodyMedium, color = Emerald500, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Avatar & Name
            item {
                FitCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(color = Emerald500.copy(alpha = 0.2f), shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.size(64.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Emerald500, modifier = Modifier.padding(16.dp))
                        }
                        if (uiState.editing) {
                            OutlinedTextField(
                                value = uiState.formName,
                                onValueChange = { viewModel.updateFormField("name", it) },
                                label = { Text("Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        } else {
                            Column {
                                Text(uiState.name.ifBlank { "No name set" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Nexal Member", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Body Stats
            item {
                Text("Body Stats", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ProfileRow("Weight", if (uiState.editing) null else "${uiState.weight} kg") {
                            OutlinedTextField(value = uiState.formWeight, onValueChange = { viewModel.updateFormField("weight", it) }, modifier = Modifier.width(100.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                        HorizontalDivider()
                        ProfileRow("Height", if (uiState.editing) null else "${uiState.height} cm") {
                            OutlinedTextField(value = uiState.formHeight, onValueChange = { viewModel.updateFormField("height", it) }, modifier = Modifier.width(100.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        HorizontalDivider()
                        ProfileRow("Age", if (uiState.editing) null else "${uiState.age} years") {
                            OutlinedTextField(value = uiState.formAge, onValueChange = { viewModel.updateFormField("age", it) }, modifier = Modifier.width(100.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        HorizontalDivider()
                        if (uiState.editing) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Gender", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Gender.entries.forEach { g ->
                                        FilterChip(
                                            selected = uiState.formGender == g.name.lowercase(),
                                            onClick = { viewModel.updateFormField("gender", g.name.lowercase()) },
                                            label = { Text(g.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                        )
                                    }
                                }
                            }
                        } else {
                            ProfileRow("Gender", uiState.gender.replaceFirstChar { it.uppercase() })
                        }
                        HorizontalDivider()
                        ProfileRow("Target Weight", if (uiState.editing) null else "${uiState.targetWeight} kg") {
                            OutlinedTextField(value = uiState.formTargetWeight, onValueChange = { viewModel.updateFormField("targetWeight", it) }, modifier = Modifier.width(100.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                    }
                }
            }

            // Activity Level
            item { Text("Activity Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = if (uiState.editing) ({ showActivityModal = true }) else null
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            val level = ActivityLevel.entries.find { it.name.lowercase() == uiState.activityLevel }
                            Text(level?.label ?: uiState.activityLevel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(level?.description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (uiState.editing) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Fitness Goals
            item { Text("Fitness Goals", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = if (uiState.editing) ({ showGoalModal = true }) else null
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            uiState.fitnessGoals.forEach { goal ->
                                val fg = FitnessGoal.entries.find { it.name.lowercase() == goal }
                                Surface(color = Emerald500.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
                                    Text(fg?.label ?: goal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Emerald500)
                                }
                            }
                        }
                        if (uiState.editing) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Training Settings
            item { Text("Training Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Rotation period", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (uiState.editing) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(6, 8).forEach { w ->
                                        FilterChip(
                                            selected = uiState.formIntervalWeeks == w,
                                            onClick = { viewModel.updateFormField("intervalWeeks", w.toString()) },
                                            label = { Text("$w wks") }
                                        )
                                    }
                                }
                            } else {
                                Text("${uiState.intervalWeeks} weeks", fontWeight = FontWeight.Medium)
                            }
                        }
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Gym days / week", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (uiState.editing) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    (3..7).forEach { d ->
                                        FilterChip(
                                            selected = uiState.formGymDays == d,
                                            onClick = { viewModel.updateFormField("gymDays", d.toString()) },
                                            label = { Text("$d") }
                                        )
                                    }
                                }
                            } else {
                                Text("${uiState.gymDaysPerWeek} days", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Lifting Experience
            item { Text("Lifting Experience", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = if (uiState.editing) ({ showLiftingExpModal = true }) else null
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            val exp = LiftingExperience.entries.find { it.name == uiState.liftingExperience }
                            Text(exp?.label ?: uiState.liftingExperience, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(exp?.description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (uiState.editing) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Training Location
            item { Text("Training Location", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = if (uiState.editing) ({ showTrainingLocModal = true }) else null
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            val loc = TrainingLocation.entries.find { it.name == uiState.trainingLocation }
                            Text(loc?.label ?: uiState.trainingLocation, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(loc?.description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (uiState.editing) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Unit System
            item { Text("Unit System", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Preferred Units", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (uiState.editing) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                UnitSystem.entries.forEach { u ->
                                    FilterChip(
                                        selected = uiState.formUnitSystem == u.name,
                                        onClick = { viewModel.updateFormField("unitSystem", u.name) },
                                        label = { Text(u.label) }
                                    )
                                }
                            }
                        } else {
                            val u = UnitSystem.entries.find { it.name == uiState.unitSystem }
                            Text(u?.label ?: uiState.unitSystem, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Appearance
            item { Text("Appearance", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = if (uiState.isDarkMode) Cyan500 else Color(0xFFF59E0B))
                            Column {
                                Text("Dark Mode", fontWeight = FontWeight.Medium)
                                Text(if (uiState.isDarkMode) "On" else "Off", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = uiState.isDarkMode, onCheckedChange = { viewModel.toggleTheme() })
                    }
                }
            }

            // Account Security
            item { Text("Account Security", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                FitCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Email", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(uiState.email, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                            TextButton(onClick = { showEmailModal = true }) { Text("Change") }
                        }
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("Password", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("••••••••", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                            TextButton(onClick = { showPasswordModal = true }) { Text("Change") }
                        }
                    }
                }
            }

            // Subscription card
            item {
                Surface(
                    onClick = onNavigateToSubscription,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(color = Color(0xFFF59E0B).copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.padding(8.dp))
                            }
                            Column {
                                Text("Subscription & Billing", fontWeight = FontWeight.Medium)
                                Text("Manage your plan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Sign out
            item {
                OutlinedButton(
                    onClick = { viewModel.signOut(); onSignedOut() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out")
                }
            }
        }

        // Activity Level Modal
        if (showActivityModal) {
            FitModal(isOpen = true, onDismiss = { showActivityModal = false }, title = "Activity Level") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActivityLevel.entries.forEach { level ->
                        val isSelected = uiState.formActivityLevel == level.name.lowercase()
                        Surface(
                            onClick = { viewModel.updateFormField("activityLevel", level.name.lowercase()); showActivityModal = false },
                            color = if (isSelected) Emerald500.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Emerald500) else null
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(level.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurface)
                                Text(level.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Goals Modal
        if (showGoalModal) {
            FitModal(isOpen = true, onDismiss = { showGoalModal = false }, title = "Fitness Goals") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select one or more goals", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    FitnessGoal.entries.forEach { goal ->
                        val isSelected = goal.name.lowercase() in uiState.formGoals
                        Surface(
                            onClick = { viewModel.toggleGoal(goal.name.lowercase()) },
                            color = if (isSelected) Emerald500.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Emerald500) else null
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(goal.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurface)
                                if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = Emerald500, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Change Password Modal
        if (showPasswordModal) {
            ChangePasswordModal(
                onDismiss = { showPasswordModal = false },
                onSubmit = { current, new_ -> viewModel.changePassword(current, new_) },
                isLoading = uiState.passwordLoading,
                error = uiState.passwordError
            )
        }

        // Change Email Modal
        if (showEmailModal) {
            ChangeEmailModal(
                onDismiss = { showEmailModal = false },
                onSubmit = { email -> viewModel.changeEmail(email) },
                isLoading = uiState.emailLoading,
                error = uiState.emailError,
                emailSent = uiState.emailSent
            )
        }

        // Lifting Experience Modal
        if (showLiftingExpModal) {
            FitModal(isOpen = true, onDismiss = { showLiftingExpModal = false }, title = "Lifting Experience") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiftingExperience.entries.forEach { exp ->
                        val isSelected = uiState.formLiftingExperience == exp.name
                        Surface(
                            onClick = { viewModel.updateFormField("liftingExperience", exp.name); showLiftingExpModal = false },
                            color = if (isSelected) Emerald500.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Emerald500) else null
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(exp.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurface)
                                Text(exp.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Training Location Modal
        if (showTrainingLocModal) {
            FitModal(isOpen = true, onDismiss = { showTrainingLocModal = false }, title = "Training Location") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrainingLocation.entries.forEach { loc ->
                        val isSelected = uiState.formTrainingLocation == loc.name
                        Surface(
                            onClick = { viewModel.updateFormField("trainingLocation", loc.name); showTrainingLocModal = false },
                            color = if (isSelected) Emerald500.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Emerald500) else null
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(loc.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurface)
                                Text(loc.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String?, editContent: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (value != null) {
            Text(value, fontWeight = FontWeight.Medium)
        } else {
            editContent?.invoke()
        }
    }
}

@Composable
private fun ChangePasswordModal(
    onDismiss: () -> Unit,
    onSubmit: (current: String, new_: String) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }

    FitModal(
        isOpen = true,
        onDismiss = onDismiss,
        title = "Change Password"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPasswords = !showPasswords }) {
                        Icon(if (showPasswords) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "Toggle visibility")
                    }
                }
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password (min. 6 chars)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()
            )
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            GradientButton(
                text = "Update Password",
                onClick = {
                    if (newPassword == confirmPassword && newPassword.length >= 6) {
                        onSubmit(currentPassword, newPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChangeEmailModal(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
    emailSent: Boolean
) {
    var newEmail by remember { mutableStateOf("") }

    FitModal(
        isOpen = true,
        onDismiss = onDismiss,
        title = "Change Email"
    ) {
        if (emailSent) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = Emerald500.copy(alpha = 0.2f), shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Emerald500, modifier = Modifier.padding(16.dp))
                }
                Text("Check your inbox", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("We sent a verification link to $newEmail. Click the link to confirm.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FitButton(text = "Done", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter your new email address. You'll need to verify it.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("New email address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp))
                }
                Spacer(Modifier.height(8.dp))
                GradientButton(
                    text = "Send Verification Email",
                    onClick = { onSubmit(newEmail) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
