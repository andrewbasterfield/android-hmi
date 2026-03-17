package com.example.hmi.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun GaugeWidget(
    label: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    fontSizeMultiplier: Float = 1.0f,
    textColorOverride: String? = null
) {
    val progress = if (maxValue > minValue) {
        ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    } else 0f

    val bg = backgroundColor?.let { ColorUtils.toColor(it) }
    val contentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> bg?.let { ColorUtils.getIndustrialContrastColor(it) } ?: LocalContentColor.current
    }

    // Added padding to prevent overlap with corner resize handle
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .semantics { contentDescription = "Gauge for $label showing ${"%.1f".format(value)}" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontSize = (MaterialTheme.typography.labelMedium.fontSize * 2) * fontSizeMultiplier
        )
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(0.8f),
                color = contentColor,
                trackColor = contentColor.copy(alpha = 0.2f)
            )
            Text(
                text = "%.1f".format(value),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                fontSize = (MaterialTheme.typography.bodySmall.fontSize * 2) * fontSizeMultiplier
            )
        }
    }
}
