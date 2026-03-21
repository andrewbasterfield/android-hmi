package com.example.hmi.widgets

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import com.example.hmi.core.ui.theme.StitchTheme
import org.junit.Rule
import org.junit.Test

class SiComplianceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gaugeWidget_formatsUnitsToSiCompliance() {
        composeTestRule.setContent {
            StitchTheme {
                GaugeWidget(
                    label = "Test Gauge",
                    value = 10.0f,
                    minValue = 0f,
                    maxValue = 100f,
                    units = "mv" // Should format to "mV"
                )
            }
        }

        // The exact formatted string includes the value 10.0 and units mV
        composeTestRule.onNodeWithContentDescription("Gauge for Test Gauge showing 10.0 mV").assertIsDisplayed()
    }
}
