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

// Built-in read-only profiles
private val BUILT_IN_PROFILES = listOf(
    PlcConnectionProfile(
        name = "Local Demo Server",
        ipAddress = "127.0.0.1",
        port = 9999,
        protocol = Protocol.RAW_TCP
    )
)

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

    // Track the originally loaded profile to detect changes
    var loadedProfile by remember { mutableStateOf<PlcConnectionProfile?>(null) }

    // Check if current profile is a built-in read-only profile
    val isReadOnlyProfile = BUILT_IN_PROFILES.any { it.name == name }

    // Dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOverwriteDialog by remember { mutableStateOf(false) }
    var pendingProfile by remember { mutableStateOf<PlcConnectionProfile?>(null) }

    // Helper to build current profile from form state
    fun buildCurrentProfile(): PlcConnectionProfile {
        val portInt = port.toIntOrNull() ?: (if (protocol == Protocol.MQTT) 1883 else 9999)
        val mqttSettings = if (protocol == Protocol.MQTT) {
            MqttSettings(
                clientId = if (clientId.isBlank()) "hmi-client-" + java.util.UUID.randomUUID().toString().take(8) else clientId,
                username = if (username.isBlank()) null else username,
                password = if (password.isBlank()) null else password,
                rootTopicPrefix = if (topicPrefix.isBlank()) null else topicPrefix
            )
        } else null
        return PlcConnectionProfile(
            name = name,
            ipAddress = ipAddress,
            port = portInt,
            protocol = protocol,
            mqttSettings = mqttSettings
        )
    }

    // Check if profile has changed from loaded state
    fun hasProfileChanged(): Boolean {
        val original = loadedProfile ?: return true // No loaded profile means it's "new"
        if (name != original.name) return true
        if (ipAddress != original.ipAddress) return true
        if (port != original.port.toString()) return true
        if (protocol != original.protocol) return true
        if (protocol == Protocol.MQTT) {
            val originalMqtt = original.mqttSettings
            if (originalMqtt == null) return clientId.isNotBlank() || username.isNotBlank() || password.isNotBlank() || topicPrefix.isNotBlank()
            if (clientId != originalMqtt.clientId) return true
            if (username != (originalMqtt.username ?: "")) return true
            if (password != (originalMqtt.password ?: "")) return true
            if (topicPrefix != (originalMqtt.rootTopicPrefix ?: "")) return true
        }
        return false
    }

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

            // Track as loaded profile so Save is disabled until changes are made
            loadedProfile = connectionProfile

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

        // Saved profiles dropdown (user saved + built-in at the end)
        val savedProfiles by viewModel.savedProfiles.collectAsState()
        val builtInNames = BUILT_IN_PROFILES.map { it.name }.toSet()
        val filteredSavedProfiles = savedProfiles.filter { it.name !in builtInNames }
        val allProfiles = filteredSavedProfiles + BUILT_IN_PROFILES
        var loadDropdownExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = loadDropdownExpanded,
            onExpandedChange = { loadDropdownExpanded = !loadDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "Load profile...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Load") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loadDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = loadDropdownExpanded,
                onDismissRequest = { loadDropdownExpanded = false }
            ) {
                allProfiles.forEach { profile ->
                    val isBuiltIn = BUILT_IN_PROFILES.any { it.name == profile.name }
                    DropdownMenuItem(
                        text = {
                            Text(if (isBuiltIn) "${profile.name} (built-in)" else profile.name)
                        },
                        onClick = {
                            // Populate form fields
                            name = profile.name
                            ipAddress = profile.ipAddress
                            port = profile.port.toString()
                            protocol = profile.protocol
                            profile.mqttSettings?.let {
                                clientId = it.clientId
                                username = it.username ?: ""
                                password = it.password ?: ""
                                topicPrefix = it.rootTopicPrefix ?: ""
                            } ?: run {
                                clientId = ""
                                username = ""
                                password = ""
                                topicPrefix = ""
                            }
                            loadedProfile = profile
                            loadDropdownExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Profile Name") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    val profile = buildCurrentProfile()
                    val existingProfile = savedProfiles.find { it.name == name }
                    if (existingProfile != null) {
                        // Profile exists - show overwrite confirmation
                        pendingProfile = profile
                        showOverwriteDialog = true
                    } else {
                        // New profile - save directly
                        viewModel.saveProfile(profile)
                        loadedProfile = profile
                    }
                },
                enabled = name.isNotBlank() && hasProfileChanged() && !isReadOnlyProfile
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                enabled = name.isNotBlank() && savedProfiles.any { it.name == name } && !isReadOnlyProfile
            ) {
                Text("Delete")
            }
        }
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

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete \"$name\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProfile(name)
                        loadedProfile = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Overwrite confirmation dialog
    if (showOverwriteDialog) {
        AlertDialog(
            onDismissRequest = { showOverwriteDialog = false },
            title = { Text("Overwrite Profile") },
            text = { Text("A profile named \"$name\" already exists. Overwrite it?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingProfile?.let {
                            viewModel.saveProfile(it)
                            loadedProfile = it
                        }
                        pendingProfile = null
                        showOverwriteDialog = false
                    }
                ) {
                    Text("Overwrite")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingProfile = null
                    showOverwriteDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
