package com.fitcore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Room entity representing a logged Workout.
 */
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val timestamp: LocalDateTime,
    val durationMinutes: Int,
    // We'll store exercises as a JSON string using TypeConverters for simplicity in this prototype.
    val exercisesJson: String,
    val isCompleted: Boolean = true
)
