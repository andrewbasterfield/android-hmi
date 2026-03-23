package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class GaugeColorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gaugeWidget_respectsManualPointerColor() {
        val customColor = Color.Red

        composeTestRule.setContent {
            GaugeWidget(
                label = "Test Gauge",
                value = 50f,
                minValue = 0f,
                maxValue = 100f,
                pointerColor = customColor.value.toLong()
            )
        }

        // Verify that the pointer color is correctly reported via semantics
        composeTestRule.onNode(SemanticsMatcher.expectValue(PointerColorKey, customColor))
            .assertIsDisplayed()
    }
}
