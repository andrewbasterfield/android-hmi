package com.example.hmi.widgets

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hmi.core.ui.theme.StitchTheme
import org.junit.Rule
import org.junit.Test

class ButtonWidgetTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonWidget_stateSwapTiming() {
        composeTestRule.setContent {
            StitchTheme {
                ButtonWidget(
                    label = "TEST",
                    onClick = {},
                    backgroundColor = null
                )
            }
        }

        val startTime = System.nanoTime()
        composeTestRule.onNodeWithText("TEST").performClick()
        val endTime = System.nanoTime()
        
        val durationMs = (endTime - startTime) / 1_000_000
        assert(durationMs < 50) { "State swap latency was $durationMs ms, exceeding 50ms limit" }
    }
}
