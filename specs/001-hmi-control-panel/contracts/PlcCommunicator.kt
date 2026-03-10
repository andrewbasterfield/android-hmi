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
     * Connects to the PLC at the given IP and port. 
     * @return Result.success if connected, Result.failure on error.
     */
    suspend fun connect(ipAddress: String, port: Int): Result<Unit>
    
    /** Disconnects from the currently connected PLC. */
    suspend fun disconnect()
    
    /**
     * Reads a continuous stream of updates for a specific tag or memory address.
     * @param tagAddress The address string (e.g., "40001" for Modbus, "ns=2;s=MyTag" for OPC UA)
     */
    fun observeTag(tagAddress: String): Flow<PlcValue>
    
    /**
     * Writes a value to a specific tag or memory address.
     * @return Result.success if the write was acknowledged, Result.failure otherwise.
     */
    suspend fun writeTag(tagAddress: String, value: PlcValue): Result<Unit>
}

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, ERROR
}

sealed class PlcValue {
    data class IntValue(val value: Int) : PlcValue()
    data class FloatValue(val value: Float) : PlcValue()
    data class BooleanValue(val value: Boolean) : PlcValue()
}
