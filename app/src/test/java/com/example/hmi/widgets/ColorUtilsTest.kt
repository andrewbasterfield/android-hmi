package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun getContrastColor_returnsWhiteOnDarkColors() {
        assertEquals(Color.White, ColorUtils.getContrastColor(Color.Black))
        assertEquals(Color.White, ColorUtils.getContrastColor(Color.DarkGray))
        assertEquals(Color.White, ColorUtils.getContrastColor(Color(0xFFD32F2F))) // Red
        assertEquals(Color.White, ColorUtils.getContrastColor(Color(0xFF1976D2))) // Blue
    }

    @Test
    fun getContrastColor_returnsBlackOnLightColors() {
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color.White))
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color.LightGray))
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color.Yellow))
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color(0xFFFBC02D))) // Industrial Yellow
    }
}
