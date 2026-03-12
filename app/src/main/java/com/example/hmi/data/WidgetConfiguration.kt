package com.example.hmi.data

import java.util.UUID

enum class WidgetType { BUTTON, SLIDER, GAUGE }

data class WidgetConfiguration(
    val id: String = UUID.randomUUID().toString(),
    val type: WidgetType,
    val x: Float,
    val y: Float,
    val width: Float = 100f,
    val height: Float = 100f,
    val tagAddress: String,
    val backgroundColor: Long? = null,
    val minValue: Float? = null,
    val maxValue: Float? = null
)
