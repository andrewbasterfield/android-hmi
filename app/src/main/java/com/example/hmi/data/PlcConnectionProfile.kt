package com.example.hmi.data

import java.util.UUID

enum class Protocol {
    RAW_TCP, MODBUS_TCP, OPC_UA
}

data class PlcConnectionProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My PLC",
    val ipAddress: String = "192.168.1.100",
    val port: Int = 9999,
    val protocol: Protocol = Protocol.RAW_TCP
)
