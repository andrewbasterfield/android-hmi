package com.example.hmi.widgets

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import com.example.hmi.data.GaugeAxis
import com.example.hmi.data.GaugeIndicator
import com.example.hmi.data.GaugeZone

class LinearGaugePainter(
    private val canvasSize: Size,
    private val gaugeAxis: GaugeAxis,
    private val density: Density
) : GaugePainter {
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
        // TODO: Implement linear rendering in next phase
    }
}
