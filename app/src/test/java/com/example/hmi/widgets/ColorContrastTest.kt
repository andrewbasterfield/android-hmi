package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {

    @Test
    fun `isDark returns true for black and false for white`() {
        assertTrue(ColorUtils.isDark(Color.Black))
        assertFalse(ColorUtils.isDark(Color.White))
    }

    @Test
    fun `getContrastColor returns white for dark backgrounds`() {
        assertEquals(Color.White, ColorUtils.getContrastColor(Color.Black))
        assertEquals(Color.White, ColorUtils.getContrastColor(Color(0xFF222222)))
    }

    @Test
    fun `getContrastColor returns black for light backgrounds`() {
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color.White))
        assertEquals(Color.Black, ColorUtils.getContrastColor(Color(0xFFEEEEEE)))
    }

    @Test
    fun `formatHexColor formats correctly`() {
        assertEquals("FF0000", ColorUtils.formatHexColor(Color.Red))
        assertEquals("00FF00", ColorUtils.formatHexColor(Color.Green))
        assertEquals("0000FF", ColorUtils.formatHexColor(Color.Blue))
        assertEquals("000000", ColorUtils.formatHexColor(Color.Black))
        assertEquals("FFFFFF", ColorUtils.formatHexColor(Color.White))
    }
}
