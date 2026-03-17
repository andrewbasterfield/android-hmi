package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Utility functions for color manipulation in the HMI.
 */
object ColorUtils {
    /**
     * Parses a hex color string (e.g., "#FF0000" or "FF0000") into a Long.
     * Returns the internal Compose Color value (64-bit ULong) as a Long.
     */
    fun parseHexColor(hex: String): Long? {
        return try {
            val cleaned = hex.removePrefix("#")
            val argb = when (cleaned.length) {
                6 -> ("FF" + cleaned).toLong(16).toInt()
                8 -> cleaned.toLong(16).toInt()
                else -> return null
            }
            // Standardize on internal Compose Color representation for consistency
            Color(argb).value.toLong()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Safely converts a Long (which could be an ARGB Int or an internal ULong) to a Color.
     */
    fun toColor(value: Long): Color {
        // Internal ULong representation for sRGB always has bits 62 or 63 set.
        // A safe heuristic for this project is checking if it's within standard 32-bit ARGB range.
        return if (value > 0 && value <= 0xFFFFFFFFL) {
            Color(value.toInt())
        } else {
            Color(value.toULong())
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
     * Determines the contrast color for the "Modern Industrial" aesthetic.
     * Prioritizes Black text (#000000) for vibrant colors, but switches to White text (#FFFFFF)
     * if the 4.5:1 contrast ratio (WCAG AA) cannot be met.
     */
    fun getIndustrialContrastColor(backgroundColor: Color): Color {
        val bgLuminance = backgroundColor.luminance()
        
        // Calculate contrast ratio for Black text: (bgL + 0.05) / (0.0 + 0.05)
        val blackContrast = (bgLuminance + 0.05f) / 0.05f
        
        return if (blackContrast >= 4.5f) {
            Color.Black
        } else {
            Color.White
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
