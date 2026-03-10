package com.example.hmi.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun GaugeWidget(
    label: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    val progress = if (maxValue > minValue) {
        ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = modifier
            .padding(8.dp)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { contentDescription = "Gauge for $label showing ${"%.1f".format(value)}" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "%.1f".format(value),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}