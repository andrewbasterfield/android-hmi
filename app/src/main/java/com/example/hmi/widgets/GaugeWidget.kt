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
import com.example.hmi.data.GaugeAxis
import com.example.hmi.data.GaugeIndicator
import com.example.hmi.data.GaugeZone

val PointerColorKey = SemanticsPropertyKey<Color>("PointerColor")
var SemanticsPropertyReceiver.pointerColor by PointerColorKey

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
    gaugeAxis: GaugeAxis = GaugeAxis.ARC,
    gaugeIndicator: GaugeIndicator = GaugeIndicator.POINTER,
    units: String? = null,
    pulseState: PulseState = PulseState.NORMAL,
    onAcknowledgeAlarm: () -> Unit = {}
) {
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
                val percent = if (maxValue > minValue) ((value - minValue) / (maxValue - minValue) * 100).coerceIn(0f, 100f) else 0f
                val styleText = if (gaugeIndicator == GaugeIndicator.FILL) " (filled to ${percent.toInt()}%)" else ""
                contentDescription = "Gauge for $label showing $metricText$styleText"
                
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
                
                val painter = remember(canvasSize, gaugeAxis, arcSweep, minValue, maxValue, targetTicks, colorZones, contentColor, density) {
                    when (gaugeAxis) {
                        GaugeAxis.ARC -> ArcGaugePainter(canvasSize, arcSweep, density)
                        else -> LinearGaugePainter(canvasSize, gaugeAxis, density)
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                        .semantics { trackBackgroundColor = layoutBgColor }
                ) {
                    val currentPointerColor = ColorUtils.resolvePointerColor(
                        currentValue = animatedValue,
                        isPointerDynamic = isPointerDynamic,
                        staticPointerColor = pointerColor,
                        colorZones = colorZones,
                        defaultColor = contentColor
                    )

                    painter.draw(
                        drawScope = this,
                        value = animatedValue,
                        minValue = minValue,
                        maxValue = maxValue,
                        colorZones = colorZones,
                        targetTicks = targetTicks,
                        contentColor = contentColor,
                        pointerColor = currentPointerColor,
                        indicatorType = gaugeIndicator
                    )
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
