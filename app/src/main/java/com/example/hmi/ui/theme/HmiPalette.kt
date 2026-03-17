package com.example.hmi.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Curated high-saturation primary color palette for the HMI Dashboard.
 * All colors are selected for industrial visibility.
 * The HybridContrast logic ensures text remains readable (Black or White).
 */
object HmiPalette {
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    val Gray = Color(0xFFBDBDBD)
    
    // Primary Industrial Colors
    val Red = Color(0xFFFF0000)
    val Green = Color(0xFF00FF00)
    val Blue = Color(0xFF0000FF)
    val Yellow = Color(0xFFFFFF00)
    val Orange = Color(0xFFFF9900)
    val Cyan = Color(0xFF00FFFF)
    val Magenta = Color(0xFFFF00FF)

    /**
     * List of all high-visibility colors suitable for widget backgrounds.
     */
    val WidgetBackgrounds = listOf(
        White,
        Gray,
        Red,
        Green,
        Blue,
        Yellow,
        Orange,
        Cyan,
        Magenta
    )

    /**
     * Map of color long values to HmiPalette names for persistence and selection.
     * We use null to represent the "Default" theme primary color.
     */
    val colorMap = WidgetBackgrounds.associateBy { it.value.toLong() }
}
