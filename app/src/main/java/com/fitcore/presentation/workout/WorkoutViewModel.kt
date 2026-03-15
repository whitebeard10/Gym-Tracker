package com.fitcore.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.repository.UserPreferencesRepository
import com.fitcore.domain.model.Exercise
import com.fitcore.domain.model.ExerciseSet
import com.fitcore.domain.model.Workout
import com.fitcore.domain.usecase.LogWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class WorkoutUiState(
    val currentWorkout: Workout? = null,
    val restTimerSeconds: Int = 0,
    val isRestTimerActive: Boolean = false,
    val cardioTimerSeconds: Int = 0,
    val isCardioTimerActive: Boolean = false,
    val isCardioTimerPaused: Boolean = false,
    val isWorkoutLogged: Boolean = false,
    val caloriesBurned: Int = 0,
    val availableWorkouts: List<String> = listOf(
        "Push Day (Chest & Triceps)",
        "Pull Day (Back & Biceps)",
        "Legs Day (Quads & Calves)",
        "Upper Body Power",
        "Lower Body Power",
        "Full Body Strength",
        "Calisthenics (Bodyweight)",
        "Core & Abs Finisher",
        "Boxing / MMA Cardio",
        "HIIT & Cardio",
        "Active Recovery / Yoga"
    )
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val logWorkoutUseCase: LogWorkoutUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var restTimerJob: Job? = null
    private var cardioTimerJob: Job? = null
    private var totalCardioElapsedSeconds = 0

    init {
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        val defaultWorkout = when (dayOfWeek) {
            1 -> "Push Day (Chest & Triceps)"
            2 -> "Pull Day (Back & Biceps)"
            3 -> "Legs Day (Quads & Calves)"
            4 -> "Upper Body Power"
            5 -> "Lower Body Power"
            6 -> "Full Body Strength"
            else -> "Active Recovery / Yoga"
        }
        selectWorkout(defaultWorkout)
    }

    fun selectWorkout(workoutName: String) {
        val exercises = when (workoutName) {
            "Push Day (Chest & Triceps)" -> listOf(
                Exercise("Flat Bench Press", List(4) { ExerciseSet(8, 60.0) }, 8, 4, 60.0),
                Exercise("Incline DB Press", List(3) { ExerciseSet(10, 22.5) }, 10, 3, 22.5),
                Exercise("Cable Flyes", List(3) { ExerciseSet(15, 12.5) }, 15, 3, 12.5),
                Exercise("Overhead Press", List(3) { ExerciseSet(10, 35.0) }, 10, 3, 35.0),
                Exercise("Tricep Pushdowns", List(3) { ExerciseSet(12, 20.0) }, 12, 3, 20.0),
                Exercise("Skullcrushers", List(3) { ExerciseSet(10, 25.0) }, 10, 3, 25.0)
            )
            "Pull Day (Back & Biceps)" -> listOf(
                Exercise("Deadlifts", List(3) { ExerciseSet(5, 100.0) }, 5, 3, 100.0),
                Exercise("Lat Pulldowns", List(3) { ExerciseSet(12, 55.0) }, 12, 3, 55.0),
                Exercise("Seated Cable Rows", List(3) { ExerciseSet(10, 50.0) }, 10, 3, 50.0),
                Exercise("Face Pulls", List(4) { ExerciseSet(15, 15.0) }, 15, 4, 15.0),
                Exercise("Barbell Curls", List(3) { ExerciseSet(12, 30.0) }, 12, 3, 30.0),
                Exercise("Hammer Curls", List(3) { ExerciseSet(12, 12.5) }, 12, 3, 12.5)
            )
            "Legs Day (Quads & Calves)" -> listOf(
                Exercise("Barbell Squats", List(4) { ExerciseSet(8, 80.0) }, 8, 4, 80.0),
                Exercise("Leg Press", List(3) { ExerciseSet(12, 120.0) }, 12, 3, 120.0),
                Exercise("Bulgarian Split Squats", List(3) { ExerciseSet(10, 15.0) }, 10, 3, 15.0),
                Exercise("Leg Extensions", List(3) { ExerciseSet(15, 40.0) }, 15, 3, 40.0),
                Exercise("Standing Calf Raises", List(4) { ExerciseSet(20, 50.0) }, 20, 4, 50.0)
            )
            "Upper Body Power" -> listOf(
                Exercise("Weighted Pullups", List(3) { ExerciseSet(8, 10.0) }, 8, 3, 10.0),
                Exercise("Incline Bench Press", List(3) { ExerciseSet(8, 55.0) }, 8, 3, 55.0),
                Exercise("Barbell Rows", List(3) { ExerciseSet(8, 50.0) }, 8, 3, 50.0),
                Exercise("Shoulder Press", List(3) { ExerciseSet(10, 17.5) }, 10, 3, 17.5),
                Exercise("Dips", List(3) { ExerciseSet(12, 0.0) }, 12, 3, 0.0)
            )
            "Lower Body Power" -> listOf(
                Exercise("RDLs", List(4) { ExerciseSet(10, 70.0) }, 10, 4, 70.0),
                Exercise("Front Squats", List(3) { ExerciseSet(8, 50.0) }, 8, 3, 50.0),
                Exercise("Lying Leg Curls", List(3) { ExerciseSet(12, 35.0) }, 12, 3, 35.0),
                Exercise("Walking Lunges", List(3) { ExerciseSet(20, 12.5) }, 20, 3, 12.5),
                Exercise("Seated Calf Raises", List(4) { ExerciseSet(15, 30.0) }, 15, 4, 30.0)
            )
            "Full Body Strength" -> listOf(
                Exercise("Squats", List(3) { ExerciseSet(5, 80.0) }, 5, 3, 80.0),
                Exercise("Bench Press", List(3) { ExerciseSet(5, 60.0) }, 5, 3, 60.0),
                Exercise("Barbell Rows", List(3) { ExerciseSet(5, 50.0) }, 5, 3, 50.0),
                Exercise("Overhead Press", List(3) { ExerciseSet(8, 40.0) }, 8, 3, 40.0),
                Exercise("Pullups", List(3) { ExerciseSet(8, 0.0) }, 8, 3, 0.0)
            )
            "Calisthenics (Bodyweight)" -> listOf(
                Exercise("Muscle Ups / Pullups", List(3) { ExerciseSet(8, 0.0) }, 8, 3, 0.0),
                Exercise("Pushups (Weighted)", List(3) { ExerciseSet(20, 10.0) }, 20, 3, 10.0),
                Exercise("Dips", List(3) { ExerciseSet(15, 0.0) }, 15, 3, 0.0),
                Exercise("Bodyweight Rows", List(3) { ExerciseSet(12, 0.0) }, 12, 3, 0.0),
                Exercise("Pistol Squats", List(3) { ExerciseSet(10, 0.0) }, 10, 3, 0.0)
            )
            "Core & Abs Finisher" -> listOf(
                Exercise("Hanging Leg Raises", List(3) { ExerciseSet(15, 0.0) }, 15, 3, 0.0),
                Exercise("Plank", List(3) { ExerciseSet(60, 0.0) }, 60, 3, 0.0),
                Exercise("Russian Twists", List(3) { ExerciseSet(30, 5.0) }, 30, 3, 5.0),
                Exercise("Bicycle Crunches", List(3) { ExerciseSet(20, 0.0) }, 20, 3, 0.0)
            )
            "Boxing / MMA Cardio" -> listOf(
                Exercise("Shadow Boxing", List(5) { ExerciseSet(3, 0.0) }, 3, 5, 0.0),
                Exercise("Heavy Bag Work", List(5) { ExerciseSet(3, 0.0) }, 3, 5, 0.0),
                Exercise("Burpees", List(3) { ExerciseSet(15, 0.0) }, 15, 3, 0.0),
                Exercise("Jump Rope", List(3) { ExerciseSet(120, 0.0) }, 120, 3, 0.0)
            )
            "HIIT & Cardio" -> listOf(
                Exercise("Burpees", List(3) { ExerciseSet(15, 0.0) }, 15, 3, 0.0),
                Exercise("Mountain Climbers", List(3) { ExerciseSet(30, 0.0) }, 30, 3, 0.0),
                Exercise("Jump Squats", List(3) { ExerciseSet(20, 0.0) }, 20, 3, 0.0),
                Exercise("High Knees", List(3) { ExerciseSet(40, 0.0) }, 40, 3, 0.0)
            )
            else -> listOf(
                Exercise("Sun Salutation", List(5) { ExerciseSet(1, 0.0) }, 1, 5, 0.0),
                Exercise("Cat-Cow", List(3) { ExerciseSet(10, 0.0) }, 10, 3, 0.0),
                Exercise("Bird-Dog", List(3) { ExerciseSet(12, 0.0) }, 12, 3, 0.0),
                Exercise("Walking (Treadmill)", List(1) { ExerciseSet(30, 0.0) }, 30, 1, 0.0)
            )
        }

        _uiState.update { 
            it.copy(
                currentWorkout = Workout(
                    id = LocalDate.now().toString(),
                    name = workoutName,
                    exercises = exercises,
                    timestamp = LocalDateTime.now(),
                    durationMinutes = 0
                ),
                caloriesBurned = 0
            )
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        _uiState.update { state ->
            val workout = state.currentWorkout ?: return@update state
            val updatedExercises = workout.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList()
            
            val currentSet = updatedSets[setIndex]
            val newState = !currentSet.isCompleted
            updatedSets[setIndex] = currentSet.copy(isCompleted = newState)
            updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
            
            if (newState) startRestTimer(90) 

            recalculateCalories(updatedExercises, totalCardioElapsedSeconds / 60)

            state.copy(
                currentWorkout = workout.copy(exercises = updatedExercises)
            )
        }
    }

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, newWeight: Double) {
        _uiState.update { state ->
            val workout = state.currentWorkout ?: return@update state
            val updatedExercises = workout.exercises.toMutableList()
            val exercise = updatedExercises[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList()
            
            updatedSets[setIndex] = updatedSets[setIndex].copy(weightKg = newWeight)
            updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
            
            recalculateCalories(updatedExercises, totalCardioElapsedSeconds / 60)

            state.copy(
                currentWorkout = workout.copy(exercises = updatedExercises)
            )
        }
    }

    private fun recalculateCalories(exercises: List<Exercise>, cardioMinutes: Int) {
        // Dynamic formula: Baseline 5kcal + (Weight / 10) per set.
        // Example: 100kg set = 15kcal, 0kg (bodyweight) = 5kcal.
        val strengthCals = exercises.sumOf { ex -> 
            ex.sets.filter { it.isCompleted }.sumOf { set -> 
                5.0 + (set.weightKg / 10.0) 
            }
        }.toInt()
        
        val cardioCals = cardioMinutes * 6 
        
        _uiState.update { it.copy(caloriesBurned = strengthCals + cardioCals) }
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _uiState.update { it.copy(restTimerSeconds = seconds, isRestTimerActive = true) }
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { it.copy(restTimerSeconds = remaining) }
            }
            _uiState.update { it.copy(isRestTimerActive = false) }
        }
    }

    fun startCardioTimer(minutes: Int) {
        if (_uiState.value.isCardioTimerPaused) {
            resumeCardioTimer()
            return
        }
        
        cardioTimerJob?.cancel()
        val totalSeconds = minutes * 60
        _uiState.update { it.copy(cardioTimerSeconds = totalSeconds, isCardioTimerActive = true, isCardioTimerPaused = false) }
        runCardioLoop()
    }

    private fun resumeCardioTimer() {
        _uiState.update { it.copy(isCardioTimerActive = true, isCardioTimerPaused = false) }
        runCardioLoop()
    }

    private fun runCardioLoop() {
        cardioTimerJob?.cancel()
        cardioTimerJob = viewModelScope.launch {
            while (_uiState.value.cardioTimerSeconds > 0) {
                delay(1000)
                totalCardioElapsedSeconds++
                _uiState.update { it.copy(cardioTimerSeconds = it.cardioTimerSeconds - 1) }
                
                if (totalCardioElapsedSeconds % 60 == 0) {
                    recalculateCalories(_uiState.value.currentWorkout?.exercises ?: emptyList(), totalCardioElapsedSeconds / 60)
                }
            }
            _uiState.update { it.copy(isCardioTimerActive = false) }
        }
    }

    fun pauseCardioTimer() {
        cardioTimerJob?.cancel()
        _uiState.update { it.copy(isCardioTimerActive = false, isCardioTimerPaused = true) }
    }

    fun stopCardioTimer() {
        cardioTimerJob?.cancel()
        _uiState.update { it.copy(isCardioTimerActive = false, isCardioTimerPaused = false, cardioTimerSeconds = 0) }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch
            val completedSets = workout.exercises.sumOf { ex -> ex.sets.count { it.isCompleted } }
            
            if (completedSets > 0 || _uiState.value.caloriesBurned > 0) {
                logWorkoutUseCase(workout.copy(isCompleted = true, durationMinutes = 60))
                userPreferencesRepository.updateStreak()
                _uiState.update { it.copy(isWorkoutLogged = true) }
            }
        }
    }
}
