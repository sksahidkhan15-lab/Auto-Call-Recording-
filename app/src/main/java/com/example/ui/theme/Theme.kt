package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = MainBackground,
    primaryContainer = SecondaryAccent,
    onPrimaryContainer = MainText,
    secondary = SecondaryAccent,
    onSecondary = MainBackground,
    background = MainBackground,
    onBackground = MainText,
    surface = CardBackground,
    onSurface = MainText,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = SecondaryText,
    outline = OutlineColor,
    error = ErrorColor,
    onError = MainText
)

@Composable
fun AutoCallRecorderTheme(
    darkTheme: Boolean = true, // Default to premium dark theme requested
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
