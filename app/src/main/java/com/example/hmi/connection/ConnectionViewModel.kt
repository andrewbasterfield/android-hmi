package com.example.hmi.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.PlcConnectionProfile
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.PlcCommunicator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository
) : ViewModel() {

    val connectionState = plcCommunicator.connectionState

    // Expose the persisted profile to the UI, defaulting to a new instance until loaded
    val connectionProfile = repository.connectionProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val keepScreenOn = repository.keepScreenOnFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    private val _hasAttemptedAutoConnect = MutableStateFlow(false)
    val hasAttemptedAutoConnect = _hasAttemptedAutoConnect.asStateFlow()
    
    private val _wasUnexpectedDisconnect = MutableStateFlow(false)
    val wasUnexpectedDisconnect = _wasUnexpectedDisconnect.asStateFlow()

    init {
        viewModelScope.launch {
            var previousState = ConnectionState.DISCONNECTED
            connectionState.collect { state ->
                // If we transition to ERROR or DISCONNECTED *from* a CONNECTED state,
                // we consider it an unexpected drop.
                if (previousState == ConnectionState.CONNECTED && 
                   (state == ConnectionState.ERROR || state == ConnectionState.DISCONNECTED)) {
                    _wasUnexpectedDisconnect.value = true
                }
                previousState = state
            }
        }
    }

    fun attemptAutoConnect(profile: PlcConnectionProfile) {
        if (!_hasAttemptedAutoConnect.value) {
            _hasAttemptedAutoConnect.value = true
            connect(profile.ipAddress, profile.port)
        }
    }

    fun connect(ipAddress: String, port: Int) {
        _wasUnexpectedDisconnect.value = false
        viewModelScope.launch {
            // Save the parameters immediately so they persist even on failure
            repository.saveConnectionProfile(PlcConnectionProfile(ipAddress = ipAddress, port = port))
            plcCommunicator.connect(ipAddress, port)
        }
    }

    fun connectToDemoServer() {
        _wasUnexpectedDisconnect.value = false
        viewModelScope.launch {
            // We don't necessarily want to overwrite the "last known" external IP 
            // when just playing with the demo server, but the spec says "Seamless switch".
            // For now, we connect directly without persisting to DataStore to preserve the real PLC settings.
            plcCommunicator.connect(DEMO_SERVER_IP, DEMO_SERVER_PORT)
        }
    }

    fun disconnect() {
        _wasUnexpectedDisconnect.value = false
        viewModelScope.launch {
            plcCommunicator.disconnect()
        }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveKeepScreenOn(enabled)
        }
    }

    companion object {
        const val DEMO_SERVER_IP = "127.0.0.1"
        const val DEMO_SERVER_PORT = 9999
    }
}
