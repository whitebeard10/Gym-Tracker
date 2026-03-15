package com.fitcore.data.repository

import com.fitcore.data.local.dao.WeightDao
import com.fitcore.data.local.entity.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao
) {
    fun getWeightHistory(): Flow<List<WeightEntry>> = weightDao.getAllWeightEntries()

    fun getRecentWeightEntries(): Flow<List<WeightEntry>> = weightDao.getRecentWeightEntries()

    suspend fun logWeight(weightKg: Double, date: LocalDate = LocalDate.now()) {
        weightDao.insertWeight(WeightEntry(date = date, weightKg = weightKg))
    }

    suspend fun getWeightOnDate(date: LocalDate): Double? = weightDao.getWeightOnDate(date)
}
