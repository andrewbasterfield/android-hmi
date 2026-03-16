package com.example.hmi.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.TestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HexValidationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun enteringValidHex_updatesColor() {
        var capturedColor: Color? = null
        composeTestRule.setContent {
            HexEntryField(
                initialColor = Color.Black,
                onColorChanged = { capturedColor = it }
            )
        }

        composeTestRule.onNodeWithTag("HexInputField").performTextClearance()
        composeTestRule.onNodeWithTag("HexInputField").performTextInput("FF0000")
        composeTestRule.waitForIdle()
        assertEquals(Color.Red, capturedColor)
    }

    @Test
    fun enteringInvalidHex_emitsNullColor() {
        var capturedColor: Color? = null
        composeTestRule.setContent {
            HexEntryField(
                initialColor = Color.Black,
                onColorChanged = { capturedColor = it }
            )
        }

        composeTestRule.onNodeWithTag("HexInputField").performTextClearance()
        composeTestRule.onNodeWithTag("HexInputField").performTextInput("GGGG")
        composeTestRule.waitForIdle()
        assertNull(capturedColor)
        composeTestRule.onNodeWithText("Enter a valid 6-digit hex code").assertExists()
    }

    @Test
    fun hexField_onlyAccepts6Characters() {
        composeTestRule.setContent {
            HexEntryField(
                initialColor = Color.Black,
                onColorChanged = {}
            )
        }

        composeTestRule.onNodeWithTag("HexInputField").performTextClearance()
        composeTestRule.onNodeWithTag("HexInputField").performTextInput("ABCDEF123")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("HexInputField").assertTextContains("ABCDEF")
    }
}
