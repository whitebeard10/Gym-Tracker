package com.fitcore.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitcore.domain.model.MacroSummary
import com.fitcore.presentation.components.*
import com.fitcore.presentation.theme.*

/**
 * Stateful FitCore Home Screen.
 */
@Composable
fun HomeScreen(
    onNavigateToLog: () -> Unit,
    onNavigateToMealPlan: () -> Unit,
    onNavigateToWorkout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    
    HomeScreenContent(
        state = uiState,
        onNavigateToLog = onNavigateToLog,
        onNavigateToMealPlan = onNavigateToMealPlan,
        onNavigateToWorkout = onNavigateToWorkout,
        onAddWater = { 
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.addWater(it) 
        }
    )
}

/**
 * Stateless Home Screen Content for testing and previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onNavigateToLog: () -> Unit,
    onNavigateToMealPlan: () -> Unit,
    onNavigateToWorkout: () -> Unit,
    onAddWater: (Double) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToLog,
                containerColor = AccentGreen,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Log")
            }
        },
        containerColor = DeepDarkBackground
    ) { padding ->
        when (state) {
            is HomeUiState.Loading -> {
                LoadingScreen()
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    item {
                        Column {
                            Text(
                                text = state.greeting,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = state.userName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            MacroRing(
                                progress = state.macroSummary.calorieProgress,
                                consumed = state.macroSummary.consumedCalories.toInt(),
                                remaining = state.macroSummary.remainingCalories.toInt()
                            )
                        }
                    }

                    item {
                        MacroRow(summary = state.macroSummary)
                    }

                    item {
                        WaterTracker(
                            consumedLiters = state.macroSummary.waterDrankLiters,
                            targetLiters = state.macroSummary.targetWaterLiters,
                            onAddWater = onAddWater
                        )
                    }

                    item {
                        StreakCard(streakDays = state.streak)
                    }

                    if (state.nextMeal != null) {
                        item {
                            SectionHeader(title = "Next Meal")
                            MealCard(
                                meal = state.nextMeal,
                                onMealClick = onNavigateToMealPlan
                            )
                        }
                    }

                    item {
                        WorkoutChip(
                            workoutName = state.todayWorkoutName,
                            onWorkoutClick = onNavigateToWorkout
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun MacroRow(summary: MacroSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacroItem(
            label = "Protein",
            consumed = summary.consumedProtein.toInt(),
            target = summary.targetProtein.toInt(),
            color = AccentGreen,
            modifier = Modifier.weight(1f)
        )
        MacroItem(
            label = "Carbs",
            consumed = summary.consumedCarbs.toInt(),
            target = summary.targetCarbs.toInt(),
            color = AmberCarbs,
            modifier = Modifier.weight(1f)
        )
        MacroItem(
            label = "Fats",
            consumed = summary.consumedFats.toInt(),
            target = summary.targetFats.toInt(),
            color = CoralFats,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MacroItem(
    label: String,
    consumed: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (if (target > 0) (consumed.toFloat() / target) else 0f).coerceIn(0f, 1f),
        label = "macroProgress"
    )

    GlassCard(
        modifier = modifier,
        cornerRadius = 20.dp
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
        Text(
            text = "${consumed}g",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color)
            )
        }
        Text(
            text = "of ${target}g",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun WorkoutChip(workoutName: String, onWorkoutClick: () -> Unit) {
    Surface(
        onClick = onWorkoutClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenGradient)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TODAY'S WORKOUT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = workoutName.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
