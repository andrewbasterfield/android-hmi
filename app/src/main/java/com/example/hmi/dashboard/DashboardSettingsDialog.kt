package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.ui.components.HmiColorPicker
import com.example.hmi.ui.theme.IndustrialShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSettingsDialog(
    initialName: String,
    initialCanvasColor: Long?,
    initialHapticEnabled: Boolean,
    initialOrientationMode: com.example.hmi.data.OrientationMode,
    viewModel: DashboardViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onConfirm: (String, Long?, Boolean, com.example.hmi.data.OrientationMode) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf<Long?>(initialCanvasColor) }
    var hapticEnabled by remember { mutableStateOf(initialHapticEnabled) }
    var orientationMode by remember { mutableStateOf(initialOrientationMode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text("Layout Settings") },
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
                    label = { Text("Layout Name") },
                    modifier = Modifier.fillMaxWidth().testTag("DashboardNameField")
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Canvas Background Color", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                HmiColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { 
                        selectedColor = it 
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Orientation Mode", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.hmi.data.OrientationMode.values().forEach { mode ->
                        val isSelected = orientationMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { orientationMode = mode },
                            label = { Text(mode.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name, selectedColor, hapticEnabled, orientationMode)
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
