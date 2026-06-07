package com.example.hmi.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.SystemProfile
import com.example.hmi.protocol.PlcConnectionProfile

@Composable
fun ManagementHubDrawer(
    systemProfiles: List<SystemProfile>,
    activeProfileId: String?,
    connections: List<PlcConnectionProfile>,
    layouts: List<DashboardLayout>,
    activeConnection: PlcConnectionProfile?,
    activeLayout: DashboardLayout?,
    isModified: Boolean,
    connectionState: com.example.hmi.protocol.ConnectionState,
    onProfileSelect: (SystemProfile) -> Unit,
    onProfileShare: (SystemProfile) -> Unit,
    onProfileDelete: (SystemProfile) -> Unit,
    onAddProfile: () -> Unit,
    onConnectionSelect: (PlcConnectionProfile) -> Unit,
    onAddConnection: () -> Unit,
    onImportConnections: () -> Unit,
    onLayoutSelect: (DashboardLayout) -> Unit,
    onEditLayout: (DashboardLayout) -> Unit,
    onAddLayout: () -> Unit,
    onImportLayouts: () -> Unit,
    onFullBackup: () -> Unit,
    onFullRestore: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Management Hub",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                HorizontalDivider()
            }

            // Current State Section
            item {
                SectionHeader("Active Environment")
                ActiveEnvironmentCard(
                    connection = activeConnection,
                    layout = activeLayout,
                    isModified = isModified,
                    connectionState = connectionState,
                    onEditLayout = { activeLayout?.let { onEditLayout(it) } }
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            // System Profiles
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader("System Profiles", Modifier.weight(1f))
                    IconButton(onClick = onAddProfile) {
                        Icon(Icons.Default.Add, contentDescription = "Save Current as Profile")
                    }
                }
            }
            if (systemProfiles.isEmpty()) {
                item {
                    Text(
                        "No profiles saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(systemProfiles) { profile ->
                    NavigationDrawerItem(
                        label = { Text(profile.name) },
                        selected = profile.id == activeProfileId,
                        onClick = { onProfileSelect(profile) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        badge = {
                            if (!profile.isReadOnly) {
                                Row {
                                    IconButton(onClick = { onProfileShare(profile) }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share Profile Bundle")
                                    }
                                    IconButton(onClick = { onProfileDelete(profile) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Profile", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            // Library - Connections
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader("Library: Connections", Modifier.weight(1f))
                    IconButton(onClick = onImportConnections) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Import Connections")
                    }
                    IconButton(onClick = onAddConnection) {
                        Icon(Icons.Default.Add, contentDescription = "Add Connection")
                    }
                }
            }
            if (connections.isEmpty()) {
                item {
                    Text(
                        "No connections saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(connections) { connection ->
                    NavigationDrawerItem(
                        label = { Text(connection.name) },
                        selected = connection.name == activeConnection?.name,
                        onClick = { onConnectionSelect(connection) },
                        icon = { Icon(Icons.Default.Dns, contentDescription = null) },
                        modifier = Modifier.height(56.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            // Library - Layouts
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader("Library: Layouts", Modifier.weight(1f))
                    IconButton(onClick = onImportLayouts) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Import Layout")
                    }
                    IconButton(onClick = onAddLayout) {
                        Icon(Icons.Default.Add, contentDescription = "Add Layout")
                    }
                }
            }
            if (layouts.isEmpty()) {
                item {
                    Text(
                        "No layouts saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(layouts) { layout ->
                    NavigationDrawerItem(
                        label = { Text(layout.name) },
                        selected = layout.id == activeLayout?.id,
                        onClick = { onLayoutSelect(layout) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        badge = {
                            IconButton(onClick = { onEditLayout(layout) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Layout Settings")
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            // Maintenance Section
            item {
                SectionHeader("Maintenance & Transfer")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onFullBackup,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.CloudDownload, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Export Full Backup")
                    }
                    OutlinedButton(
                        onClick = onFullRestore,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SettingsBackupRestore, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Import Full Backup")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun ActiveEnvironmentCard(
    connection: PlcConnectionProfile?,
    layout: DashboardLayout?,
    isModified: Boolean,
    connectionState: com.example.hmi.protocol.ConnectionState,
    onEditLayout: () -> Unit
) {
    val (statusColor, statusText) = when (connectionState) {
        com.example.hmi.protocol.ConnectionState.CONNECTED -> androidx.compose.ui.graphics.Color(0xFF4CAF50) to "Connected"
        com.example.hmi.protocol.ConnectionState.CONNECTING -> androidx.compose.ui.graphics.Color(0xFF2196F3) to "Connecting"
        com.example.hmi.protocol.ConnectionState.RECONNECTING -> androidx.compose.ui.graphics.Color(0xFFFF9800) to "Reconnecting"
        else -> androidx.compose.ui.graphics.Color(0xFFF44336) to "Disconnected"
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Current State",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    contentColor = statusColor,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = statusText.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isModified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    SuggestionChip(
                        onClick = { },
                        label = { Text("MODIFIED", style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(Icons.Default.Link, "PLC:", connection?.name ?: "None")
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoRow(Icons.Default.Layers, "UI:", layout?.name ?: "None", Modifier.weight(1f))
                IconButton(onClick = onEditLayout, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Layout Settings", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}
