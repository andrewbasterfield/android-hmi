package com.example.hmi.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ImportExport
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.ui.components.HmiColorPicker
import com.example.hmi.ui.theme.HmiPalette
import com.example.hmi.ui.theme.IndustrialShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSettingsDialog(
    initialName: String,
    initialCanvasColor: Long?,
    initialHapticEnabled: Boolean,
    viewModel: DashboardViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onConfirm: (String, Long?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf<Long?>(initialCanvasColor ?: HmiPalette.Black.value.toLong()) }
    var hapticEnabled by remember { mutableStateOf(initialHapticEnabled) }
    var showJsonTransfer by remember { mutableStateOf(false) }

    if (showJsonTransfer) {
        JsonTransferDialog(
            viewModel = viewModel,
            onDismiss = { showJsonTransfer = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dashboard Settings") },
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = IndustrialShape.Standard, // US1: 8dp rounded corners
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dashboard Name") },
                    modifier = Modifier.fillMaxWidth().testTag("DashboardNameField")
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Canvas Background Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                HmiColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { 
                        selectedColor = it 
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Haptic Feedback", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = { hapticEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { showJsonTransfer = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), // A11Y-001
                    shape = IndustrialShape.Standard
                ) {
                    Icon(Icons.Default.ImportExport, contentDescription = "JSON Transfer") // A11Y-002
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("JSON Transfer (Import/Export)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name, selectedColor, hapticEnabled)
                }
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonTransferDialog(
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    // We export the LATEST layout when the dialog opens
    var jsonText by remember { mutableStateOf(viewModel.exportLayoutToJson()) }

    // Listen for import results to show feedback
    LaunchedEffect(Unit) {
        viewModel.importResult.collect { result ->
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    Toast.makeText(context, "Layout imported successfully!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    Toast.makeText(context, result.exceptionOrNull()?.message ?: "Import failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("JSON Transfer") },
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = IndustrialShape.Standard, // US1: 8dp rounded corners
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Copy the current JSON to backup, or paste a new one to import.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("Layout JSON") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                        .testTag("LayoutJsonField"),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(viewModel.exportLayoutToJson()))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp), // A11Y-001
                        shape = IndustrialShape.Standard
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy to clipboard") // A11Y-002
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }
                    
                    Button(
                        onClick = {
                            viewModel.importLayoutFromJson(jsonText)
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp), // A11Y-001
                        shape = IndustrialShape.Standard
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Import from JSON") // A11Y-002
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
