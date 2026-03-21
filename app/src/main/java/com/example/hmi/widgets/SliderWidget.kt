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
    fontSizeMultiplier: Float = 1.0f,
    units: String? = null
) {
    // Rely on LocalContentColor provided by WidgetContainer
    val contentColor = LocalContentColor.current
    
    val formattedUnits = SiFormatter.formatUnit(units)
    val formattedValue = SiFormatter.formatValue(value)

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // BUG-007: Doubled base font size (18sp -> 36sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 36.sp * fontSizeMultiplier,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = contentColor.copy(alpha = 0.8f)
        )

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
            // BUG-007: Doubled scale Labels (16sp -> 32sp)
            Text(
                text = "%.0f".format(valueRange.start),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 32.sp * fontSizeMultiplier
                ),
                color = contentColor.copy(alpha = 0.6f)
            )
            
            // Readout with Units
            Row(verticalAlignment = Alignment.Bottom) {
                // BUG-007: Doubled Readout (24sp -> 48sp)
                Text(
                    text = formattedValue,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 48.sp * fontSizeMultiplier,
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )
                if (!formattedUnits.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedUnits,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp * fontSizeMultiplier,
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // BUG-007: Doubled scale Labels (16sp -> 32sp)
            Text(
                text = "%.0f".format(valueRange.endInclusive),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 32.sp * fontSizeMultiplier
                ),
                color = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}
