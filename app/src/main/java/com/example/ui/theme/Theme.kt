package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberNeonGreen,
    secondary = CyberEmerald,
    tertiary = CyberGlowBlue,
    background = DarkObsidianNoise,
    surface = DarkMetalSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BrightSlateWhite,
    onSurface = BrightSlateWhite,
    error = CyberWarningRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // We enforce our custom Cyberpunk Dark theme for the ultimate cyberpunk cybersecurity feel!
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
