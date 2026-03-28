package com.example.hmi.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class OrientationMode {
    AUTO,
    LANDSCAPE,
    PORTRAIT
}

@Serializable
data class DashboardLayout(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default Layout",
    val canvasColor: Long? = null,
    val widgets: List<WidgetConfiguration> = emptyList(),
    val isDarkThemeMigrated: Boolean = false,
    val isKineticCockpitMigrated: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val orientationMode: OrientationMode = OrientationMode.AUTO
)
