package com.fitcore.domain.usecase

import com.fitcore.data.repository.FoodRepositoryImpl
import com.fitcore.domain.model.Food
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to provide meal swap suggestions.
 * Suggests foods with similar calories (±50) to maintain progress.
 */
class GetMealSuggestionsUseCase @Inject constructor(
    private val foodRepository: FoodRepositoryImpl
) {
    suspend operator fun invoke(targetCalories: Double): List<Food> {
        val allFoods = foodRepository.getAllFoods().first()
        return allFoods.filter { 
            it.calories in (targetCalories - 50)..(targetCalories + 50) 
        }.take(3)
    }
}
