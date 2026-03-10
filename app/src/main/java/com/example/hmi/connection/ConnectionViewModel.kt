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
    
    private val _hasAttemptedAutoConnect = MutableStateFlow(false)
    val hasAttemptedAutoConnect = _hasAttemptedAutoConnect.asStateFlow()
    
    private val _wasUnexpectedDisconnect = MutableStateFlow(false)
    val wasUnexpectedDisconnect = _wasUnexpectedDisconnect.asStateFlow()

    init {
        viewModelScope.launch {
            connectionState.collect { state ->
                // If we transition to DISCONNECTED but we didn't explicitly call disconnect() 
                // (or if we hit an ERROR), flag it as unexpected so the UI can show the message.
                if (state == ConnectionState.ERROR) {
                    _wasUnexpectedDisconnect.value = true
                }
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

    fun disconnect() {
        _wasUnexpectedDisconnect.value = false
        viewModelScope.launch {
            plcCommunicator.disconnect()
        }
    }
}
