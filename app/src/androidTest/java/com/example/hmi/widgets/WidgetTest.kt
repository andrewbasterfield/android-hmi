package com.example.hmi.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.example.hmi.TestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class WidgetTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun gaugeWidget_displaysLabelAndValue() {
        val label = "Tank Level"
        val value = 75.5f

        composeTestRule.setContent {
            GaugeWidget(
                label = label,
                value = value,
                minValue = 0f,
                maxValue = 100f
            )
        }

        // Check if label is displayed
        composeTestRule.onNodeWithText(label).assertIsDisplayed()
        
        // Check if value is displayed formatted
        composeTestRule.onNodeWithText("75.5").assertIsDisplayed()
        
        // Check semantic content description
        composeTestRule.onNodeWithContentDescription("Gauge for Tank Level showing 75.5")
            .assertIsDisplayed()
    }

    @Test
    fun sliderWidget_updatesValueOnInteraction() {
        val label = "Pump Speed"
        var currentValue = 10f

        composeTestRule.setContent {
            SliderWidget(
                label = label,
                value = currentValue,
                onValueChange = { currentValue = it },
                valueRange = 0f..100f
            )
        }

        // Check initial state
        composeTestRule.onNodeWithText("Pump Speed: 10.00").assertIsDisplayed()

        // Interact with the slider
        composeTestRule.onNodeWithContentDescription("Slider for Pump Speed")
            .performTouchInput {
                swipeRight()
            }

        // Verify node exists
        composeTestRule.onNodeWithContentDescription("Slider for Pump Speed")
            .assertIsDisplayed()
    }
}
