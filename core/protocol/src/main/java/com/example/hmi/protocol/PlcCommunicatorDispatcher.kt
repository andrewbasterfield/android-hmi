package com.example.hmi.protocol

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PlcCommunicatorDispatcher @Inject constructor(
    private val rawTcpCommunicator: RawTcpPlcCommunicator,
    private val mqttCommunicator: MqttPlcCommunicator
) : PlcCommunicator {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _activeProtocol = MutableStateFlow<Protocol?>(null)

    private fun getActiveCommunicator(protocol: Protocol? = _activeProtocol.value): PlcCommunicator? = when (protocol) {
        Protocol.RAW_TCP -> rawTcpCommunicator
        Protocol.MQTT -> mqttCommunicator
        else -> null
    }

    override val connectionState: StateFlow<ConnectionState> = _activeProtocol.flatMapLatest { protocol ->
        getActiveCommunicator(protocol)?.connectionState ?: MutableStateFlow(ConnectionState.DISCONNECTED)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = ConnectionState.DISCONNECTED
    )

    override val attributeUpdates: Flow<Triple<String, String, String>> = _activeProtocol.flatMapLatest { protocol ->
        getActiveCommunicator(protocol)?.attributeUpdates ?: kotlinx.coroutines.flow.emptyFlow()
    }

    override suspend fun connect(profile: PlcConnectionProfile): Result<Unit> {
        // Disconnect existing if protocol changes or if already connected
        if (_activeProtocol.value != null) {
            disconnect()
        }
        
        _activeProtocol.value = profile.protocol
        val communicator = getActiveCommunicator(profile.protocol)
            ?: return Result.failure(IllegalArgumentException("Unsupported protocol: ${profile.protocol}"))
            
        return communicator.connect(profile)
    }

    override suspend fun disconnect() {
        getActiveCommunicator()?.disconnect()
        _activeProtocol.value = null
    }

    override fun observeTag(tagAddress: String): Flow<PlcValue> {
        return _activeProtocol.flatMapLatest { protocol ->
            getActiveCommunicator(protocol)?.observeTag(tagAddress) ?: kotlinx.coroutines.flow.emptyFlow()
        }
    }

    override fun observeAttribute(tagAddress: String, attribute: String): Flow<String> {
        return _activeProtocol.flatMapLatest { protocol ->
            getActiveCommunicator(protocol)?.observeAttribute(tagAddress, attribute) ?: kotlinx.coroutines.flow.emptyFlow()
        }
    }

    override suspend fun writeTag(tagAddress: String, value: PlcValue, shouldRetain: Boolean): Result<Unit> {
        return getActiveCommunicator()?.writeTag(tagAddress, value, shouldRetain) 
            ?: Result.failure(IllegalStateException("No active connection"))
    }
}
