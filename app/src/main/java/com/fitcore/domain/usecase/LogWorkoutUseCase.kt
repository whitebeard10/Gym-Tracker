package com.fitcore.domain.usecase

import com.fitcore.data.repository.WorkoutRepositoryImpl
import com.fitcore.domain.model.Workout
import javax.inject.Inject

/**
 * Use case to save a completed workout session.
 */
class LogWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepositoryImpl
) {
    suspend operator fun invoke(workout: Workout) {
        workoutRepository.saveWorkout(workout)
    }
}
