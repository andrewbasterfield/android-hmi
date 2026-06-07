package com.example.hmi.data

import com.example.hmi.protocol.PlcConnectionProfile
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * A persistent binding between a specific connection and a specific dashboard layout.
 */
@Serializable
data class SystemProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val connectionProfileName: String,
    val layoutId: String,
    val isReadOnly: Boolean = false
)

/**
 * A standalone package for sharing a single System Profile with all its dependencies.
 */
@Serializable
data class SystemProfileBundle(
    val profile: SystemProfile,
    val layout: DashboardLayout,
    val connection: PlcConnectionProfile
)
