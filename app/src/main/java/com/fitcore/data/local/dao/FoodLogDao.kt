package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.FoodLogEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_logs WHERE date = :date")
    fun getFoodLogsByDate(date: LocalDate): Flow<List<FoodLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(entry: FoodLogEntry)

    @Delete
    suspend fun deleteFoodLog(entry: FoodLogEntry)

    @Query("DELETE FROM food_logs")
    suspend fun clearAll()
}
