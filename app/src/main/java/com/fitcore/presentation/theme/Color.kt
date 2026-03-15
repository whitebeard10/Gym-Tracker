package com.fitcore.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * FitCore Premium Colors
 * Deep dark theme with glassmorphism accents.
 */
val DeepDarkBackground = Color(0xFF080808)
val SurfaceDark = Color(0xFF121212)
val GlassSurface = Color(0xCC1A1A1A)
val GlassBorder = Color(0x1AFFFFFF)

val AccentGreen = Color(0xFF5DCAA5)
val AccentGreenGlow = Color(0x335DCAA5)

// Premium Gradients
val GreenGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF7EF2AD), Color(0xFF5DCAA5))
)

val CarbsGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFDC830), Color(0xFFF37335))
)

val WaterGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
)

// Macros & Hydration
val AmberCarbs = Color(0xFFEF9F27)
val CoralFats = Color(0xFFF0997B)
val BlueWater = Color(0xFF378ADD)

// Status Colors
val TealDone = Color(0xFF009688)
val GreyUpcoming = Color(0xFF555555)
val BorderStroke = Color(0x12FFFFFF)
