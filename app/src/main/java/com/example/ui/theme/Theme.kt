package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CorpDarkGreenAccent,
    secondary = CorpDarkBlueLight,
    tertiary = CorpDarkGreenAccent,
    background = CorpDarkBg,
    surface = CorpDarkSurface,
    onPrimary = CorpDarkBg,
    onSecondary = CorpDarkTextPrimary,
    onTertiary = CorpDarkBg,
    onBackground = CorpDarkTextPrimary,
    onSurface = CorpDarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = CorpBluePrimary,
    secondary = CorpBlueSecondary,
    tertiary = CorpGreenAccent,
    background = CorpLightBg,
    surface = CorpSurface,
    onPrimary = CorpSurface,
    onSecondary = CorpSurface,
    onTertiary = CorpSurface,
    onBackground = CorpTextPrimary,
    onSurface = CorpTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep branded colors consistent across all devices by disabling dynamic coloring by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
