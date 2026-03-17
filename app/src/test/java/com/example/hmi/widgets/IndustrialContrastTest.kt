package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class IndustrialContrastTest {

    @Test
    fun getIndustrialContrastColor_returnsWhite_on_CherryRed() {
        // Cherry Red #D2042D, Luminance ~0.14, Contrast with Black ~3.8:1
        val cherryRed = Color(0xFFD2042D)
        assertEquals(Color.White, ColorUtils.getIndustrialContrastColor(cherryRed))
    }

    @Test
    fun getIndustrialContrastColor_returnsBlack_on_VibrantYellow() {
        // Vibrant Yellow #FFEB3B, Luminance ~0.84, Contrast with Black ~17.8:1
        val vibrantYellow = Color(0xFFFFEB3B)
        assertEquals(Color.Black, ColorUtils.getIndustrialContrastColor(vibrantYellow))
    }

    @Test
    fun getIndustrialContrastColor_returnsWhite_on_DeepBlue() {
        // Deep Blue #0D47A1, Luminance ~0.03, Contrast with Black ~1.6:1
        val deepBlue = Color(0xFF0D47A1)
        assertEquals(Color.White, ColorUtils.getIndustrialContrastColor(deepBlue))
    }

    @Test
    fun getIndustrialContrastColor_returnsBlack_on_LightGray() {
        // Light Gray #EEEEEE, Luminance ~0.85, Contrast with Black ~18:1
        val lightGray = Color(0xFFEEEEEE)
        assertEquals(Color.Black, ColorUtils.getIndustrialContrastColor(lightGray))
    }
}
