package com.example.hmi.protocol

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RawTcpPlcCommunicator @Inject constructor() : PlcCommunicator {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var socket: Socket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _tagUpdates = MutableSharedFlow<Pair<String, PlcValue>>(extraBufferCapacity = 64)

    override suspend fun connect(ipAddress: String, port: Int): Result<Unit> = withContext(Dispatchers.IO) {
        _connectionState.value = ConnectionState.CONNECTING
        Log.d("RawTcpPlcCommunicator", "Connecting to $ipAddress:$port")
        try {
            val newSocket = Socket()
            // Set a 5-second timeout for the connection attempt
            newSocket.connect(InetSocketAddress(ipAddress, port), 5000)
            socket = newSocket
            
            _connectionState.value = ConnectionState.CONNECTED
            Log.d("RawTcpPlcCommunicator", "Connected to $ipAddress:$port")
            
            // Start background listening loop
            startListening(newSocket)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RawTcpPlcCommunicator", "Connection failed: ${e.message}")
            _connectionState.value = ConnectionState.ERROR
            Result.failure(e)
        }
    }

    private fun startListening(socket: Socket) {
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (isActive && !socket.isClosed) {
                    val line = reader.readLine() ?: break
                    parseLine(line)
                }
            } catch (e: Exception) {
                // Only set ERROR if we didn't intentionally disconnect
                if (socket == this@RawTcpPlcCommunicator.socket) {
                    Log.e("RawTcpPlcCommunicator", "Read error: ${e.message}")
                    _connectionState.value = ConnectionState.ERROR
                }
            } finally {
                // Ensure we clean up if the loop ends (e.g., server closed connection)
                if (socket == this@RawTcpPlcCommunicator.socket) {
                    Log.d("RawTcpPlcCommunicator", "Listening loop ended")
                    disconnect()
                }
            }
        }
    }

    private suspend fun parseLine(line: String) {
        try {
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                val tagName = parts[0].trim()
                val valueStr = parts[1].trim()
                
                val value = when {
                    valueStr.equals("true", ignoreCase = true) -> PlcValue.BooleanValue(true)
                    valueStr.equals("false", ignoreCase = true) -> PlcValue.BooleanValue(false)
                    valueStr.contains(".") -> PlcValue.FloatValue(valueStr.toFloat())
                    else -> {
                        val intVal = valueStr.toIntOrNull()
                        if (intVal != null) PlcValue.IntValue(intVal) 
                        else PlcValue.FloatValue(valueStr.toFloat())
                    }
                }
                
                _tagUpdates.emit(tagName to value)
            }
        } catch (e: Exception) {
            // Ignore malformed
        }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        val currentSocket = socket
        socket = null // Set to null first to prevent loop from re-triggering ERROR
        Log.d("RawTcpPlcCommunicator", "Disconnecting")
        try {
            currentSocket?.close()
        } catch (e: Exception) {
            // Ignore
        } finally {
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    override fun observeTag(tagAddress: String): Flow<PlcValue> {
        return _tagUpdates
            .filter { it.first == tagAddress }
            .map { it.second }
    }

    override suspend fun writeTag(tagAddress: String, value: PlcValue): Result<Unit> = withContext(Dispatchers.IO) {
        val currentSocket = socket
        if (currentSocket == null || !currentSocket.isConnected) {
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
            currentSocket.getOutputStream().write(message.toByteArray())
            currentSocket.getOutputStream().flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RawTcpPlcCommunicator", "Write error: ${e.message}")
            _connectionState.value = ConnectionState.ERROR
            disconnect()
            Result.failure(e)
        }
    }
}
