package com.example.hmi.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Industrial-standard colors available in the HMI.
 */
object ColorPalette {
    val Red = Color(0xFFD32F2F)
    val Green = Color(0xFF388E3C)
    val Yellow = Color(0xFFFBC02D)
    val Blue = Color(0xFF1976D2)
    val Gray = Color(0xFF757575)
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)

    /**
     * List of colors available for selection, mapped to their ARGB Long values.
     * "Default" is represented as null to use theme-standard colors.
     */
    val Items = listOf(
        "Default" to null,
        "Red" to Red.toArgb().toLong(),
        "Green" to Green.toArgb().toLong(),
        "Yellow" to Yellow.toArgb().toLong(),
        "Blue" to Blue.toArgb().toLong(),
        "Gray" to Gray.toArgb().toLong(),
        "Black" to Black.toArgb().toLong(),
        "White" to White.toArgb().toLong()
    )
}
