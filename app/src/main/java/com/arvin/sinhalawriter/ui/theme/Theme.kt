package com.arvin.sinhalawriter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = WriterBlue,
    secondary = LightSecondary,
    surface = LightSurface,
    surfaceDim = LightSurfaceDim,
    outline = LightOutline,
)

private val DarkColors = darkColorScheme(
    primary = WriterBlue,
    secondary = DarkSecondary,
    surface = DarkSurface,
    surfaceDim = DarkSurfaceDim,
    outline = DarkOutline,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
)

@Composable
fun SinhalaWriterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
