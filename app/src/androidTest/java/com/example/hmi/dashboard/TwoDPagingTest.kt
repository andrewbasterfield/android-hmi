package com.example.hmi.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TwoDPagingTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun swipingBetweenPagesShowsDifferentWidgets() {
        // Wait for dashboard and connect
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Dashboard Settings").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Demo Mode").fetchSemanticsNodes().isNotEmpty()
        }
        if (composeTestRule.onAllNodesWithText("Demo Mode").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText("Demo Mode").performClick()
        }

        // Enter Edit Mode to add widgets on different pages
        composeTestRule.onNodeWithText("Edit Mode").performClick()
        
        // Add widget on Page 0,0 (default)
        composeTestRule.onNodeWithText("Add Widget").performClick()
        composeTestRule.onNodeWithTag("TagAddressField").performTextInput("test.p00")
        composeTestRule.onNodeWithText("Save").performClick()
        
        // Add widget on Page 1,0 (assuming 8 columns per page, so Col 8)
        // Note: For this test to be robust, we should explicitly set column to 8 in the dialog if supported, 
        // or drag it. Since dragging is harder to script precisely, we'll assume we can set it.
        // Wait, the current WidgetConfigDialog doesn't have col/row inputs. I should add them for testing/reflow.
    }
}
