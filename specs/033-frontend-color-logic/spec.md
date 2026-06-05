# Feature Specification: Frontend-Driven Color Management

**Feature Branch**: `033-frontend-color-logic`  
**Created**: 2026-06-05  
**Status**: Draft  
**Input**: User description: "we need to drop the colour support from the backend protocol, it should just be a concern of the frontend"

## Problem Statement
The current HMI system allows the backend protocol to dictate widget colors via a `.color` attribute (e.g., `MOTOR_01.color:#FF0000`). This mixes presentation logic with data transmission, increasing protocol overhead and making it difficult to maintain consistent UI themes or user-defined color thresholds without backend changes.

## Goal
Shift all color-related logic to the frontend. Colors should be determined dynamically based on tag values and locally configured "Color Zones" or static overrides, ensuring the protocol remains purely data-focused.

## Clarifications

### Session 2026-06-05
- Q: How should ColorZones evaluate and apply colors for boolean tags or discrete string states? → A: Add an `exactMatch` string field to `ColorZone` for discrete non-numeric matches.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Value-Based Dynamic Coloring (Priority: P1)

As an HMI Operator, I want my gauges and indicators to change color automatically based on the live data value (e.g., turning Red when a temperature exceeds 90°C) so that I can quickly identify critical states without the backend needing to know about my UI color preferences.

**Why this priority**: This is the core functional replacement for the existing backend-driven color logic. It ensures the system remains functional and informative.

**Independent Test**: Can be fully tested by configuring a Gauge with color zones and verifying it changes color as the value moves between those zones, while ensuring no `.color` messages are sent by the server.

**Acceptance Scenarios**:

1. **Given** a Gauge is configured with a Red zone for values > 90, **When** the tag value changes to 95, **Then** the gauge indicator turns Red.
2. **Given** the Gauge is Red, **When** the tag value drops to 50 (Normal zone), **Then** the gauge indicator returns to its default or "Normal" color.

---

### User Story 2 - Protocol Simplification (Priority: P2)

As a System Integrator, I want the PLC protocol to only contain raw data values and labels so that I don't have to program presentation logic (hex colors) into the PLC or backend server.

**Why this priority**: Reduces development effort on the backend and minimizes protocol bandwidth.

**Independent Test**: Can be verified by monitoring the TCP stream and confirming that no `.color` attributes are broadcasted during normal operation.

**Acceptance Scenarios**:

1. **Given** the Demo PLC Server is running, **When** a tag value updates, **Then** only the tag value and optional label are broadcasted, never a `.color` suffix.

---

### User Story 3 - Frontend Color Configuration (Priority: P3)

As a Dashboard Designer, I want to be able to set and modify color thresholds directly in the widget editor so that I can customize the visual feedback of my dashboard without touching the backend code.

**Why this priority**: Enhances the "Customizable HMI" value proposition.

**Independent Test**: Can be tested by using the Widget Palette to add/remove color zones and seeing the changes reflected immediately in Run Mode.

**Acceptance Scenarios**:

1. **Given** a widget is in Edit Mode, **When** I add a new Color Zone, **Then** the widget immediately respects that zone's color in the preview and when saved.

---

### Edge Cases

- **Missing Zones**: If a value falls outside all configured color zones, the widget MUST fall back to a default "Identity" color (e.g., Material Theme primary or background).
- **Overlapping Zones**: If multiple zones cover the same value range, the "first match" in the configuration list should take precedence.
- **String/Boolean Matching**: If `exactMatch` is defined on a ColorZone, the UI will attempt an equality check against the tag's raw string value, taking priority over numeric range checks.
- **Malformed Protocol**: If the backend *does* send a `.color` message (e.g., from an older version), the frontend MUST ignore it and maintain its local color logic.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST ignore `.color` attribute updates received via the PLC protocol in the `DashboardViewModel` and UI layers.
- **FR-002**: Widgets MUST determine their active color state locally by evaluating the current tag value against a list of `ColorZones`.
- **FR-003**: The `DemoPlcServer` MUST be updated to remove all logic that generates or broadcasts `.color` hex strings.
- **FR-004**: Each widget type (Gauge, Indicator, Button) MUST support a fallback "Static Color" or "Default Theme Color" if no dynamic zones are active.
- **FR-005**: The `sessionOverrides` map in the `DashboardViewModel` SHOULD no longer store or propagate "color" keys for widgets.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: UI MUST exclude non-functional decorative elements or gimmicks.

### Key Entities *(include if feature involves data)*

- **ColorZone**: Represents a range of values (`startValue`, `endValue`) or a discrete state (`exactMatch` String), and an associated `color` (Long) and `label` (String).
- **WidgetConfiguration**: Now becomes the sole authority for color thresholds via its `colorZones` property.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of widget color changes in the app are driven by local configuration and tag value evaluation.
- **SC-002**: The protocol overhead for simulated tags is reduced by removing redundant `.color` transmissions.
- **SC-003**: A user can reconfigure a gauge's "Danger" threshold from 90 to 80 in the UI and see the color change immediately without backend modification.
