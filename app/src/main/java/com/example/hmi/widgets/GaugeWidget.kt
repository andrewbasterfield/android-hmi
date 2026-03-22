package com.example.hmi.widgets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.data.GaugeZone
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.foundation.clickable
import com.example.hmi.core.ui.components.AlarmPulse
import com.example.hmi.core.ui.components.PulseState
import com.example.hmi.core.ui.utils.SiFormatter

val NeedleColorKey = SemanticsPropertyKey<Color>("NeedleColor")
var SemanticsPropertyReceiver.needleColor by NeedleColorKey

@Composable
fun GaugeWidget(
    label: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    targetTicks: Int = 6,
    colorZones: List<GaugeZone> = emptyList(),
    needleColor: Long? = null,
    isNeedleDynamic: Boolean = false,
    units: String? = null,
    pulseState: PulseState = PulseState.NORMAL,
    onAcknowledgeAlarm: () -> Unit = {}
) {
    // BUG-011 Parked: Using fastest available spring for zero-lag response.
    // Phantom needles are acknowledged as a display-persistence limitation for now.
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "gaugeValue"
    )

    val contentColor = LocalContentColor.current
    val startAngle = 135f
    val sweepAngle = 270f
    
    val metricText = SiFormatter.formatMetric(value, units)

    AlarmPulse(
        state = pulseState,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable(enabled = pulseState == PulseState.UNACKNOWLEDGED) {
                onAcknowledgeAlarm()
            }
            .semantics { 
                contentDescription = "Gauge for $label showing $metricText"
                // US1/US2: Expose needle color for testing
                val currentNeedleColor = ColorUtils.resolveNeedleColor(
                    currentValue = animatedValue,
                    isNeedleDynamic = isNeedleDynamic,
                    staticNeedleColor = needleColor,
                    colorZones = colorZones,
                    defaultColor = contentColor
                )
                this.needleColor = currentNeedleColor
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (labelFontSizeMultiplier > 0.0f) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize * labelFontSizeMultiplier,
                        letterSpacing = 1.25.sp
                    ),
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // ... (rest of the Canvas code unchanged)
                Canvas(modifier = Modifier.fillMaxSize(0.9f)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2
                    val innerRadius = radius * 0.8f
                    val strokeWidth = 4.dp.toPx()

                    // 1. Draw background track (Aligned with contentColor)
                    drawArc(
                        color = contentColor.copy(alpha = 0.2f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    // 2. Draw color zones
                    colorZones.forEach { zone ->
                        val zoneStartValue = zone.startValue.coerceIn(minValue, maxValue)
                        val zoneEndValue = zone.endValue.coerceIn(minValue, maxValue)
                        
                        if (zoneEndValue > zoneStartValue) {
                            val zoneStartAngle = startAngle + (zoneStartValue - minValue) / (maxValue - minValue) * sweepAngle
                            val zoneSweepAngle = (zoneEndValue - zoneStartValue) / (maxValue - minValue) * sweepAngle
                            
                            drawArc(
                                color = ColorUtils.toColor(zone.color),
                                startAngle = zoneStartAngle,
                                sweepAngle = zoneSweepAngle,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Butt)
                            )
                        }
                    }

                    // 3. Draw ticks
                    val step = ScaleUtils.calculateNiceStep(maxValue - minValue, targetTicks)
                    val ticks = ScaleUtils.generateTicks(minValue, maxValue, step)
                    
                    ticks.forEach { tickValue ->
                        val angle = startAngle + (tickValue - minValue) / (maxValue - minValue) * sweepAngle
                        val angleRad = Math.toRadians(angle.toDouble())
                        
                        val outerX = center.x + radius * cos(angleRad).toFloat()
                        val outerY = center.y + radius * sin(angleRad).toFloat()
                        val innerX = center.x + innerRadius * cos(angleRad).toFloat()
                        val innerY = center.y + innerRadius * sin(angleRad).toFloat()

                        drawLine(
                            color = contentColor.copy(alpha = 0.8f),
                            start = Offset(innerX, innerY),
                            end = Offset(outerX, outerY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // 4. Draw Needle
                    val currentNeedleColor = ColorUtils.resolveNeedleColor(
                        currentValue = animatedValue,
                        isNeedleDynamic = isNeedleDynamic,
                        staticNeedleColor = needleColor,
                        colorZones = colorZones,
                        defaultColor = contentColor
                    )

                    val needleAngle = startAngle + (animatedValue.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue) * sweepAngle
                    rotate(degrees = needleAngle, pivot = center) {
                        val needlePath = Path().apply {
                            moveTo(center.x + radius * 0.9f, center.y)
                            lineTo(center.x, center.y - 4.dp.toPx())
                            lineTo(center.x - 8.dp.toPx(), center.y)
                            lineTo(center.x, center.y + 4.dp.toPx())
                            close()
                        }
                        drawPath(path = needlePath, color = currentNeedleColor)
                    }

                    drawCircle(color = currentNeedleColor, radius = 6.dp.toPx(), center = center)
                }
            }

            if (metricFontSizeMultiplier > 0.0f) {
                Text(
                    text = metricText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                    ),
                    color = contentColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
