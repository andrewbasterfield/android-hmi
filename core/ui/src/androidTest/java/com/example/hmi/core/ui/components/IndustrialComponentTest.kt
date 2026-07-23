package com.example.hmi.core.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.utils.ShapeKey
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class IndustrialComponentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun industrialButton_hasMinHeight64dp() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialButton(onClick = {}, label = "TEST")
            }
        }

        composeTestRule.onNodeWithText("TEST", useUnmergedTree = true)
            .onParent()
            .assertHeightIsAtLeast(64.dp)
    }

    @Test
    fun industrialButton_doesNotFireOnReleaseOnInitialComposition() {
        var releaseCount = 0
        composeTestRule.setContent {
            StitchTheme {
                IndustrialButton(onClick = {}, onRelease = { releaseCount++ }, label = "TEST")
            }
        }

        composeTestRule.waitForIdle()

        // A momentary button must not publish its "off" payload just because it
        // entered composition; only a real press-then-release should trigger onRelease.
        assertEquals(0, releaseCount)
    }

    @Test
    fun industrialButton_firesOnReleaseAfterPressAndRelease() {
        var pressCount = 0
        var releaseCount = 0
        composeTestRule.setContent {
            StitchTheme {
                IndustrialButton(
                    onClick = {},
                    onPress = { pressCount++ },
                    onRelease = { releaseCount++ },
                    label = "TEST"
                )
            }
        }

        val button = composeTestRule.onNodeWithText("TEST", useUnmergedTree = true).onParent()
        button.performTouchInput { down(center) }
        composeTestRule.waitForIdle()
        assertEquals(1, pressCount)
        assertEquals(0, releaseCount)

        button.performTouchInput { up() }
        composeTestRule.waitForIdle()
        assertEquals(1, releaseCount)
    }

    @Test
    fun industrialButton_firesOnReleaseWhenRemovedFromCompositionMidPress() {
        var releaseCount = 0
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            StitchTheme {
                if (visible) {
                    IndustrialButton(onClick = {}, onRelease = { releaseCount++ }, label = "TEST")
                }
            }
        }

        val button = composeTestRule.onNodeWithText("TEST", useUnmergedTree = true).onParent()
        button.performTouchInput { down(center) }
        composeTestRule.waitForIdle()
        assertEquals(0, releaseCount)

        // Simulate a page swipe carrying the widget out of the viewport while the
        // finger is still down: the widget leaves composition without a touch-up.
        visible = false
        composeTestRule.waitForIdle()

        assertEquals(1, releaseCount)
    }

    @Test
    fun industrialInput_hasMinHeight64dp() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialInput(value = "", onValueChange = {}, label = "TEST")
            }
        }
        
        composeTestRule.onNodeWithText("TEST")
            .onParent()
            .assertHeightIsAtLeast(64.dp)
    }

    @Test
    fun industrialButton_usesSmallShape() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialButton(onClick = {}, label = "TEST")
            }
        }

        composeTestRule.onNodeWithText("TEST", useUnmergedTree = true)
            .onParent()
            .assert(SemanticsMatcher.expectValue(ShapeKey, "small"))
    }

    @Test
    fun industrialInput_usesSmallShape() {
        composeTestRule.setContent {
            StitchTheme {
                IndustrialInput(value = "", onValueChange = {}, label = "TEST")
            }
        }

        composeTestRule.onNodeWithText("TEST")
            .onParent()
            .assert(SemanticsMatcher.expectValue(ShapeKey, "small"))
    }
}
