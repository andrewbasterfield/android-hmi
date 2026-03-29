# Data Model: MQTT JSON Payload Support

## Core Entities

### WidgetConfiguration (Updated)
Represents the configuration of a single UI widget on the dashboard.

| Field | Type | Description |
|-------|------|-------------|
| **jsonPath** | String? | Optional dot-notation path to extract from a JSON payload. |
| **writeTemplate** | String? | Optional template for outgoing JSON messages, substituting `$VALUE`. |

### MqttSettings (Existing)
Global MQTT connection settings for a profile.

| Field | Type | Description |
|-------|------|-------------|
| **payloadFormat** | MqttPayloadFormat | `PLAIN_TEXT` or `JSON`. |
| **jsonKey** | String? | Default key to extract if `jsonPath` is not specified on the widget. |

## Relationships

- **WidgetConfiguration** uses **jsonPath** to override the default **jsonKey** in **MqttSettings** during message processing in the **MqttPlcCommunicator**.
- The **MqttPlcCommunicator** caches raw topic subscriptions, allowing multiple **WidgetConfiguration** instances to share a single MQTT topic stream.

## Validation Rules

1. **jsonPath**: Must be a dot-separated string (e.g., `temp` or `sensor.motor.rpm`). No leading or trailing dots allowed.
2. **writeTemplate**: May contain zero or more `$VALUE` tokens. All occurrences are replaced with the formatted value. If zero occurrences, the template is sent as-is (static command).

## Open Design Questions

1. **Read/Write asymmetry**: If a widget has `jsonPath = "humidity"` but no `writeTemplate`, writes send the raw value (e.g., `70`), not `{"humidity": 70}`. This means reading from JSON and writing plain text. This is intentional — many systems (e.g., Home Assistant) use entirely different schemas for state vs command topics. If a JSON write is needed, the user must explicitly set a `writeTemplate`.
