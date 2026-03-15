package com.fitcore.data.local.dao

import androidx.room.*
import com.fitcore.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for the meals table.
 */
@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE date = :date")
    fun getMealsByDate(date: LocalDate): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query("UPDATE meals SET isEaten = :isEaten WHERE id = :mealId")
    suspend fun updateMealStatus(mealId: Long, isEaten: Boolean)

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Long): MealEntity?

    @Query("UPDATE meals SET foodIds = :foodIds WHERE id = :mealId")
    suspend fun updateMealFoodIds(mealId: Long, foodIds: List<Long>)

    @Query("SELECT SUM(waterLiters) FROM meals WHERE date = :date")
    fun getWaterConsumed(date: LocalDate): Flow<Double?>

    @Query("UPDATE meals SET waterLiters = waterLiters + :amount WHERE id = (SELECT id FROM meals WHERE date = :date LIMIT 1)")
    suspend fun addWater(date: LocalDate, amount: Double)
}
