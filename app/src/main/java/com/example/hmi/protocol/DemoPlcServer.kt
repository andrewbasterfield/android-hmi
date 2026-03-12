package com.example.hmi.protocol

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
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

    fun start(port: Int = 9999) {
        if (isRunning) return
        isRunning = true
        
        scope.launch {
            try {
                serverSocket = ServerSocket(port)
                while (isActive) {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                }
            } catch (e: Exception) {
                // Server stopped
            }
        }

        // Start a background job to periodically update "simulated" tags
        scope.launch {
            while (isActive) {
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

                // Send initial state
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
                // Connection closed
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

    private fun simulateTagChanges() {
        // Randomly update any tags that look like they might be sensors
        tagValues.keys.filter { it.contains("temp", ignoreCase = true) || it.contains("level", ignoreCase = true) }
            .forEach { tag ->
                val current = tagValues[tag]?.toFloatOrNull() ?: 50f
                val newVal = current + (Random.nextFloat() * 2 - 1) // Drift by +/- 1
                val formatted = "%.2f".format(newVal)
                tagValues[tag] = formatted
                broadcast("$tag:$formatted")
            }
    }

    fun stop() {
        isRunning = false
        serverSocket?.close()
        activeConnections.forEach { it.close() }
        activeConnections.clear()
    }
}
