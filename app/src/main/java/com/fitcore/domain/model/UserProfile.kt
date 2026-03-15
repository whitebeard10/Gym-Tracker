package com.fitcore.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for User Profile and Goal settings.
 */
@Immutable
data class UserProfile(
    val name: String,
    val heightCm: Int,
    val currentWeightKg: Double,
    val goalWeightKg: Double,
    val dailyCalorieTarget: Int,
    val proteinTargetG: Int,
    val carbTargetG: Int,
    val fatTargetG: Int,
    val waterTargetLiters: Double,
    val trainingDaysPerWeek: Int = 6,
    val currentStreak: Int = 0
)
