package com.example.hmi.widgets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.hmi.data.GaugeAxis
import com.example.hmi.data.GaugeIndicator
import com.example.hmi.data.GaugeZone

class LinearGaugePainter(
    private val canvasSize: Size,
    private val gaugeAxis: GaugeAxis,
    private val density: Density,
    private val metricHeight: Float = 0f
) : GaugePainter {

    private var trackStart: Offset = Offset.Zero
    private var trackEnd: Offset = Offset.Zero
    private var trackLength: Float = 0f
    private var strokeWidth: Float = 0f
    private var tickLength: Float = 0f
    private var pointerOffset: Float = 0f
    private val pointerPath = Path()

    init {
        calculateGeometry()
    }

    private fun calculateGeometry() {
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

        val isHorizontal = gaugeAxis == GaugeAxis.LINEAR_HORIZONTAL
        
        // Dynamic Scaling Reference (Matched to ArcGaugePainter logic)
        // We use the smaller dimension as our base "unit" for proportional scaling.
        val scaleRef = minOf(canvasSize.width, canvasSize.height)

        // Proportional Weights (Derived from "Industrial Fat" baseline)
        strokeWidth = scaleRef * 0.08f 
        tickLength = scaleRef * 0.35f
        
        val baseMargin = scaleRef * 0.08f
        val sideMargin = with(density) { 16.dp.toPx() } // Keep side margins fixed for grid alignment
        
        pointerOffset = scaleRef * 0.12f

        if (isHorizontal) {
            trackLength = canvasSize.width - 2 * sideMargin
            
            // Pin to top margin, mimicking ArcGauge hanging from the label.
            val pinY = baseMargin + (scaleRef * 0.15f) 
            trackStart = Offset(sideMargin, pinY)
            trackEnd = Offset(sideMargin + trackLength, pinY)
            
            // Pointer: Proportionally scaled Triangle pointing down
            val tipY = pinY - pointerOffset
            val baseY = tipY - (scaleRef * 0.18f)
            val halfWidth = scaleRef * 0.08f
            pointerPath.apply {
                reset()
                moveTo(0f, tipY)
                lineTo(-halfWidth, baseY)
                lineTo(halfWidth, baseY)
                close()
            }
        } else {
            // Vertical: Pin top to baseMargin, bottom symmetrically above metric.
            val bottomMargin = metricHeight + baseMargin
            trackLength = (canvasSize.height - baseMargin - bottomMargin).coerceAtLeast(10f)
            
            val centerX = canvasSize.width / 2f
            trackStart = Offset(centerX, canvasSize.height - bottomMargin)
            trackEnd = Offset(centerX, baseMargin)
            
            // Pointer: Proportionally scaled Triangle pointing right
            val tipX = centerX - pointerOffset
            val baseX = tipX - (scaleRef * 0.18f)
            val halfHeight = scaleRef * 0.08f
            pointerPath.apply {
                reset()
                moveTo(tipX, 0f)
                lineTo(baseX, -halfHeight)
                lineTo(baseX, halfHeight)
                close()
            }
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
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return

        with(drawScope) {
            val isHorizontal = gaugeAxis == GaugeAxis.LINEAR_HORIZONTAL
            val range = maxValue - minValue
            if (range <= 0) return

            val fraction = ((value - minValue) / range).coerceIn(0f, 1f)

            // 1. Draw Background Track
            drawLine(
                color = contentColor.copy(alpha = 0.2f),
                start = trackStart,
                end = trackEnd,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Butt
            )

            // 2. Draw Zones along the track
            colorZones.reversed().forEach { zone ->
                val zStart = zone.startValue.coerceIn(minValue, maxValue)
                val zEnd = zone.endValue.coerceIn(minValue, maxValue)
                if (zEnd > zStart) {
                    val startFrac = (zStart - minValue) / range
                    val endFrac = (zEnd - minValue) / range
                    
                    val zStartOffset = Offset(
                        trackStart.x + (trackEnd.x - trackStart.x) * startFrac,
                        trackStart.y + (trackEnd.y - trackStart.y) * startFrac
                    )
                    val zEndOffset = Offset(
                        trackStart.x + (trackEnd.x - trackStart.x) * endFrac,
                        trackStart.y + (trackEnd.y - trackStart.y) * endFrac
                    )
                    
                    drawLine(
                        color = ColorUtils.toColor(zone.color),
                        start = zStartOffset,
                        end = zEndOffset,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                }
            }

            // 3. Draw Ticks (Drawn BEFORE indicator so fill can draw on top)
            val step = ScaleUtils.calculateNiceStep(range, targetTicks)
            ScaleUtils.generateTicks(minValue, maxValue, step).forEach { tickValue ->
                val tFrac = (tickValue - minValue) / range
                val tPos = Offset(
                    trackStart.x + (trackEnd.x - trackStart.x) * tFrac,
                    trackStart.y + (trackEnd.y - trackStart.y) * tFrac
                )
                
                val tickZone = colorZones.find { tickValue >= it.startValue && tickValue <= it.endValue }
                val tColor = tickZone?.let { ColorUtils.toColor(it.color) } ?: contentColor.copy(alpha = 0.8f)

                val tickStart: Offset = tPos // Start exactly at axis
                val tickEnd: Offset
                
                if (isHorizontal) {
                    tickEnd = Offset(tPos.x, tPos.y + tickLength)
                } else {
                    tickEnd = Offset(tPos.x + tickLength, tPos.y)
                }

                drawLine(
                    color = tColor,
                    start = tickStart,
                    end = tickEnd,
                    strokeWidth = strokeWidth * 0.5f // 6dp fat industrial ticks
                )
            }

            // 4. Draw Indicator (Drawn ON TOP of ticks to match Arc behavior)
            if (indicatorType == GaugeIndicator.FILL) {
                val fillEnd = Offset(
                    trackStart.x + (trackEnd.x - trackStart.x) * fraction,
                    trackStart.y + (trackEnd.y - trackStart.y) * fraction
                )
                drawLine(
                    color = pointerColor,
                    start = trackStart,
                    end = fillEnd,
                    strokeWidth = strokeWidth * 4f, // 48dp heavy industrial fill
                    cap = StrokeCap.Butt
                )
            } else {
                val pPos = Offset(
                    trackStart.x + (trackEnd.x - trackStart.x) * fraction,
                    trackStart.y + (trackEnd.y - trackStart.y) * fraction
                )
                
                if (isHorizontal) {
                    drawContext.canvas.save()
                    drawContext.canvas.translate(pPos.x, 0f)
                    drawPath(pointerPath, color = pointerColor)
                    drawContext.canvas.restore()
                } else {
                    drawContext.canvas.save()
                    drawContext.canvas.translate(0f, pPos.y)
                    drawPath(pointerPath, color = pointerColor)
                    drawContext.canvas.restore()
                }
            }
        }
    }
}
