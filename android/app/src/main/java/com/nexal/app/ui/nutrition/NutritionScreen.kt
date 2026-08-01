package com.nexal.app.ui.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.domain.model.FoodLogEntry
import com.nexal.app.domain.model.CardioLogEntry
import com.nexal.app.domain.model.MealSlot
import com.nexal.app.domain.model.UnitSystem
import com.nexal.app.domain.model.WaterLogEntry
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*
import com.nexal.app.util.formatWater
import kotlinx.coroutines.flow.filter

private val diaryMealSlots = listOf(
    MealSlot.BREAKFAST,
    MealSlot.LUNCH,
    MealSlot.DINNER,
    MealSlot.SNACK
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onNavigateToScanner: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddModal by remember { mutableStateOf(false) }
    var showCardioModal by remember { mutableStateOf(false) }

    fun openAddFor(slot: MealSlot) {
        viewModel.prepareAdd(slot)
        showAddModal = true
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Diary", fontWeight = FontWeight.Bold, maxLines = 1)
                },
                actions = {
                    IconButton(onClick = onNavigateToScanner) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan barcode",
                            tint = BrandBlue
                        )
                    }
                    IconButton(onClick = { viewModel.copyYesterday() }) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy yesterday",
                            tint = BrandBlue
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddFor(uiState.addSlot) },
                containerColor = BrandBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            item(key = "week") {
                FadeSlideIn {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.weekData, key = { it.date }) { day ->
                            DayCalendarCell(
                                dayName = day.dayName,
                                dayNumber = day.dayNumber,
                                calPercent = if (uiState.calorieTarget > 0) {
                                    (day.calories.toFloat() / uiState.calorieTarget).coerceAtMost(1.2f)
                                } else {
                                    0f
                                },
                                isSelected = day.date == uiState.selectedDate,
                                isToday = day.isToday,
                                onClick = { viewModel.selectDate(day.date) }
                            )
                        }
                    }
                }
            }

            item(key = "calories") {
                ScalePopIn(delayMs = 40) {
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Calories",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CalorieRing(
                                consumed = uiState.dayTotals.calories,
                                goal = uiState.calorieTarget
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "${uiState.dayTotals.calories} eaten · ${uiState.calorieTarget} goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MacroChip("Protein", uiState.dayTotals.protein, uiState.proteinTarget, MacroProtein)
                                MacroChip("Carbs", uiState.dayTotals.carbs, uiState.carbsTarget, MacroCarbs)
                                MacroChip("Fat", uiState.dayTotals.fats, uiState.fatsTarget, MacroFat)
                            }
                        }
                    }
                }
            }

            if (uiState.recentFoods.isNotEmpty()) {
                item(key = "recent") {
                    FadeSlideIn(delayMs = 80) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Recent",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.recentFoods.take(12).forEach { food ->
                                    RecentFoodChip(
                                        food = food,
                                        onClick = {
                                            viewModel.quickAddRecent(food, uiState.addSlot)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            diaryMealSlots.forEachIndexed { index, slot ->
                item(key = "meal_${slot.name}") {
                    val slotEntries = uiState.dayEntries.filter { it.mealSlot == slot }
                    val slotCalories = slotEntries.sumOf { it.macros.calories * it.quantity }
                    FadeSlideIn(delayMs = 110 + index * 45) {
                        MealSlotCard(
                            slot = slot,
                            entries = slotEntries,
                            slotCalories = slotCalories,
                            onAddFood = { openAddFor(slot) },
                            onDeleteEntry = { viewModel.removeEntry(it) }
                        )
                    }
                }
            }

            item(key = "water") {
                FadeSlideIn(delayMs = 300) {
                    WaterSection(
                        waterTotalMl = uiState.waterTotalMl,
                        waterGoalMl = uiState.waterGoalMl,
                        unitSystem = uiState.unitSystem,
                        waterEntries = uiState.waterEntries,
                        onAddWater = { viewModel.addWater(it) },
                        onRemoveWater = { viewModel.removeWaterEntry(it) }
                    )
                }
            }

            item(key = "cardio") {
                FadeSlideIn(delayMs = 340) {
                    CardioSection(
                        cardioEntries = uiState.cardioEntries,
                        cardioCaloriesToday = uiState.cardioCaloriesToday,
                        onAdd = { showCardioModal = true },
                        onDelete = { viewModel.removeCardioEntry(it) }
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        if (showAddModal) {
            LaunchedEffect(Unit) { viewModel.resetAutoFillState() }
            AddFoodModal(
                onDismiss = { showAddModal = false },
                onAdd = { name, serving, cal, protein, carbs, fats ->
                    viewModel.addManualEntry(name, serving, cal, protein, carbs, fats)
                    showAddModal = false
                },
                onFieldChange = viewModel::onFoodFieldChange,
                autoFillState = uiState.autoFillState
            )
        }

        if (showCardioModal) {
            AddCardioModal(
                cardioTypes = uiState.cardioTypes,
                onDismiss = { showCardioModal = false },
                onAdd = { type, duration, notes ->
                    viewModel.addCardioEntry(type, duration, notes)
                    showCardioModal = false
                }
            )
        }
    }

    uiState.toast?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearToast()
        }
    }
}

@Composable
private fun MealSlotCard(
    slot: MealSlot,
    entries: List<FoodLogEntry>,
    slotCalories: Int,
    onAddFood: () -> Unit,
    onDeleteEntry: (String) -> Unit
) {
    FitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    slot.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "$slotCalories cal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (slotCalories > 0) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

            if (entries.isEmpty()) {
                Text(
                    "No foods logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column {
                    entries.forEachIndexed { index, entry ->
                        DiaryFoodRow(
                            entry = entry,
                            onDelete = { onDeleteEntry(entry.id) }
                        )
                        if (index < entries.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                            )
                        }
                    }
                }
            }

            FitButton(
                text = "+ Add food",
                onClick = onAddFood,
                variant = ButtonVariant.GHOST,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DiaryFoodRow(
    entry: FoodLogEntry,
    onDelete: () -> Unit
) {
    val calories = entry.macros.calories * entry.quantity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.foodName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                buildString {
                    append(entry.servingSize.ifBlank { "1 serving" })
                    if (entry.quantity > 1) append(" × ${entry.quantity}")
                    append(" · ")
                    append("${calories} cal")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("P ${entry.macros.protein}g", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
                Text("C ${entry.macros.carbs}g", style = MaterialTheme.typography.labelSmall, color = MacroCarbs)
                Text("F ${entry.macros.fats}g", style = MaterialTheme.typography.labelSmall, color = MacroFat)
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RecentFoodChip(
    food: RecentFood,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                food.foodName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${food.macros.calories} cal",
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlue
            )
        }
    }
}

@Composable
private fun WaterSection(
    waterTotalMl: Int,
    waterGoalMl: Int,
    unitSystem: UnitSystem,
    waterEntries: List<WaterLogEntry>,
    onAddWater: (Int) -> Unit,
    onRemoveWater: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.WaterDrop, null, tint = Cyan500, modifier = Modifier.size(18.dp))
                    Text(
                        "Water",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    formatWater(waterTotalMl, unitSystem) + " / " + formatWater(waterGoalMl, unitSystem),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val waterPercent = if (waterGoalMl > 0) {
                (waterTotalMl.toFloat() / waterGoalMl).coerceAtMost(1f)
            } else {
                0f
            }
            LinearProgressIndicator(
                progress = { waterPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Cyan500,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(150, 250, 350, 500).forEach { ml ->
                    OutlinedButton(
                        onClick = { onAddWater(ml) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan500),
                        border = BorderStroke(1.dp, Cyan500.copy(alpha = 0.45f))
                    ) {
                        Text("${ml}ml", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (waterEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                waterEntries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            formatWater(entry.amount, unitSystem),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = { onRemoveWater(entry.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardioSection(
    cardioEntries: List<CardioLogEntry>,
    cardioCaloriesToday: Int,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.DirectionsRun, null, tint = MacroFat, modifier = Modifier.size(18.dp))
                    Text(
                        "Cardio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = onAdd, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = BrandBlue)
                    Text("Add", color = BrandBlue)
                }
            }
            if (cardioCaloriesToday > 0) {
                Text(
                    "Burnt: $cardioCaloriesToday cal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MacroFat
                )
            }
            if (cardioEntries.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "No cardio logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                cardioEntries.forEach { entry ->
                    CardioEntryRow(entry = entry, onDelete = { onDelete(entry.id) })
                }
            }
        }
    }
}

@Composable
private fun DayCalendarCell(
    dayName: String,
    dayNumber: String,
    calPercent: Float,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BrandBlue else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isToday) BrandBlue else MaterialTheme.colorScheme.onSurfaceVariant
    val ringColor = when {
        calPercent > 1f -> MaterialTheme.colorScheme.error
        calPercent > 0f -> BrandBlue
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        onClick = onClick,
        color = if (isSelected) BrandBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        tonalElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp).width(44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                dayName,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                dayNumber,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Canvas(modifier = Modifier.size(22.dp)) {
                val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                drawArc(color = Slate200, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * calPercent.coerceAtMost(1f),
                    useCenter = false,
                    style = stroke
                )
            }
        }
    }
}

data class AutoFillState(
    val isLoading: Boolean = false,
    val autoFilled: Boolean = false,
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fats: String = ""
)

@Composable
private fun AddFoodModal(
    onDismiss: () -> Unit,
    onAdd: (name: String, serving: String, cal: Int, protein: Int, carbs: Int, fats: Int) -> Unit,
    onFieldChange: (field: String, value: String) -> Unit,
    autoFillState: AutoFillState
) {
    var name by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    val currentAutoFillState by rememberUpdatedState(autoFillState)
    LaunchedEffect(Unit) {
        snapshotFlow { currentAutoFillState }
            .filter { it.autoFilled }
            .collect { state ->
                calories = state.calories
                protein = state.protein
                carbs = state.carbs
                fats = state.fats
            }
    }

    FitModal(
        isOpen = true,
        title = "Add Food Entry",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    onFieldChange("name", it)
                },
                label = { Text("Food Name") },
                placeholder = { Text("e.g., Chicken Breast") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = servingSize,
                onValueChange = {
                    servingSize = it
                    onFieldChange("servingSize", it)
                },
                label = { Text("Serving Size / Weight") },
                placeholder = { Text("e.g., 150g") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (autoFillState.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = BrandBlue)
                    Text("Estimating nutrition info...", style = MaterialTheme.typography.bodySmall, color = BrandBlue)
                }
            }

            if (autoFillState.autoFilled && !autoFillState.isLoading) {
                Surface(color = BrandBlue.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                    Text(
                        "Nutrition auto-filled from AI estimate. You can adjust values.",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandBlue
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            GradientButton(
                text = "Add Entry",
                onClick = {
                    if (name.isNotBlank() && calories.isNotBlank()) {
                        onAdd(
                            name,
                            servingSize.ifBlank { "1 serving" },
                            calories.toIntOrNull() ?: 0,
                            protein.toIntOrNull() ?: 0,
                            carbs.toIntOrNull() ?: 0,
                            fats.toIntOrNull() ?: 0
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CardioEntryRow(entry: CardioLogEntry, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.type, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                "${entry.durationMinutes} min • ${entry.estimatedCaloriesBurnt} cal burnt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!entry.notes.isNullOrBlank()) {
                Text(entry.notes!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCardioModal(
    cardioTypes: List<String>,
    onDismiss: () -> Unit,
    onAdd: (type: String, duration: Int, notes: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(cardioTypes.firstOrNull() ?: "Running") }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    FitModal(
        isOpen = true,
        title = "Log Cardio Session",
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cardio Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    cardioTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { selectedType = type; expanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            GradientButton(
                text = "Log Cardio",
                onClick = {
                    val dur = duration.toIntOrNull()
                    if (dur != null && dur > 0) {
                        onAdd(selectedType, dur, notes)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
