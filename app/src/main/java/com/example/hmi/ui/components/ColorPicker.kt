package com.example.hmi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.hmi.ui.theme.HmiPalette
import com.example.hmi.widgets.ColorUtils

enum class PickerTab(val label: String, val icon: ImageVector) {
    Palette("Palette", Icons.Default.Palette),
    Hex("Hex", Icons.Default.TextFields)
}

@Composable
fun HmiColorPicker(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(PickerTab.Palette) }
    var currentSelection by remember(selectedColor) { mutableStateOf(selectedColor) }

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = activeTab.ordinal) {
            PickerTab.values().forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = { Text(tab.label) },
                    icon = { Icon(tab.icon, contentDescription = null) }
                )
            }
        }

        Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
            when (activeTab) {
                PickerTab.Palette -> {
                    PaletteView(
                        selectedColor = currentSelection,
                        onColorSelected = { 
                            currentSelection = it
                            onColorSelected(it)
                        }
                    )
                }
                PickerTab.Hex -> {
                    HexEntryField(
                        initialColor = currentSelection?.let { ColorUtils.toColor(it) } ?: Color.Transparent,
                        onColorChanged = { color ->
                            val longVal = color?.value?.toLong()
                            if (longVal != currentSelection) {
                                currentSelection = longVal
                                onColorSelected(longVal)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaletteView(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit
) {
    // Include null (Default) as the first item in the palette
    val colors = remember { listOf(null) + HmiPalette.WidgetBackgrounds.map { it.value.toLong() } }
    
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(colors) { colorLong ->
            ColorItem(
                colorLong = colorLong,
                isSelected = selectedColor == colorLong,
                onClick = { onColorSelected(colorLong) }
            )
        }
    }
}

@Composable
fun ColorItem(
    colorLong: Long?,
    isSelected: Boolean,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val color = colorLong?.let { ColorUtils.toColor(it) } ?: Color.Transparent
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = CircleShape
            )
            .clickable { onClick() }
            .semantics { 
                contentDescription = colorLong?.let { 
                    "Select color ${ColorUtils.formatHexColor(ColorUtils.toColor(it))}" 
                } ?: "Reset to default color"
            },
        contentAlignment = Alignment.Center
    ) {
        if (colorLong == null) {
            // Visual for "Default" / "None"
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(size / 2)
            )
        } else if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (ColorUtils.isDark(color)) Color.White else Color.Black,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}
