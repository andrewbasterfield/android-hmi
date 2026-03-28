package com.example.hmi.data

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Defines a specific range and color for a Gauge widget's arc.
 */
@Serializable
data class GaugeZone(
    val startValue: Float,
    val endValue: Float,
    val color: Long,
    val label: String? = null,
    val id: String = UUID.randomUUID().toString()
)
