# Feature Specification: Linear Gauges

**Feature Branch**: `030-linear-gauges`  
**Created**: 2026-03-28  
**Status**: Draft  
**Input**: User description: "I would like another type of gauge, a flat vertical or horizontal versions of the existing guage which follows an arc. Specifically I am thinking something like the ARC_FILL gauge type but not operating along an arc, but operating along a straight axis, either horiozontal or vertical. And maybe having a pointer aguge, but moving across a striight line? Discuss"

## Clarifications

### Session 2026-03-28
- Q: What is the specific visual shape of the POINTER indicator for linear axes? → A: Triangle (caret), matching the existing ARC implementation.
- Q: Should the linear gauge axes use the same track width as the slider or the arc gauge? → A: Match ARC Proportions: Both the thin base track and the "fat" fill part MUST match the existing ARC axis proportions (e.g., Fill is 3x the Base track thickness).
- Q: For a Linear gauge, where should the pointer (caret) be positioned relative to the track? → A: Edge-Aligned (Side): The caret sits adjacent to the track, pointing inward at the center line (e.g., Left side for Vertical, Top side for Horizontal).
- Q: How should the Axis and Indicator options be presented in the configuration dialog? → A: Two Rows: Separate rows of chips for Axis (Arc, Horizontal, Vertical) and Indicator (Pointer, Fill) selection.
- Q: Where should the ticks and numeric labels be placed relative to the track and the pointer? → A: Opposite Side: Ticks and numeric scale labels are placed on the side opposite to the pointer caret to prevent visual crowding.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Linear Fill Gauge (Priority: P1)

As an operator, I want to see process values (like tank levels) displayed as a filling bar on a straight axis, so that I can quickly assess the state of the system using a linear mental model.

**Why this priority**: High. Linear fill gauges are the most common representation for levels and capacities in industrial HMIs.

**Independent Test**: Can be fully tested by configuring a gauge to `LINEAR_FILL` and verifying it fills correctly based on tag data in both horizontal and vertical orientations.

**Acceptance Scenarios**:

1. **Given** a gauge configured as `LINEAR_FILL` and `VERTICAL`, **When** the bound tag value increases, **Then** the bar fills from bottom to top.
2. **Given** a gauge configured as `LINEAR_FILL` and `HORIZONTAL`, **When** the value increases, **Then** the bar fills from left to right.

---

### User Story 2 - Linear Pointer Gauge (Priority: P1)

As an engineer, I want to use a needle-style indicator moving along a straight scale, so that I can provide precise readouts for parameters where the absolute position on a range is critical.

**Why this priority**: High. Provides parity with the existing `POINTER` arc gauge for linear contexts.

**Independent Test**: Can be tested by dragging a simulated tag value and verifying the pointer moves smoothly along the linear track.

**Acceptance Scenarios**:

1. **Given** a gauge configured as `LINEAR_POINTER`, **When** the value updates, **Then** a distinct indicator (pointer) moves to the corresponding position on the track without filling the background.

---

### User Story 3 - Visual Consistency & Zones (Priority: P2)

As a designer, I want linear gauges to support the same color zones and tick marks as arc gauges, so that the visual language of safety and limits is consistent across all widget types.

**Why this priority**: Medium. Ensures the "Stitch" design system remains unified.

**Independent Test**: Can be tested by defining color zones for a linear gauge and verifying they appear correctly behind or along the track.

**Acceptance Scenarios**:

1. **Given** a linear gauge with defined `colorZones`, **When** it is rendered, **Then** the background or track reflects those zones (e.g., Red for high pressure) at the correct positions on the linear scale.

---

### Edge Cases

- **Narrow/Short Widgets**: How do ticks and labels render when the widget is extremely thin (1-col vertical) or short (1-row horizontal)?
- **Inverted Logic**: Does "Fill" always mean increasing value, or can it be configured to fill from top-down for specific industrial contexts? (Assumption: Default is standard Up/Right increase).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support three Gauge Axis types: `ARC`, `LINEAR_HORIZONTAL`, and `LINEAR_VERTICAL`.
- **FR-002**: System MUST support two Gauge Indicator types: `FILL` and `POINTER`.
- **FR-003**: System MUST allow any Axis to be combined with any Indicator (e.g., Linear Vertical Axis with a Pointer indicator).
- **FR-004**: Linear Axes MUST support the existing `colorZones` configuration, rendering them as colored segments along the track.
- **FR-005**: Linear Axes MUST support `targetTicks` for rendering numeric scales along the track.
- **FR-006**: The `FILL` indicator MUST fill from the start of the axis (Bottom for Vertical, Left for Horizontal, Start-Angle for Arc).
- **FR-007**: The `POINTER` indicator MUST use a triangle (caret) visual marker that moves along the chosen axis without filling the background, matching the existing `ARC` axis pointer style. For linear axes, the caret MUST be edge-aligned (e.g., Left for Vertical, Top for Horizontal) and point inward toward the track.
- **FR-008**: System MUST maintain visual consistency in track thickness between `ARC` and `LINEAR` axes. The "fat" `FILL` indicator MUST be 3x the thickness of the base track, matching the existing `ARC` implementation.
- **FR-009**: System MUST provide a configuration UI in `WidgetConfigDialog` with two separate rows of `FilterChip` selections: one for Axis type (`ARC`, `LINEAR_HORIZONTAL`, `LINEAR_VERTICAL`) and one for Indicator type (`POINTER`, `FILL`).
- **FR-010**: For linear axes, numeric scale labels and ticks MUST be placed on the side opposite to the `POINTER` caret (e.g., if pointer is Left, ticks/labels are Right) to prevent visual crowding.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers (e.g., "Level Gauge at 75%").
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST maintain the "Stitch" aesthetics (rugged, industrial look, monospace metrics).
- **UI-003**: UI MUST ensure labels and metrics remain upright regardless of axis orientation.

### Key Entities *(include if feature involves data)*

- **GaugeAxis (Enum)**: `ARC`, `LINEAR_HORIZONTAL`, `LINEAR_VERTICAL`.
- **GaugeIndicator (Enum)**: `FILL`, `POINTER`.
- **WidgetConfiguration (Updated)**: Includes `gaugeAxis` and `gaugeIndicator` fields.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can change either the axis or indicator type in under 5 seconds via settings.
- **SC-002**: All 6 combinations (3 axes x 2 indicators) are visually distinct and functionally accurate.
- **SC-003**: 100% of defined `colorZones` are visually accurate on both linear and arc axes.
- **SC-004**: Labels and metrics remain legible across all supported grid sizes.
