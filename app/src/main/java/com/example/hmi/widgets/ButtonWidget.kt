package com.example.hmi.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.example.hmi.widgets.ColorUtils

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null
) {
    val bg = backgroundColor?.let { Color(it.toInt()) }
    val contentColor = bg?.let { ColorUtils.getContrastColor(it) } ?: Color.Unspecified

    Button(
        onClick = onClick, 
        modifier = modifier.fillMaxSize(),
        shape = RectangleShape, // Button fills the square container
        colors = if (bg != null) {
            ButtonDefaults.buttonColors(
                containerColor = bg,
                contentColor = contentColor
            )
        } else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(label)
    }
}
