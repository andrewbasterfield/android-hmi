package com.example.hmi.data

import java.util.UUID

enum class WidgetType { BUTTON, SLIDER, GAUGE }

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
    val fontSizeMultiplier: Float = 1.0f,
    val textColorOverride: String? = null, // "BLACK", "WHITE", or null (AUTO)
    val minValue: Float? = null,
    val maxValue: Float? = null,
    val colorZones: List<GaugeZone> = emptyList()
)
