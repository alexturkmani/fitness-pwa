package com.nexal.app.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.domain.model.FoodLogEntry
import com.nexal.app.domain.model.FoodSource
import com.nexal.app.domain.model.MacroNutrients
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
import java.util.*
import javax.inject.Inject

data class DayData(
    val date: String,
    val dayName: String,
    val dayNumber: String,
    val calories: Int,
    val isToday: Boolean
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
    // Water tracking
    val waterEntries: List<WaterLogEntry> = emptyList(),
    val waterTotalMl: Int = 0,
    val waterGoalMl: Int = 2500,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    // Cardio tracking
    val cardioEntries: List<CardioLogEntry> = emptyList(),
    val cardioCaloriesToday: Int = 0,
    val cardioTypes: List<String> = getCardioTypes(),
    val userWeightKg: Double = 70.0
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepo: NutritionRepository,
    private val profileRepo: ProfileRepository,
    private val aiRepo: AiRepository
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
    }

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

            // Observe the whole week's food logs for calendar calorie rings
            nutritionRepo.observeFoodLogByDateRange(weekStart, weekEnd).collect { weekLogs ->
                val updatedDays = days.map { day ->
                    val dayCals = weekLogs.filter { it.date == day.date }.sumOf { it.macros.calories }
                    day.copy(calories = dayCals)
                }
                _uiState.update { it.copy(weekData = updatedDays) }
            }
        }
    }

    private fun observeSelectedDateLogs() {
        viewModelScope.launch {
            // React to selectedDate changes and observe that date's food logs
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date ->
                    nutritionRepo.observeFoodLogByDate(date)
                }
                .collect { dayEntries ->
                    val totals = dayEntries.fold(MacroNutrients(0, 0, 0, 0)) { acc, e ->
                        MacroNutrients(
                            calories = acc.calories + e.macros.calories,
                            protein = acc.protein + e.macros.protein,
                            carbs = acc.carbs + e.macros.carbs,
                            fats = acc.fats + e.macros.fats
                        )
                    }
                    _uiState.update { it.copy(dayEntries = dayEntries, dayTotals = totals) }
                }
        }
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun resetAutoFillState() {
        lookupJob?.cancel()
        currentFoodName = ""
        currentServingSize = ""
        _uiState.update { it.copy(autoFillState = AutoFillState()) }
    }

    fun addManualEntry(name: String, serving: String, cal: Int, protein: Int, carbs: Int, fats: Int) {
        viewModelScope.launch {
            val entry = FoodLogEntry(
                id = generateId(),
                date = _uiState.value.selectedDate,
                foodName = name,
                servingSize = serving,
                quantity = 1,
                macros = MacroNutrients(calories = cal, protein = protein, carbs = carbs, fats = fats),
                source = FoodSource.MANUAL,
                createdAt = todayFormatted()
            )
            nutritionRepo.addFoodLogEntry(entry)
            _uiState.update { it.copy(toast = "$name added") }
        }
    }

    fun removeEntry(id: String) {
        viewModelScope.launch {
            nutritionRepo.deleteFoodLogEntry(id)
            _uiState.update { it.copy(toast = "Entry removed") }
        }
    }

    fun onFoodFieldChange(field: String, value: String) {
        when (field) {
            "name" -> currentFoodName = value
            "servingSize" -> currentServingSize = value
        }

        lookupJob?.cancel()
        _uiState.update { it.copy(autoFillState = AutoFillState()) }
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

    // ─── Water Tracking ──────────────────────────────────────────────────────
    private fun observeWaterLogs() {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date ->
                    nutritionRepo.observeWaterLogByDate(date)
                }
                .collect { entries ->
                    val total = entries.sumOf { it.amount }
                    _uiState.update { it.copy(waterEntries = entries, waterTotalMl = total) }
                }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val entry = WaterLogEntry(
                id = generateId(),
                date = _uiState.value.selectedDate,
                amount = amountMl,
                createdAt = todayFormatted()
            )
            nutritionRepo.addWaterLogEntry(entry)
            _uiState.update { it.copy(toast = "Water logged") }
        }
    }

    fun removeWaterEntry(id: String) {
        viewModelScope.launch {
            nutritionRepo.deleteWaterLogEntry(id)
        }
    }

    // ─── Cardio Tracking ─────────────────────────────────────────────────────
    private fun observeCardioLogs() {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date ->
                    nutritionRepo.observeCardioLogByDate(date)
                }
                .collect { entries ->
                    val totalCalories = entries.sumOf { it.estimatedCaloriesBurnt }
                    _uiState.update { it.copy(cardioEntries = entries, cardioCaloriesToday = totalCalories) }
                }
        }
    }

    fun addCardioEntry(type: String, durationMinutes: Int, notes: String = "") {
        viewModelScope.launch {
            val weight = _uiState.value.userWeightKg
            val calories = estimateCardioCalories(type, durationMinutes, weight)
            val entry = CardioLogEntry(
                id = generateId(),
                date = _uiState.value.selectedDate,
                type = type,
                durationMinutes = durationMinutes,
                estimatedCaloriesBurnt = calories,
                notes = notes,
                createdAt = todayFormatted()
            )
            nutritionRepo.addCardioLogEntry(entry)
            _uiState.update { it.copy(toast = "$type logged - $calories cal burnt") }
        }
    }

    fun removeCardioEntry(id: String) {
        viewModelScope.launch {
            nutritionRepo.deleteCardioLogEntry(id)
        }
    }
}
