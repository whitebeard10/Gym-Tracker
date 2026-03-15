package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to retrieve all meals for the current day.
 */
class GetTodayMealsUseCase @Inject constructor(
    private val mealRepository: MealRepositoryImpl
) {
    operator fun invoke(): Flow<List<Meal>> {
        return mealRepository.getMealsByDate(LocalDate.now())
    }
}
