package com.fitcore.presentation.log

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitcore.domain.model.Food
import com.fitcore.presentation.theme.*

/**
 * FitCore Food Log Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(
    mealId: String? = null,
    onBackClick: () -> Unit,
    viewModel: FoodLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mealId != null) "Add to Meal" else "Log Food") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Barcode Scanner */ }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = AccentGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepDarkBackground, titleContentColor = Color.White)
            )
        },
        containerColor = DeepDarkBackground,
        bottomBar = {
            LoggedTotalsBar(
                loggedFoods = uiState.loggedFoods,
                onSave = { viewModel.saveLogs() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Search Results
                if (uiState.searchResults.isNotEmpty()) {
                    item {
                        SectionHeader("Search Results")
                    }
                    items(uiState.searchResults) { food ->
                        FoodSearchResultItem(food, onAdd = { viewModel.addFoodToLog(food) })
                    }
                }

                // Quick Add Chips
                item {
                    SectionHeader("Quick Add")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(uiState.quickAddFoods) { food ->
                            FoodChip(food, onClick = { viewModel.addFoodToLog(food) })
                        }
                    }
                }

                // Recently Logged
                if (uiState.loggedFoods.isNotEmpty()) {
                    item {
                        SectionHeader("Recently Logged")
                    }
                    itemsIndexed(uiState.loggedFoods) { index, entry ->
                        LoggedFoodItem(
                            entry = entry,
                            onRemove = { viewModel.removeFoodFromLog(index) },
                            onQuantityChange = { viewModel.updateQuantity(index, it) }
                        )
                    }
                } else if (uiState.searchResults.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Text("No items selected", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text("Search Indian foods (e.g. Roti, Dal)") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = AccentGreen,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun FoodSearchResultItem(food: Food, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(food.name, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Text("${food.calories.toInt()} kcal • ${food.servingSize}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Icon(Icons.Default.AddCircle, contentDescription = null, tint = AccentGreen)
    }
}

@Composable
fun FoodChip(food: Food, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderStroke)
    ) {
        Text(
            text = food.name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

@Composable
fun LoggedFoodItem(
    entry: FoodEntryUi,
    onRemove: () -> Unit,
    onQuantityChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.food.name, style = MaterialTheme.typography.bodyLarge)
                Text("${(entry.food.calories * entry.quantity).toInt()} kcal", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (entry.quantity > 0.5) onQuantityChange(entry.quantity - 0.5) }) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Gray)
                }
                Text("${entry.quantity}", style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { onQuantityChange(entry.quantity + 0.5) }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AccentGreen)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun LoggedTotalsBar(loggedFoods: List<FoodEntryUi>, onSave: () -> Unit) {
    val totalCalories = loggedFoods.sumOf { it.food.calories * it.quantity }
    val totalProtein = loggedFoods.sumOf { it.food.protein * it.quantity }
    
    Surface(
        color = SurfaceDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Logged", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("${totalCalories.toInt()} kcal", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
            Text("${totalProtein.toInt()}g Protein", style = MaterialTheme.typography.titleMedium, color = AccentGreen)
            Button(
                onClick = onSave,
                enabled = loggedFoods.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = Color.Black)
            }
        }
    }
}
