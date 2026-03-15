package com.fitcore.domain.usecase

import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.data.repository.WorkoutRepositoryImpl
import com.fitcore.domain.model.MacroSummary
import com.fitcore.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to calculate the daily macro and calorie summary.
 * Integrates consumed data (plan + extra), user targets, and exercise expenditure.
 */
class GetMacroSummaryUseCase @Inject constructor(
    private val mealRepository: MealRepositoryImpl,
    private val workoutRepository: WorkoutRepositoryImpl
) {
    operator fun invoke(userProfile: UserProfile): Flow<MacroSummary> {
        val today = LocalDate.now()
        val isSunday = today.dayOfWeek == java.time.DayOfWeek.SUNDAY
        
        return combine(
            mealRepository.getMealsByDate(today),
            mealRepository.getExtraFoodLogs(today),
            mealRepository.getWaterConsumed(today),
            workoutRepository.getTodayWorkouts()
        ) { meals, extras, water, workouts ->
            // 1. Calories from planned meals marked as eaten
            val eatenMeals = meals.filter { it.isEaten }
            val plannedCalories = eatenMeals.sumOf { it.totalCalories }
            val plannedProtein = eatenMeals.sumOf { it.totalProtein }
            val plannedCarbs = eatenMeals.sumOf { it.totalCarbs }
            val plannedFats = eatenMeals.sumOf { it.totalFats }

            // 2. Calories from extra logged food
            val extraCalories = extras.sumOf { it.calories * it.quantity }
            val extraProtein = extras.sumOf { it.protein * it.quantity }
            val extraCarbs = extras.sumOf { it.carbs * it.quantity }
            val extraFats = extras.sumOf { it.fats * it.quantity }

            val totalConsumedCals = plannedCalories + extraCalories
            val totalConsumedProtein = plannedProtein + extraProtein
            val totalConsumedCarbs = plannedCarbs + extraCarbs
            val totalConsumedFats = plannedFats + extraFats
            
            // Sunday logic: reduce carbs by 30g and total calories by ~120
            val carbTarget = if (isSunday) userProfile.carbTargetG - 30 else userProfile.carbTargetG
            val calorieTarget = if (isSunday) userProfile.dailyCalorieTarget - 120 else userProfile.dailyCalorieTarget

            // Calculate calories burned from workouts
            val totalBurned = workouts.sumOf { workout ->
                workout.exercises.sumOf { ex -> ex.sets.count { it.isCompleted } * 8 }
            }.toDouble()

            MacroSummary(
                consumedCalories = totalConsumedCals,
                targetCalories = calorieTarget.toDouble(),
                burnedCalories = totalBurned,
                consumedProtein = totalConsumedProtein,
                targetProtein = userProfile.proteinTargetG.toDouble(),
                consumedCarbs = totalConsumedCarbs,
                targetCarbs = carbTarget.toDouble(),
                consumedFats = totalConsumedFats,
                targetFats = userProfile.fatTargetG.toDouble(),
                waterDrankLiters = water,
                targetWaterLiters = userProfile.waterTargetLiters
            )
        }
    }
}
