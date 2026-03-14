package com.example.hmi.dashboard

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
class LabelOverrideTest {

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
        // Use the new "Demo Server" button from feature 009
        composeTestRule.onNodeWithText("Connect to Local Demo Server").performClick()
        composeTestRule.waitUntil(30000) {
            composeTestRule.onAllNodesWithText("Connection").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun widget_label_override_manual() {
        connectToDemoBackend()

        // 1. Enter Edit Mode
        composeTestRule.onNodeWithText("Edit Mode").performClick()

        // 2. Add a Button widget
        composeTestRule.onNodeWithText("Add Widget").performClick()
        composeTestRule.onNodeWithText("Tag Address").performTextInput("MOTOR_01")
        composeTestRule.onNodeWithText("Save").performClick()

        // 3. Verify default label is tag address
        composeTestRule.onNodeWithText("MOTOR_01").assertExists()

        // 4. Edit the widget to set a custom label
        // The edit button is an IconButton with content description "Edit widget"
        composeTestRule.onNodeWithContentDescription("Edit widget").performClick()
        
        composeTestRule.onNodeWithText("Edit Widget").assertExists()
        composeTestRule.onNodeWithText("Custom Label (Optional)").performTextInput("Main Conveyor")
        composeTestRule.onNodeWithText("Save").performClick()

        // 5. Verify custom label is displayed
        composeTestRule.onNodeWithText("Main Conveyor").assertExists()
        // And tag address is NOT displayed as the main text
        composeTestRule.onNodeWithText("MOTOR_01").assertDoesNotExist()

        // 6. Clear custom label and verify fallback
        composeTestRule.onNodeWithContentDescription("Edit widget").performClick()
        // Target the TextField specifically to avoid ambiguity with the widget label
        composeTestRule.onNode(hasText("Main Conveyor") and hasSetTextAction()).performTextReplacement("") 
        composeTestRule.onNodeWithText("Save").performClick()

        // 7. Verify it falls back to tag address
        composeTestRule.onNodeWithText("MOTOR_01").assertExists()
    }
}
