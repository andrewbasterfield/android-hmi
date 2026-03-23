package com.example.hmi.data

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetConfigurationTest {
    private val gson = Gson()

    @Test
    fun `serialization includes new gauge fields`() {
        val config = WidgetConfiguration(
            type = WidgetType.GAUGE,
            tagAddress = "TEST_TAG",
            pointerColor = 0xFFFF0000L,
            isPointerDynamic = true,
            gaugeStyle = GaugeStyle.ARC_FILL,
            units = "PSI"
        )
        
        val json = gson.toJson(config)
        
        assertTrue(json.contains("\"pointerColor\":4294901760"))
        assertTrue(json.contains("\"isPointerDynamic\":true"))
        assertTrue(json.contains("\"gaugeStyle\":\"ARC_FILL\""))
        assertTrue(json.contains("\"units\":\"PSI\""))
    }

    @Test
    fun `deserialization handles new gauge fields`() {
        val json = """
            {
                "type": "GAUGE",
                "tagAddress": "TEST_TAG",
                "pointerColor": 4294901760,
                "isPointerDynamic": true,
                "gaugeStyle": "ARC_FILL",
                "units": "PSI"
            }
        """.trimIndent()
        
        val config = gson.fromJson(json, WidgetConfiguration::class.java)
        
        assertEquals(0xFFFF0000L, config.pointerColor)
        assertTrue(config.isPointerDynamic)
        assertEquals(GaugeStyle.ARC_FILL, config.gaugeStyle)
        assertEquals("PSI", config.units)
    }

    @Test
    fun `deserialization handles missing fields with defaults`() {
        val json = """
            {
                "type": "GAUGE",
                "tagAddress": "TEST_TAG"
            }
        """.trimIndent()
        
        val config = gson.fromJson(json, WidgetConfiguration::class.java)
        
        assertNull(config.pointerColor)
        assertTrue(config.isPointerDynamic)
        assertNull(config.gaugeStyle)
        assertNull(config.units)
    }
}
