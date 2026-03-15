package com.fitcore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * Room entity representing a planned or eaten meal for a specific date.
 */
@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val name: String,
    val scheduledTime: LocalTime,
    val isEaten: Boolean = false,
    // Store food items as a list of FoodEntity IDs or a JSON blob.
    // We'll use a TypeConverter for List<Long> or similar in the DB setup.
    val foodIds: List<Long> = emptyList(),
    val waterLiters: Double = 0.0 // Optional: can track water per day here or in a separate entity
)
