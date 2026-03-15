package com.fitcore.presentation.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitcore.data.local.entity.FoodLogEntry
import com.fitcore.data.repository.FoodRepositoryImpl
import com.fitcore.data.repository.MealRepositoryImpl
import com.fitcore.domain.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * UI State for the Food Log Screen.
 */
data class FoodLogUiState(
    val searchQuery: String = "",
    val searchResults: List<Food> = emptyList(),
    val quickAddFoods: List<Food> = emptyList(),
    val loggedFoods: List<FoodEntryUi> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

data class FoodEntryUi(
    val food: Food,
    val quantity: Double = 1.0
)

@OptIn(FlowPreview::class)
@HiltViewModel
class FoodLogViewModel @Inject constructor(
    private val foodRepository: FoodRepositoryImpl,
    private val mealRepository: MealRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mealId: String? = savedStateHandle["mealId"]

    private val _uiState = MutableStateFlow(FoodLogUiState())
    val uiState: StateFlow<FoodLogUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    searchFoods(query)
                } else {
                    _uiState.update { it.copy(searchResults = emptyList()) }
                }
            }
            .launchIn(viewModelScope)

        loadQuickAddFoods()
    }

    private fun loadQuickAddFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoods().take(1).collect { foods ->
                _uiState.update { it.copy(quickAddFoods = foods.take(8)) }
            }
        }
    }

    private fun searchFoods(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            foodRepository.searchFoods(query).collect { results ->
                _uiState.update { it.copy(searchResults = results, isLoading = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun addFoodToLog(food: Food) {
        val entry = FoodEntryUi(food = food)
        _uiState.update { it.copy(loggedFoods = it.loggedFoods + entry) }
    }

    fun removeFoodFromLog(index: Int) {
        _uiState.update { 
            val newList = it.loggedFoods.toMutableList()
            if (index in newList.indices) newList.removeAt(index)
            it.copy(loggedFoods = newList)
        }
    }

    fun updateQuantity(index: Int, newQuantity: Double) {
        _uiState.update { 
            val newList = it.loggedFoods.toMutableList()
            if (index in newList.indices) {
                newList[index] = newList[index].copy(quantity = newQuantity)
            }
            it.copy(loggedFoods = newList)
        }
    }

    fun saveLogs() {
        viewModelScope.launch {
            val today = LocalDate.now()
            _uiState.value.loggedFoods.forEach { entry ->
                if (mealId != null) {
                    // It's possible the food is not yet in the master list, but for simplicity
                    // we assume we add it to the extra log or we need a foodId.
                    // For this prototype, if it's a specific meal, we'll log it as extra food 
                    // AND potentially link it. But simpler is just extra food.
                    // Actually, let's just log everything as FoodLogEntry for now as it's the most flexible.
                    // If mealId is provided, we can just treat it as a category.
                }
                
                mealRepository.logExtraFood(
                    FoodLogEntry(
                        date = today,
                        foodName = entry.food.name,
                        calories = entry.food.calories,
                        protein = entry.food.protein,
                        carbs = entry.food.carbs,
                        fats = entry.food.fats,
                        quantity = entry.quantity
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
