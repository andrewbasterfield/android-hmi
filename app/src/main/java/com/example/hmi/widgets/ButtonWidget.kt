package com.example.hmi.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.hmi.core.ui.components.IndustrialButton
import com.example.hmi.core.ui.components.IndustrialButtonStyle

@Composable
fun ButtonWidget(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Long? = null,
    textColor: Long? = null,
    labelFontSizeMultiplier: Float = 1.0f,
    hapticFeedbackEnabled: Boolean = true
) {
    // Identity color of the button (background in SOLID mode)
    val identityColor = backgroundColor?.let { Color(it.toULong()) } ?: MaterialTheme.colorScheme.primary
    
    // Explicit override for the label text from the new full color picker
    val labelOverride = textColor?.let { Color(it.toULong()) }

    // We leverage the IndustrialButton from :core:ui which handles
    // the "Inverse Video" (FR-006) and haptic triggers (FR-010).
    IndustrialButton(
        onClick = onClick,
        label = if (labelFontSizeMultiplier > 0.0f) label else "",
        style = IndustrialButtonStyle.SOLID,
        fontSizeMultiplier = labelFontSizeMultiplier,
        baseContentColor = identityColor,
        contentColorOverride = labelOverride,
        modifier = modifier.fillMaxSize()
    )
}
