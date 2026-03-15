package com.fitcore.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitcore.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTdeeSheet by remember { mutableStateOf(false) }
    
    var showNameDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showGoalWeightDialog by remember { mutableStateOf(false) }
    var showCalorieDialog by remember { mutableStateOf(false) }
    var showProteinDialog by remember { mutableStateOf(false) }
    var showCarbDialog by remember { mutableStateOf(false) }
    var showFatDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepDarkBackground, titleContentColor = Color.White)
            )
        },
        containerColor = DeepDarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // Profile Section
            item {
                SettingsSectionHeader("User Profile")
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Name",
                    subtitle = uiState.userProfile.name,
                    onClick = { showNameDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Scale,
                    title = "Current Weight",
                    subtitle = "${uiState.userProfile.currentWeightKg} kg",
                    onClick = { showWeightDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Flag,
                    title = "Goal Weight",
                    subtitle = "${uiState.userProfile.goalWeightKg} kg",
                    onClick = { showGoalWeightDialog = true }
                )
            }

            // Macro Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("Daily Targets")
                SettingsItem(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "Calorie Target",
                    subtitle = "${uiState.userProfile.dailyCalorieTarget} kcal",
                    onClick = { showCalorieDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Restaurant,
                    title = "Protein Target",
                    subtitle = "${uiState.userProfile.proteinTargetG}g",
                    onClick = { showProteinDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.BakeryDining,
                    title = "Carbs Target",
                    subtitle = "${uiState.userProfile.carbTargetG}g",
                    onClick = { showCarbDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Opacity,
                    title = "Fats Target",
                    subtitle = "${uiState.userProfile.fatTargetG}g",
                    onClick = { showFatDialog = true }
                )
            }

            // App Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("App Settings")
                SettingsItem(
                    icon = Icons.Default.Calculate,
                    title = "Recalculate TDEE",
                    subtitle = "Based on current weight and activity",
                    onClick = { showTdeeSheet = true }
                )
                SettingsItem(
                    icon = Icons.Default.Refresh,
                    title = "Reset All Progress",
                    subtitle = "Clear all logs and preferences",
                    onClick = { showResetDialog = true }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    checked = uiState.darkThemeEnabled,
                    onCheckedChange = { viewModel.toggleTheme(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "App Version 1.2.0\nPersonalized for Abish with ❤️",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showTdeeSheet) {
        TdeeBottomSheet(
            tdee = viewModel.calculateTDEE(),
            onDismiss = { showTdeeSheet = false }
        )
    }

    // Dialogs
    if (showNameDialog) EditDialog("Edit Name", uiState.userProfile.name, onDismiss = { showNameDialog = false }, onConfirm = { viewModel.updateName(it); showNameDialog = false })
    if (showWeightDialog) EditDialog("Edit Weight (kg)", uiState.userProfile.currentWeightKg.toString(), KeyboardType.Decimal, onDismiss = { showWeightDialog = false }, onConfirm = { it.toDoubleOrNull()?.let { v -> viewModel.updateWeight(v) }; showWeightDialog = false })
    if (showGoalWeightDialog) EditDialog("Edit Goal Weight (kg)", uiState.userProfile.goalWeightKg.toString(), KeyboardType.Decimal, onDismiss = { showGoalWeightDialog = false }, onConfirm = { it.toDoubleOrNull()?.let { v -> viewModel.updateGoalWeight(v) }; showGoalWeightDialog = false })
    if (showCalorieDialog) EditDialog("Edit Calorie Target", uiState.userProfile.dailyCalorieTarget.toString(), KeyboardType.Number, onDismiss = { showCalorieDialog = false }, onConfirm = { it.toIntOrNull()?.let { v -> viewModel.updateCalorieTarget(v) }; showCalorieDialog = false })
    if (showProteinDialog) EditDialog("Edit Protein Target (g)", uiState.userProfile.proteinTargetG.toString(), KeyboardType.Number, onDismiss = { showProteinDialog = false }, onConfirm = { it.toIntOrNull()?.let { v -> viewModel.updateProteinTarget(v) }; showProteinDialog = false })
    if (showCarbDialog) EditDialog("Edit Carb Target (g)", uiState.userProfile.carbTargetG.toString(), KeyboardType.Number, onDismiss = { showCarbDialog = false }, onConfirm = { it.toIntOrNull()?.let { v -> viewModel.updateCarbTarget(v) }; showCarbDialog = false })
    if (showFatDialog) EditDialog("Edit Fat Target (g)", uiState.userProfile.fatTargetG.toString(), KeyboardType.Number, onDismiss = { showFatDialog = false }, onConfirm = { it.toIntOrNull()?.let { v -> viewModel.updateFatTarget(v) }; showFatDialog = false })

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("This will clear all your logs and reset settings.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetProgress(); showResetDialog = false }) { Text("RESET", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("CANCEL") }
            },
            containerColor = SurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.Gray
        )
    }
}

@Composable
fun EditDialog(
    title: String,
    initialValue: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = AccentGreen, unfocusedBorderColor = Color.Gray)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) { Text("Save", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        },
        containerColor = SurfaceDark,
        titleContentColor = Color.White
    )
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelLarge, color = AccentGreen, modifier = Modifier.padding(bottom = 8.dp, top = 16.dp))
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = SurfaceDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(color = SurfaceDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = AccentGreen, checkedTrackColor = AccentGreen.copy(alpha = 0.3f)))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TdeeBottomSheet(tdee: Double, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = SurfaceDark) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TDEE Breakdown", style = MaterialTheme.typography.headlineSmall, color = AccentGreen)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "${tdee.toInt()} kcal", style = MaterialTheme.typography.displayMedium, color = Color.White)
            Text("Total Daily Energy Expenditure", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            TdeeRow("BMR (Base Metabolism)", "${(tdee * 0.7).toInt()} kcal")
            TdeeRow("Activity Multiplier", "1.55x (Active)")
            TdeeRow("Deficit Applied", "500 kcal")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) { Text("Got it", color = Color.Black) }
        }
    }
}

@Composable
fun TdeeRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}
