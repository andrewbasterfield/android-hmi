package com.example.hmi.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.hmi.data.ColorPalette
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
    var selectedColor by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Widget") },
        text = {
            Column {
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
                OutlinedTextField(
                    value = tagAddress,
                    onValueChange = { tagAddress = it },
                    label = { Text("Tag Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (selectedType == WidgetType.BUTTON) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Background Color", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPicker(
                        selectedColor = selectedColor,
                        onColorSelected = { selectedColor = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        WidgetConfiguration(
                            type = selectedType,
                            tagAddress = tagAddress,
                            backgroundColor = selectedColor,
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

@Composable
fun ColorPicker(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(ColorPalette.Items) { (name, value) ->
            val isSelected = selectedColor == value
            Box(
                modifier = Modifier
                    .size(48.dp) // Minimum touch target 48dp
                    .clip(CircleShape)
                    .background(if (value != null) Color(value) else MaterialTheme.colorScheme.primary)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.outline else Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(value) }
                    .semantics {
                        contentDescription = "Select $name color"
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (value != null) {
                            com.example.hmi.widgets.ColorUtils.getContrastColor(Color(value))
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                }
            }
        }
    }
}
