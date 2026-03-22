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
    @Suppress("UNUSED_PARAMETER") backgroundColor: Long? = null, // Handled by WidgetContainer
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    targetTicks: Int = 6,
    arcSweep: Float = 180f,
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
    val sweepAngle = arcSweep.coerceIn(90f, 270f)
    val startAngle = 270f - (sweepAngle / 2f)  // Centered at 12 o'clock
    
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
                val layoutBgColor = MaterialTheme.colorScheme.background
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { trackBackgroundColor = layoutBgColor }
                ) {
                    val strokeWidth = 4.dp.toPx()
                    val padding = 8.dp.toPx()

                    // Calculate bounding box of visible arc (center can be outside canvas)
                    // Arc is centered at 12 o'clock (270°), spanning from startAngle to startAngle + sweepAngle
                    val startRad = Math.toRadians(startAngle.toDouble())
                    val endRad = Math.toRadians((startAngle + sweepAngle).toDouble())
                    val halfSweepRad = Math.toRadians((sweepAngle / 2f).toDouble())

                    // For arc centered at 270°: find extent of visible arc relative to center
                    // Left extent: min x coordinate (at 180° if arc crosses it, else at endpoint)
                    // Right extent: max x coordinate (at 0°/360° if arc crosses it, else at endpoint)
                    // Top extent: always -1 (at 270°, the midpoint)
                    // Bottom extent: max y of endpoints

                    val leftX = if (sweepAngle >= 180f) -1f else -kotlin.math.sin(halfSweepRad).toFloat()
                    val rightX = if (sweepAngle >= 180f) 1f else kotlin.math.sin(halfSweepRad).toFloat()
                    val topY = -1f  // Arc always reaches 12 o'clock
                    val bottomY = if (sweepAngle > 180f) {
                        kotlin.math.sin(Math.toRadians((sweepAngle - 180f) / 2.0)).toFloat()
                    } else {
                        -kotlin.math.cos(halfSweepRad).toFloat()  // Bottom of arc for < 180°
                    }

                    val arcWidth = rightX - leftX
                    val arcHeight = bottomY - topY

                    // Calculate radius to fit the arc bounding box within canvas
                    val availableWidth = size.width - padding * 2
                    val availableHeight = size.height - padding * 2
                    val maxRadiusForWidth = availableWidth / arcWidth
                    val maxRadiusForHeight = availableHeight / arcHeight

                    val radius = minOf(maxRadiusForWidth, maxRadiusForHeight)
                    val innerRadius = radius * 0.8f

                    // Position center: horizontally centered, arc pinned to top
                    val arcCenterX = (leftX + rightX) / 2f
                    val centerX = size.width / 2f - arcCenterX * radius
                    val centerY = padding - topY * radius  // Arc top at padding
                    val center = Offset(centerX, centerY)

                    // 1. Draw base arc (visible scale line for areas without zones)
                    drawArc(
                        color = contentColor.copy(alpha = 0.3f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    // 2. Draw color zones (reversed so first zone in list draws on top)
                    colorZones.reversed().forEach { zone ->
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
                    
                    val tickInnerRadius = radius * 0.88f // Shortened ticks (60% of original)
                    ticks.forEach { tickValue ->
                        val angle = startAngle + (tickValue - minValue) / (maxValue - minValue) * sweepAngle
                        val angleRad = Math.toRadians(angle.toDouble())

                        val outerX = center.x + radius * cos(angleRad).toFloat()
                        val outerY = center.y + radius * sin(angleRad).toFloat()
                        val innerX = center.x + tickInnerRadius * cos(angleRad).toFloat()
                        val innerY = center.y + tickInnerRadius * sin(angleRad).toFloat()

                        // Color tick based on zone it falls within (first zone in list wins)
                        val tickZone = colorZones.find { tickValue >= it.startValue && tickValue <= it.endValue }
                        val tickColor = tickZone?.let { ColorUtils.toColor(it.color) } ?: contentColor.copy(alpha = 0.8f)

                        drawLine(
                            color = tickColor,
                            start = Offset(innerX, innerY),
                            end = Offset(outerX, outerY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // 4. Draw Pointer (Glass Cockpit style chevron/caret)
                    val currentNeedleColor = ColorUtils.resolveNeedleColor(
                        currentValue = animatedValue,
                        isNeedleDynamic = isNeedleDynamic,
                        staticNeedleColor = needleColor,
                        colorZones = colorZones,
                        defaultColor = contentColor
                    )

                    val pointerAngle = startAngle + (animatedValue.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue) * sweepAngle
                    val pointerAngleRad = Math.toRadians(pointerAngle.toDouble())

                    // Position pointer on the inside of the arc
                    val pointerTipRadius = radius * 0.95f  // Tip points toward arc
                    val pointerBaseRadius = radius * 0.75f // Base sits inside
                    val pointerWidth = 6.dp.toPx()

                    // Calculate pointer tip (pointing outward)
                    val tipX = center.x + pointerTipRadius * cos(pointerAngleRad).toFloat()
                    val tipY = center.y + pointerTipRadius * sin(pointerAngleRad).toFloat()

                    // Calculate base corners (perpendicular to radial direction)
                    val perpAngle = pointerAngleRad + Math.PI / 2
                    val baseX = center.x + pointerBaseRadius * cos(pointerAngleRad).toFloat()
                    val baseY = center.y + pointerBaseRadius * sin(pointerAngleRad).toFloat()

                    val corner1X = baseX + pointerWidth * cos(perpAngle).toFloat()
                    val corner1Y = baseY + pointerWidth * sin(perpAngle).toFloat()
                    val corner2X = baseX - pointerWidth * cos(perpAngle).toFloat()
                    val corner2Y = baseY - pointerWidth * sin(perpAngle).toFloat()

                    val pointerPath = Path().apply {
                        moveTo(tipX, tipY)
                        lineTo(corner1X, corner1Y)
                        lineTo(corner2X, corner2Y)
                        close()
                    }
                    drawPath(path = pointerPath, color = currentNeedleColor)
                }

                // Metric display at bottom, below the arc
                if (metricFontSizeMultiplier > 0.0f) {
                    Text(
                        text = metricText,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                        ),
                        color = contentColor,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
