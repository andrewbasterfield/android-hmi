package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import com.example.hmi.data.GaugeZone
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

    @Test
    fun resolveColor_returnsStaticColor_whenDynamicIsFalse() {
        val staticColor = 0xFF00FF00L
        val defaultColor = Color.Red
        
        val result = ColorUtils.resolveColor(
            currentValueStr = "test",
            currentValueFloat = 50f,
            isColorDynamic = false,
            staticColor = staticColor,
            colorZones = emptyList(),
            defaultColor = defaultColor
        )
        
        assertEquals(ColorUtils.toColor(staticColor), result)
    }

    @Test
    fun resolveColor_returnsStringMatch() {
        val defaultColor = Color.White
        val matchColor = 0xFF00FF00L
        val zones = listOf(
            GaugeZone(0f, 100f, 0xFFFF0000L, label = null, exactMatch = "FAULT"),
            GaugeZone(0f, 100f, matchColor, label = null, exactMatch = "OK")
        )
        
        val result = ColorUtils.resolveColor(
            currentValueStr = "ok",
            currentValueFloat = 50f,
            isColorDynamic = true,
            staticColor = null,
            colorZones = zones,
            defaultColor = defaultColor
        )
        
        assertEquals(ColorUtils.toColor(matchColor), result)
    }

    @Test
    fun resolveColor_returnsNumericMatch_whenNoStringMatch() {
        val defaultColor = Color.White
        val matchColor = 0xFF0000FFL
        val zones = listOf(
            GaugeZone(0f, 49f, 0xFFFF0000L),
            GaugeZone(50f, 100f, matchColor)
        )
        
        val result = ColorUtils.resolveColor(
            currentValueStr = "something",
            currentValueFloat = 75f,
            isColorDynamic = true,
            staticColor = null,
            colorZones = zones,
            defaultColor = defaultColor
        )
        
        assertEquals(ColorUtils.toColor(matchColor), result)
    }

    @Test
    fun resolveColor_returnsDefault_whenNoMatchAndNoStaticColor() {
        val defaultColor = Color.Magenta
        val zones = listOf(
            GaugeZone(0f, 49f, 0xFFFF0000L)
        )
        
        val result = ColorUtils.resolveColor(
            currentValueStr = "something",
            currentValueFloat = 100f,
            isColorDynamic = true,
            staticColor = null,
            colorZones = zones,
            defaultColor = defaultColor
        )
        
        assertEquals(defaultColor, result)
    }
}
