package com.example.hmi.data

import java.util.UUID

enum class WidgetType { BUTTON, SLIDER, GAUGE }

enum class GaugeStyle { POINTER, ARC_FILL }

enum class AlarmState { Normal, Unacknowledged, Acknowledged }

enum class InteractionType { MOMENTARY, LATCHING, INDICATOR }

data class WidgetConfiguration(
    val id: String = UUID.randomUUID().toString(),
    val type: WidgetType = WidgetType.BUTTON,
    val column: Int = 0,
    val row: Int = 0,
    val colSpan: Int = 1,
    val rowSpan: Int = 1,
    val tagAddress: String = "",
    val customLabel: String? = null,
    val backgroundColor: Long? = null,
    val textColor: Long? = null,
    val pressedFillColor: Long? = null,
    val labelFontSizeMultiplier: Float = 1.0f,
    val metricFontSizeMultiplier: Float = 1.0f,
    val fontSizeMultiplier: Float? = null,
    val textColorOverride: String? = null,
    val minValue: Float? = null,
    val maxValue: Float? = null,
    val targetTicks: Int = 6,
    val arcSweep: Float = 180f,
    val colorZones: List<GaugeZone> = emptyList(),
    val pointerColor: Long? = null,
    val isPointerDynamic: Boolean = true,
    val gaugeStyle: GaugeStyle = GaugeStyle.POINTER,
    val units: String? = null,
    val alarmState: AlarmState = AlarmState.Normal,
    val showOutline: Boolean = false,
    val zOrder: Int = 0,
    val interactionType: InteractionType = InteractionType.MOMENTARY,
    val isInverted: Boolean = false
)
