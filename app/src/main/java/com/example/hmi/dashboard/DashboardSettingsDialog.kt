package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSettingsDialog(
    initialName: String,
    initialCanvasColor: Long?,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableStateOf(initialCanvasColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dashboard Settings") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dashboard Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Canvas Background Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name, selectedColor)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
