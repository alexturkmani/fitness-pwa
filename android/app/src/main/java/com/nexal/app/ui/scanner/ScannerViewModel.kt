package com.nexal.app.ui.scanner

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AiRepository
import com.nexal.app.data.repository.NutritionRepository
import com.nexal.app.domain.model.FoodAssessment
import com.nexal.app.domain.model.FoodLogEntry
import com.nexal.app.domain.model.FoodSource
import com.nexal.app.domain.model.ScannedProduct
import com.nexal.app.ui.theme.Cyan500
import com.nexal.app.ui.theme.Emerald500
import com.nexal.app.util.Resource
import com.nexal.app.util.generateId
import com.nexal.app.util.getProteinCalorieRatio
import com.nexal.app.util.getRatioRating
import com.nexal.app.util.todayFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val scanning: Boolean = false,
    val isLoading: Boolean = false,
    val product: ScannedProduct? = null,
    val proteinRatio: Double = 0.0,
    val ratingLabel: String = "",
    val ratingColor: Color = Color.Gray,
    val aiAssessment: FoodAssessment? = null,
    val error: String? = null,
    val toast: String? = null
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val nutritionRepo: NutritionRepository,
    private val aiRepo: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun startScanning() {
        _uiState.update { it.copy(scanning = true, error = null, product = null, aiAssessment = null) }
    }

    fun stopScanning() {
        _uiState.update { it.copy(scanning = false) }
    }

    fun onBarcodeScanned(barcode: String) {
        _uiState.update { it.copy(scanning = false, isLoading = true) }
        viewModelScope.launch {
            when (val result = aiRepo.lookupBarcode(barcode)) {
                is Resource.Success -> {
                    val product = result.data
                    val ratio = getProteinCalorieRatio(product.macros)
                    val rating = getRatioRating(ratio)
                    val ratingColor = when {
                        ratio >= 10 -> Emerald500
                        ratio >= 5 -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            proteinRatio = ratio,
                            ratingLabel = rating.label,
                            ratingColor = ratingColor
                        )
                    }

                    // If low protein ratio, get AI assessment
                    if (ratio < 10) {
                        fetchAiAssessment(product, ratio)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = "Product not found. Try scanning again or enter manually.") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun fetchAiAssessment(product: ScannedProduct, ratio: Double) {
        viewModelScope.launch {
            when (val result = aiRepo.assessFood(product.name, product.macros, ratio)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(aiAssessment = result.data) }
                }
                is Resource.Error -> { /* ignore assessment errors */ }
                is Resource.Loading -> {}
            }
        }
    }

    fun addToLog() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            val entry = FoodLogEntry(
                id = generateId(),
                date = todayFormatted(),
                foodName = product.name,
                servingSize = product.servingSize,
                quantity = 1,
                macros = product.macros,
                source = FoodSource.SCANNER,
                barcode = product.barcode,
                createdAt = todayFormatted()
            )
            nutritionRepo.addFoodLogEntry(entry)
            _uiState.update { it.copy(toast = "${product.name} added to food log") }
        }
    }

    fun resetAndScan() {
        _uiState.update {
            ScannerUiState(scanning = true)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toast = null) }
    }
}
