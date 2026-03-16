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
import androidx.compose.material.icons.filled.ColorLens
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
    Spectrum("Spectrum", Icons.Default.ColorLens),
    Hex("Hex", Icons.Default.TextFields)
}

@Composable
fun HmiColorPicker(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit,
    recentColors: List<Long> = emptyList(),
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

        Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
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
                PickerTab.Spectrum -> {
                    SpectrumPicker(
                        selectedColor = currentSelection?.let { Color(it.toULong()) } ?: Color.White,
                        onColorSelected = { color ->
                            val longVal = color.value.toLong()
                            if (longVal != currentSelection) {
                                currentSelection = longVal
                                onColorSelected(longVal)
                            }
                        }
                    )
                }
                PickerTab.Hex -> {
                    HexEntryField(
                        initialColor = currentSelection?.let { Color(it.toULong()) } ?: Color.Transparent,
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

        if (recentColors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Recent Colors",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RecentColorsRow(
                colors = recentColors,
                selectedColor = currentSelection,
                onColorSelected = {
                    currentSelection = it
                    onColorSelected(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                currentSelection = null
                onColorSelected(null)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Text("Reset to Default")
        }
    }
}

@Composable
fun PaletteView(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit
) {
    val colors = remember { HmiPalette.WidgetBackgrounds.map { it.value.toLong() } }
    
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
fun RecentColorsRow(
    colors: List<Long>,
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors) { colorLong ->
            ColorItem(
                colorLong = colorLong,
                isSelected = selectedColor == colorLong,
                onClick = { onColorSelected(colorLong) },
                size = 32.dp
            )
        }
    }
}

@Composable
fun ColorItem(
    colorLong: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val color = Color(colorLong.toULong())
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
            .semantics { contentDescription = "Select color ${ColorUtils.formatHexColor(color)}" },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (com.example.hmi.widgets.ColorUtils.isDark(Color(colorLong.toULong()))) Color.White else Color.Black,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}
