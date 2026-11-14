package com.nexal.app.ui.scanner

import com.nexal.app.domain.model.MacroNutrients
import com.nexal.app.domain.model.ScannedProduct
import org.junit.Assert.assertEquals
import org.junit.Test

class ScannedProteinRatioTest {
    @Test
    fun usesServerRatioSoSmallServingsDoNotSkewIt() {
        // 33 g serving of a 371 kcal / 13.3 g protein per 100 g product:
        // 122.4 kcal, 4.4 g protein -> rounded locally to 122 kcal, 4 g (3.28).
        val product = ScannedProduct(
            servingSize = "33 g",
            macros = MacroNutrients(calories = 122, protein = 4, carbs = 20, fats = 2),
            proteinCalorieRatio = 3.6,
        )
        assertEquals(3.6, scannedProteinRatio(product), 0.0)
    }

    @Test
    fun fallsBackToMacrosWhenServerRatioMissing() {
        val product = ScannedProduct(
            servingSize = "100 g",
            macros = MacroNutrients(calories = 200, protein = 20, carbs = 10, fats = 5),
        )
        assertEquals(10.0, scannedProteinRatio(product), 0.001)
    }

    @Test
    fun zeroCaloriesGivesZero() {
        assertEquals(0.0, scannedProteinRatio(ScannedProduct()), 0.0)
    }
}
