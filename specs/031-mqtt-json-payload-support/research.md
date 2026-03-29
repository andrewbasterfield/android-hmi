# Research: MQTT JSON Payload Support

## Decision 1: Shared Topic Subscription Cache

- **Decision**: Use a `Map<String, Flow<String>>` in `MqttPlcCommunicator` with `SharedFlow` created via `shareIn`.
- **Rationale**: This allows multiple widgets observing the same topic to share a single MQTT subscription. `shareIn(WhileSubscribed)` ensures the subscription is active only when needed, reducing network overhead.
- **Alternatives considered**: 
    - Separate subscriptions for each widget (too much overhead, higher cost on MQTT broker).
    - Manual reference counting (too error-prone, `shareIn` is built-in and reliable).

## Decision 2: Dot-Notation JSON Path Parsing

- **Decision**: Implement a custom dot-separated key traversal utility using `kotlinx.serialization.json.JsonElement`.
- **Rationale**: A full JSONPath library is too heavy for HMI use-cases. Standard dot-notation (e.g., `status.temp`) covers 99% of industrial schemas (Zigbee2MQTT, Home Assistant). Using `kotlinx.serialization` (already in `core:protocol`) avoids new dependencies.
- **Alternatives considered**: 
    - `JsonPath` library (adds binary size and complexity).
    - Manual string splitting and mapping (harder to maintain than `JsonElement` traversal).

## Decision 3: JSON Write Templates

- **Decision**: Use string-based `$VALUE` substitution for outgoing payloads.
- **Rationale**: Simple, highly performant, and flexible. Allows users to wrap values in any structure (e.g., `{"level": $VALUE}` or `{"cmd": "SET", "val": $VALUE}`).
- **Alternatives considered**: 
    - Full templating engine (Handlebars, etc.) - overkill for simple SCADA commands.
    - Fixed schema options (not flexible enough for diverse MQTT backends).

## Decision 4: Backward Compatibility and Fallback

- **Decision**: If `jsonPath` is null/empty, fall back to global `MqttSettings.jsonKey` if `MqttPayloadFormat` is `JSON`.
- **Rationale**: Ensures that existing dashboards created before this update continue to function without any user interaction.
- **Alternatives considered**: 
    - Deprecate global settings immediately (would break existing user workflows).
