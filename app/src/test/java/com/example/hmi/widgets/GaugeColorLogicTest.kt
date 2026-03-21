package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import com.example.hmi.data.GaugeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class GaugeColorLogicTest {

    private val defaultColor = Color.Gray
    private val staticColor = Color.Blue
    private val zoneColor = Color.Red
    private val colorZones = listOf(
        GaugeZone(80f, 100f, zoneColor.value.toLong())
    )

    @Test
    fun `resolves static needle color when dynamic is off`() {
        val color = ColorUtils.resolveNeedleColor(
            currentValue = 90f,
            isNeedleDynamic = false,
            staticNeedleColor = staticColor.value.toLong(),
            colorZones = colorZones,
            defaultColor = defaultColor
        )
        assertEquals(staticColor, color)
    }

    @Test
    fun `resolves zone color when dynamic is on and value in zone`() {
        val color = ColorUtils.resolveNeedleColor(
            currentValue = 90f,
            isNeedleDynamic = true,
            staticNeedleColor = staticColor.value.toLong(),
            colorZones = colorZones,
            defaultColor = defaultColor
        )
        assertEquals(zoneColor, color)
    }

    @Test
    fun `resolves static color when dynamic is on but value outside zones`() {
        val color = ColorUtils.resolveNeedleColor(
            currentValue = 50f,
            isNeedleDynamic = true,
            staticNeedleColor = staticColor.value.toLong(),
            colorZones = colorZones,
            defaultColor = defaultColor
        )
        assertEquals(staticColor, color)
    }

    @Test
    fun `resolves default color when no static or dynamic match`() {
        val color = ColorUtils.resolveNeedleColor(
            currentValue = 50f,
            isNeedleDynamic = false,
            staticNeedleColor = null,
            colorZones = colorZones,
            defaultColor = defaultColor
        )
        assertEquals(defaultColor, color)
    }
}
