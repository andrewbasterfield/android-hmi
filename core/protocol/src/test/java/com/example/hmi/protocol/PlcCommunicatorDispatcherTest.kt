package com.example.hmi.protocol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PlcCommunicatorDispatcherTest {

    private lateinit var rawTcpCommunicator: RawTcpPlcCommunicator
    private lateinit var mqttCommunicator: MqttPlcCommunicator
    private lateinit var dispatcher: PlcCommunicatorDispatcher

    private val rawTcpState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val mqttState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        rawTcpCommunicator = mock()
        mqttCommunicator = mock()
        
        whenever(rawTcpCommunicator.connectionState).thenReturn(rawTcpState)
        whenever(mqttCommunicator.connectionState).thenReturn(mqttState)
        
        dispatcher = PlcCommunicatorDispatcher(rawTcpCommunicator, mqttCommunicator)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `connect with RAW_TCP delegates to rawTcpCommunicator`() = runTest {
        val profile = PlcConnectionProfile(protocol = Protocol.RAW_TCP)
        whenever(rawTcpCommunicator.connect(profile)).thenReturn(Result.success(Unit))

        val result = dispatcher.connect(profile)

        assertEquals(Result.success(Unit), result)
        verify(rawTcpCommunicator).connect(profile)
        
        rawTcpState.value = ConnectionState.CONNECTED
        val state = dispatcher.connectionState.first { it == ConnectionState.CONNECTED }
        assertEquals(ConnectionState.CONNECTED, state)
    }

    @Test
    fun `connect with MQTT delegates to mqttCommunicator`() = runTest {
        val profile = PlcConnectionProfile(protocol = Protocol.MQTT)
        whenever(mqttCommunicator.connect(profile)).thenReturn(Result.success(Unit))

        val result = dispatcher.connect(profile)

        assertEquals(Result.success(Unit), result)
        verify(mqttCommunicator).connect(profile)
        
        mqttState.value = ConnectionState.CONNECTED
        val state = dispatcher.connectionState.first { it == ConnectionState.CONNECTED }
        assertEquals(ConnectionState.CONNECTED, state)
    }

    @Test
    fun `disconnect delegates to active communicator`() = runTest {
        val profile = PlcConnectionProfile(protocol = Protocol.MQTT)
        whenever(mqttCommunicator.connect(profile)).thenReturn(Result.success(Unit))
        dispatcher.connect(profile)

        dispatcher.disconnect()

        verify(mqttCommunicator).disconnect()
        val state = dispatcher.connectionState.first { it == ConnectionState.DISCONNECTED }
        assertEquals(ConnectionState.DISCONNECTED, state)
    }
}
