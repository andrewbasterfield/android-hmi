package com.example.hmi.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class GaugeUnitsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gaugeWidget_displaysUnitsWhenConfigured() {
        composeTestRule.setContent {
            GaugeWidget(
                label = "Pressure",
                value = 75.5f,
                minValue = 0f,
                maxValue = 100f,
                units = "PSI"
            )
        }

        // Verify that the accessibility description includes the units
        composeTestRule.onNodeWithContentDescription("Gauge for Pressure showing 75.5 PSI")
            .assertIsDisplayed()
    }

    @Test
    fun gaugeWidget_doesNotDisplayUnitsWhenNull() {
        composeTestRule.setContent {
            GaugeWidget(
                label = "Temperature",
                value = 23.4f,
                minValue = 0f,
                maxValue = 100f,
                units = null
            )
        }

        // Verify that only the value is shown
        composeTestRule.onNodeWithContentDescription("Gauge for Temperature showing 23.4")
            .assertIsDisplayed()
    }

    @Test
    fun gaugeWidget_displaysSpecialCharacters() {
        composeTestRule.setContent {
            GaugeWidget(
                label = "Volume",
                value = 10.0f,
                minValue = 0f,
                maxValue = 100f,
                units = "m³"
            )
        }

        composeTestRule.onNodeWithContentDescription("Gauge for Volume showing 10.0 m³")
            .assertIsDisplayed()
    }
}
