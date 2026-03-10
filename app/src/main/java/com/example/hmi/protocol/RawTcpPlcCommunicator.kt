package com.example.hmi.protocol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawTcpPlcCommunicator @Inject constructor() : PlcCommunicator {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override suspend fun connect(ipAddress: String, port: Int): Result<Unit> = withContext(Dispatchers.IO) {
        _connectionState.value = ConnectionState.CONNECTING
        try {
            socket = Socket(ipAddress, port)
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
            _connectionState.value = ConnectionState.CONNECTED
            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            Result.failure(e)
        }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore close exceptions
        } finally {
            socket = null
            inputStream = null
            outputStream = null
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    override fun observeTag(tagAddress: String): Flow<PlcValue> = flow {
        // Mock implementation for observation since this is raw TCP and no actual protocol is defined
        // We will just emit a dummy value for now to satisfy the interface.
        emit(PlcValue.FloatValue(0f))
    }

    override suspend fun writeTag(tagAddress: String, value: PlcValue): Result<Unit> = withContext(Dispatchers.IO) {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return@withContext Result.failure(IllegalStateException("Not connected"))
        }

        try {
            val message = "$tagAddress:${
                when (value) {
                    is PlcValue.IntValue -> value.value
                    is PlcValue.FloatValue -> value.value
                    is PlcValue.BooleanValue -> value.value
                }
            }\n"
            outputStream?.write(message.toByteArray())
            outputStream?.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            disconnect()
            Result.failure(e)
        }
    }
}
