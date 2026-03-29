package com.example.hmi.protocol.utils

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class JsonPathUtilsTest {

    @Test
    fun `test single key extraction`() {
        val json = Json.parseToJsonElement("""{"temp": 25.5, "status": "OK", "active": true}""")
        
        assertEquals("25.5", JsonPathUtils.extractJsonPath(json, "temp")?.content)
        assertEquals("OK", JsonPathUtils.extractJsonPath(json, "status")?.content)
        assertEquals("true", JsonPathUtils.extractJsonPath(json, "active")?.content)
    }

    @Test
    fun `test missing key returns null`() {
        val json = Json.parseToJsonElement("""{"temp": 25.5}""")
        assertNull(JsonPathUtils.extractJsonPath(json, "humidity"))
    }

    @Test
    fun `test non-primitive value returns null`() {
        val json = Json.parseToJsonElement("""{"nested": {"key": "value"}}""")
        assertNull(JsonPathUtils.extractJsonPath(json, "nested"))
    }

    @Test
    fun `test nested key extraction`() {
        val json = Json.parseToJsonElement("""{"motor": {"status": {"temp": 72.5}}}""")
        assertEquals("72.5", JsonPathUtils.extractJsonPath(json, "motor.status.temp")?.content)
    }

    @Test
    fun `test invalid intermediate key returns null`() {
        val json = Json.parseToJsonElement("""{"motor": {"status": {"temp": 72.5}}}""")
        assertNull(JsonPathUtils.extractJsonPath(json, "motor.health.temp"))
    }

    @Test
    fun `test leading or trailing dots return null`() {
        val json = Json.parseToJsonElement("""{"temp": 25.5}""")
        assertNull(JsonPathUtils.extractJsonPath(json, ".temp"))
        assertNull(JsonPathUtils.extractJsonPath(json, "temp."))
    }

    @Test
    fun `test missing leaf key returns null`() {
        val json = Json.parseToJsonElement("""{"motor": {"status": {}}}""")
        assertNull(JsonPathUtils.extractJsonPath(json, "motor.status.temp"))
    }

    @Test
    fun `test leaf is not primitive returns null`() {
        val json = Json.parseToJsonElement("""{"motor": {"status": {"details": {"msg": "OK"}}}}""")
        assertNull(JsonPathUtils.extractJsonPath(json, "motor.status.details"))
    }

    @Test
    fun `test empty path returns null`() {
        val json = Json.parseToJsonElement("""{"temp": 25.5}""")
        assertNull(JsonPathUtils.extractJsonPath(json, ""))
    }
}
