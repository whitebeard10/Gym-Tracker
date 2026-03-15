package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for the weight_entries table.
 */
@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 30")
    fun getRecentWeightEntries(): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntry)

    @Query("SELECT weightKg FROM weight_entries WHERE date = :date")
    suspend fun getWeightOnDate(date: LocalDate): Double?
}
