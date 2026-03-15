package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.data.repository.WorkoutRepositoryImpl
import com.fitcore.domain.model.Meal
import com.fitcore.domain.model.UserProfile
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetMacroSummaryUseCaseTest {

    private lateinit var mealRepository: MealRepositoryImpl
    private lateinit var workoutRepository: WorkoutRepositoryImpl
    private lateinit var getMacroSummaryUseCase: GetMacroSummaryUseCase

    @Before
    fun setUp() {
        mealRepository = mockk()
        workoutRepository = mockk()
        getMacroSummaryUseCase = GetMacroSummaryUseCase(mealRepository, workoutRepository)
    }

    @Test
    fun `invoke returns correct summary`() = runBlocking {
        val userProfile = UserProfile(
            name = "Abish",
            heightCm = 160,
            currentWeightKg = 59.0,
            goalWeightKg = 56.0,
            dailyCalorieTarget = 1750,
            proteinTargetG = 130,
            carbTargetG = 170,
            fatTargetG = 45,
            waterTargetLiters = 3.5
        )

        val meal = Meal(
            id = "1",
            name = "Breakfast",
            totalCalories = 500.0,
            totalProtein = 30.0,
            totalCarbs = 50.0,
            totalFats = 15.0,
            isEaten = true,
            scheduledTime = LocalTime.NOON,
            foods = emptyList()
        )

        every { mealRepository.getMealsByDate(any()) } returns flowOf(listOf(meal))
        every { mealRepository.getWaterConsumed(any()) } returns flowOf(1.0)
        every { workoutRepository.getTodayWorkouts() } returns flowOf(emptyList())

        val summary = getMacroSummaryUseCase(userProfile).first()

        assertEquals(500.0, summary.consumedCalories, 0.1)
        assertEquals(1750.0, summary.targetCalories, 0.1)
        assertEquals(30.0, summary.consumedProtein, 0.1)
        assertEquals(1.0, summary.waterDrankLiters, 0.1)
    }
}
