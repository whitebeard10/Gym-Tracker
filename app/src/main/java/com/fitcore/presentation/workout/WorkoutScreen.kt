package com.fitcore.presentation.workout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitcore.domain.model.Exercise
import com.fitcore.presentation.settings.EditDialog
import com.fitcore.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onBackClick: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showWorkoutSelector by remember { mutableStateOf(false) }
    
    var showWeightDialog by remember { mutableStateOf(false) }
    var selectedExIdx by remember { mutableIntStateOf(0) }
    var selectedSetIdx by remember { mutableIntStateOf(0) }
    var initialWeight by remember { mutableStateOf("0.0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showWorkoutSelector = true }
                    ) {
                        Text(
                            uiState.currentWorkout?.name ?: "Select Workout", 
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                actions = {
                    Text(
                        "${uiState.caloriesBurned} kcal",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = AccentGreen
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepDarkBackground, titleContentColor = Color.White)
            )
        },
        containerColor = DeepDarkBackground,
        bottomBar = {
            if (uiState.isRestTimerActive) {
                RestTimerBar(seconds = uiState.restTimerSeconds)
            } else if (uiState.isCardioTimerActive || uiState.isCardioTimerPaused) {
                CardioTimerBar(
                    seconds = uiState.cardioTimerSeconds,
                    isPaused = uiState.isCardioTimerPaused,
                    onPause = { viewModel.pauseCardioTimer() },
                    onResume = { viewModel.startCardioTimer(0) },
                    onStop = { viewModel.stopCardioTimer() }
                )
            } else {
                FinishWorkoutBar(onFinish = { viewModel.finishWorkout() })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { WorkoutHeader() }

            uiState.currentWorkout?.let { workout ->
                itemsIndexed(workout.exercises) { exIdx, exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onSetToggle = { setIdx -> viewModel.toggleSetCompleted(exIdx, setIdx) },
                        onWeightClick = { setIdx, weight ->
                            selectedExIdx = exIdx
                            selectedSetIdx = setIdx
                            initialWeight = weight.toString()
                            showWeightDialog = true
                        }
                    )
                }
            }

            item { 
                CardioBlock(
                    isActive = uiState.isCardioTimerActive,
                    isPaused = uiState.isCardioTimerPaused,
                    onStartTimer = { viewModel.startCardioTimer(30) } 
                ) 
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        if (showWorkoutSelector) {
            ModalBottomSheet(
                onDismissRequest = { showWorkoutSelector = false },
                containerColor = SurfaceDark
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Workout Type", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                    uiState.availableWorkouts.forEach { name ->
                        Surface(
                            onClick = { 
                                viewModel.selectWorkout(name)
                                showWorkoutSelector = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Transparent
                        ) {
                            Text(
                                name, 
                                modifier = Modifier.padding(16.dp),
                                color = if (uiState.currentWorkout?.name == name) AccentGreen else Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showWeightDialog) {
            EditDialog(
                title = "Adjust Weight (kg)",
                initialValue = initialWeight,
                keyboardType = KeyboardType.Decimal,
                onDismiss = { showWeightDialog = false },
                onConfirm = { 
                    it.toDoubleOrNull()?.let { weight ->
                        viewModel.updateSetWeight(selectedExIdx, selectedSetIdx, weight)
                    }
                    showWeightDialog = false
                }
            )
        }

        if (uiState.isWorkoutLogged) {
            AlertDialog(
                onDismissRequest = onBackClick,
                title = { Text("Workout Logged!") },
                text = { Text("Great job today! Your streak and calories have been updated.") },
                confirmButton = {
                    Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
                        Text("Awesome", color = Color.Black)
                    }
                },
                containerColor = SurfaceDark,
                titleContentColor = Color.White,
                textContentColor = Color.Gray
            )
        }
    }
}

@Composable
fun WorkoutHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Daily Plan", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = AmberCarbs, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Tap weight to adjust", style = MaterialTheme.typography.labelSmall, color = AmberCarbs)
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onSetToggle: (Int) -> Unit,
    onWeightClick: (Int, Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(0.5.dp, BorderStroke, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(exercise.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text("${exercise.targetSets} × ${exercise.targetReps}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        exercise.sets.forEachIndexed { index, set ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Set ${index + 1}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                
                Text(
                    text = "${set.weightKg} kg", 
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .clickable { onWeightClick(index, set.weightKg) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = AccentGreen
                )
                
                IconButton(
                    onClick = { onSetToggle(index) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (set.isCompleted) TealDone else Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (set.isCompleted) Color.Black else Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CardioBlock(isActive: Boolean, isPaused: Boolean, onStartTimer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Cardio: LISS", style = MaterialTheme.typography.titleSmall, color = BlueWater)
        Spacer(modifier = Modifier.height(8.dp))
        Text("30 min walk @ 5.5 km/h", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onStartTimer,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isActive && !isPaused,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isActive || isPaused) Color.Gray.copy(alpha = 0.1f) else BlueWater.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = if (isActive || isPaused) Color.Gray else BlueWater)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isActive) "Timer Running..." else if (isPaused) "Timer Paused" else "Start 30 Min Timer", 
                color = if (isActive || isPaused) Color.Gray else BlueWater
            )
        }
    }
}

@Composable
fun RestTimerBar(seconds: Int) {
    Surface(
        color = AmberCarbs,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "REST: ${seconds / 60}:${String.format("%02d", seconds % 60)}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CardioTimerBar(
    seconds: Int, 
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Surface(
        color = BlueWater,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${seconds / 60}:${String.format("%02d", seconds % 60)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row {
                if (isPaused) {
                    IconButton(onClick = onResume) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = Color.White)
                    }
                } else {
                    IconButton(onClick = onPause) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
                    }
                }
                IconButton(onClick = onStop) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun FinishWorkoutBar(onFinish: () -> Unit) {
    Surface(
        color = SurfaceDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onFinish,
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log Workout", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
