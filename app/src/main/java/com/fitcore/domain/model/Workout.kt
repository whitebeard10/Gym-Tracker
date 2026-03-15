package com.fitcore.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

/**
 * Domain model representing a Workout session.
 */
@Immutable
data class Workout(
    val id: String,
    val name: String, // e.g., "Push Day", "Pull Day"
    val exercises: List<Exercise>,
    val timestamp: LocalDateTime,
    val durationMinutes: Int,
    val isCompleted: Boolean = false
)

@Immutable
data class Exercise(
    val name: String,
    val sets: List<ExerciseSet>,
    val targetReps: Int,
    val targetSets: Int,
    val weightKg: Double,
    val isPersonalBest: Boolean = false
)

@Immutable
data class ExerciseSet(
    val reps: Int,
    val weightKg: Double,
    val isCompleted: Boolean = false
)
