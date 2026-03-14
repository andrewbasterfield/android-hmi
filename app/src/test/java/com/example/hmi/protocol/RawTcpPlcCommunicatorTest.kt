package com.example.hmi.protocol

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RawTcpPlcCommunicatorTest {

    private lateinit var communicator: RawTcpPlcCommunicator

    @Before
    fun setup() {
        communicator = RawTcpPlcCommunicator()
    }

    @Test(timeout = 2000)
    fun `parseLine should correctly identify attribute updates`() = runBlocking {
        // Test label update
        communicator.parseLine("MOTOR_01.label:Main Pump")
        val label = communicator.observeAttribute("MOTOR_01", "label").first()
        assertEquals("Main Pump", label)

        // Test color update
        communicator.parseLine("MOTOR_01.color:#FF0000")
        val color = communicator.observeAttribute("MOTOR_01", "color").first()
        assertEquals("#FF0000", color)
    }

    @Test(timeout = 2000)
    fun `parseLine should handle dots in tag names correctly`() = runBlocking {
        // Tag name has a dot: "System.Clock"
        communicator.parseLine("System.Clock.label:Current Time")
        val label = communicator.observeAttribute("System.Clock", "label").first()
        assertEquals("Current Time", label)
    }

    @Test(timeout = 2000)
    fun `parseLine should still handle standard tag updates`() = runBlocking {
        communicator.parseLine("TANK_LEVEL:75.5")
        val value = communicator.observeTag("TANK_LEVEL").first()
        val floatValue = (value as PlcValue.FloatValue).value
        assertEquals(75.5f, floatValue, 0.01f)
    }
}
