package com.nexal.app.ui.meals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.domain.model.FoodItem
import com.nexal.app.domain.model.Meal
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.Cyan500
import com.nexal.app.ui.theme.Emerald500

private val COMMON_ALLERGIES = listOf(
    "Dairy", "Gluten", "Nuts", "Peanuts", "Eggs",
    "Soy", "Shellfish", "Fish", "Wheat", "Sesame"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(
    viewModel: MealsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAllergyModal by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showSubModal by remember { mutableStateOf(false) }
    var selectedAllergies by remember { mutableStateOf(listOf<String>()) }
    var customAllergy by remember { mutableStateOf("") }
    var subReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Plan") },
                actions = {
                    if (uiState.plan != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, "Delete plan", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(onClick = { showAllergyModal = true }) {
                            Icon(Icons.Default.Refresh, "New plan")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen(message = "AI is creating your meal plan...")
        } else if (uiState.plan == null) {
            Column(modifier = Modifier.padding(padding)) {
                EmptyState(
                    icon = Icons.Default.Restaurant,
                    title = "No Meal Plan",
                    description = "Generate an AI-powered meal plan tailored to your goals and macro targets.",
                    actionLabel = "Generate Meal Plan",
                    onAction = { showAllergyModal = true }
                )
                uiState.error?.let { error ->
                    FitCard(modifier = Modifier.padding(16.dp)) {
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        } else {
            val plan = uiState.plan!!
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Daily Totals", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            MacroRow(Icons.Default.LocalFireDepartment, "Calories", "${plan.dailyTotals.calories}", Emerald500)
                            MacroRow(Icons.Default.FitnessCenter, "Protein", "${plan.dailyTotals.protein}g", Emerald500)
                            MacroRow(Icons.Default.Grain, "Carbs", "${plan.dailyTotals.carbs}g", Cyan500)
                            MacroRow(Icons.Default.WaterDrop, "Fats", "${plan.dailyTotals.fats}g", Color(0xFFF59E0B))
                        }
                    }
                }

                // Daily Water Recommendation
                plan.dailyWaterIntakeMl?.let { waterMl ->
                    if (waterMl > 0) {
                        item {
                            FitCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        color = Cyan500.copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.WaterDrop, null, tint = Cyan500, modifier = Modifier.padding(8.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Daily Water Intake", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text("Recommended: ${waterMl} ml (${"%.1f".format(waterMl / 1000.0)}L)", style = MaterialTheme.typography.bodySmall, color = Cyan500)
                                    }
                                }
                            }
                        }
                    }
                }

                plan.aiNotes?.let { notes ->
                    if (notes.isNotBlank()) {
                        item {
                            FitCard {
                                Text(
                                    notes,
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                items(plan.meals, key = { it.id }) { meal ->
                    MealCard(
                        meal = meal,
                        onAddToLog = { viewModel.addMealToLog(meal) },
                        onSubstitute = { food ->
                            viewModel.selectFoodForSub(meal.name, food)
                            subReason = ""
                            showSubModal = true
                        }
                    )
                }
            }
        }

        // Allergy Modal
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

        // Delete Confirmation
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
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    FitButton(text = "Delete Plan", onClick = {
                        viewModel.deletePlan()
                        showDeleteConfirm = false
                    })
                }
            }
        }

        // Substitution Modal
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
private fun MacroRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MealCard(
    meal: Meal,
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
                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${meal.totalMacros.calories} cal", style = MaterialTheme.typography.labelMedium, color = Emerald500)
                    IconButton(onClick = onAddToLog, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add to log", tint = Emerald500, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            meal.foods.forEach { food ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(food.name, style = MaterialTheme.typography.bodyMedium)
                        Text(food.servingSize, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${food.macros.calories} cal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("P:${food.macros.protein}g C:${food.macros.carbs}g F:${food.macros.fats}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onSubstitute(food) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Substitute", modifier = Modifier.size(16.dp))
                        }
                    }
                }
                if (food != meal.foods.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("P: ${meal.totalMacros.protein}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("C: ${meal.totalMacros.carbs}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("F: ${meal.totalMacros.fats}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
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
            Text("Select any food allergies so the AI can avoid them.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                COMMON_ALLERGIES.forEach { allergy ->
                    FilterChip(
                        selected = allergy in allergies,
                        onClick = { onToggleAllergy(allergy) },
                        label = { Text(allergy) }
                    )
                }
            }

            val customOnes = allergies.filter { it !in COMMON_ALLERGIES }
            if (customOnes.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    customOnes.forEach { a ->
                        InputChip(
                            selected = true,
                            onClick = { onToggleAllergy(a) },
                            label = { Text(a) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                Surface(color = Color(0xFFF59E0B).copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Text("The meal plan will exclude: ${allergies.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF59E0B))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
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

            Text("Why do you want to substitute? (optional)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    placeholder = { Text("e.g. I don't like it...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = onSearch, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Search")
                    }
                }
            }

            if (substitutions.isNotEmpty()) {
                Text("Tap to replace", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                substitutions.forEach { sub ->
                    Surface(
                        onClick = { onReplace(sub) },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(sub.name, style = MaterialTheme.typography.titleSmall)
                                Text("${sub.macros?.calories ?: "–"} cal", style = MaterialTheme.typography.labelSmall, color = Emerald500)
                            }
                            sub.servingSize?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            sub.macros?.let { m ->
                                Text("P:${m.protein}g C:${m.carbs}g F:${m.fats}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            sub.reason?.let { Text(it, style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
            }
        }
    }
}
