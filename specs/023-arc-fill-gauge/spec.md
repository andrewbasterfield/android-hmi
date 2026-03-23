# Feature Specification: Arc-Filling Gauge Support

**Feature Branch**: `023-arc-fill-gauge`  
**Created**: 2026-03-23  
**Status**: Implemented  
**Input**: User description: "It would be great to support different types of gauge. How about a type of gauge where the arc fills in or completes as the reading rises?"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Configure Gauge Display Style (Priority: P1)

As a dashboard designer, I want to choose between a "Pointer" and "Arc Fill" style for any Gauge widget so that I can create a more modern and visually diverse interface.

**Why this priority**: Core functionality that enables the new visual style.

**Independent Test**: Can be tested by opening the Gauge configuration dialog, selecting "Arc Fill", and saving. The widget should update its appearance in the dashboard.

**Acceptance Scenarios**:

1. **Given** a new Gauge widget is being added, **When** the "Arc Fill" style is selected, **Then** the widget renders a solid filling arc instead of a pointer.
2. **Given** an existing Gauge widget with a pointer, **When** I edit it to use "Arc Fill", **Then** the appearance updates immediately upon saving.

---

### User Story 2 - Dynamic Arc Filling (Priority: P1)

As an operator, I want the gauge arc to accurately reflect the current process value by filling up proportionally from the minimum to the maximum value.

**Why this priority**: Ensures the gauge is functional and provides accurate visual feedback.

**Independent Test**: Can be tested by changing the tag value associated with an "Arc Fill" gauge and observing the arc length.

**Acceptance Scenarios**:

1. **Given** a Gauge is set to "Arc Fill", **When** the value is at `minValue`, **Then** the arc is empty (or at its minimum thickness).
2. **Given** a Gauge is set to "Arc Fill", **When** the value is at `maxValue`, **Then** the arc is completely filled across the entire `arcSweep`.
3. **Given** a Gauge is set to "Arc Fill", **When** the value is halfway between `minValue` and `maxValue`, **Then** the arc is 50% filled.

---

### User Story 3 - Arc Color Customization (Priority: P2)

As a dashboard designer, I want the filling arc to use the same color logic as the pointer (static color or dynamic zone-based color) so that I maintain visual consistency across my dashboard.

**Why this priority**: Provides aesthetic consistency and functional color-coding for alarms/states.

**Independent Test**: Can be tested by configuring color zones and observing the arc color change as the value enters different zones.

**Acceptance Scenarios**:

1. **Given** "Pointer matches Zone Color" is enabled, **When** the value enters a defined color zone, **Then** the filling arc changes color to match that zone.
2. **Given** "Pointer matches Zone Color" is disabled, **When** a static pointer color is defined, **Then** the filling arc uses that static color.

---

### Edge Cases

- **Value out of bounds**: If the value exceeds `maxValue`, the arc should remain fully filled (clamped). If it is below `minValue`, it should remain empty.
- **Overlapping Zones**: If multiple color zones overlap, the arc should use the color of the first matching zone in the list (consistent with current pointer behavior).
- **Small Arc Sweeps**: For gauges with a small `arcSweep` (e.g., 90°), the filling logic must still accurately map the 0-100% range to that 90° span.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to select between "Pointer" and "Arc Fill" display styles for Gauge widgets.
- **FR-002**: System MUST persist the selected gauge style in the widget configuration.
- **FR-003**: In "Arc Fill" mode, the system MUST render a background "track" representing the full range of the gauge.
- **FR-004**: In "Arc Fill" mode, the system MUST render a foreground "fill" arc whose length is proportional to the current value relative to the min/max range.
- **FR-005**: The foreground fill MUST support both static coloring and dynamic zone-based coloring (based on the existing `isPointerDynamic` setting).
- **FR-006**: In "Arc Fill" mode, the pointer/chevron MUST be hidden.
- **FR-007**: The "Arc Fill" gauge MUST respect existing `arcSweep` and `startAngle` calculations to ensure it remains centered.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers (e.g., "Gauge at 75%").
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: UI MUST exclude non-functional decorative elements or gimmicks.
- **UI-004**: The background track for the filling arc MUST have a distinct, low-contrast color to indicate the total range without distracting from the current value.

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Represents the persistent state of a widget.
    - **gaugeStyle**: Enum (POINTER, ARC_FILL) indicating the visual style.
- **GaugeZone**: Defines color-coded ranges for the gauge.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can switch a Gauge from "Pointer" to "Arc Fill" in under 10 seconds (measured from opening the config dialog).
- **SC-002**: The filling arc accurately represents the numeric value with less than 1% visual error across the full sweep.
- **SC-003**: 100% of existing color zone configurations are correctly applied to the "Arc Fill" style without requiring user reconfiguration.
- **SC-004**: The visual transition between styles (saving a config change) occurs in under 200ms.
