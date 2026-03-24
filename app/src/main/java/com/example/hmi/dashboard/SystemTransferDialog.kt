package com.example.hmi.dashboard

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.connection.ConnectionViewModel
import com.example.hmi.data.TransferEvent
import com.example.hmi.ui.theme.IndustrialShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemTransferDialog(
    dashboardViewModel: DashboardViewModel,
    connectionViewModel: ConnectionViewModel,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    val layoutExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { dashboardViewModel.exportLayout(it) }
    }

    val layoutImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { dashboardViewModel.importLayout(it) }
    }

    val profileExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { connectionViewModel.exportProfiles(it) }
    }

    val profileImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { connectionViewModel.importProfiles(it) }
    }

    val fullBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { dashboardViewModel.exportFullBackup(it) }
    }

    var jsonText by remember { mutableStateOf(dashboardViewModel.exportLayoutToJson()) }

    LaunchedEffect(Unit) {
        dashboardViewModel.transferEvents.collect { event ->
            withContext(Dispatchers.Main) {
                handleTransferEvent(context, event, onDismiss)
            }
        }
    }

    LaunchedEffect(Unit) {
        connectionViewModel.transferEvents.collect { event ->
            withContext(Dispatchers.Main) {
                handleTransferEvent(context, event, onDismiss)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("System Transfer Center") },
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = IndustrialShape.Standard,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Dashboard Layout
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Dashboard Layout", style = MaterialTheme.typography.titleMedium)
                        
                        OutlinedTextField(
                            value = jsonText,
                            onValueChange = { jsonText = it },
                            label = { Text("Layout JSON") },
                            modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp),
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(dashboardViewModel.exportLayoutToJson()))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.ContentCopy, null)
                                Text("Copy", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { dashboardViewModel.importLayoutFromJson(jsonText) },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.FileDownload, null)
                                Text("Import", fontSize = 11.sp)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { layoutExportLauncher.launch("dashboard_layout.json") },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.Save, null)
                                Text("Save File", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { layoutImportLauncher.launch(arrayOf("application/json")) },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.FileUpload, null)
                                Text("Open File", fontSize = 11.sp)
                            }
                        }
                        
                        Button(
                            onClick = { dashboardViewModel.shareLayout(context) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            shape = IndustrialShape.Standard,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Share, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Layout")
                        }
                    }
                }

                // Section 2: Connection Profiles
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Connection Profiles", style = MaterialTheme.typography.titleMedium)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { profileExportLauncher.launch("connection_profiles.json") },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.Save, null)
                                Text("Export All", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { profileImportLauncher.launch(arrayOf("application/json")) },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                shape = IndustrialShape.Standard
                            ) {
                                Icon(Icons.Default.FileUpload, null)
                                Text("Import", fontSize = 11.sp)
                            }
                        }
                        
                        Button(
                            onClick = { connectionViewModel.shareProfiles(context) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            shape = IndustrialShape.Standard,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Share, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Profiles")
                        }
                    }
                }

                // Section 3: Full Maintenance
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Full System Backup", style = MaterialTheme.typography.titleMedium)
                        Text("Exports layout and all profiles to a single package.", style = MaterialTheme.typography.bodySmall)
                        
                        Button(
                            onClick = { fullBackupLauncher.launch("hmi_full_backup.json") },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            shape = IndustrialShape.Standard,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Default.CloudDownload, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Generate Full Backup")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun handleTransferEvent(context: android.content.Context, event: TransferEvent, onDismiss: () -> Unit) {
    when (event) {
        is TransferEvent.Success -> {
            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            if (event.message.contains("import", ignoreCase = true)) {
                onDismiss()
            }
        }
        is TransferEvent.ValidationError -> {
            Toast.makeText(context, "Validation Error: ${event.message}", Toast.LENGTH_LONG).show()
        }
        is TransferEvent.Error -> {
            Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
        }
        else -> {}
    }
}
