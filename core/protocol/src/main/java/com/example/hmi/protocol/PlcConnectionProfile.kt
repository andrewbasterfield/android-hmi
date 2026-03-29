package com.example.hmi.protocol

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class Protocol {
    RAW_TCP, MQTT, MODBUS_TCP, OPC_UA
}

@Serializable
data class MqttSettings(
    val clientId: String = "hmi-client-" + UUID.randomUUID().toString().take(8),
    val username: String? = null,
    val password: String? = null,
    val rootTopicPrefix: String? = null,
    val payloadFormat: MqttPayloadFormat = MqttPayloadFormat.PLAIN_TEXT,
    val jsonKey: String? = "value"
)

@Serializable
enum class MqttPayloadFormat {
    PLAIN_TEXT, JSON
}

@Serializable
data class PlcConnectionProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My PLC",
    val ipAddress: String = "192.168.1.100",
    val port: Int = 9999,
    val protocol: Protocol = Protocol.RAW_TCP,
    val mqttSettings: MqttSettings? = null
)
