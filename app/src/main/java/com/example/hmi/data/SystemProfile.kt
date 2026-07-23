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
 * Note: Exports use FullBackupPackage V2 as the wire format for schema reuse.
 * This class is retained for serialization contracts and test validation.
 */
@Serializable
data class SystemProfileBundle(
    val profile: SystemProfile,
    val layout: DashboardLayout,
    val connection: PlcConnectionProfile
)
