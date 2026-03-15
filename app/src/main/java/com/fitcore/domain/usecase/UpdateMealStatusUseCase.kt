package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import javax.inject.Inject

/**
 * Use case to toggle the 'eaten' status of a meal.
 */
class UpdateMealStatusUseCase @Inject constructor(
    private val mealRepository: MealRepositoryImpl
) {
    suspend operator fun invoke(mealId: Long, isEaten: Boolean) {
        mealRepository.updateMealStatus(mealId, isEaten)
    }
}
