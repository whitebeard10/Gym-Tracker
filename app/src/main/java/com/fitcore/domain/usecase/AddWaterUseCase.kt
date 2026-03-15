package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to log water consumption.
 */
class AddWaterUseCase @Inject constructor(
    private val mealRepository: MealRepositoryImpl
) {
    suspend operator fun invoke(amountLiters: Double) {
        mealRepository.addWater(LocalDate.now(), amountLiters)
    }
}
