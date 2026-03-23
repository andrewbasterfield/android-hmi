# Tasks: MQTT Backend Support

**Input**: Design documents from `/specs/025-mqtt-backend-support/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T000 Create `:core:protocol` module and move existing `PlcCommunicator` and `RawTcpPlcCommunicator` to it
- [x] T001 Add HiveMQ MQTT Client dependency to `core/protocol/build.gradle.kts`
- [x] T002 Update `Protocol` enum and add `MqttSettings` to `app/src/main/java/com/example/hmi/data/PlcConnectionProfile.kt`
- [x] T003 [P] Update `DashboardRepository.kt` to support JSON serialization for `PlcConnectionProfile` using GSON
- [x] T004 [P] Create initial `MqttPlcCommunicator.kt` skeleton in `core/protocol/src/main/java/com/example/hmi/protocol/`


---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure for multi-protocol support

- [x] T005 Implement `PlcCommunicatorDispatcher.kt` in `core/protocol/src/main/java/com/example/hmi/protocol/` to delegate calls based on profile protocol
- [x] T006 Update `AppModule.kt` in `app/src/main/java/com/example/hmi/di/` to bind `PlcCommunicatorDispatcher` as the `PlcCommunicator` implementation
- [x] T007 [P] Unit test for `PlcCommunicatorDispatcher` in `core/protocol/src/test/java/com/example/hmi/protocol/PlcCommunicatorDispatcherTest.kt`

**Checkpoint**: Foundation ready - the app can now handle multiple protocol implementations.

---

## Phase 3: User Story 1 - Connect to MQTT Broker (Priority: P1) 🎯 MVP

**Goal**: Establish a connection to an MQTT broker with credentials and custom settings.

**Independent Test**: Successfully establish a connection to a local Mosquitto broker and see "Connected" status.

### Tests for User Story 1

- [x] T008 [P] [US1] Unit test for MQTT connection lifecycle in `core/protocol/src/test/java/com/example/hmi/protocol/MqttPlcCommunicatorTest.kt`
- [ ] T009 [US1] UI test for MQTT connection configuration in `app/src/androidTest/java/com/example/hmi/connection/MqttConnectionTest.kt`

### Implementation for User Story 1

- [x] T010 [US1] Implement MQTT v3.1.1 connection logic with HiveMQ client in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`
- [x] T011 [US1] Update `ConnectionViewModel.kt` to handle full `PlcConnectionProfile` persistence and connection logic
- [x] T012 [US1] Update `ConnectionScreen.kt` with protocol selector and MQTT-specific fields (Client ID, Username, Password)
- [x] T013 [US1] Implement "online" status publication upon successful connection in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`

**Checkpoint**: User Story 1 is functional - the HMI can connect to an MQTT broker.

---

## Phase 4: User Story 2 - Real-time Data Monitoring via Topics (Priority: P1)

**Goal**: Subscribe to MQTT topics and parse incoming payloads (Plain Text or JSON).

**Independent Test**: Publish a value to a topic using `mosquitto_pub` and see the HMI dashboard update.

### Tests for User Story 2

- [x] T014 [P] [US2] Unit test for MQTT payload parsing (Plain Text and JSON) in `core/protocol/src/test/java/com/example/hmi/protocol/MqttPlcCommunicatorTest.kt`

### Implementation for User Story 2

- [x] T015 [US2] Implement topic subscription with global prefix support in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`
- [x] T016 [US2] Implement `observeTag` with support for JSON key extraction in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`
- [x] T017 [US2] Process retained messages on connection to show immediate state in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`

**Checkpoint**: User Story 2 is functional - the HMI displays live MQTT data.

---

## Phase 5: User Story 3 - Control Devices via MQTT Publish (Priority: P2)

**Goal**: Publish control commands to MQTT topics with QoS 1 guarantees.

**Independent Test**: Toggle a switch in HMI and verify message receipt via `mosquitto_sub`.

### Tests for User Story 3

- [x] T018 [P] [US3] Unit test for MQTT publishing with QoS 1 and no-retain in `core/protocol/src/test/java/com/example/hmi/protocol/MqttPlcCommunicatorTest.kt`

### Implementation for User Story 3

- [x] T019 [US3] Implement `writeTag` with QoS 1 and `retain = false` in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`

**Checkpoint**: User Story 3 is functional - bi-directional communication is fully enabled.

---

## Phase 6: User Story 4 - Dynamic Attribute Updates via MQTT (Priority: P2)

**Goal**: Support UI overrides (color, label) via sub-topics.

**Independent Test**: Publish a color code to `topic/color` and see the widget change color.

### Tests for User Story 4

- [x] T020 [P] [US4] Unit test for attribute sub-topic parsing in `core/protocol/src/test/java/com/example/hmi/protocol/MqttPlcCommunicatorTest.kt`

### Implementation for User Story 4

- [x] T021 [US4] Implement `attributeUpdates` flow using sub-topic matching in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`

**Checkpoint**: User Story 4 is functional - the UI can be dynamically controlled via MQTT.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Error handling, resilience, and final refinements.

- [x] T022 Implement Last Will and Testament (LWT) for "offline" signaling in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`
- [x] T023 Implement automatic reconnection logic with exponential backoff in `core/protocol/src/main/java/com/example/hmi/protocol/MqttPlcCommunicator.kt`
- [x] T024 [P] Update `README.md` with MQTT configuration and testing instructions
- [x] T025 [SC-002] Implement automated latency benchmark test to verify <200ms processing delay
- [x] T026 [SC-003] Verify 5s reconnection behavior via manual network interruption test
- [x] T027 Run full validation using `specs/025-mqtt-backend-support/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 completion.
- **User Stories (Phase 3-6)**: All depend on Phase 2 completion.
  - US1 (Phase 3) is the highest priority.
  - US2 (Phase 4) depends on US1 (Connection).
  - US3 (Phase 5) depends on US1 (Connection).
  - US4 (Phase 6) depends on US2 (Subscribing logic).
- **Polish (Phase 7)**: Depends on all user stories.

### Parallel Opportunities

- T003 and T004 can run in parallel.
- T007 can run in parallel with dispatcher implementation.
- T008 (Tests) can run in parallel with US1 implementation.
- All unit tests for different user stories can be developed in parallel once the base communicator is in place.

---

## Implementation Strategy

### MVP First (User Story 1 & 2)

1. Complete Setup and Foundational phases.
2. Complete US1 (Connection) and US2 (Basic Monitoring).
3. **STOP and VALIDATE**: Verify that the HMI can connect and display a simple value from an MQTT topic.

### Incremental Delivery

1. Add US3 (Control) to enable bi-directional communication.
2. Add US4 (Dynamic Attributes) for advanced UI features.
3. Finish with Phase 7 (LWT, Reconnection) for industrial-grade reliability.
