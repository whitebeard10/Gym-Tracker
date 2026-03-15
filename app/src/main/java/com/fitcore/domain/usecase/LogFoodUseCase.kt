package com.fitcore.domain.usecase

import com.fitcore.data.repository.FoodRepositoryImpl
import com.fitcore.domain.model.Food
import javax.inject.Inject

/**
 * Use case to log a food item.
 */
class LogFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepositoryImpl
) {
    suspend operator fun invoke(food: Food) {
        foodRepository.logCustomFood(food)
    }
}
