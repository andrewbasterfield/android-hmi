# Research: MQTT Backend Support

## Decision 1: MQTT Client Library
- **Decision**: HiveMQ MQTT Client (`com.hivemq:hivemq-mqtt-client:1.3.3`)
- **Rationale**: 
    - **Industrial Reliability**: HiveMQ is a leading MQTT broker provider, and their client is highly stable.
    - **Reactive Core**: Designed with reactive streams, making it easy to bridge to Kotlin `Flow`.
    - **MQTT 5.0 Support**: While we target v3.1.1, MQTT 5.0 support ensures future-proofing.
    - **Backpressure Handling**: Essential for high-frequency industrial data (e.g., fast-updating gauges).
- **Alternatives considered**: 
    - **Eclipse Paho**: Legacy, maintenance mode, lacks native coroutine patterns.
    - **KMqtt**: Kotlin-native but newer and less proven in industrial environments.

## Decision 2: Reactive Integration Pattern
- **Decision**: Bridge HiveMQ reactive streams to Kotlin `Flow` using `callbackFlow`.
- **Rationale**: 
    - `callbackFlow` provides a clean way to handle asynchronous MQTT message callbacks.
    - It allows for structured concurrency: when the UI collector stops, the MQTT subscription is automatically cleaned up.
    - Ensures thread-safety when emitting updates to the UI.

## Decision 3: Protocol Dispatcher (Delegation)
- **Decision**: Implement `PlcCommunicatorDispatcher` using the Delegate pattern.
- **Rationale**: 
    - ViewModels should not care if they are talking to Raw TCP or MQTT.
    - This satisfies **FR-009** (Protocol Abstraction) from the original specification (001-hmi-control-panel).
    - It allows for runtime protocol switching based on the user's saved connection profile.

## Decision 4: Configuration Persistence
- **Decision**: Use GSON to serialize/deserialize the entire `PlcConnectionProfile` into a single `stringPreferencesKey` in Jetpack DataStore.
- **Rationale**: 
    - As more protocols are added (MQTT, Modbus, OPC UA), the number of configuration fields grows.
    - Serializing the whole object is more maintainable than creating dozens of individual DataStore keys.
    - Allows for flexible schema evolution (e.g., adding optional MQTT credentials).

## Decision 5: MQTT Payload Parsing
- **Decision**: Support both Plain Text and Simple JSON (GSON) for tag values.
- **Rationale**: 
    - Plain text is standard for simple MQTT deployments.
    - JSON is common in more advanced IoT architectures.
    - Satisfies **FR-009** from the MQTT spec.
