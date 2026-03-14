package com.example.hmi.protocol

import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class DemoPlcServerTest {

    private lateinit var server: DemoPlcServer
    private val testPort = 9998

    @Before
    fun setup() {
        server = DemoPlcServer()
        server.start(testPort)
    }

    @After
    fun teardown() {
        server.stop()
    }

    @Test
    fun `server should send initial state to a new client`() = runBlocking {
        withTimeoutOrNull(5000) {
            val socket = Socket("127.0.0.1", testPort)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            
            val initialLines = mutableListOf<String>()
            // Expecting at least the initialized tags
            repeat(4) {
                val line = reader.readLine()
                if (line != null) {
                    initialLines.add(line)
                }
            }
            
            assertTrue(initialLines.any { it.contains("SIM_TEMP") })
            assertTrue(initialLines.any { it.contains("SIM_PRESSURE") })
            assertTrue(initialLines.any { it.contains("SIM_STATUS") })
            assertTrue(initialLines.any { it.contains("USER_LEVEL") })
            
            socket.close()
        } ?: throw AssertionError("Timeout waiting for server initial state")
    }

    @Test
    fun `server should broadcast updates to all clients`() = runBlocking {
        withTimeoutOrNull(5000) {
            val client1 = Socket("127.0.0.1", testPort)
            val client2 = Socket("127.0.0.1", testPort)
            
            val reader1 = BufferedReader(InputStreamReader(client1.getInputStream()))
            val writer1 = PrintWriter(client1.getOutputStream(), true)
            val reader2 = BufferedReader(InputStreamReader(client2.getInputStream()))

            // Drain initial state for both
            repeat(4) { reader1.readLine() }
            repeat(4) { reader2.readLine() }

            // Client 1 sends an update
            writer1.println("TEST_TAG:123.45")

            // Client 2 should receive the update
            val receivedBy2 = reader2.readLine()
            assertTrue(receivedBy2 == "TEST_TAG:123.45")
            
            // Client 1 should also receive the broadcast (echo)
            val receivedBy1 = reader1.readLine()
            assertTrue(receivedBy1 == "TEST_TAG:123.45")

            client1.close()
            client2.close()
        } ?: throw AssertionError("Timeout waiting for server broadcast")
    }
}
