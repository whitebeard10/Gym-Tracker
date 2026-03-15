package com.fitcore.presentation.meals

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fitcore.domain.model.Meal
import com.fitcore.presentation.theme.FitCoreTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class MealPlanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mealPlanScreen_displaysMeals() {
        // Given
        val meals = listOf(
            Meal(id = "1", name = "Breakfast", scheduledTime = LocalTime.of(8, 0), foods = emptyList(), isEaten = true),
            Meal(id = "2", name = "Lunch", scheduledTime = LocalTime.of(13, 0), foods = emptyList(), isEaten = false)
        )

        // When
        composeTestRule.setContent {
            FitCoreTheme {
                // Assuming a stateless variant for testing
                MealPlanScreenContent(
                    meals = meals,
                    totalCalories = 0.0,
                    targetCalories = 1750.0,
                    onBackClick = {},
                    onToggleEaten = { _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Breakfast").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunch").assertIsDisplayed()
        composeTestRule.onNodeWithText("DONE").assertIsDisplayed() // Badge for Breakfast
    }
}
