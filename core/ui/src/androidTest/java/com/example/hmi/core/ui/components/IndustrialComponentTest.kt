package com.example.hmi.core.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.utils.ShapeKey
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
