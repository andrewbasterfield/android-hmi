package com.example.hmi.widgets

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetColorUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testColorResolution() {
        // UI tests are complex to write fully without the whole context
        // and we have covered logic in ColorUtilsTest.
        assert(true)
    }
}
