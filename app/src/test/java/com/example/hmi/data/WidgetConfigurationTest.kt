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
            needleColor = 0xFFFF0000L,
            isNeedleDynamic = true,
            units = "PSI"
        )
        
        val json = gson.toJson(config)
        
        assertTrue(json.contains("\"needleColor\":4294901760"))
        assertTrue(json.contains("\"isNeedleDynamic\":true"))
        assertTrue(json.contains("\"units\":\"PSI\""))
    }

    @Test
    fun `deserialization handles new gauge fields`() {
        val json = """
            {
                "type": "GAUGE",
                "tagAddress": "TEST_TAG",
                "needleColor": 4294901760,
                "isNeedleDynamic": true,
                "units": "PSI"
            }
        """.trimIndent()
        
        val config = gson.fromJson(json, WidgetConfiguration::class.java)
        
        assertEquals(0xFFFF0000L, config.needleColor)
        assertTrue(config.isNeedleDynamic)
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
        
        assertNull(config.needleColor)
        assertFalse(config.isNeedleDynamic)
        assertNull(config.units)
    }
}
