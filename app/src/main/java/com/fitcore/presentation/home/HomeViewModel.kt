package com.fitcore.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.repository.UserPreferencesRepository
import com.fitcore.data.repository.WorkoutRepositoryImpl
import com.fitcore.domain.model.MacroSummary
import com.fitcore.domain.model.Meal
import com.fitcore.domain.model.UserProfile
import com.fitcore.domain.usecase.AddWaterUseCase
import com.fitcore.domain.usecase.GetMacroSummaryUseCase
import com.fitcore.domain.usecase.GetTodayMealsUseCase
import com.fitcore.domain.usecase.UpdateMealStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Sealed class for Home UI State.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val userName: String,
        val greeting: String,
        val macroSummary: MacroSummary,
        val todayMeals: List<Meal>,
        val streak: Int,
        val nextMeal: Meal?,
        val todayWorkoutName: String
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel for the Home Screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMacroSummaryUseCase: GetMacroSummaryUseCase,
    private val getTodayMealsUseCase: GetTodayMealsUseCase,
    private val addWaterUseCase: AddWaterUseCase,
    private val updateMealStatusUseCase: UpdateMealStatusUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val workoutRepository: WorkoutRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            userPreferencesRepository.userProfileFlow.flatMapLatest { profile ->
                combine(
                    getMacroSummaryUseCase(profile),
                    getTodayMealsUseCase(),
                    workoutRepository.getTodayWorkouts()
                ) { summary, meals, todayWorkouts ->
                    val now = LocalTime.now()
                    val nextMeal = meals.filter { !it.isEaten && it.scheduledTime.isAfter(now) }
                        .minByOrNull { it.scheduledTime }
                        ?: meals.firstOrNull { !it.isEaten }

                    val dayOfWeek = LocalDate.now().dayOfWeek.value
                    val defaultWorkoutName = when (dayOfWeek) {
                        1 -> "Push Day (Chest & Triceps)"
                        2 -> "Pull Day (Back & Biceps)"
                        3 -> "Legs Day (Quads & Calves)"
                        4 -> "Upper Body Power"
                        5 -> "Lower Body Power"
                        6 -> "Full Body Strength"
                        else -> "Active Recovery / Yoga"
                    }

                    val workoutName = todayWorkouts.lastOrNull()?.name ?: defaultWorkoutName

                    HomeUiState.Success(
                        userName = profile.name,
                        greeting = getGreeting(),
                        macroSummary = summary,
                        todayMeals = meals,
                        streak = profile.currentStreak,
                        nextMeal = nextMeal,
                        todayWorkoutName = workoutName
                    )
                }
            }.catch { e ->
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun addWater(amount: Double) {
        viewModelScope.launch {
            addWaterUseCase(amount)
            userPreferencesRepository.updateStreak()
        }
    }

    fun toggleMealStatus(mealId: String, isEaten: Boolean) {
        viewModelScope.launch {
            updateMealStatusUseCase(mealId.toLong(), isEaten)
            if (isEaten) {
                userPreferencesRepository.updateStreak()
            }
        }
    }

    private fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
