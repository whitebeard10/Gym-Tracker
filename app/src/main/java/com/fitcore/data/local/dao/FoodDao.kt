package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the master foods table.
 */
@Dao
interface FoodDao {
    @Query("SELECT * FROM foods")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    fun searchFoods(query: String): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodEntity>)

    @Delete
    suspend fun deleteFood(food: FoodEntity)

    @Query("SELECT * FROM foods WHERE id IN (:ids)")
    suspend fun getFoodsByIds(ids: List<Long>): List<FoodEntity>

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getFoodCount(): Int
}
