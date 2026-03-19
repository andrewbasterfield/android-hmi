package com.example.hmi.widgets

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import com.example.hmi.ui.theme.HmiTheme
import org.junit.Rule
import org.junit.Test

class ButtonAnimationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonShouldChangeStateWhenPressed() {
        composeTestRule.setContent {
            HmiTheme {
                ButtonWidget(
                    label = "Test Button",
                    onClick = {}
                )
            }
        }

        // Initially idle
        composeTestRule.onNodeWithText("Test Button").assertExists()

        // We can't easily assert exact scale/elevation in standard Compose UI tests without custom semantics
        // but we can at least verify it's clickable and doesn't crash.
        // To really test the "Tactile" part, we might need to add semantics for testing.
        
        composeTestRule.onNodeWithText("Test Button").performTouchInput {
            down(center)
        }
        
        // Assert pressed state (once semantics are added)
        
        composeTestRule.onNodeWithText("Test Button").performTouchInput {
            up()
        }
    }
}
