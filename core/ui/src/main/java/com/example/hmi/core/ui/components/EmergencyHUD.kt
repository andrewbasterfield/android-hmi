package com.example.hmi.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.core.ui.theme.StitchTheme

@Composable
fun EmergencyHUD(
    status: HealthStatus,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HUD Pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (status == HealthStatus.CRITICAL) 500 else 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse Alpha"
    )

    val glowColor = when (status) {
        HealthStatus.NORMAL -> Color.Transparent
        HealthStatus.CAUTION -> StitchTheme.tokens.statusAmber
        HealthStatus.CRITICAL -> StitchTheme.tokens.statusRed
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                if (glowColor != Color.Transparent) {
                    val brush = Brush.verticalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = alpha),
                            Color.Transparent,
                            glowColor.copy(alpha = alpha)
                        )
                    )
                    drawRect(
                        brush = brush,
                        alpha = alpha
                    )
                    
                    // Side glow
                    val horizontalBrush = Brush.horizontalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = alpha),
                            Color.Transparent,
                            glowColor.copy(alpha = alpha)
                        )
                    )
                    drawRect(
                        brush = horizontalBrush,
                        alpha = alpha
                    )
                }
            }
    ) {
        content()
        
        if (status == HealthStatus.CRITICAL) {
            BackdropBlur(blurRadius = 12.dp) {
                // Warning modal or overlay can go here
            }
        }
    }
}
