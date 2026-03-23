# Implementation Plan: MQTT Backend Support

**Branch**: `025-mqtt-backend-support` | **Date**: 2026-03-23 | **Spec**: /specs/025-mqtt-backend-support/spec.md
**Input**: Feature specification for MQTT backend protocol support.

## Summary
Implement MQTT v3.1.1 support as an industrial communication protocol for the HMI. This feature introduces a `MqttPlcCommunicator` using the HiveMQ MQTT Client library, integrated via Kotlin Coroutines and Flow. It provides bi-directional communication (telemetry and control), supports dynamic attribute updates via sub-topics, and features a robust reconnection mechanism with LWT signaling.

## Technical Context

**Language/Version**: Kotlin 1.9.22
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON, **HiveMQ MQTT Client (v1.3.3)**
**Storage**: Jetpack DataStore (serialized JSON for profiles)
**Testing**: JUnit 4, Mockito, Compose UI Test, Coroutines Test
**Target Platform**: Android (API 24+)
**Project Type**: Mobile Application
**Performance Goals**: <15s connection time, <200ms processing latency
**Constraints**: Volatile session overrides, QoS 0/1, Reconnection within 5s
**Scale/Scope**: Support for thousands of tags via MQTT topics

## Constitution Check

- [x] **Compose-First**: Utilizing Jetpack Compose for connection settings and dashboard updates.
- [x] **Unidirectional Data Flow**: Using StateFlow for connection state and tag updates in ViewModels.
- [x] **Test-First**: Unit tests for MQTT parsing and UI tests for connection/updates are planned.
- [x] **Accessibility**: Support for 48x48dp touch targets and content descriptions on all new UI.
- [x] **Clarity by Design**: High-contrast UI for MQTT settings and data display.
- [x] **Low Cognitive Load**: Progressive disclosure in connection profile editor.
- [x] **No Gimmicks**: Functional status indicators for MQTT connection (online/offline).
- [x] **Modular Architecture**: Create a new `:core:protocol` module to encapsulate all protocol logic (TCP, MQTT), decoupling it from the `app` module.

## Project Structure

### Documentation (this feature)

```text
specs/025-mqtt-backend-support/
├── plan.md              # This file
├── research.md          # MQTT client choice and integration patterns
├── data-model.md        # MqttSettings and Profile updates
├── quickstart.md        # Testing MQTT locally with Mosquitto
└── tasks.md             # Task decomposition (to be generated)
```

### Source Code (repository root)

```text
core/protocol/
├── build.gradle.kts                  # Module configuration + HiveMQ dependency
└── src/main/java/com/example/hmi/protocol/
    ├── PlcCommunicator.kt            # Interface (moved)
    ├── RawTcpPlcCommunicator.kt      # Implementation (moved)
    ├── MqttPlcCommunicator.kt        # NEW: MQTT implementation
    └── PlcCommunicatorDispatcher.kt  # NEW: Protocol dispatcher

app/src/main/java/com/example/hmi/
├── data/
│   ├── PlcConnectionProfile.kt       # UPDATED: Add MqttSettings
│   └── DashboardRepository.kt        # UPDATED: JSON serialization
├── connection/                       # UI Layer: Manages MQTT credentials & connection
│   ├── ConnectionViewModel.kt        # UPDATED: Protocol-aware connection logic
│   └── ConnectionScreen.kt           # UPDATED: Protocol selector + MQTT fields
```

**Structure Decision**: Standard Android single-module structure with enhanced `protocol` package and updated `data` layer for flexible persistence.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Multi-protocol Dispatcher | Allows seamless switching between TCP and MQTT. | Direct coupling in ViewModels is fragile and violates OCP. |
| JSON Profile Persistence | Handles increasing complexity of connection settings. | Individual DataStore keys for 20+ fields is unmaintainable. |
