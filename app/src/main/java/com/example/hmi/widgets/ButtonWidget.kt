package com.example.hmi.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.hmi.core.ui.components.IndustrialButton

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    fontSizeMultiplier: Float = 1.0f,
    textColorOverride: String? = null,
    hapticFeedbackEnabled: Boolean = true
) {
    // Resolve the base content color for the button
    val baseContentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> LocalContentColor.current
    }

    // We leverage the IndustrialButton from :core:ui which handles
    // the "Inverse Video" (FR-006) and haptic triggers (FR-010).
    IndustrialButton(
        onClick = onClick,
        label = label,
        fontSizeMultiplier = fontSizeMultiplier,
        baseContentColor = baseContentColor,
        modifier = modifier.fillMaxSize()
    )
}
