package com.example.hmi.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.PlcConnectionProfile
import com.example.hmi.protocol.PlcCommunicator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
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
    
    private var hasAttemptedAutoConnect = false

    fun attemptAutoConnect(profile: PlcConnectionProfile) {
        if (!hasAttemptedAutoConnect) {
            hasAttemptedAutoConnect = true
            connect(profile.ipAddress, profile.port)
        }
    }

    fun connect(ipAddress: String, port: Int) {
        viewModelScope.launch {
            // Save the parameters immediately so they persist even on failure
            repository.saveConnectionProfile(PlcConnectionProfile(ipAddress = ipAddress, port = port))
            plcCommunicator.connect(ipAddress, port)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            plcCommunicator.disconnect()
        }
    }
}
