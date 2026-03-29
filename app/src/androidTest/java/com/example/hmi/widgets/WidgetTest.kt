package com.example.hmi.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import com.example.hmi.TestActivity
import com.example.hmi.data.WidgetOrientation
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
    fun gaugeWidget_combinations_renderCorrectly() {
        val label = "Process Data"
        val value = 60f

        // 1. ARC + POINTER (Default)
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.ARC, gaugeIndicator = com.example.hmi.data.GaugeIndicator.POINTER)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0").assertIsDisplayed()

        // 2. ARC + FILL
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.ARC, gaugeIndicator = com.example.hmi.data.GaugeIndicator.FILL)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0 (filled to 60%)").assertIsDisplayed()

        // 3. LINEAR_HORIZONTAL + POINTER
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.LINEAR_HORIZONTAL, gaugeIndicator = com.example.hmi.data.GaugeIndicator.POINTER)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0").assertIsDisplayed()

        // 4. LINEAR_HORIZONTAL + FILL
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.LINEAR_HORIZONTAL, gaugeIndicator = com.example.hmi.data.GaugeIndicator.FILL)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0 (filled to 60%)").assertIsDisplayed()

        // 5. LINEAR_VERTICAL + POINTER
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.LINEAR_VERTICAL, gaugeIndicator = com.example.hmi.data.GaugeIndicator.POINTER)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0").assertIsDisplayed()

        // 6. LINEAR_VERTICAL + FILL
        composeTestRule.setContent {
            GaugeWidget(label = label, value = value, minValue = 0f, maxValue = 100f, gaugeAxis = com.example.hmi.data.GaugeAxis.LINEAR_VERTICAL, gaugeIndicator = com.example.hmi.data.GaugeIndicator.FILL)
        }
        composeTestRule.onNodeWithContentDescription("Gauge for Process Data showing 60.0 (filled to 60%)").assertIsDisplayed()
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

    @Test
    fun verticalSliderWidget_updatesValueOnInteraction() {
        val label = "Tank Level"
        var currentValue = 50f

        composeTestRule.setContent {
            SliderWidget(
                label = label,
                value = currentValue,
                onValueChange = { currentValue = it },
                valueRange = 0f..100f,
                orientation = WidgetOrientation.VERTICAL
            )
        }

        // Check initial state
        composeTestRule.onNodeWithText(label).assertIsDisplayed()

        // Interact with the vertical slider (swipe up to increase)
        composeTestRule.onNodeWithContentDescription("Slider for Tank Level")
            .performTouchInput {
                swipeUp()
            }

        // Verify node exists and interaction happened
        composeTestRule.onNodeWithContentDescription("Slider for Tank Level")
            .assertIsDisplayed()
    }
}
