package com.example.hmi.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hmi.data.GaugeZone
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.ui.components.HmiColorPicker
import com.example.hmi.ui.theme.IndustrialShape
import com.example.hmi.widgets.ColorUtils
import com.example.hmi.widgets.ScaleUtils

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
        shape = IndustrialShape.Standard, // US1: 8dp rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.heightIn(min = 48.dp), // A11Y-001
                shape = IndustrialShape.Standard
            ) {
                Text("Add Widget")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigDialog(
    initialWidget: WidgetConfiguration? = null,
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
    var textColorOverride by remember { mutableStateOf(initialWidget?.textColorOverride) }
    var minValue by remember { mutableStateOf(initialWidget?.minValue?.toString() ?: "0") }
    var maxValue by remember { mutableStateOf(initialWidget?.maxValue?.toString() ?: "100") }
    var targetTicks by remember { mutableFloatStateOf(initialWidget?.targetTicks?.toFloat() ?: 6f) }
    var needleColor by remember { mutableStateOf(initialWidget?.needleColor) }
    var isNeedleDynamic by remember { mutableStateOf(initialWidget?.isNeedleDynamic ?: false) }
    
    val colorZones = remember { mutableStateListOf<GaugeZone>().apply { 
        addAll(initialWidget?.colorZones ?: emptyList()) 
    } }

    // Scale Assistance logic (FR-006)
    val scaleOutcome = remember(minValue, maxValue, targetTicks) {
        val min = minValue.toFloatOrNull() ?: 0f
        val max = maxValue.toFloatOrNull() ?: 100f
        val range = max - min
        if (range <= 0) "Invalid Range"
        else {
            val step = ScaleUtils.calculateNiceStep(range, targetTicks.toInt())
            val ticks = ScaleUtils.generateTicks(min, max, step)
            "Outcome: ~${ticks.size} Ticks (Steps of $step)"
        }
    }

    // Adaptive default size logic: Calculate required columns based on text length and font size
    val calculatedColSpan = remember(tagAddress, customLabel, fontSizeMultiplier) {
        val text = customLabel.ifBlank { tagAddress }
        if (text.isEmpty()) 1
        else {
            // Heuristic: ~0.6 chars per sp width, 80dp cell size
            val baseFontSize = 18f 
            val charWidthSp = (baseFontSize * 2) * 0.6f * fontSizeMultiplier
            val totalWidthSp = text.length * charWidthSp
            val requiredCells = kotlin.math.ceil(totalWidthSp / 80f).toInt().coerceAtLeast(1)
            requiredCells.coerceAtMost(8)
        }
    }

    // Auto-apply calculated size for NEW widgets or if current size is 1
    LaunchedEffect(calculatedColSpan) {
        if (initialWidget == null || colSpan == "1") {
            colSpan = calculatedColSpan.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialWidget == null) "Add Widget" else "Edit Widget") },
        shape = IndustrialShape.Standard, // US1: 8dp rounded corners
        modifier = Modifier.fillMaxWidth(0.95f),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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

                Spacer(modifier = Modifier.height(16.dp))
                Text("Text Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val options = listOf("Auto" to null, "Black" to "BLACK", "White" to "WHITE")
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, (label, value) ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                            onClick = { textColorOverride = value },
                            selected = textColorOverride == value
                        ) {
                            Text(label)
                        }
                    }
                }

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

                if (selectedType == WidgetType.GAUGE) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tick Density: ${targetTicks.toInt()}", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = targetTicks,
                        onValueChange = { targetTicks = it },
                        valueRange = 2f..20f,
                        steps = 18
                    )
                    Text(
                        text = scaleOutcome,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Needle Styling", style = MaterialTheme.typography.titleSmall)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dynamic Needle Color", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isNeedleDynamic,
                            onCheckedChange = { isNeedleDynamic = it }
                        )
                    }
                    Text(
                        "Needle matches the color of the current value's zone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!isNeedleDynamic) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Static Needle Color", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        HmiColorPicker(
                            selectedColor = needleColor,
                            onColorSelected = { needleColor = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Gauge Color Zones", style = MaterialTheme.typography.titleSmall)
                    
                    colorZones.forEachIndexed { index, zone ->
                        ZoneEditCard(
                            zone = zone,
                            onZoneChanged = { colorZones[index] = it },
                            onDelete = { colorZones.removeAt(index) }
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { colorZones.add(GaugeZone(0f, 100f, 0xFF00FF00uL.toLong())) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        shape = IndustrialShape.Standard
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Color Zone")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Background Color", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                HmiColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { 
                        selectedColor = it 
                    }
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
                            textColorOverride = textColorOverride,
                            colSpan = colSpan.toIntOrNull() ?: 1,
                            rowSpan = rowSpan.toIntOrNull() ?: 1,
                            minValue = minValue.toFloatOrNull(),
                            maxValue = maxValue.toFloatOrNull(),
                            targetTicks = targetTicks.toInt(),
                            colorZones = colorZones.toList(),
                            needleColor = needleColor,
                            isNeedleDynamic = isNeedleDynamic
                        )
                    )
                },
                modifier = Modifier.heightIn(min = 48.dp) // A11Y-001
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.heightIn(min = 48.dp), // A11Y-001
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.heightIn(min = 48.dp) // A11Y-001
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun ZoneEditCard(
    zone: GaugeZone,
    onZoneChanged: (GaugeZone) -> Unit,
    onDelete: () -> Unit
) {
    var startInput by remember { mutableStateOf(zone.startValue.toString()) }
    var endInput by remember { mutableStateOf(zone.endValue.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = IndustrialShape.Small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startInput,
                    onValueChange = { input ->
                        startInput = input
                        input.toFloatOrNull()?.let { onZoneChanged(zone.copy(startValue = it)) }
                    },
                    label = { Text("Start") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = endInput,
                    onValueChange = { input ->
                        endInput = input
                        input.toFloatOrNull()?.let { onZoneChanged(zone.copy(endValue = it)) }
                    },
                    label = { Text("End") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Zone", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(ColorUtils.toColor(zone.color), shape = IndustrialShape.Small)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zone Color", style = MaterialTheme.typography.labelSmall)
            }
            HmiColorPicker(
                selectedColor = zone.color,
                onColorSelected = { onZoneChanged(zone.copy(color = it ?: 0xFF00FF00uL.toLong())) }
            )
        }
    }
}
