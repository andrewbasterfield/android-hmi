package com.example.hmi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.hmi.TestActivity
import com.example.hmi.ui.components.HmiColorPicker
import com.example.hmi.ui.theme.HmiPalette
import com.example.hmi.widgets.ColorUtils
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
    fun colorPicker_displaysAllPaletteColors() {
        composeTestRule.setContent {
            HmiColorPicker(
                selectedColor = null,
                onColorSelected = {}
            )
        }

        HmiPalette.WidgetBackgrounds.forEach { color ->
            val hex = ColorUtils.formatHexColor(color)
            composeTestRule.onNodeWithContentDescription("Select color $hex").assertIsDisplayed()
        }
    }

    @Test
    fun colorPicker_selectsColorOnClick() {
        var selectedColor: Long? = null
        
        composeTestRule.setContent {
            HmiColorPicker(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
        }

        val firstColor = HmiPalette.WidgetBackgrounds[0]
        val hex = ColorUtils.formatHexColor(firstColor)
        
        composeTestRule.onNodeWithContentDescription("Select color $hex")
            .performClick()

        assertEquals(firstColor.value.toLong(), selectedColor)
    }

    @Test
    fun colorPicker_showsTabs() {
        composeTestRule.setContent {
            HmiColorPicker(
                selectedColor = null,
                onColorSelected = {}
            )
        }

        composeTestRule.onNodeWithText("Palette").assertIsDisplayed()
        composeTestRule.onNodeWithText("Spectrum").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hex").assertIsDisplayed()
    }
}
