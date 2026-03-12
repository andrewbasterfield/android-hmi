package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Utility functions for color manipulation in the HMI.
 */
object ColorUtils {
    /**
     * Determines whether black or white text should be used on top of a given background color
     * to ensure maximum readability (WCAG 2.1 compliance).
     */
    fun getContrastColor(backgroundColor: Color): Color {
        return if (backgroundColor.luminance() > 0.5f) {
            Color.Black
        } else {
            Color.White
        }
    }
}
