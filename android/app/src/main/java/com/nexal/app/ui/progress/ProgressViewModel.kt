package com.nexal.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.data.repository.WorkoutRepository
import com.nexal.app.domain.model.WeightEntry
import com.nexal.app.util.generateId
import com.nexal.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ProgressUiState(
    val weeklyWorkouts: Int = 0,
    val avgDailyCalories: Int = 0,
    val currentWeight: Double = 0.0,
    val targetWeight: Double = 0.0,
    val weightChange: Double = 0.0,
    val weightData: List<Pair<String, Double>> = emptyList(), // date label, weight
    val volumeData: List<Triple<String, Int, Int>> = emptyList(), // week, volume, workouts
    val calorieData: List<Triple<String, Int, Int>> = emptyList(), // day, calories, protein
    val avgProtein: Int = 0,
    val avgCarbs: Int = 0,
    val avgFats: Int = 0,
    // Water
    val waterData: List<Pair<String, Int>> = emptyList(), // day, totalMl
    val avgDailyWater: Int = 0,
    // Cardio
    val cardioData: List<Triple<String, Int, Int>> = emptyList(), // day, calories, duration
    val avgCardioCalories: Int = 0
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val workoutRepo: WorkoutRepository,
    private val nutritionRepo: NutritionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profile = profileRepo.getProfile()
            val targetWeight = profile?.targetWeight ?: 0.0

            val sdfRange = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val endDate = sdfRange.format(Date())
            val calRange = Calendar.getInstance()
            calRange.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = sdfRange.format(calRange.time)

            // Combine all data flows
            combine(
                workoutRepo.observeLogs(),
                nutritionRepo.observeWeightEntries(),
                nutritionRepo.observeFoodLogByDateRange(startDate, endDate),
                nutritionRepo.observeWaterLogByDateRange(startDate, endDate),
                nutritionRepo.observeCardioLogByDateRange(startDate, endDate)
            ) { workoutLogs, weightEntries, foodLogs, waterLogs, cardioLogs ->
                data class CombinedData(
                    val workoutLogs: List<com.nexal.app.domain.model.WorkoutLog>,
                    val weightEntries: List<com.nexal.app.domain.model.WeightEntry>,
                    val foodLogs: List<com.nexal.app.domain.model.FoodLogEntry>,
                    val waterLogs: List<com.nexal.app.domain.model.WaterLogEntry>,
                    val cardioLogs: List<com.nexal.app.domain.model.CardioLogEntry>
                )
                CombinedData(workoutLogs, weightEntries, foodLogs, waterLogs, cardioLogs)
            }.collect { combined ->

                val workoutLogs = combined.workoutLogs
                val weightEntries = combined.weightEntries
                val foodLogs = combined.foodLogs
                val waterLogs = combined.waterLogs
                val cardioLogs = combined.cardioLogs

                // Weekly workouts
                val cal = Calendar.getInstance()
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
                cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
                val weekStart = cal.time
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                val thisWeekLogs = workoutLogs.filter {
                    try { sdf.parse(it.date)?.after(weekStart) == true } catch (_: Exception) { false }
                }

                // Weight data
                val weightData = weightEntries.takeLast(30).map { entry ->
                    val displayDate = try {
                        val d = sdf.parse(entry.date)
                        SimpleDateFormat("MMM d", Locale.US).format(d!!)
                    } catch (_: Exception) { entry.date }
                    Pair(displayDate, entry.weight)
                }

                val latestWeight = weightEntries.lastOrNull()?.weight ?: profile?.weight ?: 0.0
                val weightChange = if (weightEntries.size >= 2) {
                    weightEntries.last().weight - weightEntries.first().weight
                } else 0.0

                // Volume data (last 8 weeks)
                val volumeData = (7 downTo 0).map { i ->
                    val wCal = Calendar.getInstance()
                    wCal.add(Calendar.WEEK_OF_YEAR, -i)
                    wCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    wCal.set(Calendar.HOUR_OF_DAY, 0); wCal.set(Calendar.MINUTE, 0); wCal.set(Calendar.SECOND, 0)
                    val wStart = wCal.time
                    val wEnd = Calendar.getInstance().apply { time = wStart; add(Calendar.DAY_OF_YEAR, 7) }.time

                    val weekLogs = workoutLogs.filter {
                        try {
                            val d = sdf.parse(it.date)
                            d != null && d >= wStart && d < wEnd
                        } catch (_: Exception) { false }
                    }

                    val volume = weekLogs.sumOf { log ->
                        log.exercises.sumOf { ex ->
                            ex.sets.filter { it.completed }.sumOf { s -> (s.weight * s.reps).toInt() }
                        }
                    }

                    Triple("W${8 - i}", volume, weekLogs.size)
                }

                // Calorie data (last 7 days)
                val dayNameFmt = SimpleDateFormat("EEE", Locale.US)
                val calorieData = (6 downTo 0).map { i ->
                    val dCal = Calendar.getInstance()
                    dCal.add(Calendar.DAY_OF_YEAR, -i)
                    val date = sdf.format(dCal.time)
                    val dayName = dayNameFmt.format(dCal.time)
                    val dayLogs = foodLogs.filter { it.date == date }
                    val totalCals = dayLogs.sumOf { it.macros.calories }
                    val totalProtein = dayLogs.sumOf { it.macros.protein }
                    Triple(dayName, totalCals, totalProtein)
                }

                val avgCalories = if (calorieData.isNotEmpty()) calorieData.sumOf { it.second } / 7 else 0

                // Average macros
                val last7DaysLogs = (6 downTo 0).flatMap { i ->
                    val dCal = Calendar.getInstance()
                    dCal.add(Calendar.DAY_OF_YEAR, -i)
                    val date = sdf.format(dCal.time)
                    foodLogs.filter { it.date == date }
                }
                val totalProtein = last7DaysLogs.sumOf { it.macros.protein }
                val totalCarbs = last7DaysLogs.sumOf { it.macros.carbs }
                val totalFats = last7DaysLogs.sumOf { it.macros.fats }

                // Water data (last 7 days)
                val waterData = (6 downTo 0).map { i ->
                    val dCal = Calendar.getInstance()
                    dCal.add(Calendar.DAY_OF_YEAR, -i)
                    val date = sdf.format(dCal.time)
                    val dayName = dayNameFmt.format(dCal.time)
                    val dayTotal = waterLogs.filter { it.date == date }.sumOf { it.amount }
                    Pair(dayName, dayTotal)
                }
                val avgWater = if (waterData.isNotEmpty()) waterData.sumOf { it.second } / 7 else 0

                // Cardio data (last 7 days)
                val cardioData = (6 downTo 0).map { i ->
                    val dCal = Calendar.getInstance()
                    dCal.add(Calendar.DAY_OF_YEAR, -i)
                    val date = sdf.format(dCal.time)
                    val dayName = dayNameFmt.format(dCal.time)
                    val dayCals = cardioLogs.filter { it.date == date }.sumOf { it.estimatedCaloriesBurnt }
                    val dayDuration = cardioLogs.filter { it.date == date }.sumOf { it.durationMinutes }
                    Triple(dayName, dayCals, dayDuration)
                }
                val avgCardioCal = if (cardioData.isNotEmpty()) cardioData.sumOf { it.second } / 7 else 0

                _uiState.update {
                    it.copy(
                        weeklyWorkouts = thisWeekLogs.size,
                        avgDailyCalories = avgCalories,
                        currentWeight = latestWeight,
                        targetWeight = targetWeight,
                        weightChange = weightChange,
                        weightData = weightData,
                        volumeData = volumeData,
                        calorieData = calorieData,
                        avgProtein = totalProtein / 7,
                        avgCarbs = totalCarbs / 7,
                        avgFats = totalFats / 7,
                        waterData = waterData,
                        avgDailyWater = avgWater,
                        cardioData = cardioData,
                        avgCardioCalories = avgCardioCal
                    )
                }
            }
        }
    }

    fun addWeight(weight: Double) {
        viewModelScope.launch {
            val entry = WeightEntry(
                date = todayFormatted(),
                weight = weight
            )
            nutritionRepo.logWeight(entry)
        }
    }
}
