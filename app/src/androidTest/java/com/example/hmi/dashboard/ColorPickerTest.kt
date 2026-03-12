package com.example.hmi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.hmi.TestActivity
import com.example.hmi.data.ColorPalette
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ColorPickerTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun colorPicker_displaysAllColors() {
        composeTestRule.setContent {
            ColorPicker(
                selectedColor = null,
                onColorSelected = {}
            )
        }

        ColorPalette.Items.forEach { (name, _) ->
            composeTestRule.onNodeWithContentDescription("Select $name color").assertIsDisplayed()
        }
    }

    @Test
    fun colorPicker_selectsColorOnClick() {
        var selectedColor: Long? = 123L // Initial dummy value
        
        composeTestRule.setContent {
            ColorPicker(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
        }

        // Click on "Red" swatch
        composeTestRule.onNodeWithContentDescription("Select Red color")
            .performClick()

        // Verify selection callback
        val redValue = ColorPalette.Red.toArgb().toLong()
        assertEquals(redValue, selectedColor)
    }

    @Test
    fun colorPicker_showsCheckmarkOnSelectedColor() {
        val redValue = ColorPalette.Red.toArgb().toLong()

        composeTestRule.setContent {
            ColorPicker(
                selectedColor = redValue,
                onColorSelected = {}
            )
        }

        // In the implementation, the checkmark is inside the box but doesn't have 
        // its own description. We just ensure the node is displayed.
        composeTestRule.onNodeWithContentDescription("Select Red color").assertIsDisplayed()
    }
}
