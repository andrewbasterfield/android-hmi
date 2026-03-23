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
    onPress: () -> Unit = {},
    onRelease: () -> Unit = {},
    backgroundColor: Long? = null,
    textColor: Long? = null,
    labelFontSizeMultiplier: Float = 1.0f,
    hapticFeedbackEnabled: Boolean = true,
    isChecked: Boolean = false,
    isInteractive: Boolean = true,
    isInverted: Boolean = false
) {
    // Identity color of the button (background in SOLID mode)
    val identityColor = backgroundColor?.let { Color(it.toULong()) } ?: MaterialTheme.colorScheme.primary
    
    // Explicit override for the label text from the new full color picker
    val labelOverride = textColor?.let { Color(it.toULong()) }

    IndustrialButton(
        onClick = onClick,
        onPress = onPress,
        onRelease = onRelease,
        enabled = isInteractive,
        isChecked = isChecked,
        isInverted = isInverted,
        label = if (labelFontSizeMultiplier > 0.0f) label else "",
        style = IndustrialButtonStyle.SOLID,
        fontSizeMultiplier = labelFontSizeMultiplier,
        baseContentColor = identityColor,
        contentColorOverride = labelOverride,
        hapticFeedbackEnabled = hapticFeedbackEnabled,
        modifier = modifier.fillMaxSize()
    )
}
