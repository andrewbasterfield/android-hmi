package com.example.hmi.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetConfigurationTest {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    @Test
    fun `serialization includes orientation`() {
        val config = WidgetConfiguration(
            type = WidgetType.SLIDER,
            tagAddress = "TEST_TAG",
            orientation = WidgetOrientation.VERTICAL
        )
        
        val jsonStr = json.encodeToString(config)
        
        assertTrue(jsonStr.contains("\"orientation\":\"VERTICAL\""))
    }

    @Test
    fun `deserialization handles orientation`() {
        val jsonStr = """
            {
                "type": "SLIDER",
                "tagAddress": "TEST_TAG",
                "orientation": "VERTICAL"
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertEquals(WidgetOrientation.VERTICAL, config.orientation)
    }

    @Test
    fun `deserialization handles missing orientation with default`() {
        val jsonStr = """
            {
                "type": "SLIDER",
                "tagAddress": "TEST_TAG"
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertEquals(WidgetOrientation.HORIZONTAL, config.orientation)
    }

    @Test
    fun `serialization includes interaction type and inversion`() {
        val config = WidgetConfiguration(
            type = WidgetType.BUTTON,
            tagAddress = "TEST_TAG",
            interactionType = InteractionType.LATCHING,
            isInverted = true
        )
        
        val jsonStr = json.encodeToString(config)
        
        assertTrue(jsonStr.contains("\"interactionType\":\"LATCHING\""))
        assertTrue(jsonStr.contains("\"isInverted\":true"))
    }

    @Test
    fun `deserialization handles interaction type and inversion`() {
        val jsonStr = """
            {
                "type": "BUTTON",
                "tagAddress": "TEST_TAG",
                "interactionType": "INDICATOR",
                "isInverted": true
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertEquals(InteractionType.INDICATOR, config.interactionType)
        assertTrue(config.isInverted)
    }

    @Test
    fun `deserialization handles missing interaction fields with defaults`() {
        val jsonStr = """
            {
                "type": "BUTTON",
                "tagAddress": "TEST_TAG"
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertEquals(InteractionType.MOMENTARY, config.interactionType)
        assertFalse(config.isInverted)
    }

    @Test
    fun `serialization includes new gauge fields`() {
        val config = WidgetConfiguration(
            type = WidgetType.GAUGE,
            tagAddress = "TEST_TAG",
            pointerColor = 0xFFFF0000L,
            isPointerDynamic = true,
            gaugeAxis = GaugeAxis.LINEAR_VERTICAL,
            gaugeIndicator = GaugeIndicator.FILL,
            gaugeStyle = GaugeStyle.ARC_FILL,
            units = "PSI"
        )
        
        val jsonStr = json.encodeToString(config)
        
        assertTrue(jsonStr.contains("\"pointerColor\":4294901760"))
        assertTrue(jsonStr.contains("\"isPointerDynamic\":true"))
        assertTrue(jsonStr.contains("\"gaugeAxis\":\"LINEAR_VERTICAL\""))
        assertTrue(jsonStr.contains("\"gaugeIndicator\":\"FILL\""))
        assertTrue(jsonStr.contains("\"gaugeStyle\":\"ARC_FILL\""))
        assertTrue(jsonStr.contains("\"units\":\"PSI\""))
    }

    @Test
    fun `deserialization handles new gauge fields`() {
        val jsonStr = """
            {
                "type": "GAUGE",
                "tagAddress": "TEST_TAG",
                "pointerColor": 4294901760,
                "isPointerDynamic": true,
                "gaugeAxis": "LINEAR_VERTICAL",
                "gaugeIndicator": "FILL",
                "gaugeStyle": "ARC_FILL",
                "units": "PSI"
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertEquals(0xFFFF0000L, config.pointerColor)
        assertTrue(config.isPointerDynamic)
        assertEquals(GaugeAxis.LINEAR_VERTICAL, config.gaugeAxis)
        assertEquals(GaugeIndicator.FILL, config.gaugeIndicator)
        assertEquals(GaugeStyle.ARC_FILL, config.gaugeStyle)
        assertEquals("PSI", config.units)
    }

    @Test
    fun `deserialization handles missing fields with defaults`() {
        val jsonStr = """
            {
                "type": "GAUGE",
                "tagAddress": "TEST_TAG"
            }
        """.trimIndent()
        
        val config = json.decodeFromString<WidgetConfiguration>(jsonStr)
        
        assertNull(config.pointerColor)
        assertTrue(config.isPointerDynamic)
        assertEquals(GaugeAxis.ARC, config.gaugeAxis)
        assertEquals(GaugeIndicator.POINTER, config.gaugeIndicator)
        assertEquals(GaugeStyle.POINTER, config.gaugeStyle)
        assertNull(config.units)
    }
}
