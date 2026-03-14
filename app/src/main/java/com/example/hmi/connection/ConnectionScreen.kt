package com.example.hmi.connection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.protocol.ConnectionState

@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel = hiltViewModel(),
    onConnected: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val connectionProfile by viewModel.connectionProfile.collectAsState()
    val wasUnexpectedDisconnect by viewModel.wasUnexpectedDisconnect.collectAsState()
    
    var ipAddress by remember { mutableStateOf("192.168.1.100") }
    var port by remember { mutableStateOf("9999") }
    var loaded by remember { mutableStateOf(false) }

    // Update local state when profile loads from DataStore
    LaunchedEffect(connectionProfile) {
        if (connectionProfile != null && !loaded) {
            ipAddress = connectionProfile!!.ipAddress
            port = connectionProfile!!.port.toString()
            loaded = true
            viewModel.attemptAutoConnect(connectionProfile!!)
        }
    }

    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.CONNECTED) {
            onConnected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("PLC Connection Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("IP Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Port") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        val keepScreenOn by viewModel.keepScreenOn.collectAsState()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = keepScreenOn,
                onCheckedChange = { viewModel.setKeepScreenOn(it) }
            )
            Text("Keep screen on while dashboard is active")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val portInt = port.toIntOrNull() ?: 9999
                viewModel.connect(ipAddress, portInt)
            },
            enabled = connectionState != ConnectionState.CONNECTING && connectionState != ConnectionState.CONNECTED,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (connectionState == ConnectionState.CONNECTING) "Connecting..." else "Connect")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                viewModel.connectToDemoServer()
            },
            enabled = connectionState != ConnectionState.CONNECTING && connectionState != ConnectionState.CONNECTED,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to Local Demo Server")
        }

        if (connectionState == ConnectionState.ERROR) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Connection Error!", color = MaterialTheme.colorScheme.error)
        } else if (wasUnexpectedDisconnect) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Disconnected!", color = MaterialTheme.colorScheme.error)
        }
    }
}
