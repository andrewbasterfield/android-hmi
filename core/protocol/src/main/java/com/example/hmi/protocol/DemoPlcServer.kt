package com.example.hmi.protocol

import android.util.Log
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * A local TCP server that runs within the app process to provide a "demo" backend.
 * Listens on 127.0.0.1:9999 and follows the TAG:VALUE protocol.
 */
@Singleton
class DemoPlcServer @Inject constructor() {
    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeConnections = mutableListOf<Socket>()
    private var isRunning = false

    // State of tags in the demo server
    private val tagValues = mutableMapOf<String, String>()

    init {
        // Initialize with core simulated tags per spec
        tagValues["SIM_TEMP"] = "25.5"
        tagValues["SIM_PRESSURE"] = "101.3"
        tagValues["SIM_STATUS"] = "false"
        tagValues["USER_LEVEL"] = "50.0"
    }

    fun start(port: Int = 9999) {
        if (isRunning) return
        isRunning = true
        Log.d("DemoPlcServer", "Starting demo server on port $port")
        
        scope.launch {
            try {
                serverSocket = ServerSocket(port)
                Log.d("DemoPlcServer", "Server socket created on port $port")
                while (isRunning && isActive) {
                    val client = serverSocket?.accept() ?: break
                    Log.d("DemoPlcServer", "Accepted connection from ${client.inetAddress}")
                    handleClient(client)
                }
            } catch (e: Exception) {
                Log.e("DemoPlcServer", "Server error: ${e.message}")
            }
        }

        // Start a background job to periodically update "simulated" tags
        scope.launch {
            while (isRunning && isActive) {
                delay(1000)
                simulateTagChanges()
            }
        }
    }

    private fun handleClient(socket: Socket) {
        activeConnections.add(socket)
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = PrintWriter(socket.getOutputStream(), true)

                // Send initial state to the new client
                tagValues.forEach { (tag, value) ->
                    writer.println("$tag:$value")
                }

                while (isActive && !socket.isClosed) {
                    val line = reader.readLine() ?: break
                    val parts = line.split(":", limit = 2)
                    if (parts.size == 2) {
                        val tag = parts[0].trim()
                        val value = parts[1].trim()
                        tagValues[tag] = value
                        
                        // Broadcast change to ALL connected clients (including the sender)
                        broadcast("$tag:$value")
                    }
                }
            } catch (e: Exception) {
                Log.d("DemoPlcServer", "Client disconnected: ${e.message}")
            } finally {
                activeConnections.remove(socket)
                socket.close()
            }
        }
    }

    private fun broadcast(message: String) {
        activeConnections.forEach { socket ->
            try {
                val writer = PrintWriter(socket.getOutputStream(), true)
                writer.println(message)
            } catch (e: Exception) {
                // Failed to write to this client
            }
        }
    }

    private var simulationStep = 0L

    private fun simulateTagChanges() {
        simulationStep++
        
        // 1. Special handling for SIM_TEMP to ensure it hits all ranges for demo
        val tempTag = "SIM_TEMP"
        // Cycle from 20 to 100 degrees over 60 steps (1 minute)
        val phase = (simulationStep % 60) / 60f
        val tempVal = 20f + (80f * (0.5f + 0.5f * kotlin.math.sin(phase * 2.0 * kotlin.math.PI).toFloat()))
        val formattedTemp = String.format(Locale.US, "%.2f", tempVal)
        tagValues[tempTag] = formattedTemp
        broadcast("$tempTag:$formattedTemp")

        // Update colors based on the new sine-wave value
        val colorHex = when {
            tempVal < 40f -> "#0000FF" // Blue
            tempVal < 70f -> "#00FF00" // Green
            tempVal < 90f -> "#FFA500" // Orange
            else -> "#FF0000" // Red
        }
        broadcast("$tempTag.color:$colorHex")
        
        val stateLabel = when {
            tempVal < 40f -> "Cold"
            tempVal < 70f -> "Optimal"
            tempVal < 90f -> "Warning"
            else -> "CRITICAL"
        }
        broadcast("$tempTag.label:Temp ($stateLabel)")

        // 2. Drift simulation for other numeric tags
        tagValues.keys.filter { 
            it != tempTag && (
            it.contains("temp", ignoreCase = true) || 
            it.contains("pressure", ignoreCase = true) ||
            it.contains("level", ignoreCase = true))
        }.forEach { tag ->
            val current = tagValues[tag]?.toFloatOrNull() ?: 50f
            // Adjust drift range based on tag type
            val drift = if (tag.contains("pressure", ignoreCase = true)) {
                Random.nextFloat() * 0.4f - 0.2f // Small pressure drift
            } else {
                Random.nextFloat() * 2.0f - 1.0f // Normal temp/level drift
            }
            
            val newVal = (current + drift).coerceIn(0f, 1000f)
            val formatted = String.format(Locale.US, "%.2f", newVal)
            tagValues[tag] = formatted
            broadcast("$tag:$formatted")
        }

        // 3. Periodic toggle for status tags
        tagValues.keys.filter { it.contains("status", ignoreCase = true) }.forEach { tag ->
            // 10% chance to toggle every second
            if (Random.nextFloat() < 0.1f) {
                val current = tagValues[tag]?.toBooleanStrictOrNull() ?: false
                val newVal = !current
                tagValues[tag] = newVal.toString()
                broadcast("$tag:$newVal")
            }
        }
    }

    fun stop() {
        isRunning = false
        serverSocket?.close()
        activeConnections.forEach { it.close() }
        activeConnections.clear()
    }

    /**
     * Manually broadcasts a message to all connected clients.
     * Useful for testing protocol-driven UI updates.
     */
    fun sendRawMessage(message: String) {
        broadcast(message)
    }
}
