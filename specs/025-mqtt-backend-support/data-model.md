# Data Model: MQTT Backend Support

## Entities

### PlcConnectionProfile (Updated)
Represents the user-configured connection to a backend PLC or IoT broker.

- **id**: String (UUID)
- **name**: String (Human-readable name)
- **protocol**: Enum (RAW_TCP, MQTT, MODBUS_TCP, OPC_UA)
- **host**: String (IP Address or Hostname)
- **port**: Int (Default: 9999 for TCP, 1883 for MQTT)
- **mqttSettings**: Optional<MqttSettings>

### MqttSettings
Specific configuration for MQTT-based connections.

- **clientId**: String (Unique identifier for the broker)
- **username**: String? (Optional credentials)
- **password**: String? (Optional credentials)
- **rootTopicPrefix**: String? (Optional, prepended to all tag topics)
- **statusTopic**: String (For LWT and "online" signaling)
- **payloadFormat**: Enum (PLAIN_TEXT, JSON)
- **jsonKey**: String? (Key to extract from JSON payload, defaults to "value")

## Relationships

1. **PlcConnectionProfile** is persisted in `DashboardRepository` via Jetpack DataStore (serialized JSON).
2. **PlcCommunicatorDispatcher** selects the correct implementation (`RawTcpPlcCommunicator` or `MqttPlcCommunicator`) based on the `protocol` field.

## State Transitions

### MQTT Connection Lifecycle
- **DISCONNECTED**: Initial state or after explicit `disconnect()`.
- **CONNECTING**: While `connect()` is in progress.
- **CONNECTED**: Session established, subscriptions active, and "online" published to status topic.
- **ERROR**: Failed connection or abrupt disconnection (LWT triggered by broker).
