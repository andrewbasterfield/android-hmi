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

    // --- reconnect cap ---

    @Test
    fun `reconnect cap trips even when the broker has never connected`() {
        // lastConnectedTime stays at its initial 0, simulating a broker that's
        // down at app start, before any successful connection has ever happened.
        repeat(4) {
            assertEquals(ConnectionState.RECONNECTING, communicator.callHandleDisconnect(hasError = true))
        }

        assertEquals(ConnectionState.ERROR, communicator.callHandleDisconnect(hasError = true))
    }

    @Test
    fun `giving up after max attempts is not overwritten by the resulting disconnect event`() {
        repeat(4) { communicator.callHandleDisconnect(hasError = true) }
        assertEquals(ConnectionState.ERROR, communicator.callHandleDisconnect(hasError = true))

        // The disconnect() call triggered by giving up fires the listener again,
        // and HiveMQ reports that self-triggered event as "no error" (intentional).
        assertEquals(ConnectionState.ERROR, communicator.callHandleDisconnect(hasError = false))
    }

    @Test
    fun `an intentional disconnect resets attempts and reports DISCONNECTED`() {
        communicator.callHandleDisconnect(hasError = true)

        val state = communicator.callHandleDisconnect(hasError = false)

        assertEquals(ConnectionState.DISCONNECTED, state)
        assertEquals(0, communicator.getReconnectAttemptsForTest())
    }

    @Test
    fun `a stable connection resets the attempt counter before the next failure`() {
        repeat(3) { communicator.callHandleDisconnect(hasError = true) }
        assertEquals(3, communicator.getReconnectAttemptsForTest())

        communicator.setLastConnectedTimeForTest(System.currentTimeMillis() - 6000)
        communicator.callHandleDisconnect(hasError = true)

        assertEquals(1, communicator.getReconnectAttemptsForTest())
    }

    // Helper to call private method for testing
    private fun MqttPlcCommunicator.callParsePayload(payload: String, settings: MqttSettings, jsonPathOverride: String? = null): PlcValue {
        val method = this.javaClass.getDeclaredMethod("parsePayload", String::class.java, MqttSettings::class.java, String::class.java, String::class.java)
        method.isAccessible = true
        return method.invoke(this, payload, settings, null, jsonPathOverride) as PlcValue
    }

    private fun MqttPlcCommunicator.callHandleDisconnect(hasError: Boolean, cause: Throwable? = null): ConnectionState {
        val method = this.javaClass.getDeclaredMethod("handleDisconnect", Boolean::class.java, Throwable::class.java)
        method.isAccessible = true
        return method.invoke(this, hasError, cause) as ConnectionState
    }

    private fun MqttPlcCommunicator.getReconnectAttemptsForTest(): Int {
        val field = this.javaClass.getDeclaredField("reconnectAttempts")
        field.isAccessible = true
        return field.getInt(this)
    }

    private fun MqttPlcCommunicator.setLastConnectedTimeForTest(value: Long) {
        val field = this.javaClass.getDeclaredField("lastConnectedTime")
        field.isAccessible = true
        field.setLong(this, value)
    }
}
