package com.example.hmi.core.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

object ColorUtils {
    /**
     * Determines whether black or white text should be used on top of a given background color
     * to ensure maximum readability (WCAG 2.1 compliance).
     */
    fun getContrastColor(backgroundColor: Color): Color {
        return if (backgroundColor.luminance() < 0.5f) {
            Color.White
        } else {
            Color.Black
        }
    }

    /**
     * Determines the contrast color for the "Modern Industrial" aesthetic.
     * Prioritizes Black text (#000000) for vibrant colors, but switches to White text (#FFFFFF)
     * if the background is too dark for comfortable reading.
     */
    fun getIndustrialContrastColor(backgroundColor: Color): Color {
        if (backgroundColor == Color.Transparent) return Color.White
        val bgLuminance = backgroundColor.luminance()
        
        // For industrial HMI, we prefer White text on any color that isn't distinctly "bright"
        // OSHA Blue and Red typically require White. Green and Yellow typically require Black.
        return if (bgLuminance > 0.35f) {
            Color.Black
        } else {
            Color.White
        }
    }
}
