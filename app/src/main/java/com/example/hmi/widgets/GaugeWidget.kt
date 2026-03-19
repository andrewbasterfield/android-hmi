package com.example.hmi.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.data.GaugeZone
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GaugeWidget(
    label: String,
    value: Float,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    fontSizeMultiplier: Float = 1.0f,
    textColorOverride: String? = null,
    colorZones: List<GaugeZone> = emptyList()
) {
    // Interpolate value for smooth 60fps movement
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 300),
        label = "gaugeValue"
    )

    val bg = backgroundColor?.let { ColorUtils.toColor(it) }
    val contentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> bg?.let { ColorUtils.getIndustrialContrastColor(it) } ?: LocalContentColor.current
    }

    // 270° Arc from 135° to 405°
    val startAngle = 135f
    val sweepAngle = 270f

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
        
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize(0.9f)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                val innerRadius = radius * 0.8f
                val strokeWidth = 4.dp.toPx()

                // Draw background track
                drawArc(
                    color = contentColor.copy(alpha = 0.1f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw color zones
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

                // Draw ticks and labels
                val step = ScaleUtils.calculateNiceStep(maxValue - minValue)
                val ticks = ScaleUtils.generateTicks(minValue, maxValue, step)
                
                ticks.forEach { tickValue ->
                    val angle = startAngle + (tickValue - minValue) / (maxValue - minValue) * sweepAngle
                    val angleRad = Math.toRadians(angle.toDouble())
                    
                    val outerX = center.x + radius * cos(angleRad).toFloat()
                    val outerY = center.y + radius * sin(angleRad).toFloat()
                    val innerX = center.x + innerRadius * cos(angleRad).toFloat()
                    val innerY = center.y + innerRadius * sin(angleRad).toFloat()

                    drawLine(
                        color = contentColor,
                        start = Offset(innerX, innerY),
                        end = Offset(outerX, outerY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw tick label
                    val labelRadius = innerRadius - 15.dp.toPx()
                    val labelX = center.x + labelRadius * cos(angleRad).toFloat()
                    val labelY = center.y + labelRadius * sin(angleRad).toFloat()
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        if (tickValue % 1f == 0f) tickValue.toInt().toString() else "%.1f".format(tickValue),
                        labelX,
                        labelY + 5.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(
                                (contentColor.alpha * 255).toInt(),
                                (contentColor.red * 255).toInt(),
                                (contentColor.green * 255).toInt(),
                                (contentColor.blue * 255).toInt()
                            )
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 10.sp.toPx() * fontSizeMultiplier
                        }
                    )
                }

                // Draw needle
                val needleAngle = startAngle + (animatedValue.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue) * sweepAngle
                rotate(degrees = needleAngle, pivot = center) {
                    val needlePath = Path().apply {
                        moveTo(center.x + radius * 0.9f, center.y)
                        lineTo(center.x, center.y - 6.dp.toPx()) // Fatter (was 4dp)
                        lineTo(center.x - 12.dp.toPx(), center.y) // Slightly longer back (was 10dp)
                        lineTo(center.x, center.y + 6.dp.toPx()) // Fatter (was 4dp)
                        close()
                    }
                    drawPath(path = needlePath, color = contentColor)
                }

                // Draw center hub
                drawCircle(color = contentColor, radius = 8.dp.toPx(), center = center) // Fatter hub (was 6dp)
            }
        }

        Text(
            text = "%.1f".format(value),
            style = MaterialTheme.typography.labelMedium, // Match label style
            color = contentColor,
            fontSize = (MaterialTheme.typography.labelMedium.fontSize * 2) * fontSizeMultiplier // Match label size
        )
    }
}
