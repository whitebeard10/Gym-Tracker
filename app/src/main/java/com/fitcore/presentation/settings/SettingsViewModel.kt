package com.fitcore.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.data.repository.UserPreferencesRepository
import com.fitcore.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Settings Screen.
 */
data class SettingsUiState(
    val userProfile: UserProfile = UserProfile(
        name = "Abish",
        heightCm = 160,
        currentWeightKg = 59.0,
        goalWeightKg = 56.0,
        dailyCalorieTarget = 1750,
        proteinTargetG = 130,
        carbTargetG = 170,
        fatTargetG = 45,
        waterTargetLiters = 3.5
    ),
    val mealRemindersEnabled: Boolean = true,
    val waterRemindersEnabled: Boolean = true,
    val workoutRemindersEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val mealRepository: MealRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.darkThemeFlow.collect { isDark ->
                _uiState.update { it.copy(darkThemeEnabled = isDark) }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.userProfileFlow.collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
    }

    fun updateName(newName: String) = viewModelScope.launch {
        userPreferencesRepository.updateUserName(newName)
    }

    fun updateWeight(newWeight: Double) = viewModelScope.launch {
        userPreferencesRepository.updateWeight(newWeight)
    }

    fun updateGoalWeight(newGoal: Double) = viewModelScope.launch {
        userPreferencesRepository.updateGoalWeight(newGoal)
    }

    fun updateCalorieTarget(newTarget: Int) = viewModelScope.launch {
        userPreferencesRepository.updateCalorieTarget(newTarget)
    }

    fun updateProteinTarget(newTarget: Int) = viewModelScope.launch {
        userPreferencesRepository.updateProteinTarget(newTarget)
    }

    fun updateCarbTarget(newTarget: Int) = viewModelScope.launch {
        userPreferencesRepository.updateCarbTarget(newTarget)
    }

    fun updateFatTarget(newTarget: Int) = viewModelScope.launch {
        userPreferencesRepository.updateFatTarget(newTarget)
    }

    fun toggleMealReminders(enabled: Boolean) {
        _uiState.update { it.copy(mealRemindersEnabled = enabled) }
    }

    fun toggleWaterReminders(enabled: Boolean) {
        _uiState.update { it.copy(waterRemindersEnabled = enabled) }
    }

    fun toggleWorkoutReminders(enabled: Boolean) {
        _uiState.update { it.copy(workoutRemindersEnabled = enabled) }
    }

    fun toggleTheme(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateDarkTheme(enabled)
    }

    fun resetProgress() = viewModelScope.launch {
        userPreferencesRepository.clearAll()
        mealRepository.clearAll()
    }

    fun calculateTDEE(): Double {
        val profile = _uiState.value.userProfile
        val age = 25 
        val bmr = (10 * profile.currentWeightKg) + (6.25 * profile.heightCm) - (5 * age) + 5
        return bmr * 1.55 
    }
}
