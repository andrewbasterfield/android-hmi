package com.example.hmi.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.core.ui.theme.StitchTheme
import org.junit.Rule
import org.junit.Test

class TelemetryCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun telemetryCard_displaysLabelAndValue() {
        composeTestRule.setContent {
            StitchTheme {
                TelemetryCard(
                    label = "ENGINE I",
                    value = "1200",
                    unit = "RPM",
                    status = HealthStatus.NORMAL
                )
            }
        }

        // These should fail because the stub is empty
        composeTestRule.onNodeWithText("ENGINE I").assertIsDisplayed()
        composeTestRule.onNodeWithText("1200").assertIsDisplayed()
        composeTestRule.onNodeWithText("RPM").assertIsDisplayed()
    }
}
