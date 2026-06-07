package com.example.hmi.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hmi.dashboard.components.ManagementHubDrawer
import com.example.hmi.data.SystemProfile
import com.example.hmi.protocol.ConnectionState
import org.junit.Rule
import org.junit.Test

class ManagementHubTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun managementHubDrawer_showsAllSections() {
        composeTestRule.setContent {
            ManagementHubDrawer(
                systemProfiles = listOf(
                    SystemProfile(name = "Test Profile", connectionProfileName = "PLC1", layoutId = "L1")
                ),
                activeProfileId = null,
                connections = emptyList(),
                layouts = emptyList(),
                activeConnection = null,
                activeLayout = null,
                isModified = false,
                connectionState = ConnectionState.DISCONNECTED,
                onProfileSelect = {},
                onProfileShare = {},
                onConnectionSelect = {},
                onLayoutSelect = {},
                onSaveProfile = {}
            )
        }

        // Verify sections
        composeTestRule.onNodeWithText("Management Hub").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Environment").assertIsDisplayed()
        composeTestRule.onNodeWithText("Systems (Presets)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Library: Connections").assertIsDisplayed()
        composeTestRule.onNodeWithText("Library: Layouts").assertIsDisplayed()
        
        // Verify preset item
        composeTestRule.onNodeWithText("Test Profile").assertIsDisplayed()
    }
}
