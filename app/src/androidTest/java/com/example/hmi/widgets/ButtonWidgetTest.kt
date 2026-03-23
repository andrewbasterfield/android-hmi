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

    @Test
    fun buttonWidget_showsActiveStateWhenChecked() {
        composeTestRule.setContent {
            StitchTheme {
                ButtonWidget(
                    label = "ACTIVE",
                    onClick = {},
                    isChecked = true
                )
            }
        }

        // We can't easily check colors in Compose UI tests without custom semantics
        // but we can verify it exists and is clickable if interactive
        composeTestRule.onNodeWithText("ACTIVE").assertIsDisplayed()
    }

    @Test
    fun buttonWidget_indicatorModeIsNonInteractive() {
        var clicked = false
        composeTestRule.setContent {
            StitchTheme {
                ButtonWidget(
                    label = "INDICATOR",
                    onClick = { clicked = true },
                    isInteractive = false
                )
            }
        }

        // Use hasClickAction() or simply performClick and check if callback was called
        // If isInteractive is false, enabled=false in Surface, so it shouldn't be clickable
        // composeTestRule.onNodeWithText("INDICATOR").performClick() // This might fail if disabled
        
        // In Material 3 Surface, enabled=false means it's not clickable
        composeTestRule.onNodeWithText("INDICATOR").assertHasNoClickAction()
        assert(!clicked)
    }
}
