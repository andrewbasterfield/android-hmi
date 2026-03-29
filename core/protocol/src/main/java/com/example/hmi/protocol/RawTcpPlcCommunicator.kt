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
    private var heartbeatJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _tagUpdates = MutableSharedFlow<Pair<String, PlcValue>>(
        replay = 16,
        extraBufferCapacity = 64
    )

    override suspend fun connect(profile: PlcConnectionProfile): Result<Unit> = withContext(Dispatchers.IO) {
        // Close existing connection if any (BUG-015 fix)
        disconnect()

        val ipAddress = profile.ipAddress
        val port = profile.port
        _connectionState.value = ConnectionState.CONNECTING
        Log.d("RawTcpPlcCommunicator", "Connecting to $ipAddress:$port")
        val newSocket = Socket()
        try {
            // Set a 5-second timeout for the connection attempt
            newSocket.connect(InetSocketAddress(ipAddress, port), 5000)
            
            // ARCH-3.1: Detection of half-open connections
            newSocket.soTimeout = 10000 // 10s Read timeout
            newSocket.keepAlive = true
            
            socket = newSocket

            _connectionState.value = ConnectionState.CONNECTED
            Log.d("RawTcpPlcCommunicator", "Connected to $ipAddress:$port")

            // Start background listening loop
            startListening(newSocket)
            
            // Start active heartbeat probe
            startHeartbeat(newSocket)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RawTcpPlcCommunicator", "Connection failed: ${e.message}")
            // Close socket to prevent file descriptor leak on failed connection
            try { newSocket.close() } catch (_: Exception) {}
            _connectionState.value = ConnectionState.ERROR
            Result.failure(e)
        }
    }

    private fun startHeartbeat(socket: Socket) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive && !socket.isClosed) {
                delay(5000) // 5s probe interval
                try {
                    // Send a "No-Op" newline to trigger a socket error if the link is dead
                    socket.getOutputStream().write("\n".toByteArray())
                    socket.getOutputStream().flush()
                } catch (e: Exception) {
                    if (socket == this@RawTcpPlcCommunicator.socket) {
                        Log.w("RawTcpPlcCommunicator", "Heartbeat failed: ${e.message}")
                        disconnect()
                    }
                    break
                }
            }
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

    internal suspend fun parseLine(line: String) {
        try {
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                val fullTagName = parts[0].trim()
                val valueStr = parts[1].trim()
                
                // Check for attribute suffix (e.g., TAG.color)
                val dotIndex = fullTagName.lastIndexOf('.')
                if (dotIndex != -1) {
                    val tagName = fullTagName.substring(0, dotIndex)
                    val attribute = fullTagName.substring(dotIndex + 1)
                    // Emit as a specialized string update for attributes
                    _tagUpdates.emit("$tagName.$attribute" to PlcValue.StringValue(valueStr))
                } else {
                    // Standard value update
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
                    _tagUpdates.emit(fullTagName to value)
                }
            }
        } catch (e: Exception) {
            // Ignore malformed
        }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        heartbeatJob?.cancel()
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

    override val attributeUpdates: Flow<Triple<String, String, String>> = _tagUpdates
        .filter { it.first.contains('.') }
        .map { (fullKey, value) ->
            val dotIndex = fullKey.lastIndexOf('.')
            val tag = fullKey.substring(0, dotIndex)
            val attr = fullKey.substring(dotIndex + 1)
            Triple(tag, attr, (value as? PlcValue.StringValue)?.value ?: "")
        }

    override fun observeTag(tagAddress: String): Flow<PlcValue> {
        return _tagUpdates
            .filter { it.first == tagAddress }
            .map { it.second }
    }

    override fun observeAttribute(tagAddress: String, attribute: String): Flow<String> {
        return _tagUpdates
            .filter { it.first == "$tagAddress.$attribute" }
            .map { (it.second as? PlcValue.StringValue)?.value ?: "" }
    }

    override suspend fun writeTag(tagAddress: String, value: PlcValue, shouldRetain: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        val currentSocket = socket
        if (currentSocket == null || !currentSocket.isConnected) {
            return@withContext Result.failure(IllegalStateException("Not connected"))
        }

        try {
            val message = "$tagAddress:${
                when (value) {
                    is PlcValue.IntValue -> value.value
                    is PlcValue.FloatValue -> value.value.toBigDecimal().stripTrailingZeros().toPlainString()
                    is PlcValue.BooleanValue -> value.value
                    is PlcValue.StringValue -> value.value
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
