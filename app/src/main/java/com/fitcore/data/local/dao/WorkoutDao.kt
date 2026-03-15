package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for the workouts table.
 */
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT COUNT(*) FROM workouts")
    fun getWorkoutCount(): Flow<Int>

    @Query("SELECT * FROM workouts WHERE timestamp >= :start AND timestamp <= :end")
    fun getWorkoutsInDateRange(start: LocalDateTime, end: LocalDateTime): Flow<List<WorkoutEntity>>
}
