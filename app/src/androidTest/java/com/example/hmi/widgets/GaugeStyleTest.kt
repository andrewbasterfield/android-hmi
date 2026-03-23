package com.example.hmi.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.hmi.data.GaugeStyle
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.dashboard.WidgetConfigDialog
import org.junit.Rule
import org.junit.Test

class GaugeStyleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun widgetConfigDialog_showsStyleSelectorForGauge() {
        composeTestRule.setContent {
            WidgetConfigDialog(
                initialWidget = WidgetConfiguration(
                    type = WidgetType.GAUGE,
                    tagAddress = "TEST_TAG"
                ),
                onDismiss = {},
                onConfirm = {}
            )
        }

        // Verify style chips are visible
        composeTestRule.onNodeWithText("POINTER").assertIsDisplayed()
        composeTestRule.onNodeWithText("ARC_FILL").assertIsDisplayed()
    }

    @Test
    fun gaugeWidget_rendersArcFillWithCorrectDescription() {
        composeTestRule.setContent {
            GaugeWidget(
                label = "Style Test",
                value = 50f,
                minValue = 0f,
                maxValue = 100f,
                gaugeStyle = GaugeStyle.ARC_FILL
            )
        }

        // Verify content description includes (filled to 50%)
        composeTestRule.onNodeWithContentDescription("Gauge for Style Test showing 50.0 (filled to 50%)")
            .assertIsDisplayed()
    }

    @Test
    fun gaugeWidget_arcFillMatchesZoneColor() {
        val zoneColor = androidx.compose.ui.graphics.Color.Red
        val colorZones = listOf(
            com.example.hmi.data.GaugeZone(80f, 100f, zoneColor.value.toLong())
        )

        composeTestRule.setContent {
            GaugeWidget(
                label = "Zone Color Test",
                value = 90f,
                minValue = 0f,
                maxValue = 100f,
                colorZones = colorZones,
                isPointerDynamic = true,
                gaugeStyle = GaugeStyle.ARC_FILL
            )
        }

        // I need to ensure pointerColor semantics are exposed even in ARC_FILL mode for testing
        // I'll update GaugeWidget to do this.
        composeTestRule.onNode(androidx.compose.ui.test.SemanticsMatcher.expectValue(PointerColorKey, zoneColor))
            .assertIsDisplayed()
    }
}
