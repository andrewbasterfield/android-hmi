package com.example.hmi.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.ConfigTransferManager
import com.example.hmi.data.DashboardRepository
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcConnectionProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository,
    private val transferManager: ConfigTransferManager,
    private val json: Json
) : ViewModel() {

    val connectionState = plcCommunicator.connectionState

    val transferEvents = transferManager.events

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

    val savedProfiles = repository.savedProfilesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    private val _hasAttemptedAutoConnect = MutableStateFlow(false)
    val hasAttemptedAutoConnect = _hasAttemptedAutoConnect.asStateFlow()
    
    private val _wasUnexpectedDisconnect = MutableStateFlow(false)
    val wasUnexpectedDisconnect = _wasUnexpectedDisconnect.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String?>(replay = 1)
    val errorMessage = _errorMessage.asSharedFlow()

    private var isManuallyDisconnecting = false

    init {
        viewModelScope.launch {
            var previousState = ConnectionState.DISCONNECTED
            connectionState.collect { state ->
                // If we transition to ERROR or DISCONNECTED *from* a CONNECTED state,
                // we consider it an unexpected drop, unless we are intentionally disconnecting.
                if (previousState == ConnectionState.CONNECTED && 
                   (state == ConnectionState.ERROR || state == ConnectionState.DISCONNECTED) &&
                   !isManuallyDisconnecting) {
                    _wasUnexpectedDisconnect.value = true
                }
                
                if (state == ConnectionState.CONNECTED) {
                    isManuallyDisconnecting = false
                }
                
                previousState = state
            }
        }
    }

    fun attemptAutoConnect(profile: PlcConnectionProfile) {
        if (!_hasAttemptedAutoConnect.value) {
            _hasAttemptedAutoConnect.value = true
            connect(profile)
        }
    }

    fun connect(profile: PlcConnectionProfile) {
        _wasUnexpectedDisconnect.value = false
        _errorMessage.tryEmit(null)
        viewModelScope.launch {
            repository.saveConnectionProfile(profile)
            val result = plcCommunicator.connect(profile)
            result.onFailure { error ->
                _errorMessage.emit(formatErrorMessage(error))
            }
        }
    }

    fun connectToDemoServer() {
        _wasUnexpectedDisconnect.value = false
        _errorMessage.tryEmit(null)
        viewModelScope.launch {
            // Demo server uses RAW_TCP on 127.0.0.1:9999
            val demoProfile = PlcConnectionProfile(
                name = "Local Demo Server",
                ipAddress = DEMO_SERVER_IP,
                port = DEMO_SERVER_PORT
            )
            val result = plcCommunicator.connect(demoProfile)
            result.onFailure { error ->
                _errorMessage.emit(formatErrorMessage(error))
            }
        }
    }

    fun disconnect() {
        _wasUnexpectedDisconnect.value = false
        isManuallyDisconnecting = true
        viewModelScope.launch {
            plcCommunicator.disconnect()
        }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveKeepScreenOn(enabled)
        }
    }

    fun saveProfile(profile: PlcConnectionProfile) {
        viewModelScope.launch {
            repository.saveToSavedProfiles(profile)
        }
    }

    fun deleteProfile(profileName: String) {
        viewModelScope.launch {
            repository.deleteFromSavedProfiles(profileName)
        }
    }

    fun exportProfiles(uri: android.net.Uri) {
        viewModelScope.launch {
            transferManager.exportProfiles(uri)
        }
    }

    fun importProfiles(uri: android.net.Uri) {
        viewModelScope.launch {
            transferManager.importProfiles(uri)
        }
    }

    fun shareProfiles(context: android.content.Context) {
        viewModelScope.launch {
            val profiles = repository.savedProfilesFlow.first()
            val backup = com.example.hmi.data.FullBackupPackage(profiles = profiles)
            val jsonStr = json.encodeToString(backup)
            transferManager.shareConfig(context, jsonStr, "connection_profiles.json")
        }
    }

    private fun formatErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("timeout", ignoreCase = true) == true ||
            error.message?.contains("timed out", ignoreCase = true) == true ->
                "Connection timed out. Check that the server is reachable."

            error.message?.contains("refused", ignoreCase = true) == true ->
                "Connection refused. Check that the server is running on the specified port."

            error.message?.contains("unreachable", ignoreCase = true) == true ||
            error.message?.contains("no route", ignoreCase = true) == true ->
                "Host unreachable. Check the IP address and network connection."

            error.message?.contains("auth", ignoreCase = true) == true ||
            error.message?.contains("credential", ignoreCase = true) == true ||
            error.message?.contains("unauthorized", ignoreCase = true) == true ->
                "Authentication failed. Check your username and password."

            error.message?.contains("resolve", ignoreCase = true) == true ||
            error.message?.contains("unknown host", ignoreCase = true) == true ->
                "Could not resolve hostname. Check the broker address."

            else -> error.message ?: "Connection failed"
        }
    }

    companion object {
        const val DEMO_SERVER_IP = "127.0.0.1"
        const val DEMO_SERVER_PORT = 9999
    }
}
