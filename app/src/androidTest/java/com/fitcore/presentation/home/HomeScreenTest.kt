package com.fitcore.presentation.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fitcore.domain.model.MacroSummary
import com.fitcore.presentation.theme.FitCoreTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysGreetingAndUser() {
        // Given
        val state = HomeUiState.Success(
            userName = "Aarav",
            greeting = "Good morning",
            macroSummary = MacroSummary(
                consumedCalories = 500.0,
                targetCalories = 1750.0,
                consumedProtein = 40.0,
                targetProtein = 130.0,
                consumedCarbs = 60.0,
                targetCarbs = 170.0,
                consumedFats = 15.0,
                targetFats = 45.0,
                waterDrankLiters = 1.0,
                targetWaterLiters = 3.5
            ),
            todayMeals = emptyList(),
            streak = 12,
            nextMeal = null
        )

        // When
        composeTestRule.setContent {
            FitCoreTheme {
                HomeScreenContent(
                    state = state,
                    onNavigateToLog = {},
                    onNavigateToMealPlan = {},
                    onNavigateToWorkout = {},
                    onAddWater = {}
                )
            }
        }
