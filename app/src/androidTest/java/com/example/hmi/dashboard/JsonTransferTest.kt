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
class JsonTransferTest {

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
    fun json_export_flow() {
        connectToDemoBackend()

        // 1. Enter Edit Mode
        composeTestRule.onNodeWithText("Edit Mode").performClick()

        // 2. Open Dashboard Settings
        composeTestRule.onNodeWithText("Dashboard Settings").performClick()

        // 2.5 Open JSON Transfer sub-dialog
        composeTestRule.onNodeWithText("JSON Transfer (Import/Export)").performClick()

        // 3. Verify JSON Transfer title exists in the sub-dialog
        composeTestRule.onNodeWithText("JSON Transfer").assertExists()
        
        // 4. Verify Layout JSON field is populated (contains initial layout name)
        // Use testTag to avoid ambiguity
        composeTestRule.onNodeWithTag("LayoutJsonField").assertTextContains("Default Layout", substring = true)

        // 5. Test Copy button
        composeTestRule.onNodeWithText("Copy").performClick()
    }

    @Test
    fun json_import_valid_layout() {
        connectToDemoBackend()

        // 1. Enter Edit Mode
        composeTestRule.onNodeWithText("Edit Mode").performClick()

        // 2. Open Dashboard Settings
        composeTestRule.onNodeWithText("Dashboard Settings").performClick()

        // 2.5 Open JSON Transfer sub-dialog
        composeTestRule.onNodeWithText("JSON Transfer (Import/Export)").performClick()

        // 3. Paste a new layout JSON
        val newLayoutJson = """
            {
              "name": "Imported Via Test",
              "widgets": [
                {
                  "type": "BUTTON",
                  "tagAddress": "TEST_TAG",
                  "column": 0,
                  "row": 0
                }
              ]
            }
        """.trimIndent()

        // Find the TextField by tag and replace content
        composeTestRule.onNodeWithTag("LayoutJsonField").performTextReplacement(newLayoutJson)

        // 4. Apply Import (Button text is "Import")
        composeTestRule.onNodeWithText("Import").performClick()

        // 5. Verify Dashboard updates (title changes)
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Imported Via Test").fetchSemanticsNodes().isNotEmpty()
        }
        
        // 6. Verify widget exists
        composeTestRule.onNodeWithText("TEST_TAG").assertExists()
    }

    @Test
    fun json_import_invalid_layout_shows_error() {
        connectToDemoBackend()

        // 1. Enter Edit Mode
        composeTestRule.onNodeWithText("Edit Mode").performClick()

        // 2. Open Dashboard Settings
        composeTestRule.onNodeWithText("Dashboard Settings").performClick()

        // 2.5 Open JSON Transfer sub-dialog
        composeTestRule.onNodeWithText("JSON Transfer (Import/Export)").performClick()

        // 3. Paste invalid JSON
        composeTestRule.onNodeWithTag("LayoutJsonField").performTextReplacement("invalid random string")

        // 4. Apply Import
        composeTestRule.onNodeWithText("Import").performClick()

        // 5. Dialog should remain open
        // Try finding by tag if text is ambiguous or use useUnmergedTree
        composeTestRule.onNodeWithText("Layout JSON", useUnmergedTree = true).assertExists()
        
        composeTestRule.onNodeWithText("Close").performClick() // Sub-dialog close button is "Close"
        composeTestRule.onNodeWithText("Cancel").performClick() // Main dialog dismiss button is "Cancel"
        composeTestRule.onNodeWithText("Default Layout").assertExists()
    }
}
