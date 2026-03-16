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
        return if (isDark(backgroundColor)) {
            Color.White
        } else {
            Color.Black
        }
    }

    /**
     * Returns true if the color is considered "dark" (luminance < 0.5).
     */
    fun isDark(color: Color): Boolean {
        return color.luminance() < 0.5f
    }

    /**
     * Formats a color value as a 6-digit hex string (e.g., "FF0000").
     */
    fun formatHexColor(color: Color): String {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        return String.format("%02X%02X%02X", r, g, b)
    }
}
