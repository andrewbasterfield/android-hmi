package com.example.hmi.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import com.example.hmi.TestActivity
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.theme.Void
import com.example.hmi.widgets.SliderWidget
import com.example.hmi.widgets.GaugeWidget
import com.example.hmi.widgets.TrackBackgroundColorKey
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@HiltAndroidTest
class AutomaticColorTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun widgetContainer_automaticBackground_matchesLayoutObsidian() {
        composeTestRule.setContent {
            StitchTheme {
                // backgroundColor = null means "Automatic"
                WidgetContainer(
                    backgroundColor = null,
                    modifier = Modifier.testTag("AutomaticContainer")
                ) {
                    androidx.compose.material3.Text("Automatic Background Test")
                }
            }
        }

        val expectedColor = Void
        
        // Assert the semantics property we exposed
        composeTestRule.onNodeWithTag("AutomaticContainer")
            .assert(SemanticsMatcher.expectValue(TrackBackgroundColorKey, expectedColor))
    }

    @Test
    fun slider_trackBackground_matchesLayoutObsidian() {
        composeTestRule.setContent {
            StitchTheme {
                SliderWidget(
                    label = "Test Slider",
                    value = 50f,
                    onValueChange = {},
                    modifier = Modifier.testTag("SliderNode")
                )
            }
        }

        val expectedColor = Void
        
        // Assert the track background color we exposed via semantics in SliderWidget
        // Note: The semantics is on the track Box inside SliderWidget
        composeTestRule.onNode(SemanticsMatcher.expectValue(TrackBackgroundColorKey, expectedColor))
            .assertIsDisplayed()
    }

    @Test
    fun gauge_arcBackground_matchesLayoutObsidian() {
        composeTestRule.setContent {
            StitchTheme {
                GaugeWidget(
                    label = "Test Gauge",
                    value = 50f,
                    minValue = 0f,
                    maxValue = 100f,
                    modifier = Modifier.testTag("GaugeNode")
                )
            }
        }

        val expectedColor = Void
        
        // Assert the arc background color we exposed via semantics in GaugeWidget
        composeTestRule.onNode(SemanticsMatcher.expectValue(TrackBackgroundColorKey, expectedColor))
            .assertIsDisplayed()
    }
}
