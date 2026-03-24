package com.example.hmi.data

import com.example.hmi.protocol.PlcConnectionProfile

data class FullBackupPackage(
    val version: Int = 1,
    val layout: DashboardLayout? = null,
    val profiles: List<PlcConnectionProfile>? = null
)
