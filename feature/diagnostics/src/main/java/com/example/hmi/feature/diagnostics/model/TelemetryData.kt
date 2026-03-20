package com.example.hmi.feature.diagnostics.model

import com.example.hmi.core.ui.theme.HealthStatus

data class TelemetryData(
    val id: String,
    val label: String,
    val value: String,
    val unit: String,
    val status: HealthStatus,
    val trend: Float? = null
)
