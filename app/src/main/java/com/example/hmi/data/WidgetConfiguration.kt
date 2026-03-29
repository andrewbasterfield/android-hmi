package com.example.hmi.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class WidgetType { BUTTON, SLIDER, GAUGE }

@Serializable
enum class GaugeAxis { ARC, LINEAR_HORIZONTAL, LINEAR_VERTICAL }

@Serializable
enum class GaugeIndicator { POINTER, FILL }

@Serializable
enum class GaugeStyle { POINTER, ARC_FILL }

@Serializable
enum class AlarmState { Normal, Unacknowledged, Acknowledged }

@Serializable
enum class InteractionType { MOMENTARY, LATCHING, INDICATOR }

@Serializable
enum class WidgetOrientation { HORIZONTAL, VERTICAL }

@Serializable
data class WidgetConfiguration(
    val id: String = UUID.randomUUID().toString(),
    val type: WidgetType = WidgetType.BUTTON,
    val orientation: WidgetOrientation = WidgetOrientation.HORIZONTAL,
    val column: Int = 0,
    val row: Int = 0,
    val colSpan: Int = 1,
    val rowSpan: Int = 1,
    val tagAddress: String = "",
    val writeAddress: String? = null,
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
    val gaugeAxis: GaugeAxis = GaugeAxis.ARC,
    val gaugeIndicator: GaugeIndicator = GaugeIndicator.POINTER,
    val gaugeStyle: GaugeStyle = GaugeStyle.POINTER,
    val decimalPlaces: Int = 1,
    val units: String? = null,
    val alarmState: AlarmState = AlarmState.Normal,
    val showOutline: Boolean = false,
    val zOrder: Int = 0,
    val interactionType: InteractionType = InteractionType.MOMENTARY,
    val isInverted: Boolean = false,
    val trueValues: List<String> = listOf("true", "1", "on"),
    val falseValues: List<String> = listOf("false", "0", "off"),
    val jsonPath: String? = null,
    val writeTemplate: String? = null
)
