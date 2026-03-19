package com.example.hmi.data

/**
 * Defines a specific range and color for a Gauge widget's arc.
 */
data class GaugeZone(
    val startValue: Float,
    val endValue: Float,
    val color: Long,
    val label: String? = null
)
