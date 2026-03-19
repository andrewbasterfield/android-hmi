package com.example.hmi.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.hmi.ui.theme.IndustrialShape

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
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed && hapticFeedbackEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Stronger haptic
        }
    }

    // "Binary" feel: Instant down, springy up
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = if (isPressed) tween(0) else spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "scale"
    )

    val contentOffset by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = if (isPressed) tween(0) else spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "offset"
    )

    val baseBg = backgroundColor?.let { ColorUtils.toColor(it) } ?: MaterialTheme.colorScheme.primary
    
    // Calculate content color for the IDLE state first
    val idleContentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> ColorUtils.getIndustrialContrastColor(baseBg)
    }

    // Calculate the 'pushed' background based on the idle text color
    // This ensures vibrant colors like Red/Orange (which use black text) always darken.
    val pushedBg = if (idleContentColor == Color.Black) {
        // Vibrant/Light background: Darken significantly
        baseBg.copy(alpha = 0.5f).compositeOver(Color.Black)
    } else {
        // Dark background: Lighten significantly
        baseBg.copy(alpha = 0.5f).compositeOver(Color.White)
    }

    // Immediate color swap for that "mechanical" look
    val animatedBg by animateColorAsState(
        targetValue = if (isPressed) pushedBg else baseBg,
        animationSpec = tween(0),
        label = "bgColor"
    )

    // Calculate content color for the PUSHED state
    val pushedContentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> ColorUtils.getIndustrialContrastColor(pushedBg)
    }

    val animatedContentColor by animateColorAsState(
        targetValue = if (isPressed) pushedContentColor else idleContentColor,
        animationSpec = tween(0),
        label = "contentColor"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = IndustrialShape.Standard,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedBg,
            contentColor = animatedContentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics { contentDescription = "Button for $label" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, contentOffset.roundToPx()) },
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = label,
                color = animatedContentColor,
                fontSize = (MaterialTheme.typography.bodyLarge.fontSize * 2) * fontSizeMultiplier
            )
        }
    }
}
