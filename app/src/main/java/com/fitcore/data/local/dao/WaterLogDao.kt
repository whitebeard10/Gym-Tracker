package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.WaterLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for the water_logs table.
 */
@Dao
interface WaterLogDao {
    @Query("SELECT amountLiters FROM water_logs WHERE date = :date")
    fun getWaterConsumed(date: LocalDate): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWater(waterLog: WaterLogEntity)

    @Query("SELECT * FROM water_logs WHERE date = :date")
    suspend fun getWaterLogByDate(date: LocalDate): WaterLogEntity?
}
