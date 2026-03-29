package com.example.hmi.protocol

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MqttPlcCommunicatorTest {

    private lateinit var communicator: MqttPlcCommunicator

    @Before
    fun setup() {
        communicator = MqttPlcCommunicator()
    }

    @Test
    fun `initial state is DISCONNECTED`() {
        assertEquals(ConnectionState.DISCONNECTED, communicator.connectionState.value)
    }

    @Test
    fun `parsePayload parses plain text float`() {
        val settings = MqttSettings(payloadFormat = MqttPayloadFormat.PLAIN_TEXT)
        val result = communicator.callParsePayload("25.5", settings)
        assertEquals(PlcValue.FloatValue(25.5f), result)
    }

    @Test
    fun `parsePayload parses plain text boolean`() {
        val settings = MqttSettings(payloadFormat = MqttPayloadFormat.PLAIN_TEXT)
        val result = communicator.callParsePayload("true", settings)
        assertEquals(PlcValue.BooleanValue(true), result)
    }

    @Test
    fun `parsePayload parses JSON value`() {
        val settings = MqttSettings(payloadFormat = MqttPayloadFormat.JSON, jsonKey = "val")
        val result = communicator.callParsePayload("{\"val\": 42.0, \"unit\": \"C\"}", settings)
        assertEquals(PlcValue.FloatValue(42.0f), result)
    }

    @Test
    fun `parsePayload parses JSON boolean`() {
        val settings = MqttSettings(payloadFormat = MqttPayloadFormat.JSON, jsonKey = "active")
        val result = communicator.callParsePayload("{\"active\": true}", settings)
        assertEquals(PlcValue.BooleanValue(true), result)
    }

    @Test
    fun `getFullTopic prepends prefix correctly`() {
        val profile = PlcConnectionProfile(
            mqttSettings = MqttSettings(rootTopicPrefix = "factory/line1/")
        )
        val method = communicator.javaClass.getDeclaredField("currentProfile")
        method.isAccessible = true
        method.set(communicator, profile)
        
        val getFullTopicMethod = communicator.javaClass.getDeclaredMethod("getFullTopic", String::class.java)
        getFullTopicMethod.isAccessible = true
        
        assertEquals("factory/line1/tag1", getFullTopicMethod.invoke(communicator, "tag1"))
        assertEquals("tag2", getFullTopicMethod.invoke(communicator, "/tag2"))
    }

    @Test
    fun `parsePayload with jsonPath override`() {
        val settings = MqttSettings(payloadFormat = MqttPayloadFormat.PLAIN_TEXT) // Global setting is PLAIN_TEXT
        val payload = "{\"temp\": 25.5, \"humidity\": 60}"
        
        val tempResult = communicator.callParsePayload(payload, settings, "temp")
        val humidityResult = communicator.callParsePayload(payload, settings, "humidity")
        
        assertEquals(PlcValue.FloatValue(25.5f), tempResult)
        assertEquals(PlcValue.FloatValue(60.0f), humidityResult)
    }

    // Helper to call private method for testing
    private fun MqttPlcCommunicator.callParsePayload(payload: String, settings: MqttSettings, jsonPathOverride: String? = null): PlcValue {
        val method = this.javaClass.getDeclaredMethod("parsePayload", String::class.java, MqttSettings::class.java, String::class.java, String::class.java)
        method.isAccessible = true
        return method.invoke(this, payload, settings, null, jsonPathOverride) as PlcValue
    }
}
