package com.example.hmi.data

import java.util.UUID

enum class OrientationMode {
    AUTO,
    FORCE_LANDSCAPE,
    FORCE_PORTRAIT
}

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
