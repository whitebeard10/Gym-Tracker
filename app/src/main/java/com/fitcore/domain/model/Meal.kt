package com.fitcore.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalTime

/**
 * Domain model representing a Meal slot in the daily plan.
 */
@Immutable
data class Meal(
    val id: String,
    val name: String,
    val scheduledTime: LocalTime,
    val foods: List<Food>,
    val isEaten: Boolean = false,
    val description: String = ""
) {
    val totalCalories: Double get() = foods.sumOf { it.calories }
    val totalProtein: Double get() = foods.sumOf { it.protein }
    val totalCarbs: Double get() = foods.sumOf { it.carbs }
    val totalFats: Double get() = foods.sumOf { it.fats }
}
