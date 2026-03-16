package com.example.hmi.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Curated high-contrast color palette for the HMI Dashboard.
 * All colors are selected to provide at least 4.5:1 contrast ratio against black (#000000) text.
 */
object HmiPalette {
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)
    
    // Vibrant colors for widget backgrounds
    val BrightGreen = Color(0xFF00FF00)      // Contrast ~15.3:1
    val SafetyOrange = Color(0xFFFF9900)     // Contrast ~9.7:1
    val SkyBlue = Color(0xFF00CCFF)          // Contrast ~11.2:1
    val CanaryYellow = Color(0xFFFFFF00)     // Contrast ~19.6:1
    val NeonPink = Color(0xFFFF33CC)         // Contrast ~7.1:1
    val ElectricPurple = Color(0xFFCC99FF)   // Contrast ~10.9:1
    val MintGreen = Color(0xFF98FF98)        // Contrast ~16.5:1
    val SalmonPink = Color(0xFFFF91A4)       // Contrast ~11.4:1
    val GoldenRod = Color(0xFFDAA520)        // Contrast ~8.4:1
    val Cyan = Color(0xFF00FFFF)             // Contrast ~16.7:1

    /**
     * List of all high-contrast colors suitable for widget backgrounds.
     * Excludes pure black as it would hide black text.
     */
    val WidgetBackgrounds = listOf(
        White,
        BrightGreen,
        SafetyOrange,
        SkyBlue,
        CanaryYellow,
        NeonPink,
        ElectricPurple,
        MintGreen,
        SalmonPink,
        GoldenRod,
        Cyan
    )

    /**
     * Map of color long values to HmiPalette names for persistence and selection.
     */
    val colorMap = WidgetBackgrounds.associateBy { it.value.toLong() }
}
