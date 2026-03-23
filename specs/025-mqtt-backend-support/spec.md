# Feature Specification: MQTT Backend Support

**Feature Branch**: `025-mqtt-backend-support`  
**Created**: 2026-03-23  
**Status**: Draft  
**Input**: User description: "I would like us to implement mqtt support as the backend protocol. discuss"

## Clarifications

### Session 2026-03-23
- Q: Which MQTT version should be supported? → A: MQTT v3.1.1
- Q: Which MQTT Quality of Service (QoS) level should be used? → A: QoS 1 for Control (Egress) and QoS 0 for Telemetry (Ingress)
- Q: How should Retained Messages be handled? → A: Subscribe to retained messages (telemetry) to get the current state on connection; do NOT publish control messages as retained.
- Q: Should Last Will and Testament (LWT) be used? → A: Enable LWT - Publish "online" to a status topic on connection and configure "offline" as the LWT message.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Connect to MQTT Broker (Priority: P1)

As an operator, I want to connect the HMI application to an industrial MQTT broker so that I can monitor and control devices using standard IoT protocols.

**Why this priority**: Essential for establishing any communication via MQTT. Without a connection, no data can be exchanged.

**Independent Test**: Successfully establishing a connection to a local or remote MQTT broker and seeing the "Connected" status in the HMI.

**Acceptance Scenarios**:

1. **Given** a valid MQTT broker address and port, **When** I initiate a connection, **Then** the HMI status changes to "Connected".
2. **Given** an invalid broker address, **When** I initiate a connection, **Then** the HMI displays a clear connection error.

---

### User Story 2 - Real-time Data Monitoring via Topics (Priority: P1)

As an operator, I want to see real-time updates for PLC tags by subscribing to specific MQTT topics so that I can monitor the current state of the industrial process.

**Why this priority**: High. This is the primary function of the HMI—to display live data from the backend.

**Independent Test**: Publishing a value to an MQTT topic and seeing the corresponding widget on the HMI dashboard update instantly.

**Acceptance Scenarios**:

1. **Given** a widget configured with an MQTT topic address, **When** a new message is published to that topic, **Then** the widget reflects the new value immediately.
2. **Given** a widget subscribed to a topic, **When** the MQTT connection is lost, **Then** the widget indicates a "stale" or "disconnected" state.

---

### User Story 3 - Control Devices via MQTT Publish (Priority: P2)

As an operator, I want to change the state of a device (e.g., turn a motor on/off) by interacting with HMI widgets that publish messages to MQTT topics.

**Why this priority**: Medium. Enables bi-directional communication, allowing the HMI to act as a control interface.

**Independent Test**: Clicking a button on the HMI and verifying that the correct message is published to the configured MQTT topic.

**Acceptance Scenarios**:

1. **Given** a switch widget configured with a command topic, **When** I toggle the switch, **Then** a message with the new state is published to that topic.
2. **Given** a setpoint entry widget, **When** I submit a new value, **Then** that value is published to the designated control topic.

---

### User Story 4 - Dynamic Attribute Updates via MQTT (Priority: P2)

As an operator, I want widget attributes like labels and colors to be updateable via MQTT sub-topics so that the UI can dynamically adapt to process conditions (e.g., a tank turning red on high-level alarm).

**Why this priority**: Medium. Consistent with existing "Raw TCP" protocol features for dynamic UI updates.

**Independent Test**: Publishing a hex color code to `[topic]/color` and seeing the widget background change color.

**Acceptance Scenarios**:

1. **Given** a widget for `TANK_LEVEL`, **When** a message is published to `TANK_LEVEL/color`, **Then** the widget's color updates to match.
2. **Given** a widget for `PUMP_01`, **When** a message is published to `PUMP_01/label`, **Then** the displayed name for that widget updates.

---

### Edge Cases

- **Broker Downtime**: If the broker becomes unreachable, the HMI should show a "Reconnecting" state and attempt to re-establish the session without user intervention.
- **Malformed Payloads**: If a non-numeric string is received on a topic mapped to a numeric gauge, the HMI should preserve the last known good value and display a small warning indicator.
- **Topic Collisions**: If multiple widgets are mapped to the same topic but with different configurations, the HMI must ensure consistent state across all instances.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support connecting to MQTT brokers using standard host and port configurations via the MQTT v3.1.1 protocol.
- **FR-002**: System MUST allow users to specify a unique Client ID or generate one automatically.
- **FR-003**: System MUST support subscribing to arbitrary topic strings as "Tag Addresses" using QoS 0 and MUST process retained messages to immediately display the latest known state upon connection.
- **FR-004**: System MUST support publishing values to topics when user interacts with control widgets using QoS 1 and MUST NOT set the retain flag for control messages.
- **FR-005**: System MUST support dynamic attribute updates via sub-topics using the convention `[tagAddress]/[attribute]` (e.g., `mytag/color`).
- **FR-006**: System MUST persist MQTT connection settings (Broker, Port, Client ID) as part of the connection profile.
- **FR-007**: System MUST support Username/Password authentication for connecting to secured MQTT brokers.
- **FR-008**: System MUST support an optional global "Root Topic Prefix"; if provided, it is prepended to all tag addresses unless the address starts with a leading forward slash (indicating an absolute path).
- **FR-009**: System MUST support Simple JSON payload parsing; for each tag, the user MUST be able to specify the JSON key that contains the value (defaulting to "value" if not specified).
- **FR-010**: System MUST support Last Will and Testament (LWT); it MUST publish "online" to a configurable status topic upon connection and set "offline" as the LWT message to be published by the broker upon abrupt disconnection.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: UI MUST exclude non-functional decorative elements or gimmicks.

### Key Entities *(include if feature involves data)*

- **MQTT Connection Profile**: Represents the settings required to connect to an MQTT broker (Host, Port, Client ID, and optional credentials).
- **MQTT Tag**: A mapping between an HMI widget and one or more MQTT topics for data and attributes.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can establish an MQTT connection in under 15 seconds from the Connection Screen.
- **SC-002**: Data updates from the broker are reflected in the UI with less than 200ms of processing latency (excluding network transit).
- **SC-003**: The system successfully initiates a reconnection within 5 seconds after a transient network failure.
- **SC-004**: 100% of published control messages are successfully handed off to the MQTT client layer.
