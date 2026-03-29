package com.example.hmi.protocol

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class MqttSubscriptionSharingTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // A simplified version of MqttPlcCommunicator that tracks internal subscription calls
    // without needing a real or mocked HiveMQ client.
    private class TestableMqttCommunicator(val scope: CoroutineScope) : PlcCommunicator {
        var subscribeCount = 0
        var unsubscribeCount = 0
        private val tagFlows = mutableMapOf<Pair<String, String?>, Flow<PlcValue>>()

        override val connectionState = kotlinx.coroutines.flow.MutableStateFlow(ConnectionState.CONNECTED)
        override val attributeUpdates = kotlinx.coroutines.flow.MutableSharedFlow<Triple<String, String, String>>()

        override suspend fun connect(profile: PlcConnectionProfile) = Result.success(Unit)
        override suspend fun disconnect() {}
        override suspend fun writeTag(tagAddress: String, value: PlcValue, shouldRetain: Boolean) = Result.success(Unit)
        override fun observeAttribute(tagAddress: String, attribute: String) = kotlinx.coroutines.flow.emptyFlow<String>()

        override fun observeTag(tagAddress: String, jsonPath: String?): Flow<PlcValue> {
            val key = tagAddress to jsonPath
            return synchronized(tagFlows) {
                tagFlows.getOrPut(key) {
                    callbackFlow<PlcValue> {
                        subscribeCount++
                        awaitClose {
                            unsubscribeCount++
                        }
                    }.shareIn(
                        scope = scope,
                        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                        replay = 1
                    )
                }
            }
        }
    }

    private lateinit var communicator: TestableMqttCommunicator

    @Before
    fun setup() {
        communicator = TestableMqttCommunicator(testScope)
    }

    @Test
    fun `multiple observers share a single subscription`() = runTest(testDispatcher) {
        val topic = "test/tag"
        
        // First observer
        val job1 = communicator.observeTag(topic).launchIn(testScope)
        assertEquals(1, communicator.subscribeCount)

        // Second observer
        val job2 = communicator.observeTag(topic).launchIn(testScope)
        assertEquals(1, communicator.subscribeCount) // Still 1

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `unsubscribes only after grace period when all observers are gone`() = runTest(testDispatcher) {
        val topic = "test/tag"
        
        // Start observation
        val job = communicator.observeTag(topic).launchIn(testScope)
        assertEquals(1, communicator.subscribeCount)

        // Cancel observation
        job.cancel()

        // Should NOT unsubscribe immediately (grace period 5000ms)
        assertEquals(0, communicator.unsubscribeCount)

        // Advance time beyond grace period
        testScheduler.advanceTimeBy(6000)
        
        // Now it should unsubscribe
        assertEquals(1, communicator.unsubscribeCount)
    }
}
