package com.example.hmi.dashboard

import android.view.WindowManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DashboardTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private fun connectToDemoBackend() {
        // Wait for the activity to settle and the hierarchy to be available
        composeTestRule.waitForIdle()
        
        // Wait for connection screen
        composeTestRule.waitUntil(20000) {
            composeTestRule.onAllNodesWithText("PLC Connection Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Enter Demo backend details (Local server)
        composeTestRule.onNodeWithText("IP Address").performTextReplacement("127.0.0.1")
        composeTestRule.onNodeWithText("Port").performTextReplacement("9999")
        
        // Click Connect
        composeTestRule.onNodeWithText("Connect").performClick()
        
        // Wait for dashboard (Connection button appears in top bar)
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Connection").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun dashboard_full_flow_with_demo_backend() {
        connectToDemoBackend()

        // 1. Verify we are on Dashboard
        composeTestRule.onNodeWithText("Dashboard Appearance").assertExists()

        // 2. Verify window flag is SET while on Dashboard
        composeTestRule.runOnIdle {
            val flags = composeTestRule.activity.window.attributes.flags
            val isKeepScreenOn = (flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
            assertTrue("FLAG_KEEP_SCREEN_ON should be set while viewing Dashboard", isKeepScreenOn)
        }

        // 3. Test Navigation back to Connection
        composeTestRule.onNodeWithText("Connection").performClick()
        
        // Wait for Connection screen
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithText("PLC Connection Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // 4. Verify flag is CLEARED
        composeTestRule.runOnIdle {
            val flags = composeTestRule.activity.window.attributes.flags
            val isKeepScreenOn = (flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0
            assertTrue("FLAG_KEEP_SCREEN_ON should be cleared when leaving Dashboard", !isKeepScreenOn)
        }
    }
}
