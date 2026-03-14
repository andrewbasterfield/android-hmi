# Feature Specification: Built-in Demo Server Integration

**Feature Branch**: `009-builtin-demo-server`  
**Created**: 2026-03-14  
**Status**: Draft  
**Input**: User description: "building the ncat style server into the app, so it could be tested / played with without dependencies"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Instant "Play" Mode (Priority: P1)

As a first-time user, I want to immediately see how the app works without having to configure a physical PLC or a network connection.

**Why this priority**: High. This is the primary "Aha!" moment for new users and is critical for lowering the barrier to entry.

**Independent Test**: The user can launch the app and connect to a functioning simulation in under 10 seconds without typing an IP address.

**Acceptance Scenarios**:

1. **Given** the app is launched and on the Connection Screen, **When** the user taps "Connect to Local Demo Server", **Then** the app transitions to the Dashboard and shows live data updates.
2. **Given** the app is in "Demo Mode", **When** the user adds a new Gauge widget and points it to a simulated tag (e.g., `SIM_TEMP`), **Then** the gauge immediately begins displaying fluctuating values.

---

### User Story 2 - Interaction Testing (Priority: P2)

As a developer or tester, I want to verify that widgets correctly send data to a backend without needing an external `ncat` server.

**Why this priority**: Medium. Facilitates faster development cycles and automated UI testing.

**Independent Test**: Can be tested by dragging a slider in the app and verifying that the internal "server" state updates accordingly.

**Acceptance Scenarios**:

1. **Given** the app is connected to the internal demo server, **When** the user moves a Slider widget, **Then** the value is sent to the internal server and reflected in other widgets bound to the same tag.
2. **Given** the app is connected to the internal demo server, **When** the user presses a Button widget, **Then** a boolean signal is toggled in the internal server.

---

### User Story 3 - Standalone Presentation (Priority: P3)

As a presenter, I want to demonstrate the app's capabilities in environments without reliable Wi-Fi or local network access.

**Why this priority**: Low. Useful for marketing and sales but less critical for core engineering tasks.

**Independent Test**: Can be tested by putting the device in Airplane Mode and still successfully using all features of the app with the demo server.

**Acceptance Scenarios**:

1. **Given** the device has no network connectivity, **When** the user selects "Demo Mode", **Then** the app functions perfectly using the local loopback interface.

---

### Edge Cases

- **Port Conflict**: What happens if the default port 9999 is already in use by another app on the device?
- **Server Lifecycle**: Does the server keep running in the background when the app is minimized?
- **Reconnection**: How does the app handle "connecting" to the demo server if it's already running?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST include a pre-configured "Local Demo Server" option on the Connection Screen.
- **FR-002**: The Demo Server MUST start automatically when the application launches and remain active throughout the application's lifecycle.
- **FR-003**: The Demo Server MUST listen on `127.0.0.1:9999` using the standard HMI "TAG:VALUE" protocol.
- **FR-004**: The Demo Server MUST provide at least three pre-defined simulated tags: `SIM_TEMP` (random float), `SIM_PRESSURE` (random float), and `SIM_STATUS` (random boolean).
- **FR-005**: The Demo Server MUST echo any values written to it back to all connected clients (supporting the loopback behavior).
- **FR-006**: The system MUST allow users to switch between "Demo Mode" and "External PLC Mode" seamlessly from the Connection Screen.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: The "Demo Mode" entry point MUST have a minimum touch target of 48x48dp.
- **A11Y-002**: The connection status (Connected to Local vs. External) MUST be clearly indicated to the user via a label or icon.
- **A11Y-003**: All UI elements in the demo mode MUST follow existing accessibility standards (high contrast, dynamic text support).

### Key Entities *(include if feature involves data)*

- **Demo Server**: The internal component that mimics a PLC's behavior, maintaining a map of tags and their current values.
- **Connection Profile (Local)**: A pre-defined `PlcConnectionProfile` object pointing to `127.0.0.1`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can reach a "Live Data" state from the first app launch in under 3 taps.
- **SC-002**: 100% of widget types (Button, Slider, Gauge) are fully functional and testable using only the built-in server.
- **SC-003**: No external dependencies (ncat, physical PLC, network access) are required to demonstrate core app functionality.
- **SC-004**: The demo server consumes less than 5% of average CPU during active simulation.
