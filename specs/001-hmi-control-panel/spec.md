# Feature Specification: HMI Control Panel

**Feature Branch**: `001-hmi-control-panel`  
**Created**: 2026-03-10  
**Status**: Draft  
**Input**: User description: "I want to design an app to be run on an android phone/tablet that I can easily customize to show controls such as buttons, sliders and also guages. This is an industrial control panel such as HMI / SCADA to interface with a PLC. The connection to PLC will be tcp/ip (TBD)"

## Assumptions

- **Target Audience**: Industrial operators and automation engineers.
- **Protocol**: Since the specific protocol over TCP/IP is marked as TBD in the input, we assume a generic or widely used industrial protocol (like Modbus TCP) but will flag this for clarification.
- **Orientation**: App will likely be used primarily in landscape mode on tablets, but should support phones.
- **Modes**: There are two distinct modes: an "Edit Mode" for customization and a "Run Mode" for operation.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Configure and Connect to PLC (Priority: P1)

As an operator, I want to enter TCP/IP connection details so that the app can communicate with the target PLC.

**Why this priority**: Without a connection to the PLC, the app cannot send or receive data, making all other features useless. This is the foundational capability.

**Independent Test**: Can be fully tested by entering an IP address and port, tapping "Connect," and verifying that the connection status changes to "Connected" (e.g., against a simulated PLC or local echo server).

**Acceptance Scenarios**:

1. **Given** the app is disconnected, **When** I enter a valid IP address and port and tap Connect, **Then** the app establishes a TCP/IP connection and indicates success.
2. **Given** the app is attempting to connect, **When** the PLC is unreachable or times out, **Then** the app shows an error message indicating connection failure and remains in a disconnected state.

---

### User Story 2 - Operate Basic Controls (Priority: P1)

As an operator, I want to view gauges and manipulate buttons and sliders so that I can monitor and control the industrial process.

**Why this priority**: Monitoring and controlling the process is the core value proposition of an HMI/SCADA application.

**Independent Test**: Can be tested by loading a pre-configured dashboard of controls, manipulating a slider or button, and verifying the expected payload is sent over the network (and gauges update upon receiving a payload).

**Acceptance Scenarios**:

1. **Given** an active PLC connection, **When** I tap a button control, **Then** the system sends the corresponding command to the PLC.
2. **Given** an active PLC connection, **When** the PLC sends updated sensor data, **Then** the gauge control instantly updates its visual display to reflect the new value.

---

### User Story 3 - Customize Dashboard Layout (Priority: P2)

As an engineer, I want to easily add, move, and configure controls on the screen so that I can tailor the interface to different PLC setups without writing code.

**Why this priority**: The user specifically requested an app they can "easily customize." This differentiates the app from a hard-coded UI.

**Independent Test**: Can be tested entirely offline by entering edit mode, dropping a new gauge onto the screen, saving the layout, restarting the app, and verifying the gauge is still there.

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I select a "Slider" from the control palette and place it on the screen, **Then** the slider appears in the specified location.
2. **Given** a customized layout with several controls, **When** I save the layout and restart the app, **Then** the app loads the exact same layout upon startup.

### Edge Cases

- **Network Interruption**: What happens if the TCP/IP connection drops unexpectedly? The app should visually indicate a disconnected state and attempt to automatically reconnect.
- **Invalid PLC Data**: What happens if the PLC sends an invalid value for a bound tag (e.g., a string instead of an expected integer for a gauge)? The gauge should display an error state (e.g., grayed out or a warning icon) without crashing the app.
- **Overlapping Controls**: What happens if the user places two controls on top of each other in Edit Mode? The UI should allow layering, but perhaps visually warn the user, or snap controls to a grid to prevent complete obfuscation.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to configure connection settings (IP Address, Port).
- **FR-002**: System MUST establish and maintain a TCP/IP connection to the specified PLC.
- **FR-003**: System MUST provide an "Edit Mode" to customize the dashboard.
- **FR-004**: System MUST allow users to add, remove, and reposition Button, Slider, and Gauge controls in Edit Mode.
- **FR-005**: System MUST allow users to bind each UI control to a specific PLC address/tag.
- **FR-006**: System MUST persist the customized dashboard layout and the last-used connection settings (IP Address and Port) locally on the device, pre-populating them on startup.
- **FR-007**: System MUST transmit control updates (button presses, slider changes) to the PLC when in "Run Mode".
- **FR-008**: System MUST visually update controls (like Gauges) based on data received from the PLC in real-time.
- **FR-010**: System MUST detect unexpected socket disconnections from the server and automatically navigate the user back to the Connection Screen, displaying an error state.

*Clarifications Needed:*

- **FR-009**: System MUST abstract the communication protocol layer, allowing users to select from multiple implemented protocols (e.g., Modbus TCP, OPC UA, or Raw TCP) when configuring a PLC connection.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.

### Key Entities

- **PLC Connection Profile**: Stores the connection state, IP address, and port.
- **Dashboard Layout**: A collection of configured widgets representing the current HMI screen.
- **Widget Configuration**: Defines a single UI element (Type: Button/Slider/Gauge), its visual coordinates on the screen, and the PLC data address/tag it is bound to.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can configure and establish a successful PLC connection in under 60 seconds.
- **SC-002**: The UI updates in response to incoming PLC data with a perceived latency of less than 200 milliseconds.
- **SC-003**: An engineer can add a new control, bind it to a data tag, and place it on the screen in under 30 seconds.
- **SC-004**: The app sustains a continuous PLC connection for at least 8 hours without crashing or unexplained disconnects.
