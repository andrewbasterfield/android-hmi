package com.example.hmi.widgets

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.hmi.ui.theme.IndustrialShape

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    fontSizeMultiplier: Float = 1.0f,
    textColorOverride: String? = null
) {
    val bg = backgroundColor?.let { ColorUtils.toColor(it) }
    // US2: Industrial Hybrid Contrast, respecting manual override
    val contentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> bg?.let { ColorUtils.getIndustrialContrastColor(it) } ?: LocalContentColor.current
    }

    Button(
        onClick = onClick, 
        modifier = modifier
            .fillMaxSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp) // A11Y-001
            .semantics { contentDescription = "Button for $label" },
        shape = IndustrialShape.Standard, // US1: 8dp rounded corners for buttons
        colors = if (bg != null) {
            ButtonDefaults.buttonColors(
                containerColor = bg,
                contentColor = contentColor
            )
        } else {
            ButtonDefaults.buttonColors(contentColor = contentColor)
        }
    ) {
        Text(
            text = label, 
            color = contentColor,
            fontSize = (MaterialTheme.typography.bodyLarge.fontSize * 2) * fontSizeMultiplier
        )
    }
}
