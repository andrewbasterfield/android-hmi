package com.example.hmi.dashboard

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
class WidgetContainerTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun widgetContainer_defaultColor_setsCorrectContentColor() {
        var capturedContentColor: Color = Color.Unspecified
        var expectedContentColor: Color = Color.Unspecified

        composeTestRule.setContent {
            val primary = MaterialTheme.colorScheme.primary
            expectedContentColor = ColorUtils.getContrastColor(primary)
            
            WidgetContainer(backgroundColor = null) {
                capturedContentColor = LocalContentColor.current
                Text("Test Content")
            }
        }

        composeTestRule.onNodeWithText("Test Content").assertIsDisplayed()
        
        // Verifying that the content color correctly contrasts with the theme's Primary color
        assertEquals("Content color should contrast with Primary background", expectedContentColor, capturedContentColor)
    }
}
