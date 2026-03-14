package com.example.hmi.connection

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.MainActivity
import com.example.hmi.protocol.DemoPlcServer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DemoModeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var demoServer: DemoPlcServer

    @Before
    fun setup() {
        hiltRule.inject()
        // Manually start the demo server because HiltTestApplication is used in instrumentation tests
        demoServer.start()
    }

    @After
    fun teardown() {
        demoServer.stop()
    }

    @Test
    fun connect_to_local_demo_server_navigates_to_dashboard() {
        // Wait for the connection screen to be available
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("PLC Connection Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Click the "Connect to Local Demo Server" button
        composeTestRule.onNodeWithText("Connect to Local Demo Server").performClick()
        
        // Wait for dashboard (Connection button appears in top bar)
        // This verifies that US1 (Priority: P1) is functional
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Connection").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we are on the Dashboard screen
        composeTestRule.onNodeWithText("Edit Mode").assertExists()
        
        // Go back to connection
        composeTestRule.onNodeWithText("Connection").performClick()

        // Wait for Connection screen
        composeTestRule.waitUntil(20000) {
            composeTestRule.onAllNodesWithText("PLC Connection Profile").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify we are back on connection screen
        composeTestRule.onNodeWithText("Connect to Local Demo Server").assertExists()
    }
}
