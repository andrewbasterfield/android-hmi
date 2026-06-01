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
) {
    companion object {
        fun createDemoLayout(): DashboardLayout {
            return DashboardLayout(
                name = "Demo Layout",
                widgets = listOf(
                    WidgetConfiguration(
                        type = WidgetType.TEXT,
                        customLabel = "Welcome to the HMI Demo. This dashboard is showing simulated data from the local server. To connect to a real device or create a custom layout, use the settings menu.",
                        row = 0,
                        column = 1,
                        colSpan = 8,
                        rowSpan = 2
                    ),
                    WidgetConfiguration(
                        type = WidgetType.GAUGE,
                        tagAddress = "SIM_TEMP",
                        customLabel = "Temperature",
                        units = "°C",
                        minValue = 0f,
                        maxValue = 100f,
                        row = 2,
                        column = 1,
                        colSpan = 4,
                        rowSpan = 4
                    ),
                    WidgetConfiguration(
                        type = WidgetType.GAUGE,
                        tagAddress = "SIM_PRESSURE",
                        customLabel = "Pressure",
                        units = "kPa",
                        minValue = 0f,
                        maxValue = 200f,
                        row = 2,
                        column = 5,
                        colSpan = 4,
                        rowSpan = 4
                    ),
                    WidgetConfiguration(
                        type = WidgetType.BUTTON,
                        interactionType = InteractionType.INDICATOR,
                        tagAddress = "SIM_STATUS",
                        customLabel = "System Status",
                        row = 6,
                        column = 3,
                        colSpan = 4,
                        rowSpan = 2
                    )
                )
            )
        }
    }
}
