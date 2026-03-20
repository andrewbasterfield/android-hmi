package com.example.hmi.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.Outline
import com.example.hmi.core.ui.theme.StitchTheme

@Composable
fun IndustrialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "",
    fontSizeMultiplier: Float = 1.0f,
    baseContentColor: Color = LocalContentColor.current, // Added to support overrides
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit = {}
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Inverse Video Logic (FR-006)
    // If pressed, swap to Primary/OnPrimary. 
    // If not pressed, use the provided baseContentColor (or LocalContentColor).
    val backgroundColor = if (isPressed) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isPressed) MaterialTheme.colorScheme.onPrimary else baseContentColor
    val borderColor = if (isPressed) Color.Transparent else baseContentColor.copy(alpha = 0.5f)

    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RectangleShape, // 0dp
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = if (!isPressed) BorderStroke(2.dp, borderColor) else null,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        // BUG-007: Doubled base font size
                        fontSize = (MaterialTheme.typography.labelMedium.fontSize * 2) * fontSizeMultiplier
                    ),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
fun IndustrialInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .background(StitchTheme.tokens.surfaceContainerHighest)
                .drawBehind {
                    // 4px bottom border "shelf"
                    val strokeWidth = 4.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Outline,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(16.dp)
        )
    }
}
