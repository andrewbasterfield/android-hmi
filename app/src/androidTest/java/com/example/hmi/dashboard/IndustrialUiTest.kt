package com.example.hmi.dashboard

import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.hmi.TestActivity
import com.example.hmi.widgets.ColorUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class IndustrialUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun widgetContainer_usesIndustrialContrastColor() {
        var capturedContentColor: Color = Color.Unspecified
        val vibrantColor = Color(0xFFFF9900) // Safety Orange

        composeTestRule.setContent {
            WidgetContainer(backgroundColor = vibrantColor.value.toLong()) {
                capturedContentColor = LocalContentColor.current
                androidx.compose.material3.Text("Industrial Test")
            }
        }

        composeTestRule.onNodeWithText("Industrial Test").assertIsDisplayed()
        
        // Safety Orange has high luminance, so it should use Black text
        assertEquals(Color.Black, capturedContentColor)
    }

    @Test
    fun widgetContainer_usesWhiteText_on_DarkBackground() {
        var capturedContentColor: Color = Color.Unspecified
        val darkColor = Color(0xFF111111) // Very Dark Gray (L < 0.2)

        composeTestRule.setContent {
            WidgetContainer(backgroundColor = darkColor.value.toLong()) {
                capturedContentColor = LocalContentColor.current
                androidx.compose.material3.Text("Dark Background Test")
            }
        }

        composeTestRule.onNodeWithText("Dark Background Test").assertIsDisplayed()
        
        // Very Dark Gray has low luminance, so it should switch to White text for accessibility
        assertEquals(Color.White, capturedContentColor)
    }
}
