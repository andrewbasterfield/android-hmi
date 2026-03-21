package com.example.hmi.core.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.StatusRed

enum class PulseState {
    NORMAL,
    UNACKNOWLEDGED,
    ACKNOWLEDGED
}

@Composable
fun AlarmPulse(
    state: PulseState,
    modifier: Modifier = Modifier,
    normalColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    val borderColor = when (state) {
        PulseState.NORMAL -> normalColor
        PulseState.ACKNOWLEDGED -> StatusRed
        PulseState.UNACKNOWLEDGED -> {
            val infiniteTransition = rememberInfiniteTransition(label = "AlarmPulse")
            val color by infiniteTransition.animateColor(
                initialValue = StatusRed,
                targetValue = normalColor,
                animationSpec = infiniteRepeatable(
                    // 4Hz = 250ms per cycle. 125ms per tween for half a cycle.
                    animation = tween(durationMillis = 125, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "BorderColor"
            )
            color
        }
    }

    Box(
        modifier = modifier.border(
            width = 2.dp,
            color = borderColor,
            shape = RoundedCornerShape(2.dp)
        )
    ) {
        content()
    }
}
