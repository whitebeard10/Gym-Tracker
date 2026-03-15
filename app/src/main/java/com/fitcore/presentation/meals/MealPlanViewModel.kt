package com.fitcore.presentation.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.domain.model.Meal
import com.fitcore.domain.usecase.GetTodayMealsUseCase
import com.fitcore.domain.usecase.UpdateMealStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * Sealed class for Meal Plan UI State.
 */
sealed class MealPlanUiState {
    object Loading : MealPlanUiState()
    data class Success(
        val meals: List<Meal>,
        val totalCalories: Double,
        val targetCalories: Double = 1750.0
    ) : MealPlanUiState()
    data class Error(val message: String) : MealPlanUiState()
}

// Actually let's redefine the state to be clean
data class MealPlanState(
    val meals: List<Meal> = emptyList(),
    val totalCalories: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Meal Plan Screen.
 */
@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val getTodayMealsUseCase: GetTodayMealsUseCase,
    private val updateMealStatusUseCase: UpdateMealStatusUseCase,
    private val mealRepository: MealRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanState(isLoading = true))
    val uiState: StateFlow<MealPlanState> = _uiState.asStateFlow()

    init {
        loadMealPlan()
    }

    private fun loadMealPlan() {
        viewModelScope.launch {
            getTodayMealsUseCase()
                .onEach { meals ->
                    val eatenCalories = meals.filter { it.isEaten }.sumOf { it.totalCalories }
                    _uiState.update { it.copy(
                        meals = meals,
                        totalCalories = eatenCalories,
                        isLoading = false
                    )}
                }
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect()
        }
    }

    fun toggleMealEaten(mealId: String, isEaten: Boolean) {
        viewModelScope.launch {
            updateMealStatusUseCase(mealId.toLong(), isEaten)
        }
    }

    fun addMeal(name: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            mealRepository.addCustomMeal(name, LocalTime.of(hour, minute))
        }
    }
}
