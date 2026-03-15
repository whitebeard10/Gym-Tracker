package com.fitcore.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model representing a Food item.
 */
@Immutable
data class Food(
    val id: String,
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    val servingSize: String,
    val isCustom: Boolean = false
)
