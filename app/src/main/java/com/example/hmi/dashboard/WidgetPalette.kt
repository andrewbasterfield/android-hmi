package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.ui.components.HmiColorPicker

@Composable
fun WidgetPalette(
    onAddWidget: (WidgetConfiguration) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        WidgetConfigDialog(
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
fun WidgetConfigDialog(
    initialWidget: WidgetConfiguration? = null,
    viewModel: DashboardViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onConfirm: (WidgetConfiguration) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var selectedType by remember { mutableStateOf(initialWidget?.type ?: WidgetType.BUTTON) }
    var tagAddress by remember { mutableStateOf(initialWidget?.tagAddress ?: "") }
    var customLabel by remember { mutableStateOf(initialWidget?.customLabel ?: "") }
    var selectedColor by remember { mutableStateOf(initialWidget?.backgroundColor) }
    var colSpan by remember { mutableStateOf(initialWidget?.colSpan?.toString() ?: "1") }
    var rowSpan by remember { mutableStateOf(initialWidget?.rowSpan?.toString() ?: "1") }
    var fontSizeMultiplier by remember { mutableFloatStateOf(initialWidget?.fontSizeMultiplier ?: 1.0f) }
    var minValue by remember { mutableStateOf(initialWidget?.minValue?.toString() ?: "0") }
    var maxValue by remember { mutableStateOf(initialWidget?.maxValue?.toString() ?: "100") }
    val recentColors by viewModel.recentColors.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialWidget == null) "Add Widget" else "Edit Widget") },
        text = {
            Column {
                if (initialWidget == null) {
                    Text("Type", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WidgetType.values().forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.name) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                OutlinedTextField(
                    value = tagAddress,
                    onValueChange = { tagAddress = it },
                    label = { Text("Tag Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text("Custom Label (Optional)") },
                    placeholder = { Text(tagAddress.ifEmpty { "Enter label" }) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = colSpan,
                        onValueChange = { colSpan = it },
                        label = { Text("Width") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = rowSpan,
                        onValueChange = { rowSpan = it },
                        label = { Text("Height") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Font Size: ${"%.1f".format(fontSizeMultiplier)}x", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = fontSizeMultiplier,
                    onValueChange = { fontSizeMultiplier = it },
                    valueRange = 0.5f..2.5f,
                    steps = 20
                )

                if (selectedType == WidgetType.SLIDER || selectedType == WidgetType.GAUGE) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = minValue,
                            onValueChange = { minValue = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = maxValue,
                            onValueChange = { maxValue = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Background Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                HmiColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { 
                        selectedColor = it 
                        if (it != null) viewModel.saveRecentColor(it)
                    },
                    recentColors = recentColors
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        (initialWidget ?: WidgetConfiguration(type = selectedType, tagAddress = tagAddress)).copy(
                            tagAddress = tagAddress,
                            customLabel = customLabel.ifBlank { null },
                            backgroundColor = selectedColor,
                            fontSizeMultiplier = fontSizeMultiplier,
                            colSpan = colSpan.toIntOrNull() ?: 1,
                            rowSpan = rowSpan.toIntOrNull() ?: 1,
                            minValue = minValue.toFloatOrNull(),
                            maxValue = maxValue.toFloatOrNull()
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
