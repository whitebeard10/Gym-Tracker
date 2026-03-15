package com.fitcore.data.repository

import com.fitcore.data.local.dao.FoodDao
import com.fitcore.data.local.entity.FoodEntity
import com.fitcore.domain.model.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) {
    fun getAllFoods(): Flow<List<Food>> {
        return foodDao.getAllFoods().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchFoods(query: String): Flow<List<Food>> {
        return foodDao.searchFoods(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun logCustomFood(food: Food) {
        foodDao.insertFood(
            FoodEntity(
                name = food.name,
                calories = food.calories,
                protein = food.protein,
                carbs = food.carbs,
                fats = food.fats,
                servingSize = food.servingSize,
                isCustom = true
            )
        )
    }
}
