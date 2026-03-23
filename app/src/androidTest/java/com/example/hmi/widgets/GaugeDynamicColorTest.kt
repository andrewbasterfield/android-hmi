package com.example.hmi.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hmi.data.GaugeZone
import org.junit.Rule
import org.junit.Test

class GaugeDynamicColorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gaugeWidget_pointerFollowsZoneColorWhenValueInZone() {
        val zoneColor = Color.Red
        val defaultColor = Color.Blue
        val colorZones = listOf(
            GaugeZone(80f, 100f, zoneColor.value.toLong())
        )

        composeTestRule.setContent {
            GaugeWidget(
                label = "Dynamic Test In",
                value = 90f,
                minValue = 0f,
                maxValue = 100f,
                colorZones = colorZones,
                isPointerDynamic = true,
                pointerColor = defaultColor.value.toLong()
            )
        }

        composeTestRule.onNode(SemanticsMatcher.expectValue(PointerColorKey, zoneColor))
            .assertIsDisplayed()
    }

    @Test
    fun gaugeWidget_pointerFallsBackToStaticWhenOutsideZone() {
        val zoneColor = Color.Red
        val defaultColor = Color.Blue
        val colorZones = listOf(
            GaugeZone(80f, 100f, zoneColor.value.toLong())
        )

        composeTestRule.setContent {
            GaugeWidget(
                label = "Dynamic Test Out",
                value = 50f,
                minValue = 0f,
                maxValue = 100f,
                colorZones = colorZones,
                isPointerDynamic = true,
                pointerColor = defaultColor.value.toLong()
            )
        }

        composeTestRule.onNode(SemanticsMatcher.expectValue(PointerColorKey, defaultColor))
            .assertIsDisplayed()
    }
}
