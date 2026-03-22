package com.example.hmi.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.utils.SiFormatter

@Composable
fun TelemetryCard(
    label: String,
    value: String,
    unit: String,
    status: HealthStatus,
    modifier: Modifier = Modifier,
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    pulseState: PulseState = PulseState.NORMAL,
    onAcknowledgeAlarm: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onDetailsClick: (() -> Unit)? = null // Reserved for drill-down
) {
    val statusColor = when (status) {
        HealthStatus.NORMAL -> StitchTheme.tokens.statusGreen
        HealthStatus.CAUTION -> StitchTheme.tokens.statusAmber
        HealthStatus.CRITICAL -> StitchTheme.tokens.statusRed
    }

    val statusIcon = when (status) {
        HealthStatus.NORMAL -> Icons.Default.CheckCircle
        HealthStatus.CAUTION -> Icons.Default.Warning
        HealthStatus.CRITICAL -> Icons.Default.Error
    }
    
    val metricText = SiFormatter.formatMetric(value, unit)

    AlarmPulse(
        state = pulseState,
        normalColor = MaterialTheme.colorScheme.outline,
        modifier = modifier
            .background(StitchTheme.tokens.surfaceContainerLow)
            .heightIn(min = 100.dp)
            .clickable(enabled = pulseState == PulseState.UNACKNOWLEDGED) {
                onAcknowledgeAlarm()
            }
    ) {
        Box(
            modifier = Modifier.padding(12.dp).fillMaxSize()
        ) {
            // Health Accent Bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(statusColor)
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize * labelFontSizeMultiplier
                        ),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = status.name,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = metricText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }
        }
    }
}
