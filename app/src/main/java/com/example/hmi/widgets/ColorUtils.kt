package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Utility functions for color manipulation in the HMI.
 */
object ColorUtils {
    /**
     * Parses a hex color string (e.g., "#FF0000" or "FF0000") into a Long.
     * Supports both RRGGBB and AARRGGBB formats.
     */
    fun parseHexColor(hex: String): Long? {
        return try {
            val cleaned = hex.removePrefix("#")
            val colorLong = when (cleaned.length) {
                6 -> {
                    // RRGGBB -> AARRGGBB (fully opaque)
                    ("FF" + cleaned).toLong(16)
                }
                8 -> {
                    // AARRGGBB
                    cleaned.toLong(16)
                }
                else -> return null
            }
            colorLong
        } catch (e: Exception) {
            null
        }
    }

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
