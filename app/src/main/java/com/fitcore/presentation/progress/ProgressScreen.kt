package com.fitcore.presentation.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitcore.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Progress", style = MaterialTheme.typography.headlineSmall) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { WeightTrendSection(uiState.currentWeight, uiState.targetWeight) }
            
            item {
                Text("Weight History (Last 30 Days)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SimpleLineChartPlaceholder()
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatsCard("Workouts", uiState.totalWorkouts.toString(), AccentGreen, Modifier.weight(1f))
                    StatsCard("Avg Protein", "${uiState.avgDailyProtein}g", AmberCarbs, Modifier.weight(1f))
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatsCard("Longest Streak", "${uiState.longestStreak}d", Color(0xFFFF5722), Modifier.weight(1f))
                    StatsCard("Goal Date", "April 15", BlueWater, Modifier.weight(1f))
                }
            }

            item { GoalProjectionCard() }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun WeightTrendSection(current: Double, target: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Current Weight", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("${current}kg", style = MaterialTheme.typography.displaySmall, color = Color.White)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingDown, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Text(" 3.0kg to go", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                }
                Text("Target: ${target}kg", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = accentColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SimpleLineChartPlaceholder() {
    // A simple Canvas-drawn line chart placeholder until Vico is fully configured
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.4f)
                lineTo(size.width * 0.2f, size.height * 0.5f)
                lineTo(size.width * 0.4f, size.height * 0.3f)
                lineTo(size.width * 0.6f, size.height * 0.6f)
                lineTo(size.width * 0.8f, size.height * 0.45f)
                lineTo(size.width, size.height * 0.7f)
            }
            drawPath(
                path = path,
                color = AccentGreen,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw points
            drawCircle(AccentGreen, 4.dp.toPx(), center = Offset(0f, size.height * 0.4f))
            drawCircle(AccentGreen, 4.dp.toPx(), center = Offset(size.width, size.height * 0.7f))
        }
    }
}

@Composable
fun GoalProjectionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Goal Projection",
                style = MaterialTheme.typography.titleSmall,
                color = AccentGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "At your current rate of 0.4kg/week loss, you'll reach your 56kg goal by April 14, 2026.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
