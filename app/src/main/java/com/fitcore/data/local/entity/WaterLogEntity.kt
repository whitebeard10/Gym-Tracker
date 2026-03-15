package com.fitcore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a water intake log for a specific date.
 */
@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey val date: LocalDate,
    val amountLiters: Double
)
