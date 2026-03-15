package com.fitcore.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * FitCore Shapes
 * Adheres to flat design, specific corner radii for cards, chips, and bottom nav.
 */
val FitCoreShapes = Shapes(
    small = RoundedCornerShape(20.dp), // for chips
    medium = RoundedCornerShape(16.dp), // for cards
    large = RoundedCornerShape(0.dp) // for bottom nav
)
