# Feature Specification: Latching Buttons and Indicator Lights

**Feature Branch**: `024-button-switch-indicator`  
**Created**: 2026-03-23  
**Status**: Draft  
**Input**: User description: "support latching buttons (aka switches), and buttons that can be toggled through the backend protocol (aka indicator lights) to lay foundation for MQTT support"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Toggle a Latching Switch (Priority: P1)

As an HMI operator, I want to toggle a physical or logical switch on the dashboard and see its state persist visually, so I can control and monitor persistent processes (like turning a pump on and keeping it on).

**Why this priority**: Core functionality for industrial control; required for any process that isn't purely momentary.

**Independent Test**: Can be tested by placing a button in "Latching" mode, clicking it, and verifying it stays in the "Active" visual state until clicked again.

**Acceptance Scenarios**:

1. **Given** a button configured as "Latching", **When** the user clicks the button, **Then** the button toggles to the "Active" visual state and sends a `true` signal to the backend.
2. **Given** a button in "Active" state, **When** the user clicks it again, **Then** it toggles to the "Inactive" visual state and sends a `false` signal to the backend.

---

### User Story 2 - Monitor Status via Indicator Light (Priority: P2)

As an HMI operator, I want to see the real-time status of a backend process reflected in a non-interactive button-like widget, so I can monitor equipment health or state without accidentally triggering a command.

**Why this priority**: Essential for safety and situational awareness in industrial environments.

**Independent Test**: Can be tested by configuring a button as an "Indicator" and updating the backend tag value, verifying the visual state changes without allowing user interaction.

**Acceptance Scenarios**:

1. **Given** a button configured as "Indicator", **When** the backend tag value becomes `true`, **Then** the button displays the "Active" visual state.
2. **Given** an "Indicator" button, **When** the user clicks it, **Then** no signal is sent to the backend and the state does not change.

---

### User Story 3 - Configure Button Interaction Mode (Priority: P3)

As a dashboard designer, I want to select whether a button acts as a Momentary trigger, a Latching switch, or a read-only Indicator, so I can customize the interface for specific control needs.

**Why this priority**: Required for users to actually use the new button types in their custom layouts.

**Independent Test**: Can be tested by opening the Widget Configuration dialog for a button and selecting different modes from a `selectable chips` selection row (similar to Gauge Style selection).

**Acceptance Scenarios**:

1. **Given** the Widget Configuration dialog for a button, **When** the user selects "LATCHING" from the Interaction Type row and saves, **Then** the button on the dashboard immediately adopts latching behavior.

---

### User Story 4 - Invert Logic for Visual Feedback (Priority: P3)

As a dashboard designer, I want to invert the relationship between the backend tag state and the button's visual state, so I can match specific hardware conventions (e.g., "Active Low" logic) or visual preferences.

**Why this priority**: Adds flexibility for diverse industrial equipment where a `false` signal might represent an "Active" or "Alarm" state.

**Independent Test**: Can be tested by toggling the "Invert" switch in configuration and verifying that a `true` tag value results in an "Inactive" visual state.

**Acceptance Scenarios**:

1. **Given** a button with "Invert" enabled, **When** the backend tag is `true`, **Then** the button displays the "Inactive" visual state.
2. **Given** a button with "Invert" enabled, **When** the backend tag is `false`, **Then** the button displays the "Active" (Identity Swap) visual state.

---

### Edge Cases

- **Backend Latency**: What happens if a latching button is toggled but the backend update takes several seconds? (Requirement: Use optimistic UI updates; UI toggles immediately).
- **Initialization State**: How does a latching button appear when the app first loads but the backend hasn't reported a state yet? (Requirement: Default to the same unpushed/Inactive state as momentary buttons, respecting the Invert toggle).
- **Conflicting Updates**: If a user toggles a switch while the backend is also pushing a state change, which one wins? (Requirement: Backend state is the source of truth; user interaction initiates a request that the backend eventually confirms via the status flow).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support three button interaction modes: `MOMENTARY`, `LATCHING`, and `INDICATOR`.
- **FR-002**: `MOMENTARY` buttons MUST send a `true` signal on press and return to an inactive visual state immediately (existing behavior).
- **FR-003**: `LATCHING` buttons MUST toggle between `true` and `false` states on each click and maintain a corresponding visual state based on the current tag value.
- **FR-004**: `INDICATOR` buttons MUST NOT respond to user clicks but MUST update their visual state based on backend tag values.
- **FR-005**: All button modes MUST reflect the "Active" (Logic True) state using the widget's high-contrast "Pressed" color theme (Identity Swap) by default.
- **FR-006**: System MUST allow configuration of the interaction mode via a row of `selectable chips` components in the widget settings dialog, matching the `GaugeStyle` selection pattern.
- **FR-007**: System MUST handle backend write failures for latching buttons using the same strategy as sliders (optimistic local update followed by eventual consistency from the backend status flow).
- **FR-008**: System MUST default all buttons to the "Inactive" (False/Unpushed) state upon dashboard load until a backend value is received, respecting the Invert toggle.
- **FR-009**: System MUST provide an "Invert Logic" toggle in the configuration dialog.
- **FR-010**: When "Invert Logic" is ENABLED, the visual "Active" state MUST correspond to a backend `false` value, and the "Inactive" state MUST correspond to a backend `true` value.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: `INDICATOR` buttons MUST have a distinct visual treatment (e.g., no hover/press ripple or haptic feedback) to signal they are non-interactive.

### Key Entities *(include if feature involves data)*

- **InteractionType**: An enumeration of behaviors for the button widget (`MOMENTARY`, `LATCHING`, `INDICATOR`).
- **WidgetConfiguration**: Updated to include `InteractionType` (defaulting to `MOMENTARY`) and `isInverted` (boolean, defaulting to `false`).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Operators can successfully toggle a latching button and confirm its state change within 500ms (including visual feedback).
- **SC-002**: 100% of state changes pushed from the backend are reflected in "Indicator" buttons within 250ms of receipt.
- **SC-003**: Zero regressions in existing momentary button functionality across all current dashboard layouts.
- **SC-004**: Users can change the interaction mode of a button in under 15 seconds via the configuration dialog.
