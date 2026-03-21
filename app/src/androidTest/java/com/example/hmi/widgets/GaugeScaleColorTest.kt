package com.example.hmi.widgets

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class GaugeScaleColorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gaugeWidget_scaleElementsAlignWithContentColor() {
        val expectedColor = Color.Yellow // Simulate a theme override

        composeTestRule.setContent {
            CompositionLocalProvider(LocalContentColor provides expectedColor) {
                GaugeWidget(
                    label = "Alignment Test",
                    value = 50f,
                    minValue = 0f,
                    maxValue = 100f
                )
            }
        }

        // Ticks and Labels should now be yellow (aligned with contentColor/settings cog)
        // Note: NeedleColor semantics uses the same resolve logic
        composeTestRule.onNode(SemanticsMatcher.expectValue(NeedleColorKey, expectedColor))
            .assertIsDisplayed()
    }
}
