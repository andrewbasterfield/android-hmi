package com.example.hmi.data

import java.util.UUID

data class DashboardLayout(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default Layout",
    val canvasColor: Long? = null,
    val widgets: List<WidgetConfiguration> = emptyList()
)
