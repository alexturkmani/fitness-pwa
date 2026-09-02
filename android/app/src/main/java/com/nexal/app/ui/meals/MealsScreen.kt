package com.nexal.app.ui.meals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.domain.model.FoodItem
import com.nexal.app.domain.model.Meal
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*

private val COMMON_ALLERGIES = listOf(
    "Dairy", "Gluten", "Nuts", "Peanuts", "Eggs",
    "Soy", "Shellfish", "Fish", "Wheat", "Sesame"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    viewModel: MealsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAllergyModal by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showSubModal by remember { mutableStateOf(false) }
    var selectedAllergies by remember { mutableStateOf(listOf<String>()) }
    var customAllergy by remember { mutableStateOf("") }
    var subReason by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SuccessGreen,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Food Diary", style = MaterialTheme.typography.headlineMedium)
                },
                actions = {
                    if (uiState.plan != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, "Delete plan", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(
                            onClick = { if (isPremium) showAllergyModal = true else onUpgrade() }
                        ) {
                            Icon(Icons.Default.Refresh, "New plan", tint = BrandBlue)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen(
                message = "AI is creating your meal plan...",
                modifier = Modifier.padding(padding)
            )
        } else if (uiState.plan == null) {
            EmptyState(
                icon = Icons.Default.Restaurant,
                title = "No Meal Plan Yet",
                description = if (isPremium) {
                    "Generate an AI meal plan tailored to your goals and macro targets."
                } else {
                    "Track meals manually in Diary, or unlock a personalized AI meal plan."
                },
                actionLabel = if (isPremium) "Generate Meal Plan" else "Unlock AI Meal Plan",
                onAction = { if (isPremium) showAllergyModal = true else onUpgrade() },
                error = uiState.error,
                modifier = Modifier.padding(padding)
            )
        } else {
            val plan = uiState.plan!!
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    ScalePopIn {
                        FitCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Daily Totals",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "${plan.dailyTotals.calories}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlue
                                )
                                Text(
                                    "calories planned",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MacroChip("Protein", plan.dailyTotals.protein, plan.dailyTotals.protein, MacroProtein)
                                    MacroChip("Carbs", plan.dailyTotals.carbs, plan.dailyTotals.carbs, MacroCarbs)
                                    MacroChip("Fat", plan.dailyTotals.fats, plan.dailyTotals.fats, MacroFat)
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item {
                        FadeSlideIn {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                    if (!isPremium && error.contains("Premium", ignoreCase = true)) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(onClick = onUpgrade, modifier = Modifier.fillMaxWidth()) {
                                            Icon(Icons.Default.WorkspacePremium, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("View Premium Plans")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                plan.dailyWaterIntakeMl?.let { waterMl ->
                    if (waterMl > 0) {
                        item {
                            FadeSlideIn(delayMs = 80) {
                                FitCard {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Accent.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.WaterDrop, null, tint = Accent, modifier = Modifier.size(20.dp))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Daily Water",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                "Recommended: ${waterMl} ml (${"%.1f".format(waterMl / 1000.0)}L)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Accent
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                plan.aiNotes?.let { notes ->
                    if (notes.isNotBlank()) {
                        item {
                            FadeSlideIn(delayMs = 100) {
                                FitCard {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Coach Notes",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = BrandBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            notes,
                                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    FadeSlideIn(delayMs = 120) {
                        Text(
                            "MEALS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                itemsIndexed(
                    plan.meals,
                    key = { _, meal -> MealsViewModel.mealKey(meal) }
                ) { index, meal ->
                    FadeSlideIn(delayMs = 140 + index * 55) {
                        MealCard(
                            meal = meal,
                            added = MealsViewModel.mealKey(meal) in uiState.addedMealIds,
                            onAddToLog = { viewModel.addMealToLog(meal) },
                            onSubstitute = { food ->
                                if (isPremium) {
                                    viewModel.selectFoodForSub(meal.name, food)
                                    subReason = ""
                                    showSubModal = true
                                } else {
                                    onUpgrade()
                                }
                            }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        uiState.toast?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearToast()
            }
        }

        if (showAllergyModal) {
            AllergySelectionModal(
                allergies = selectedAllergies,
                customAllergy = customAllergy,
                onCustomAllergyChange = { customAllergy = it },
                onAddCustom = {
                    val trimmed = customAllergy.trim()
                    if (trimmed.isNotEmpty() && trimmed !in selectedAllergies) {
                        selectedAllergies = selectedAllergies + trimmed
                    }
                    customAllergy = ""
                },
                onToggleAllergy = { allergy ->
                    selectedAllergies = if (allergy in selectedAllergies) selectedAllergies - allergy else selectedAllergies + allergy
                },
                onGenerate = {
                    viewModel.generatePlan(selectedAllergies)
                    showAllergyModal = false
                },
                onDismiss = { showAllergyModal = false }
            )
        }

        if (showDeleteConfirm) {
            FitModal(
                isOpen = true,
                title = "Delete Meal Plan?",
                onDismiss = { showDeleteConfirm = false }
            ) {
                Text(
                    "This will permanently delete your current meal plan. You can always generate a new one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    FitButton(text = "Delete Plan", onClick = {
                        viewModel.deletePlan()
                        showDeleteConfirm = false
                    })
                }
            }
        }

        if (showSubModal && uiState.selectedFood != null) {
            SubstitutionModal(
                foodName = uiState.selectedFood!!.second.name,
                mealName = uiState.selectedFood!!.first,
                macros = uiState.selectedFood!!.second.macros,
                reason = subReason,
                onReasonChange = { subReason = it },
                onSearch = { viewModel.getSubstitutions(subReason) },
                substitutions = uiState.substitutions,
                isLoading = uiState.subLoading,
                onReplace = { sub ->
                    viewModel.replaceFood(sub)
                    showSubModal = false
                },
                onDismiss = { showSubModal = false }
            )
        }
    }
}

@Composable
private fun MealCard(
    meal: Meal,
    added: Boolean,
    onAddToLog: () -> Unit,
    onSubstitute: (FoodItem) -> Unit
) {
    FitCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        meal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (added) "Added to diary · ${meal.totalMacros.calories} cal"
                        else "${meal.totalMacros.calories} cal",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (added) SuccessGreen else BrandBlue
                    )
                }
                AddConfirmIconButton(
                    added = added,
                    onClick = onAddToLog,
                    contentDescription = "Add to log"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            meal.foods.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(food.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(
                            food.servingSize,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${food.macros.calories} cal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("P ${food.macros.protein}g", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
                                Text("C ${food.macros.carbs}g", style = MaterialTheme.typography.labelSmall, color = MacroCarbs)
                                Text("F ${food.macros.fats}g", style = MaterialTheme.typography.labelSmall, color = MacroFat)
                            }
                        }
                        IconButton(onClick = { onSubstitute(food) }, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "Substitute",
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                if (food != meal.foods.last()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MealMacroBadge("P", "${meal.totalMacros.protein}g", MacroProtein)
                MealMacroBadge("C", "${meal.totalMacros.carbs}g", MacroCarbs)
                MealMacroBadge("F", "${meal.totalMacros.fats}g", MacroFat)
            }
        }
    }
}

@Composable
private fun MealMacroBadge(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            "$label $value",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllergySelectionModal(
    allergies: List<String>,
    customAllergy: String,
    onCustomAllergyChange: (String) -> Unit,
    onAddCustom: () -> Unit,
    onToggleAllergy: (String) -> Unit,
    onGenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    FitModal(
        isOpen = true,
        title = "Any Allergies?",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Select any food allergies so the AI can avoid them.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                COMMON_ALLERGIES.forEach { allergy ->
                    FilterChip(
                        selected = allergy in allergies,
                        onClick = { onToggleAllergy(allergy) },
                        label = { Text(allergy) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Accent,
                            selectedLabelColor = BrandBlue
                        )
                    )
                }
            }

            val customOnes = allergies.filter { it !in COMMON_ALLERGIES }
            if (customOnes.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    customOnes.forEach { a ->
                        InputChip(
                            selected = true,
                            onClick = { onToggleAllergy(a) },
                            label = { Text(a) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customAllergy,
                    onValueChange = onCustomAllergyChange,
                    placeholder = { Text("Add custom allergy...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                FitButton(
                    text = "Add",
                    onClick = onAddCustom,
                    variant = ButtonVariant.SECONDARY,
                    enabled = customAllergy.isNotBlank()
                )
            }

            if (allergies.isNotEmpty()) {
                Surface(
                    color = WarningAmber.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "The meal plan will exclude: ${allergies.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            GradientButton(
                text = "Generate Meal Plan",
                onClick = onGenerate,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SubstitutionModal(
    foodName: String,
    mealName: String,
    macros: com.nexal.app.domain.model.MacroNutrients,
    reason: String,
    onReasonChange: (String) -> Unit,
    onSearch: () -> Unit,
    substitutions: List<com.nexal.app.domain.model.MealSubstitution>,
    isLoading: Boolean,
    onReplace: (com.nexal.app.domain.model.MealSubstitution) -> Unit,
    onDismiss: () -> Unit
) {
    FitModal(
        isOpen = true,
        title = "Substitute Ingredient",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(foodName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "from $mealName • ${macros.calories} cal • P:${macros.protein}g C:${macros.carbs}g F:${macros.fats}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                "Why do you want to substitute? (optional)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    placeholder = { Text("e.g. I don't like it...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = onSearch, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrandBlue)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Search", tint = BrandBlue)
                    }
                }
            }

            if (substitutions.isNotEmpty()) {
                Text(
                    "Tap to replace",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                substitutions.forEach { sub ->
                    Surface(
                        onClick = { onReplace(sub) },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sub.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${sub.macros?.calories ?: "–"} cal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandBlue
                                )
                            }
                            sub.servingSize?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            sub.macros?.let { m ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("P:${m.protein}g", style = MaterialTheme.typography.bodySmall, color = MacroProtein)
                                    Text("C:${m.carbs}g", style = MaterialTheme.typography.bodySmall, color = MacroCarbs)
                                    Text("F:${m.fats}g", style = MaterialTheme.typography.bodySmall, color = MacroFat)
                                }
                            }
                            sub.reason?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
