package com.example.hmi.widgets

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null
) {
    val bg = backgroundColor?.let { Color(it) }
    val contentColor = bg?.let { ColorUtils.getContrastColor(it) } ?: Color.Unspecified

    Button(
        onClick = onClick, 
        modifier = modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
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
