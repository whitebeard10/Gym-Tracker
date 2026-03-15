package com.fitcore.data.repository

import com.fitcore.data.local.dao.WorkoutDao
import com.fitcore.data.local.entity.WorkoutEntity
import com.fitcore.domain.model.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalDate

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val gson: Gson
) {
    fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { it.toDomain(gson) }
        }
    }

    fun getTodayWorkouts(): Flow<List<Workout>> {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        return workoutDao.getWorkoutsInDateRange(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain(gson) }
        }
    }

    suspend fun saveWorkout(workout: Workout) {
        workoutDao.insertWorkout(
            WorkoutEntity(
                name = workout.name,
                timestamp = workout.timestamp,
                durationMinutes = workout.durationMinutes,
                exercisesJson = gson.toJson(workout.exercises),
                isCompleted = workout.isCompleted
            )
        )
    }

    fun getWorkoutCount(): Flow<Int> = workoutDao.getWorkoutCount()
}

fun WorkoutEntity.toDomain(gson: Gson): Workout {
    val type = object : TypeToken<List<com.fitcore.domain.model.Exercise>>() {}.type
    return Workout(
        id = id.toString(),
        name = name,
        exercises = gson.fromJson(exercisesJson, type),
        timestamp = timestamp,
        durationMinutes = durationMinutes,
        isCompleted = isCompleted
    )
}
