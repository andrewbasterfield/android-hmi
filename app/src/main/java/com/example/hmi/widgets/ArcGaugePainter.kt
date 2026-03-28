package com.example.hmi.widgets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.hmi.data.GaugeIndicator
import com.example.hmi.data.GaugeZone
import kotlin.math.cos
import kotlin.math.sin

interface GaugePainter {
    fun draw(
        drawScope: DrawScope,
        value: Float,
        minValue: Float,
        maxValue: Float,
        colorZones: List<GaugeZone>,
        targetTicks: Int,
        contentColor: Color,
        pointerColor: Color,
        indicatorType: GaugeIndicator
    )
}

class ArcGaugePainter(
    private val canvasSize: Size,
    private val arcSweep: Float,
    private val density: Density
) : GaugePainter {

    private var arcCenter: Offset = Offset.Zero
    private var radius: Float = 0f
    private var strokeWidth: Float = 0f
    private var tickStrokeWidth: Float = 0f
    private var startAngle: Float = 0f
    private var sweepAngle: Float = 0f
    private val pointerPath = Path()

    init {
        calculateGeometry()
    }

    private fun calculateGeometry() {
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

        sweepAngle = arcSweep.coerceIn(90f, 270f)
        startAngle = 270f - (sweepAngle / 2f)

        // 1. Calculate the bounding box of a unit arc centered at 12 o'clock (270°)
        val halfSweepRad = Math.toRadians((sweepAngle / 2f).toDouble())
        val leftX = if (sweepAngle >= 180f) -1f else -sin(halfSweepRad).toFloat()
        val rightX = if (sweepAngle >= 180f) 1f else sin(halfSweepRad).toFloat()
        val topY = -1f
        val bottomY = if (sweepAngle > 180f) {
            sin(Math.toRadians((sweepAngle - 180f) / 2.0)).toFloat()
        } else {
            -cos(halfSweepRad).toFloat()
        }

        val arcWidth = rightX - leftX
        val arcHeight = bottomY - topY

        // 2. Industrial Scaling Factors (Restored from commit a586a08 / 4aa0b37)
        // We use initialRadius to determine the baseline strokeWidth
        val initialRadius = minOf(canvasSize.width / arcWidth, canvasSize.height / arcHeight)
        strokeWidth = initialRadius * 0.05f
        val maxStrokeWidth = strokeWidth * 3.0f
        val margin = maxStrokeWidth / 2f + with(density) { 4.dp.toPx() }
        
        val availableWidth = canvasSize.width - margin * 2
        val availableHeight = canvasSize.height - margin * 2
        radius = minOf(availableWidth / arcWidth, availableHeight / arcHeight)
        tickStrokeWidth = radius * 0.025f

        // 3. Position center: horizontally centered, arc top pinned to margin
        val arcCenterX = (leftX + rightX) / 2f
        val centerX = canvasSize.width / 2f - arcCenterX * radius
        val centerY = margin - topY * radius
        arcCenter = Offset(centerX, centerY)

        // 4. Pointer Path (Defined relative to center, pointing OUTWARD at 0 degrees)
        val pointerTipRadius = radius * 0.90f
        val pointerBaseRadius = radius * 0.70f
        val pointerWidth = radius * 0.05f
        
        pointerPath.apply {
            reset()
            moveTo(arcCenter.x + pointerTipRadius, arcCenter.y)
            lineTo(arcCenter.x + pointerBaseRadius, arcCenter.y + pointerWidth)
            lineTo(arcCenter.x + pointerBaseRadius, arcCenter.y - pointerWidth)
            close()
        }
    }

    override fun draw(
        drawScope: DrawScope,
        value: Float,
        minValue: Float,
        maxValue: Float,
        colorZones: List<GaugeZone>,
        targetTicks: Int,
        contentColor: Color,
        pointerColor: Color,
        indicatorType: GaugeIndicator
    ) {
        with(drawScope) {
            // Draw base arc
            drawArc(
                color = contentColor.copy(alpha = 0.3f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(arcCenter.x - radius, arcCenter.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Draw zones
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
                        topLeft = Offset(arcCenter.x - radius, arcCenter.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                }
            }

            // Draw ticks
            val range = maxValue - minValue
            if (range > 0) {
                val step = ScaleUtils.calculateNiceStep(range, targetTicks)
                ScaleUtils.generateTicks(minValue, maxValue, step).forEach { tickValue ->
                    val angle = startAngle + (tickValue - minValue) / range * sweepAngle
                    val angleRad = Math.toRadians(angle.toDouble())
                    val outerX = arcCenter.x + radius * cos(angleRad).toFloat()
                    val outerY = arcCenter.y + radius * sin(angleRad).toFloat()
                    val innerX = arcCenter.x + (radius * 0.82f) * cos(angleRad).toFloat()
                    val innerY = arcCenter.y + (radius * 0.82f) * sin(angleRad).toFloat()
                    
                    val tickZone = colorZones.find { tickValue >= it.startValue && tickValue <= it.endValue }
                    val color = tickZone?.let { ColorUtils.toColor(it.color) } ?: contentColor.copy(alpha = 0.8f)

                    drawLine(
                        color = color,
                        start = Offset(innerX, innerY),
                        end = Offset(outerX, outerY),
                        strokeWidth = tickStrokeWidth
                    )
                }
            }

            // Draw indicator
            val fraction = if (range > 0) ((value - minValue) / range).coerceIn(0f, 1f) else 0f
            if (indicatorType == GaugeIndicator.FILL) {
                drawArc(
                    color = pointerColor,
                    startAngle = startAngle,
                    sweepAngle = fraction * sweepAngle,
                    useCenter = false,
                    topLeft = Offset(arcCenter.x - radius, arcCenter.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth * 3.0f, cap = StrokeCap.Butt)
                )
            } else {
                val pointerAngle = startAngle + fraction * sweepAngle
                rotate(degrees = pointerAngle, pivot = arcCenter) {
                    drawPath(path = pointerPath, color = pointerColor)
                }
            }
        }
    }
}
