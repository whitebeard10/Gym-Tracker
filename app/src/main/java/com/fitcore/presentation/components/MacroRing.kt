package com.fitcore.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitcore.presentation.theme.AccentGreen
import com.fitcore.presentation.theme.AccentGreenGlow
import com.fitcore.presentation.theme.DeepDarkBackground
import com.fitcore.presentation.theme.GreenGradient

@Composable
fun MacroRing(
    progress: Float,
    consumed: Int,
    remaining: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1500)
        )
    }

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glow / Shadow (Simulated)
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            drawCircle(
                color = AccentGreenGlow,
                radius = size.minDimension / 2,
                style = Stroke(width = 30.dp.toPx())
            )
        }

        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            // Track
            drawArc(
                color = Color.White.copy(alpha = 0.05f),
                startAngle = -225f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            // Progress with Gradient
            drawArc(
                brush = GreenGradient,
                startAngle = -225f,
                sweepAngle = 270f * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = remaining.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                ),
                color = Color.White
            )
            Text(
                text = "kcal left",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).padding(2.dp)) // spacer
                Text(
                    text = "Eaten: $consumed",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentGreen
                )
            }
        }
    }
}
