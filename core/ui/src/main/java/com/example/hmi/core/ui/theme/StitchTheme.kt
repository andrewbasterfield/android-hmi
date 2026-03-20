package com.example.hmi.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class StitchThemeTokens(
    val statusGreen: Color = StatusGreen,
    val statusAmber: Color = StatusAmber,
    val statusRed: Color = StatusRed,
    val surfaceContainerLow: Color = SurfaceContainerLow,
    val surfaceContainerHigh: Color = SurfaceContainerHigh,
    val surfaceContainerHighest: Color = SurfaceContainerHighest
)

val LocalStitchTokens = staticCompositionLocalOf { StitchThemeTokens() }

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    background = Void,
    surface = Void,
    onBackground = Primary,
    onSurface = Primary,
    outline = Outline,
    outlineVariant = OutlineVariant
)

@Composable
fun StitchTheme(
    content: @Composable () -> Unit
) {
    val stitchTokens = StitchThemeTokens()

    CompositionLocalProvider(
        LocalStitchTokens provides stitchTokens
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object StitchTheme {
    val tokens: StitchThemeTokens
        @Composable
        get() = LocalStitchTokens.current
}
