package com.example.hmi.widgets

import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.example.hmi.TestActivity
import com.example.hmi.dashboard.WidgetContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HybridContrastTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun industrialContrast_vibrantColor_usesBlackText() {
        var capturedColor = Color.Unspecified
        val vibrantColor = Color(0xFFFBC02D) // Industrial Yellow

        composeTestRule.setContent {
            WidgetContainer(backgroundColor = vibrantColor.value.toLong()) {
                ButtonWidget(
                    label = "Contrast Test",
                    onClick = {},
                    backgroundColor = vibrantColor.value.toLong()
                )
                capturedColor = LocalContentColor.current
            }
        }

        composeTestRule.onNodeWithText("Contrast Test").assertIsDisplayed()
        assertEquals(Color.Black, capturedColor)
    }

    @Test
    fun industrialContrast_darkColor_usesWhiteText() {
        var capturedColor = Color.Unspecified
        val darkColor = Color(0xFF000080) // Navy Blue (L ≈ 0.03)

        composeTestRule.setContent {
            WidgetContainer(backgroundColor = darkColor.value.toLong()) {
                ButtonWidget(
                    label = "Dark Contrast Test",
                    onClick = {},
                    backgroundColor = darkColor.value.toLong()
                )
                capturedColor = LocalContentColor.current
            }
        }

        composeTestRule.onNodeWithText("Dark Contrast Test").assertIsDisplayed()
        assertEquals(Color.White, capturedColor)
    }

    @Test
    fun industrialContrast_cherryRed_usesWhiteText_forAccessibility() {
        // As per FR-005 refined: Accessibility (4.5:1) takes precedence.
        // Cherry Red #D2042D has ~3.8:1 contrast with Black, so it MUST use White.
        var capturedColor = Color.Unspecified
        val cherryRed = Color(0xFFD2042D)

        composeTestRule.setContent {
            WidgetContainer(backgroundColor = cherryRed.value.toLong()) {
                ButtonWidget(
                    label = "Cherry Red Test",
                    onClick = {},
                    backgroundColor = cherryRed.value.toLong()
                )
                capturedColor = LocalContentColor.current
            }
        }

        composeTestRule.onNodeWithText("Cherry Red Test").assertIsDisplayed()
        assertEquals(Color.White, capturedColor)
    }
}
