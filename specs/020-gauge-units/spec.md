# Feature Specification: Gauge Units Support

**Feature Branch**: `020-gauge-units`  
**Created**: 2026-03-21  
**Status**: Draft  
**Input**: User description: "Add support for measurement units (e.g., PSI, °C) to be displayed next to the gauge numeric readout. The units should be appended to the value string for optimal real estate usage."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Contextual Data Readouts (Priority: P1)

As an operator, I want to see the units of measurement (e.g., PSI, RPM) displayed next to the gauge value so that I can interpret the data correctly without looking at the widget label.

**Why this priority**: Essential for industrial safety and clarity. A raw number like "75.5" is ambiguous without units.

**Independent Test**: Configure a gauge with units "PSI", verify that the dashboard readout displays "75.5 PSI".

**Acceptance Scenarios**:

1. **Given** a Gauge widget is configured with units "°C", **When** the value is 23.4, **Then** the readout MUST display "23.4 °C".
2. **Given** no units are configured, **When** the value is 23.4, **Then** the readout MUST only display "23.4" (no trailing space).

---

### User Story 2 - Real Estate Optimization (Priority: P2)

As a dashboard designer, I want the units to be appended to the value string on the same line so that the gauge arc remains as large as possible.

**Why this priority**: Directly requested to maximize screen real estate for the visual indicator.

**Independent Test**: Verify that adding units does not shift the gauge arc upwards or reduce its size.

## Edge Cases

- **Long Unit Strings**: What happens if the user enters a long unit like "gallons per minute"? The text should probably scale down or use ellipsis to prevent layout breakage.
- **Empty Units**: Ensure no extra whitespace is appended if the unit field is empty.
- **Character Support**: Ensure special characters like "°", "²", or "µ" are rendered correctly.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to specify an optional `units` string for Gauge widgets.
- **FR-002**: System MUST persist the `units` attribute in the `WidgetConfiguration` (DataStore).
- **FR-003**: The `GaugeWidget` MUST display the `units` string appended to the current value (e.g., "$value $units").
- **FR-004**: If `units` is null or blank, the space and unit suffix MUST NOT be rendered.

### Accessibility & UI Requirements *(mandatory)*

- **UI-001**: The units input field MUST be added to the Gauge configuration section in `WidgetPalette`.
- **UI-002**: The units text SHOULD use a slightly lighter weight or smaller scale than the main numeric value to maintain hierarchy.
- **UI-003**: The entire readout (value + units) MUST remain centered below the gauge arc.

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Updated to include `units: String?`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of configured units are correctly persisted across app restarts.
- **SC-002**: Gauge readout updates (value + units) occur in under 100ms when data changes.
- **SC-003**: Zero layout shifting occurs in the gauge arc when units are toggled/changed.
