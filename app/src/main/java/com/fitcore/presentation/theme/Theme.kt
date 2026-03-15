package com.fitcore.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitCoreDarkColorScheme = darkColorScheme(
    background = DeepDarkBackground,
    surface = SurfaceDark,
    primary = AccentGreen,
    secondary = AmberCarbs,
    tertiary = CoralFats,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.Black
)

private val FitCoreLightColorScheme = lightColorScheme(
    // App primarily uses Dark theme, but light theme is provided for Settings toggle
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
    surface = androidx.compose.ui.graphics.Color.White,
    primary = AccentGreen,
    secondary = AmberCarbs,
    tertiary = CoralFats,
    onBackground = androidx.compose.ui.graphics.Color.Black,
    onSurface = androidx.compose.ui.graphics.Color.Black,
    onPrimary = androidx.compose.ui.graphics.Color.White
)

/**
 * FitCore Main Theme
 */
@Composable
fun FitCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitCoreDarkColorScheme else FitCoreLightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitCoreTypography,
        shapes = FitCoreShapes,
        content = content
    )
}
