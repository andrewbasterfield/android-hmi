package com.example.hmi.widgets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.core.ui.components.AlarmPulse
import com.example.hmi.core.ui.components.PulseState
import com.example.hmi.core.ui.utils.SiFormatter
import com.example.hmi.data.GaugeZone
import kotlin.math.cos
import kotlin.math.sin

val PointerColorKey = SemanticsPropertyKey<Color>("PointerColor")
var SemanticsPropertyReceiver.pointerColor by PointerColorKey

private data class TickData(
    val start: Offset,
    val end: Offset,
    val color: Color
)

private data class ZoneData(
    val startAngle: Float,
    val sweepAngle: Float,
    val color: Color
)

private data class GaugeGeometry(
    val center: Offset,
    val radius: Float,
    val strokeWidth: Float,
    val tickStrokeWidth: Float,
    val ticks: List<TickData>,
    val zones: List<ZoneData>,
    val pointerPath: Path,
    val startAngle: Float,
    val sweepAngle: Float
)

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
    pointerColor: Long? = null,
    isPointerDynamic: Boolean = false,
    gaugeStyle: com.example.hmi.data.GaugeStyle? = com.example.hmi.data.GaugeStyle.POINTER,
    units: String? = null,
    pulseState: PulseState = PulseState.NORMAL,
    onAcknowledgeAlarm: () -> Unit = {}
) {
    // BUG-011 Parked: Using fastest available spring for zero-lag response.
    // Phantom pointers are acknowledged as a display-persistence limitation for now.
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "gaugeValue"
    )

    val contentColor = LocalContentColor.current
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
                val percent = ((value - minValue) / (maxValue - minValue) * 100).coerceIn(0f, 100f)
                val styleText = if (gaugeStyle == com.example.hmi.data.GaugeStyle.ARC_FILL) " (filled to ${percent.toInt()}%)" else ""
                contentDescription = "Gauge for $label showing $metricText$styleText"
                
                // US1/US2/US3: Expose pointer color for testing (used by both styles)
                val currentPointerColor = ColorUtils.resolvePointerColor(
                    currentValue = animatedValue,
                    isPointerDynamic = isPointerDynamic,
                    staticPointerColor = pointerColor,
                    colorZones = colorZones,
                    defaultColor = contentColor
                )
                this.pointerColor = currentPointerColor
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
                var canvasSize by remember { mutableStateOf(Size.Zero) }
                val density = LocalDensity.current
                
                val geometry = remember(canvasSize, minValue, maxValue, arcSweep, targetTicks, colorZones, contentColor, density) {
                    if (canvasSize.width <= 0f || canvasSize.height <= 0f) return@remember null
                    
                    val sweepAngle = arcSweep.coerceIn(90f, 270f)
                    val startAngle = 270f - (sweepAngle / 2f)

                    // 1. Calculate the bounding box of a unit arc centered at 12 o'clock (270°)
                    val halfSweepRad = Math.toRadians((sweepAngle / 2f).toDouble())
                    val leftX = if (sweepAngle >= 180f) -1f else -sin(halfSweepRad).toFloat()
                    val rightX = if (sweepAngle >= 180f) 1f else sin(halfSweepRad).toFloat()
                    val topY = -1f  // Arc always reaches 12 o'clock
                    val bottomY = if (sweepAngle > 180f) {
                        sin(Math.toRadians((sweepAngle - 180f) / 2.0)).toFloat()
                    } else {
                        -cos(halfSweepRad).toFloat()  // Bottom of arc for < 180°
                    }

                    val arcWidth = rightX - leftX
                    val arcHeight = bottomY - topY

                    // 2. Industrial Scaling Factors
                    val initialRadius = minOf(canvasSize.width / arcWidth, canvasSize.height / arcHeight)
                    val strokeWidth = initialRadius * 0.05f
                    val maxStrokeWidth = strokeWidth * 3.0f
                    val margin = maxStrokeWidth / 2f + with(density) { 4.dp.toPx() }
                    
                    val availableWidth = canvasSize.width - margin * 2
                    val availableHeight = canvasSize.height - margin * 2
                    val radius = minOf(availableWidth / arcWidth, availableHeight / arcHeight)
                    val tickStrokeWidth = radius * 0.025f

                    // 3. Position center
                    val arcCenterX = (leftX + rightX) / 2f
                    val centerX = canvasSize.width / 2f - arcCenterX * radius
                    val centerY = margin - topY * radius
                    val center = Offset(centerX, centerY)

                    // 4. Pre-calculate zones
                    val zones = colorZones.reversed().mapNotNull { zone ->
                        val zoneStartValue = zone.startValue.coerceIn(minValue, maxValue)
                        val zoneEndValue = zone.endValue.coerceIn(minValue, maxValue)
                        
                        if (zoneEndValue > zoneStartValue) {
                            val zoneStartAngle = startAngle + (zoneStartValue - minValue) / (maxValue - minValue) * sweepAngle
                            val zoneSweepAngle = (zoneEndValue - zoneStartValue) / (maxValue - minValue) * sweepAngle
                            ZoneData(zoneStartAngle, zoneSweepAngle, ColorUtils.toColor(zone.color))
                        } else null
                    }

                    // 5. Pre-calculate ticks
                    val step = ScaleUtils.calculateNiceStep(maxValue - minValue, targetTicks)
                    val ticks = ScaleUtils.generateTicks(minValue, maxValue, step).map { tickValue ->
                        val angle = startAngle + (tickValue - minValue) / (maxValue - minValue) * sweepAngle
                        val angleRad = Math.toRadians(angle.toDouble())

                        val outerX = center.x + radius * cos(angleRad).toFloat()
                        val outerY = center.y + radius * sin(angleRad).toFloat()
                        val innerX = center.x + (radius * 0.82f) * cos(angleRad).toFloat()
                        val innerY = center.y + (radius * 0.82f) * sin(angleRad).toFloat()

                        val tickZone = colorZones.find { tickValue >= it.startValue && tickValue <= it.endValue }
                        val color = tickZone?.let { ColorUtils.toColor(it.color) } ?: contentColor.copy(alpha = 0.8f)

                        TickData(Offset(innerX, innerY), Offset(outerX, outerY), color)
                    }

                    // 6. Pre-calculate pointer path (at 0 degrees relative to center)
                    val pointerTipRadius = radius * 0.90f
                    val pointerBaseRadius = radius * 0.70f
                    val pointerWidth = radius * 0.05f
                    
                    val pointerPath = Path().apply {
                        moveTo(center.x + pointerTipRadius, center.y)
                        lineTo(center.x + pointerBaseRadius, center.y + pointerWidth)
                        lineTo(center.x + pointerBaseRadius, center.y - pointerWidth)
                        close()
                    }

                    GaugeGeometry(
                        center = center,
                        radius = radius,
                        strokeWidth = strokeWidth,
                        tickStrokeWidth = tickStrokeWidth,
                        ticks = ticks,
                        zones = zones,
                        pointerPath = pointerPath,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                        .semantics { trackBackgroundColor = layoutBgColor }
                ) {
                    geometry?.let { geo ->
                        // 1. Draw base arc
                        drawArc(
                            color = contentColor.copy(alpha = 0.3f),
                            startAngle = geo.startAngle,
                            sweepAngle = geo.sweepAngle,
                            useCenter = false,
                            topLeft = Offset(geo.center.x - geo.radius, geo.center.y - geo.radius),
                            size = Size(geo.radius * 2, geo.radius * 2),
                            style = Stroke(width = geo.strokeWidth, cap = StrokeCap.Butt)
                        )

                        if (gaugeStyle == com.example.hmi.data.GaugeStyle.ARC_FILL) {
                            drawArc(
                                color = contentColor.copy(alpha = 0.15f),
                                startAngle = geo.startAngle,
                                sweepAngle = geo.sweepAngle,
                                useCenter = false,
                                topLeft = Offset(geo.center.x - geo.radius, geo.center.y - geo.radius),
                                size = Size(geo.radius * 2, geo.radius * 2),
                                style = Stroke(width = geo.strokeWidth, cap = StrokeCap.Butt)
                            )
                        }

                        // 2. Draw color zones
                        geo.zones.forEach { zone ->
                            drawArc(
                                color = zone.color,
                                startAngle = zone.startAngle,
                                sweepAngle = zone.sweepAngle,
                                useCenter = false,
                                topLeft = Offset(geo.center.x - geo.radius, geo.center.y - geo.radius),
                                size = Size(geo.radius * 2, geo.radius * 2),
                                style = Stroke(width = geo.strokeWidth, cap = StrokeCap.Butt)
                            )
                        }

                        // 3. Draw pre-calculated ticks
                        geo.ticks.forEach { tick ->
                            drawLine(
                                color = tick.color,
                                start = tick.start,
                                end = tick.end,
                                strokeWidth = geo.tickStrokeWidth
                            )
                        }

                        // 4. Draw dynamic components
                        val currentPointerColor = ColorUtils.resolvePointerColor(
                            currentValue = animatedValue,
                            isPointerDynamic = isPointerDynamic,
                            staticPointerColor = pointerColor,
                            colorZones = colorZones,
                            defaultColor = contentColor
                        )

                        if (gaugeStyle == com.example.hmi.data.GaugeStyle.ARC_FILL) {
                            val fillSweepAngle = (animatedValue.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue) * geo.sweepAngle
                            drawArc(
                                color = currentPointerColor,
                                startAngle = geo.startAngle,
                                sweepAngle = fillSweepAngle,
                                useCenter = false,
                                topLeft = Offset(geo.center.x - geo.radius, geo.center.y - geo.radius),
                                size = Size(geo.radius * 2, geo.radius * 2),
                                style = Stroke(width = geo.strokeWidth * 3.0f, cap = StrokeCap.Butt)
                            )
                        }

                        if (gaugeStyle == com.example.hmi.data.GaugeStyle.POINTER) {
                            val pointerAngle = geo.startAngle + (animatedValue.coerceIn(minValue, maxValue) - minValue) / (maxValue - minValue) * geo.sweepAngle
                            rotate(degrees = pointerAngle, pivot = geo.center) {
                                drawPath(path = geo.pointerPath, color = currentPointerColor)
                            }
                        }
                    }
                }

                // Metric display at bottom
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
