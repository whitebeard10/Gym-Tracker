package com.fitcore.data.repository

import com.fitcore.data.local.dao.MealDao
import com.fitcore.data.local.dao.FoodDao
import com.fitcore.data.local.dao.FoodLogDao
import com.fitcore.data.local.dao.WaterLogDao
import com.fitcore.data.local.entity.MealEntity
import com.fitcore.data.local.entity.FoodLogEntry
import com.fitcore.data.local.entity.WaterLogEntity
import com.fitcore.domain.model.Food
import com.fitcore.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val foodDao: FoodDao,
    private val foodLogDao: FoodLogDao,
    private val waterLogDao: WaterLogDao
) {
    fun getMealsByDate(date: LocalDate): Flow<List<Meal>> {
        return mealDao.getMealsByDate(date).map { entities ->
            entities.map { entity ->
                val foods = foodDao.getFoodsByIds(entity.foodIds).map { it.toDomain() }
                entity.toDomain(foods)
            }
        }
    }

    fun getExtraFoodLogs(date: LocalDate): Flow<List<FoodLogEntry>> {
        return foodLogDao.getFoodLogsByDate(date)
    }

    suspend fun logExtraFood(entry: FoodLogEntry) {
        foodLogDao.insertFoodLog(entry)
    }

    suspend fun updateMealStatus(mealId: Long, isEaten: Boolean) {
        mealDao.updateMealStatus(mealId, isEaten)
    }

    fun getWaterConsumed(date: LocalDate): Flow<Double> {
        return waterLogDao.getWaterConsumed(date).map { it ?: 0.0 }
    }

    suspend fun addWater(date: LocalDate, amount: Double) {
        val currentLog = waterLogDao.getWaterLogByDate(date)
        val newAmount = (currentLog?.amountLiters ?: 0.0) + amount
        waterLogDao.insertOrUpdateWater(WaterLogEntity(date, newAmount))
    }

    suspend fun addCustomMeal(name: String, time: java.time.LocalTime) {
        mealDao.insertMeal(
            MealEntity(
                date = LocalDate.now(),
                name = name,
                scheduledTime = time,
                isEaten = false,
                foodIds = emptyList()
            )
        )
    }

    suspend fun addFoodToMeal(mealId: Long, foodId: Long) {
        val meal = mealDao.getMealById(mealId) ?: return
        val updatedFoodIds = meal.foodIds.toMutableList().apply { add(foodId) }
        mealDao.updateMealFoodIds(mealId, updatedFoodIds)
    }

    suspend fun clearAll() {
        foodLogDao.clearAll()
        // We could also reset all meal plan statuses here
    }
}

// Extension mappers
fun com.fitcore.data.local.entity.FoodEntity.toDomain() = Food(
    id = id.toString(),
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fats = fats,
    servingSize = servingSize,
    isCustom = isCustom
)

fun MealEntity.toDomain(foods: List<Food>) = Meal(
    id = id.toString(),
    name = name,
    scheduledTime = scheduledTime,
    foods = foods,
    isEaten = isEaten
)
