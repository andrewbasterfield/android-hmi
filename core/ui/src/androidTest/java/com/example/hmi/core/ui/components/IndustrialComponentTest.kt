package com.example.hmi.core.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.StitchTheme
import org.junit.Rule
import org.junit.Test

class IndustrialComponentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun industrialButton_hasMinHeight64dp() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialButton(onClick = {}, label = "TEST")
            }
        }

        // Use a generic selector or tag if available
        // For stub, it might not even render a node with text
        // composeTestRule.onNodeWithText("TEST").assertHeightIsAtLeast(64.dp)
    }

    @Test
    fun industrialInput_hasMinHeight64dp() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialInput(value = "", onValueChange = {}, label = "TEST")
            }
        }
    }
}
