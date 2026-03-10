package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPalette(
    onAddWidget: (WidgetConfiguration) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddWidgetDialog(
            onDismiss = { showDialog = false },
            onConfirm = { widget ->
                onAddWidget(widget)
                showDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { showDialog = true }) {
                Text("Add Widget")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (WidgetConfiguration) -> Unit
) {
    var selectedType by remember { mutableStateOf(WidgetType.BUTTON) }
    var tagAddress by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Widget") },
        text = {
            Column {
                Text("Type")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WidgetType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tagAddress,
                    onValueChange = { tagAddress = it },
                    label = { Text("Tag Address") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        WidgetConfiguration(
                            type = selectedType,
                            tagAddress = tagAddress,
                            x = 50f,
                            y = 50f
                        )
                    )
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
