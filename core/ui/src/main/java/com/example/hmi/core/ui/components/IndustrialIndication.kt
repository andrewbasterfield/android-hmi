package com.example.hmi.core.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.flow.collect

object IndustrialIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val haptic = LocalHapticFeedback.current
        var pressed by remember { mutableStateOf(false) }

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    is PressInteraction.Release -> pressed = false
                    is PressInteraction.Cancel -> pressed = false
                }
            }
        }

        return remember(pressed) {
            IndustrialIndicationInstance(pressed)
        }
    }

    private class IndustrialIndicationInstance(
        private val isPressed: Boolean
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            // The inversion is handled by the component surface color selection
            // but we ensure the content is drawn.
            drawContent()
        }
    }
}
