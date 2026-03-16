package com.example.hmi.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.hmi.widgets.ColorUtils

@Composable
fun SpectrumPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val hsv = remember(selectedColor) {
        val hsvArray = FloatArray(3)
        android.graphics.Color.colorToHSV(selectedColor.value.toLong().toInt(), hsvArray)
        hsvArray
    }

    var hue by remember(hsv[0]) { mutableFloatStateOf(hsv[0]) }
    var saturation by remember(hsv[1]) { mutableFloatStateOf(hsv[1]) }
    var value by remember(hsv[2]) { mutableFloatStateOf(hsv[2]) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saturation-Value Box
        Box(
            modifier = Modifier
                .size(150.dp, 120.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            SaturationValueCanvas(
                hue = hue,
                saturation = saturation,
                value = value,
                onValueChange = { s, v ->
                    saturation = s
                    value = v
                    onColorSelected(Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, s, v))))
                }
            )
        }

        // Hue Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            HueCanvas(
                hue = hue,
                onHueChange = {
                    hue = it
                    onColorSelected(Color(android.graphics.Color.HSVToColor(floatArrayOf(it, saturation, value))))
                }
            )
        }
    }
}

@Composable
private fun SaturationValueCanvas(
    hue: Float,
    saturation: Float,
    value: Float,
    onValueChange: (Float, Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val s = (offset.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                    onValueChange(s, v)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val offset = change.position
                    val s = (offset.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                    onValueChange(s, v)
                }
            }
    ) {
        val baseColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
        
        // Background color
        drawRect(baseColor)
        
        // Saturation gradient (white to transparent)
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color.White, Color.Transparent))
        )
        
        // Value gradient (transparent to black)
        drawRect(
            brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
        )

        // Selection circle
        val cursorX = saturation * size.width
        val cursorY = (1f - value) * size.height
        drawCircle(
            color = if (value > 0.5f) Color.Black else Color.White,
            radius = 8.dp.toPx(),
            center = Offset(cursorX, cursorY),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun HueCanvas(
    hue: Float,
    onHueChange: (Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val h = (offset.x / size.width).coerceIn(0f, 1f) * 360f
                    onHueChange(h)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val h = (change.position.x / size.width).coerceIn(0f, 1f) * 360f
                    onHueChange(h)
                }
            }
    ) {
        val hueColors = listOf(
            Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
        )
        drawRect(brush = Brush.horizontalGradient(hueColors))

        // Selection line
        val cursorX = (hue / 360f) * size.width
        drawRect(
            color = Color.White,
            topLeft = Offset(cursorX - 2.dp.toPx(), 0f),
            size = Size(4.dp.toPx(), size.height),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
