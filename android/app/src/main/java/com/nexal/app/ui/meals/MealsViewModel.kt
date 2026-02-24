package com.nexal.app.ui.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.data.repository.ProfileRepository
import com.nexal.app.domain.model.*
import com.nexal.app.util.Resource
import com.nexal.app.util.generateId
import com.nexal.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealsUiState(
    val plan: MealPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFood: Pair<String, FoodItem>? = null,
    val substitutions: List<MealSubstitution> = emptyList(),
    val subLoading: Boolean = false,
    val toast: String? = null
)

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val nutritionRepo: NutritionRepository,
    private val profileRepo: ProfileRepository,
    private val aiRepo: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealsUiState())
    val uiState: StateFlow<MealsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            nutritionRepo.observeMealPlans().collect { plans ->
                _uiState.update { it.copy(plan = plans.firstOrNull()) }
            }
        }
    }

    fun generatePlan(allergies: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val profile = profileRepo.getProfile()
            if (profile == null) {
                _uiState.update { it.copy(isLoading = false, error = "Complete onboarding first") }
                return@launch
            }
            when (val result = aiRepo.generateMealPlan(profile, allergies)) {
                is Resource.Success -> {
                    nutritionRepo.saveMealPlan(result.data)
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deletePlan() {
        viewModelScope.launch {
            _uiState.value.plan?.let { nutritionRepo.deleteMealPlan(it.id) }
            _uiState.update { it.copy(toast = "Meal plan deleted") }
        }
    }

    fun addMealToLog(meal: Meal) {
        viewModelScope.launch {
            val entry = FoodLogEntry(
                id = generateId(),
                date = todayFormatted(),
                foodName = meal.name,
                servingSize = "1 serving",
                quantity = 1,
                macros = meal.totalMacros,
                source = FoodSource.MEAL_PLAN,
                createdAt = todayFormatted()
            )
            nutritionRepo.addFoodLogEntry(entry)
            _uiState.update { it.copy(toast = "${meal.name} added to food log") }
        }
    }

    fun selectFoodForSub(mealName: String, food: FoodItem) {
        _uiState.update { it.copy(selectedFood = Pair(mealName, food), substitutions = emptyList()) }
    }

    fun getSubstitutions(reason: String) {
        val selected = _uiState.value.selectedFood ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(subLoading = true) }
            when (val result = aiRepo.getMealSubstitutions(
                mealName = selected.first,
                foodName = selected.second.name,
                reason = reason,
                currentMacros = selected.second.macros
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(subLoading = false, substitutions = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(subLoading = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun replaceFood(sub: MealSubstitution) {
        val plan = _uiState.value.plan ?: return
        val selected = _uiState.value.selectedFood ?: return

        val updatedMeals = plan.meals.map { meal ->
            if (meal.name != selected.first) return@map meal
            val updatedFoods = meal.foods.map { food ->
                if (food.name != selected.second.name) food
                else food.copy(
                    name = sub.name,
                    servingSize = sub.servingSize ?: food.servingSize,
                    macros = sub.macros ?: food.macros
                )
            }
            val totalMacros = updatedFoods.fold(MacroNutrients(0, 0, 0, 0)) { acc, f ->
                MacroNutrients(
                    calories = acc.calories + f.macros.calories,
                    protein = acc.protein + f.macros.protein,
                    carbs = acc.carbs + f.macros.carbs,
                    fats = acc.fats + f.macros.fats
                )
            }
            meal.copy(foods = updatedFoods, totalMacros = totalMacros)
        }

        val dailyTotals = updatedMeals.fold(MacroNutrients(0, 0, 0, 0)) { acc, m ->
            MacroNutrients(
                calories = acc.calories + m.totalMacros.calories,
                protein = acc.protein + m.totalMacros.protein,
                carbs = acc.carbs + m.totalMacros.carbs,
                fats = acc.fats + m.totalMacros.fats
            )
        }

        val updatedPlan = plan.copy(meals = updatedMeals, dailyTotals = dailyTotals)
        viewModelScope.launch {
            nutritionRepo.saveMealPlan(updatedPlan)
            _uiState.update {
                it.copy(
                    selectedFood = null,
                    substitutions = emptyList(),
                    toast = "Replaced ${selected.second.name} with ${sub.name}"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toast = null) }
    }
}
