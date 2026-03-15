package com.fitcore.presentation.meals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fitcore.domain.model.Meal
import com.fitcore.presentation.components.LoadingScreen
import com.fitcore.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    onBackClick: () -> Unit,
    onNavigateToLog: (String) -> Unit,
    viewModel: MealPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddMealDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Plan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMealDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Meal", tint = AccentGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepDarkBackground, titleContentColor = Color.White)
            )
        },
        containerColor = DeepDarkBackground
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MealPlanHeader(
                        current = uiState.totalCalories,
                        target = 1750.0 
                    )
                }

                items(uiState.meals) { meal ->
                    MealItem(
                        meal = meal,
                        onToggleEaten = { viewModel.toggleMealEaten(meal.id, it) },
                        onAddFood = { onNavigateToLog(meal.id) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (showAddMealDialog) {
            AddMealDialog(
                onDismiss = { showAddMealDialog = false },
                onConfirm = { name, h, m ->
                    viewModel.addMeal(name, h, m)
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
fun AddMealDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Meal") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Meal Name (e.g. Midnight Snack)") },
                colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, 12, 0) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = SurfaceDark,
        titleContentColor = Color.White
    )
}

@Composable
fun MealPlanHeader(current: Double, target: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text("Daily Progress", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "${current.toInt()} / ${target.toInt()} kcal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${((current / target) * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = AccentGreen
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { (current / target).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = AccentGreen,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun MealItem(
    meal: Meal,
    onToggleEaten: (Boolean) -> Unit,
    onAddFood: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (meal.isEaten) SurfaceDark.copy(alpha = 0.5f) else SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(meal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(meal.scheduledTime.toString(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Checkbox(
                    checked = meal.isEaten,
                    onCheckedChange = onToggleEaten,
                    colors = CheckboxDefaults.colors(checkedColor = TealDone)
                )
            }
            
            if (meal.foods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(8.dp))
                meal.foods.forEach { food ->
                    Text("• ${food.name}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAddFood,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Food", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
