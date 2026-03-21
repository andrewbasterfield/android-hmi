package com.example.hmi.data

import java.util.UUID

enum class WidgetType { BUTTON, SLIDER, GAUGE }

enum class AlarmState { Normal, Unacknowledged, Acknowledged }

data class WidgetConfiguration(
    val id: String = UUID.randomUUID().toString(),
    val type: WidgetType,
    val column: Int = 0,
    val row: Int = 0,
    val colSpan: Int = 1,
    val rowSpan: Int = 1,
    val tagAddress: String,
    val customLabel: String? = null,
    val backgroundColor: Long? = null,
    val textColor: Long? = null, // Replaced String with Long? for full color selector
    val pressedFillColor: Long? = null, // Kept in model for backward compat, but UI will remove
    val fontSizeMultiplier: Float = 1.0f,
    val textColorOverride: String? = null, // Deprecated: use textColor instead
    val minValue: Float? = null,
    val maxValue: Float? = null,
    val targetTicks: Int = 6,
    val colorZones: List<GaugeZone> = emptyList(),
    val needleColor: Long? = null,
    val isNeedleDynamic: Boolean = false,
    val units: String? = null,
    val alarmState: AlarmState = AlarmState.Normal
)
