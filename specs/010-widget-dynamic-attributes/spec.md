# Feature Specification: Custom Labels and Dynamic Attribute Updates

**Feature Branch**: `010-widget-dynamic-attributes`  
**Created**: 2026-03-14  
**Status**: Draft  
**Input**: User description: "How about different UI labels to the protocol tags (UI label defaults to protocol tag but can be changed)? And it would be awesome to be able to change attributes of widgets through the protocol (specifically the label and the colour)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Manual Label Override (Priority: P1)

As a user, I want to set a custom display name for a widget in the app so that I can see human-readable text instead of technical PLC tag addresses (e.g., "Main Pump" instead of `PUMP_01_RUN`).

**Why this priority**: High. This is a fundamental usability improvement that decouples the UI from the underlying communication protocol.

**Independent Test**: The user can edit a widget, type a custom label, and see that label displayed on the dashboard instead of the tag address.

**Acceptance Scenarios**:

1. **Given** a widget is bound to tag `MOTOR_RPM`, **When** no custom label is set, **Then** the widget displays "MOTOR_RPM".
2. **Given** the app is in Edit Mode, **When** the user sets the custom label to "Engine Speed", **Then** the widget displays "Engine Speed" while remaining bound to `MOTOR_RPM`.

---

### User Story 2 - Dynamic Attribute Updates via Protocol (Priority: P2)

As a system integrator, I want to change widget attributes (label and color) directly from the PLC/Server so that the UI can react to changing process conditions or machine states.

**Why this priority**: Medium. Enables "Smart" HMIs where the backend controls the presentation layer for better operator guidance.

**Independent Test**: Sending a specific protocol command (e.g., `TANK_LEVEL.color:#FF0000`) should immediately change the widget's appearance without user interaction.

**Acceptance Scenarios**:

1. **Given** a widget is bound to `VALVE_01`, **When** the server sends `VALVE_01.label:Emergency Shutoff`, **Then** the widget's displayed label updates to "Emergency Shutoff".
2. **Given** a Gauge widget is bound to `TEMP_SENSOR`, **When** the server sends `TEMP_SENSOR.color:#FFCC00`, **Then** the gauge background color changes to orange.

---

### Edge Cases

- **Partial Tag Matching**: If multiple widgets are bound to the same tag, do they all update their attributes? (Decision: Yes, attribute updates are tag-scoped).
- **Persistence vs. Volatile**: Protocol-driven attribute changes are volatile and MUST NOT be saved to permanent storage. They last only for the current application session. Manual edits in Edit Mode remain persistent.
- **Conflict Resolution**: The system follows a "Last Change Wins" strategy. The UI will reflect the most recent update, whether it originated from a manual user edit or a protocol message. However, only manual edits update the underlying persistent configuration.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The `WidgetConfiguration` MUST include an optional `customLabel` field.
- **FR-002**: Widgets MUST display the `customLabel` if present; otherwise, they MUST fall back to the `tagAddress`.
- **FR-003**: The communication protocol MUST support attribute updates using the format `TAG.ATTRIBUTE:VALUE`.
- **FR-004**: The system MUST support the `label` attribute to update the display name of widgets.
- **FR-005**: The system MUST support the `color` attribute to update the background color of widgets using Hex format (`#RRGGBB` or `#AARRGGBB`).
- **FR-006**: The system MUST implement a "Last Change Wins" conflict resolution strategy between manual edits and protocol updates. Protocol updates MUST be transient (session-only).

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: Custom labels MUST support dynamic text scaling.
- **A11Y-002**: When a color changes via protocol, the text contrast MUST remain accessible (logic should check luminance and switch between white/black text).

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Extended with `customLabel` (String) and `dynamicColor` (Long).
- **AttributeUpdate**: A transient state update received via protocol that targets a specific `tagAddress`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can override a widget label in under 15 seconds in Edit Mode.
- **SC-002**: Protocol-driven attribute updates are reflected in the UI in under 100ms (latency from socket read to Compose recomposition).
- **SC-003**: 100% of widget types (Button, Slider, Gauge) support dynamic label and color updates.
