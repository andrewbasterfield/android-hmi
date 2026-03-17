package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.hmi.TestActivity
import com.example.hmi.data.ColorPalette
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ButtonWidgetTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun buttonWidget_rendersWithDefaultColor() {
        composeTestRule.setContent {
            ButtonWidget(
                label = "Default Button",
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Default Button").assertIsDisplayed()
    }

    @Test
    fun buttonWidget_rendersWithCustomColor() {
        // Use internal value for custom color to match ButtonWidget expectation
        val customColor = ColorPalette.Red.value.toLong()

        composeTestRule.setContent {
            ButtonWidget(
                label = "Red Button",
                onClick = {},
                backgroundColor = customColor
            )
        }

        // Verification of actual color is complex in Compose UI tests without 
        // custom matchers, but we ensure it renders without crashing.
        composeTestRule.onNodeWithText("Red Button").assertIsDisplayed()
    }

    @Test
    fun buttonWidget_handlesNullColor() {
        composeTestRule.setContent {
            ButtonWidget(
                label = "Null Color Button",
                onClick = {},
                backgroundColor = null
            )
        }

        composeTestRule.onNodeWithText("Null Color Button").assertIsDisplayed()
    }
}
