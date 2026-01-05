package com.lakshmanrekha.protect.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern, high-contrast palette
private val DeepNavy = Color(0xFF0F172A)
private val SurfaceNavy = Color(0xFF1E293B)
private val PrimaryBlue = Color(0xFF38BDF8) // Sleek Cyan-Blue
private val AccentGreen = Color(0xFF4ADE80) // Safety Green
private val DangerRed = Color(0xFFF87171)   // Soft but clear Red

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentGreen,
    tertiary = Color(0xFFFBBF24), // Amber for warnings
    background = DeepNavy,
    surface = SurfaceNavy,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = DangerRed
)

@Composable
fun LakshmanRekhaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Follows system but looks great in dark
    content: @Composable () -> Unit
) {
    // We stay with a dark-dominant theme as it's often easier on senior eyes (less glare)
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Make sure to use the Typography defined below
        content = content
    )
}