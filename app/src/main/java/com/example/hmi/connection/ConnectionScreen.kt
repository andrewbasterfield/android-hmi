package com.example.hmi.connection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.MqttSettings
import com.example.hmi.protocol.PlcConnectionProfile
import com.example.hmi.protocol.Protocol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel = hiltViewModel(),
    onConnected: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val connectionProfile by viewModel.connectionProfile.collectAsState()
    val wasUnexpectedDisconnect by viewModel.wasUnexpectedDisconnect.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    
    var name by remember { mutableStateOf("My Connection") }
    var ipAddress by remember { mutableStateOf("192.168.1.100") }
    var port by remember { mutableStateOf("9999") }
    var protocol by remember { mutableStateOf(Protocol.RAW_TCP) }
    
    // MQTT specific
    var clientId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var topicPrefix by remember { mutableStateOf("") }
    
    var loaded by remember { mutableStateOf(false) }

    // Update local state when profile loads from DataStore
    LaunchedEffect(connectionProfile) {
        if (connectionProfile != null && !loaded) {
            name = connectionProfile!!.name
            ipAddress = connectionProfile!!.ipAddress
            port = connectionProfile!!.port.toString()
            protocol = connectionProfile!!.protocol
            
            connectionProfile!!.mqttSettings?.let {
                clientId = it.clientId
                username = it.username ?: ""
                password = it.password ?: ""
                topicPrefix = it.rootTopicPrefix ?: ""
            }
            
            loaded = true
        }
    }

    LaunchedEffect(connectionState) {
        if (connectionState == ConnectionState.CONNECTED) {
            kotlinx.coroutines.delay(1000)
            onConnected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Connection Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Profile Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Protocol Selector
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = protocol.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Protocol") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Protocol.values().forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.name) },
                        onClick = {
                            protocol = p
                            expanded = false
                            if (p == Protocol.MQTT && port == "9999") port = "1883"
                            if (p == Protocol.RAW_TCP && port == "1883") port = "9999"
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text(if (protocol == Protocol.MQTT) "Broker Host" else "IP Address") },
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
        
        if (protocol == Protocol.MQTT) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("MQTT Settings", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Optional)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = topicPrefix,
                onValueChange = { topicPrefix = it },
                label = { Text("Root Topic Prefix (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                val portInt = port.toIntOrNull() ?: (if (protocol == Protocol.MQTT) 1883 else 9999)
                val mqttSettings = if (protocol == Protocol.MQTT) {
                    MqttSettings(
                        clientId = if (clientId.isBlank()) "hmi-client-" + java.util.UUID.randomUUID().toString().take(8) else clientId,
                        username = if (username.isBlank()) null else username,
                        password = if (password.isBlank()) null else password,
                        rootTopicPrefix = if (topicPrefix.isBlank()) null else topicPrefix
                    )
                } else null
                
                val profile = PlcConnectionProfile(
                    name = name,
                    ipAddress = ipAddress,
                    port = portInt,
                    protocol = protocol,
                    mqttSettings = mqttSettings
                )
                viewModel.connect(profile)
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

        if (connectionState == ConnectionState.ERROR && errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else if (connectionState == ConnectionState.ERROR) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Connection failed", color = MaterialTheme.colorScheme.error)
        } else if (wasUnexpectedDisconnect) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Disconnected!", color = MaterialTheme.colorScheme.error)
        }
    }
}
