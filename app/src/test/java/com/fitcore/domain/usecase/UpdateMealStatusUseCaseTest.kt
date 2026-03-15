package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.domain.model.Meal
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateMealStatusUseCaseTest {

    private lateinit var mealRepository: MealRepositoryImpl
    private lateinit var updateMealStatusUseCase: UpdateMealStatusUseCase

    @BeforeEach
    fun setUp() {
        mealRepository = mockk(relaxed = true)
        updateMealStatusUseCase = UpdateMealStatusUseCase(mealRepository)
    }

    @Test
    fun `invoke should call updateMealStatus on repository`() = runBlocking {
        // Given
        val mealId = 123L
        val isEaten = true

        // When
        updateMealStatusUseCase(mealId, isEaten)

        // Then
        coVerify { mealRepository.updateMealStatus(mealId, isEaten) }
    }
}
