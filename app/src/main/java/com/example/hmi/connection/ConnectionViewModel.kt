package com.example.hmi.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.protocol.PlcCommunicator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator
) : ViewModel() {

    val connectionState = plcCommunicator.connectionState

    fun connect(ipAddress: String, port: Int) {
        viewModelScope.launch {
            plcCommunicator.connect(ipAddress, port)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            plcCommunicator.disconnect()
        }
    }
}
