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
                        type = WidgetType.GAUGE,
                        tagAddress = "SIM_TEMP",
                        customLabel = "Temperature",
                        units = "°C",
                        minValue = 0f,
                        maxValue = 100f,
                        targetTicks = 11,
                        arcSweep = 240f,
                        colorZones = listOf(
                            GaugeZone(85f, 100f, 0xFFFF0000uL.toLong(), "CRITICAL"),
                            GaugeZone(70f, 84.9f, 0xFFFF9900uL.toLong(), "Warning"),
                            GaugeZone(0f, 69.9f, 0xFF00FF00uL.toLong(), "Optimal")
                        ),
                        row = 1,
                        column = 0,
                        colSpan = 5,
                        rowSpan = 4
                    ),
                    WidgetConfiguration(
                        type = WidgetType.GAUGE,
                        tagAddress = "SIM_PRESSURE",
                        customLabel = "Pressure",
                        units = "kPa",
                        minValue = 0f,
                        maxValue = 200f,
                        targetTicks = 11,
                        arcSweep = 120f,
                        colorZones = listOf(
                            GaugeZone(170f, 200f, 0xFFFF0000uL.toLong(), "High"),
                            GaugeZone(140f, 169.9f, 0xFFFF9900uL.toLong(), "Elevated"),
                            GaugeZone(0f, 139.9f, 0xFF00FF00uL.toLong(), "Normal")
                        ),
                        row = 0,
                        column = 5,
                        colSpan = 5,
                        rowSpan = 3
                    ),
                    WidgetConfiguration(
                        type = WidgetType.SLIDER,
                        tagAddress = "SIM_TARGET",
                        writeAddress = "SIM_TARGET",
                        customLabel = "Target Setpoint",
                        units = "%",
                        minValue = 0f,
                        maxValue = 100f,
                        row = 6,
                        column = 0,
                        colSpan = 10,
                        rowSpan = 2
                    ),
                    WidgetConfiguration(
                        type = WidgetType.BUTTON,
                        interactionType = InteractionType.LATCHING,
                        tagAddress = "SIM_STATUS",
                        writeAddress = "SIM_STATUS",
                        customLabel = "ENABLED",
                        backgroundColor = -71777218572845056L,
                        textColor = -72057594037927936L,
                        isInverted = true,
                        row = 8,
                        column = 2,
                        colSpan = 3,
                        rowSpan = 2
                    ),
                    WidgetConfiguration(
                        type = WidgetType.BUTTON,
                        interactionType = InteractionType.INDICATOR,
                        tagAddress = "SIM_STATUS",
                        customLabel = "DRYING",
                        backgroundColor = -1099511627776L,
                        textColor = -72057594037927936L,
                        isInverted = true,
                        row = 8,
                        column = 6,
                        colSpan = 3,
                        rowSpan = 2
                    )
                )
            )
        }
    }
}
