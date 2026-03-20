package com.example.hmi.data

import java.util.UUID

data class DashboardLayout(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default Layout",
    val canvasColor: Long? = 0xFF000000uL.toLong(),
    val widgets: List<WidgetConfiguration> = emptyList(),
    val isDarkThemeMigrated: Boolean = false,
    val isKineticCockpitMigrated: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true
)
