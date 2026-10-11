package com.example.mesoffres.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Couleurs principales du design.
val PrimaryBlue = Color(0xFF1769FF)
val DarkBlue = Color(0xFF0F172A)
val LightBackground = Color(0xFFF7F9FC)
val Orange = Color(0xFFFF7900)
val Green = Color(0xFF19A974)
val Red = Color(0xFFE5484D)

private val AppColors = lightColorScheme(
    primary = PrimaryBlue,
    secondary = Orange,
    background = LightBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = DarkBlue,
    onSurface = DarkBlue
)

/**
 * Thème global.
 */
@Composable
fun mesoffresTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}
