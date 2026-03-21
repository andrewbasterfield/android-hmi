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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.Outline
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.utils.ColorUtils
import com.example.hmi.core.ui.utils.componentShape

enum class IndustrialButtonStyle {
    OUTLINED, SOLID
}

@Composable
fun IndustrialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "",
    style: IndustrialButtonStyle = IndustrialButtonStyle.OUTLINED,
    fontSizeMultiplier: Float = 1.0f,
    baseContentColor: Color = LocalContentColor.current, // The "Identity" color
    contentColorOverride: Color? = null, // Explicit override for the label
    pressedFillColor: Color? = null, // Custom override for the "Push" background
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit = {}
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val backgroundColor: Color
    val contentColor: Color
    val borderStroke: BorderStroke?

    // 1. Resolve the "Identity Pair" for the Normal state
    val identityColor = baseContentColor
    val contrastColor = contentColorOverride ?: ColorUtils.getIndustrialContrastColor(identityColor)

    if (style == IndustrialButtonStyle.SOLID) {
        // SOLID style: Simply swap the Identity Pair on press
        // Normal: Identity BG / Contrast Text
        // Pressed: Contrast BG / Identity Text
        backgroundColor = if (isPressed) {
            pressedFillColor ?: contrastColor
        } else {
            identityColor
        }
        
        contentColor = if (isPressed) {
            if (pressedFillColor != null) {
                ColorUtils.getIndustrialContrastColor(pressedFillColor)
            } else {
                identityColor
            }
        } else {
            contrastColor
        }
        
        // Always maintain a thin bezel of the identity color when pressed
        // to keep the button visible if it swaps to a dark contrast color.
        borderStroke = if (isPressed) BorderStroke(2.dp, identityColor) else null
    } else {
        // OUTLINED style: Invert to solid Identity BG
        backgroundColor = if (isPressed) identityColor else Color.Transparent
        contentColor = if (isPressed) contrastColor else identityColor
        borderStroke = if (isPressed) null else BorderStroke(2.dp, identityColor.copy(alpha = 0.5f))
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth()
            .semantics { 
                componentShape = "small"
            },
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = borderStroke,
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
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium.copy(
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
            .semantics { 
                componentShape = "small"
            }
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
                .clip(MaterialTheme.shapes.small)
                .background(StitchTheme.tokens.surfaceContainerHighest)
                .drawBehind {
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
