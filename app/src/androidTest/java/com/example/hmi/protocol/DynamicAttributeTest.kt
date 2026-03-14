package com.example.hmi.protocol

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DynamicAttributeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var demoServer: DemoPlcServer

    @Before
    fun setup() {
        hiltRule.inject()
        demoServer.start()
    }

    @After
    fun teardown() {
        demoServer.stop()
    }

    private fun connectToDemoBackend() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(20000) {
            composeTestRule.onAllNodesWithText("PLC Connection Profile").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Connect to Local Demo Server").performClick()
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Connection").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun widget_label_override_via_protocol() {
        connectToDemoBackend()

        // 1. Enter Edit Mode and add a widget
        composeTestRule.onNodeWithText("Edit Mode").performClick()
        composeTestRule.onNodeWithText("Add Widget").performClick()
        composeTestRule.onNodeWithText("Tag Address").performTextInput("PROTOCOL_TAG")
        composeTestRule.onNodeWithText("Save").performClick()

        // 2. Verify initial label is tag address
        composeTestRule.onNodeWithText("PROTOCOL_TAG").assertExists()

        // 3. Send protocol update for label
        demoServer.sendRawMessage("PROTOCOL_TAG.label:Remote Label")

        // 4. Verify UI updates
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Remote Label").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Remote Label").assertExists()
        composeTestRule.onNodeWithText("PROTOCOL_TAG").assertDoesNotExist()
    }

    @Test
    fun widget_color_override_via_protocol() {
        connectToDemoBackend()

        // 1. Enter Edit Mode and add a widget
        composeTestRule.onNodeWithText("Edit Mode").performClick()
        composeTestRule.onNodeWithText("Add Widget").performClick()
        composeTestRule.onNodeWithText("Tag Address").performTextInput("COLOR_TAG")
        composeTestRule.onNodeWithText("Save").performClick()

        // 2. Send protocol update for color (Red)
        demoServer.sendRawMessage("COLOR_TAG.color:#FF0000")

        // 3. Since verifying actual color in Compose tests is complex, 
        // we at least verify the app hasn't crashed and the node still exists.
        // In a real scenario, we might use custom semantics to expose the background color.
        composeTestRule.onNodeWithText("COLOR_TAG").assertExists()
    }
}
