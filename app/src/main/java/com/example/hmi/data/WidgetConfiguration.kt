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
    val backgroundColor: Long? = null,
    val minValue: Float? = null,
    val maxValue: Float? = null
)
