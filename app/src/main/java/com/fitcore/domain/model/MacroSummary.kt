package com.fitcore.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for daily macro and calorie summary.
 */
@Immutable
data class MacroSummary(
    val consumedCalories: Double,
    val targetCalories: Double,
    val burnedCalories: Double = 0.0,
    val consumedProtein: Double,
    val targetProtein: Double,
    val consumedCarbs: Double,
    val targetCarbs: Double,
    val consumedFats: Double,
    val targetFats: Double,
    val waterDrankLiters: Double,
    val targetWaterLiters: Double
) {
    // Net calories = Consumed - Burned
    val netCalories: Double get() = (consumedCalories - burnedCalories).coerceAtLeast(0.0)
    val remainingCalories: Double get() = (targetCalories - netCalories).coerceAtLeast(0.0)
    val calorieProgress: Float get() = (netCalories / targetCalories).toFloat().coerceIn(0f, 1f)
}
