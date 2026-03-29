package com.example.hmi.protocol

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract: PlcCommunicator
 * 
 * Defines how the HMI application interacts with various industrial protocols.
 * This abstraction satisfies FR-009, allowing implementations like Raw TCP, 
 * Modbus TCP, or OPC UA to be plugged in.
 */
interface PlcCommunicator {
    
    /** Current state of the connection to the PLC */
    val connectionState: StateFlow<ConnectionState>
    
    /** 
     * Connects to the PLC based on the given profile. 
     * @return Result.success if connected, Result.failure on error.
     */
    suspend fun connect(profile: PlcConnectionProfile): Result<Unit>
    
    /** Disconnects from the currently connected PLC. */
    suspend fun disconnect()
    
    /**
     * Reads a continuous stream of updates for a specific tag or memory address.
     * @param tagAddress The address string (e.g., "40001" for Modbus, "ns=2;s=MyTag" for OPC UA)
     * @param jsonPath Optional path to extract from a JSON payload (e.g., "motor.temp")
     */
    fun observeTag(tagAddress: String, jsonPath: String? = null): Flow<PlcValue>

    /**
     * A flow of all attribute updates received (TagAddress, AttributeName, Value).
     */
    val attributeUpdates: Flow<Triple<String, String, String>>

    /**
     * Reads updates for a specific attribute of a tag (e.g., "label", "color").
     */
    fun observeAttribute(tagAddress: String, attribute: String): Flow<String>
    
    /**
     * Writes a value to a specific tag or memory address.
     * @param shouldRetain If true, the backend (e.g., MQTT broker) should store the last value.
     * @return Result.success if the write was acknowledged, Result.failure otherwise.
     */
    suspend fun writeTag(tagAddress: String, value: PlcValue, shouldRetain: Boolean = true): Result<Unit>
}

enum class ConnectionState {
    DISCONNECTED, CONNECTING, RECONNECTING, CONNECTED, ERROR
}

sealed class PlcValue {
    data class IntValue(val value: Int) : PlcValue()
    data class FloatValue(val value: Float) : PlcValue()
    data class BooleanValue(val value: Boolean) : PlcValue()
    data class StringValue(val value: String) : PlcValue()
}
