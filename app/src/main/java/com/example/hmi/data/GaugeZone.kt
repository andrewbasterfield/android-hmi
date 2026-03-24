package com.example.hmi.data

import java.util.UUID

/**
 * Defines a specific range and color for a Gauge widget's arc.
 */
data class GaugeZone(
    val startValue: Float,
    val endValue: Float,
    val color: Long,
    val label: String? = null,
    val id: String = UUID.randomUUID().toString()
)
