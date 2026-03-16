package com.example.hmi.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.hmi.widgets.ColorUtils

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    fontSizeMultiplier: Float = 1.0f
) {
    val bg = backgroundColor?.let { Color(it.toULong()) }
    val contentColor = bg?.let { ColorUtils.getContrastColor(it) } ?: Color.Unspecified

    Button(
        onClick = onClick, 
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Button for $label" },
        shape = RectangleShape, // Button fills the square container
        colors = if (bg != null) {
            ButtonDefaults.buttonColors(
                containerColor = bg,
                contentColor = Color.Black
            )
        } else {
            ButtonDefaults.buttonColors(contentColor = Color.Black)
        }
    ) {
        Text(
            text = label, 
            color = Color.Black,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontSizeMultiplier
        )
    }
}
