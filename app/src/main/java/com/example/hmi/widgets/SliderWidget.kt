package com.example.hmi.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.theme.MonospaceFont
import com.example.hmi.core.ui.utils.SiFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidget(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    backgroundColor: Long? = null,
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    units: String? = null
) {
    // Rely on LocalContentColor provided by WidgetContainer
    val contentColor = LocalContentColor.current
    
    val metricText = SiFormatter.formatMetric(value, units)

    Column(
        modifier = modifier.padding(8.dp),
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

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = SliderDefaults.colors(
                thumbColor = contentColor,
                activeTrackColor = contentColor,
                inactiveTrackColor = contentColor.copy(alpha = 0.2f)
            ),
            thumb = {
                // Rugged Rectangle Thumb
                Box(
                    modifier = Modifier
                        .size(24.dp, 32.dp)
                        .background(contentColor, RectangleShape)
                )
            },
            track = { sliderState ->
                // Rugged Rectangle Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(contentColor.copy(alpha = 0.1f), RectangleShape)
                        .align(Alignment.CenterHorizontally)
                ) {
                    // Active part
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
                            .fillMaxHeight()
                            .background(contentColor, RectangleShape)
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (labelFontSizeMultiplier > 0.0f) {
                Text(
                    text = "%.0f".format(valueRange.start),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonospaceFont,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                    ),
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
            
            if (metricFontSizeMultiplier > 0.0f) {
                Text(
                    text = metricText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                    ),
                    color = contentColor
                )
            }

            if (labelFontSizeMultiplier > 0.0f) {
                Text(
                    text = "%.0f".format(valueRange.endInclusive),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonospaceFont,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                    ),
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}
