package com.nexal.app.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.domain.model.AuthState
import com.nexal.app.domain.model.FoodLogEntry
import com.nexal.app.domain.model.FoodSource
import com.nexal.app.domain.model.MacroNutrients
import com.nexal.app.domain.model.MealSlot
import com.nexal.app.domain.model.WaterLogEntry
import com.nexal.app.domain.model.CardioLogEntry
import com.nexal.app.domain.model.UnitSystem
import com.nexal.app.util.Resource
import com.nexal.app.util.calculateMacroTargets
import com.nexal.app.util.calculateDailyWaterIntakeMl
import com.nexal.app.util.estimateCardioCalories
import com.nexal.app.util.generateId
import com.nexal.app.util.todayFormatted
import com.nexal.app.util.getCardioTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

data class DayData(
    val date: String,
    val dayName: String,
    val dayNumber: String,
    val calories: Int,
    val isToday: Boolean
)

data class RecentFood(
    val foodName: String,
    val servingSize: String,
    val macros: MacroNutrients,
    val mealSlot: MealSlot
)

data class NutritionUiState(
    val selectedDate: String = "",
    val weekData: List<DayData> = emptyList(),
    val dayEntries: List<FoodLogEntry> = emptyList(),
    val dayTotals: MacroNutrients = MacroNutrients(0, 0, 0, 0),
    val calorieTarget: Int = 2000,
    val proteinTarget: Int = 150,
    val carbsTarget: Int = 250,
    val fatsTarget: Int = 65,
    val autoFillState: AutoFillState = AutoFillState(),
    val toast: String? = null,
    val waterEntries: List<WaterLogEntry> = emptyList(),
    val waterTotalMl: Int = 0,
    val waterGoalMl: Int = 2500,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val cardioEntries: List<CardioLogEntry> = emptyList(),
    val cardioCaloriesToday: Int = 0,
    val cardioTypes: List<String> = getCardioTypes(),
    val userWeightKg: Double = 70.0,
    val recentFoods: List<RecentFood> = emptyList(),
    val addSlot: MealSlot = MealSlot.BREAKFAST,
    val isPremium: Boolean = false
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepo: NutritionRepository,
    private val profileRepo: ProfileRepository,
    private val aiRepo: AiRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState(selectedDate = todayFormatted()))
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    private var lookupJob: Job? = null
    private var currentFoodName = ""
    private var currentServingSize = ""

    init {
        loadTargets()
        loadWeekData()
        observeSelectedDateLogs()
        observeWaterLogs()
        observeCardioLogs()
        refreshRecentFoods()
        viewModelScope.launch {
            authRepo.authState.collect { auth ->
                _uiState.update {
                    it.copy(isPremium = (auth as? AuthState.Authenticated)?.isPremium == true)
                }
            }
        }
    }

    fun entriesFor(slot: MealSlot): List<FoodLogEntry> =
        _uiState.value.dayEntries.filter { it.mealSlot == slot }

    fun slotCalories(slot: MealSlot): Int =
        entriesFor(slot).sumOf { it.macros.calories * it.quantity }

    private fun loadTargets() {
        viewModelScope.launch {
            val profile = profileRepo.getProfile()
            if (profile != null) {
                val targets = calculateMacroTargets(profile)
                val waterGoal = calculateDailyWaterIntakeMl(profile.weight, profile.activityLevel)
                _uiState.update {
                    it.copy(
                        calorieTarget = targets.calories,
                        proteinTarget = targets.protein,
                        carbsTarget = targets.carbs,
                        fatsTarget = targets.fats,
                        waterGoalMl = waterGoal,
                        unitSystem = profile.unitSystem,
                        userWeightKg = profile.weight
                    )
                }
            }
        }
    }

    private fun loadWeekData() {
        viewModelScope.launch {
            val today = todayFormatted()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dayNameFmt = SimpleDateFormat("EEE", Locale.US)
            val dayNumFmt = SimpleDateFormat("d", Locale.US)
            val cal = Calendar.getInstance()

            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
            cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

            val weekStart = sdf.format(cal.time)
            val calEnd = Calendar.getInstance().apply {
                time = cal.time
                add(Calendar.DAY_OF_YEAR, 6)
            }
            val weekEnd = sdf.format(calEnd.time)

            val days = (0..6).map { offset ->
                val c = Calendar.getInstance().apply {
                    time = cal.time
                    add(Calendar.DAY_OF_YEAR, offset)
                }
                val date = sdf.format(c.time)
                DayData(
                    date = date,
                    dayName = dayNameFmt.format(c.time),
                    dayNumber = dayNumFmt.format(c.time),
                    calories = 0,
                    isToday = date == today
                )
            }
            _uiState.update { it.copy(weekData = days) }

            nutritionRepo.observeFoodLogByDateRange(weekStart, weekEnd).collect { weekLogs ->
                val updatedDays = days.map { day ->
                    val dayCals = weekLogs.filter { it.date == day.date }.sumOf { it.macros.calories * it.quantity }
                    day.copy(calories = dayCals)
                }
                _uiState.update { it.copy(weekData = updatedDays) }
            }
        }
    }

    private fun observeSelectedDateLogs() {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date -> nutritionRepo.observeFoodLogByDate(date) }
                .collect { dayEntries ->
                    val totals = dayEntries.fold(MacroNutrients(0, 0, 0, 0)) { acc, e ->
                        MacroNutrients(
                            calories = acc.calories + e.macros.calories * e.quantity,
                            protein = acc.protein + e.macros.protein * e.quantity,
                            carbs = acc.carbs + e.macros.carbs * e.quantity,
                            fats = acc.fats + e.macros.fats * e.quantity
                        )
                    }
                    _uiState.update { it.copy(dayEntries = dayEntries, dayTotals = totals) }
                }
        }
    }

    private fun refreshRecentFoods() {
        viewModelScope.launch {
            val recent = nutritionRepo.getRecentFoods(50)
                .distinctBy { it.foodName.lowercase() to it.servingSize.lowercase() }
                .take(12)
                .map {
                    RecentFood(
                        foodName = it.foodName,
                        servingSize = it.servingSize,
                        macros = it.macros,
                        mealSlot = it.mealSlot
                    )
                }
            _uiState.update { it.copy(recentFoods = recent) }
        }
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun prepareAdd(slot: MealSlot) {
        _uiState.update { it.copy(addSlot = slot) }
        resetAutoFillState()
    }

    fun resetAutoFillState() {
        lookupJob?.cancel()
        currentFoodName = ""
        currentServingSize = ""
        _uiState.update { it.copy(autoFillState = AutoFillState()) }
    }

    fun addManualEntry(
        name: String,
        serving: String,
        cal: Int,
        protein: Int,
        carbs: Int,
        fats: Int,
        slot: MealSlot = _uiState.value.addSlot
    ) {
        viewModelScope.launch {
            val entry = FoodLogEntry(
                id = generateId(),
                date = _uiState.value.selectedDate,
                foodName = name,
                servingSize = serving,
                quantity = 1,
                macros = MacroNutrients(calories = cal, protein = protein, carbs = carbs, fats = fats),
                source = FoodSource.MANUAL,
                mealSlot = slot,
                createdAt = todayFormatted()
            )
            nutritionRepo.addFoodLogEntry(entry)
            _uiState.update { it.copy(toast = "$name added to ${slot.label}") }
            refreshRecentFoods()
        }
    }

    fun quickAddRecent(food: RecentFood, slot: MealSlot = _uiState.value.addSlot) {
        addManualEntry(
            name = food.foodName,
            serving = food.servingSize,
            cal = food.macros.calories,
            protein = food.macros.protein,
            carbs = food.macros.carbs,
            fats = food.macros.fats,
            slot = slot
        )
    }

    fun copyYesterday() {
        viewModelScope.launch {
            val selected = _uiState.value.selectedDate
            val yesterday = try {
                LocalDate.parse(selected).minusDays(1).toString()
            } catch (_: Exception) {
                _uiState.update { it.copy(toast = "Couldn't copy previous day") }
                return@launch
            }
            val previous = nutritionRepo.getFoodLogByDate(yesterday)
            if (previous.isEmpty()) {
                _uiState.update { it.copy(toast = "Nothing to copy from yesterday") }
                return@launch
            }
            previous.forEach { src ->
                nutritionRepo.addFoodLogEntry(
                    src.copy(
                        id = generateId(),
                        date = selected,
                        createdAt = todayFormatted()
                    )
                )
            }
            _uiState.update { it.copy(toast = "Copied ${previous.size} foods from yesterday") }
            refreshRecentFoods()
        }
    }

    fun removeEntry(id: String) {
        viewModelScope.launch {
            nutritionRepo.deleteFoodLogEntry(id)
            _uiState.update { it.copy(toast = "Entry removed") }
            refreshRecentFoods()
        }
    }

    fun onFoodFieldChange(field: String, value: String) {
        when (field) {
            "name" -> currentFoodName = value
            "servingSize" -> currentServingSize = value
        }

        lookupJob?.cancel()
        _uiState.update { it.copy(autoFillState = AutoFillState()) }
        if (!_uiState.value.isPremium) return

        if (currentFoodName.length >= 2) {
            lookupJob = viewModelScope.launch {
                delay(800)
                _uiState.update { it.copy(autoFillState = AutoFillState(isLoading = true)) }
                when (val result = aiRepo.lookupFoodMacros(currentFoodName, currentServingSize.ifBlank { "standard serving" })) {
                    is Resource.Success -> {
                        val data = result.data
                        _uiState.update {
                            it.copy(
                                autoFillState = AutoFillState(
                                    autoFilled = true,
                                    calories = data.calories.toString(),
                                    protein = data.protein.toString(),
                                    carbs = data.carbs.toString(),
                                    fats = data.fats.toString()
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(autoFillState = AutoFillState(), toast = "Could not estimate nutrition") }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toast = null) }
    }

    private fun observeWaterLogs() {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date -> nutritionRepo.observeWaterLogByDate(date) }
                .collect { entries ->
                    _uiState.update { it.copy(waterEntries = entries, waterTotalMl = entries.sumOf { e -> e.amount }) }
                }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            nutritionRepo.addWaterLogEntry(
                WaterLogEntry(
                    id = generateId(),
                    date = _uiState.value.selectedDate,
                    amount = amountMl,
                    createdAt = todayFormatted()
                )
            )
            _uiState.update { it.copy(toast = "Water logged") }
        }
    }

    fun removeWaterEntry(id: String) {
        viewModelScope.launch { nutritionRepo.deleteWaterLogEntry(id) }
    }

    private fun observeCardioLogs() {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date -> nutritionRepo.observeCardioLogByDate(date) }
                .collect { entries ->
                    _uiState.update {
                        it.copy(
                            cardioEntries = entries,
                            cardioCaloriesToday = entries.sumOf { e -> e.estimatedCaloriesBurnt }
                        )
                    }
                }
        }
    }

    fun addCardioEntry(type: String, durationMinutes: Int, notes: String = "") {
        viewModelScope.launch {
            val calories = estimateCardioCalories(type, durationMinutes, _uiState.value.userWeightKg)
            nutritionRepo.addCardioLogEntry(
                CardioLogEntry(
                    id = generateId(),
                    date = _uiState.value.selectedDate,
                    type = type,
                    durationMinutes = durationMinutes,
                    estimatedCaloriesBurnt = calories,
                    notes = notes,
                    createdAt = todayFormatted()
                )
            )
            _uiState.update { it.copy(toast = "$type logged · $calories cal") }
        }
    }

    fun removeCardioEntry(id: String) {
        viewModelScope.launch { nutritionRepo.deleteCardioLogEntry(id) }
    }
}
