package com.fitcore.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.local.entity.WeightEntry
import com.fitcore.data.repository.ProgressRepositoryImpl
import com.fitcore.data.repository.UserPreferencesRepository
import com.fitcore.data.repository.WorkoutRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProgressUiState(
    val weightHistory: List<WeightEntry> = emptyList(),
    val totalWorkouts: Int = 0,
    val avgDailyProtein: Int = 0,
    val longestStreak: Int = 12,
    val targetWeight: Double = 56.0,
    val currentWeight: Double = 59.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepositoryImpl,
    private val workoutRepository: WorkoutRepositoryImpl,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgressData()
    }

    private fun loadProgressData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                progressRepository.getRecentWeightEntries(),
                workoutRepository.getWorkoutCount(),
                userPreferencesRepository.userProfileFlow
            ) { weights, workoutCount, profile ->
                ProgressUiState(
                    weightHistory = weights,
                    totalWorkouts = workoutCount,
                    avgDailyProtein = 125, // Mocked for now
                    longestStreak = profile.currentStreak,
                    targetWeight = profile.goalWeightKg,
                    currentWeight = profile.currentWeightKg,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun logNewWeight(weight: Double) {
        viewModelScope.launch {
            progressRepository.logWeight(weight, LocalDate.now())
            userPreferencesRepository.updateWeight(weight)
        }
    }
}
