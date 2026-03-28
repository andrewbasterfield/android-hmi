# Feature Specification: Vertical Slider Variant

**Feature Branch**: `029-vertical-slider`  
**Created**: 2026-03-28  
**Status**: Draft  
**Input**: User description: "Can we have a vertical varient of the current (horizontal) slider? Discuss."

## Clarifications

### Session 2026-03-28
- Q: Should the slider's orientation be a fixed property or automatically flip when the device is rotated? → A: Fixed Property: The user explicitly chooses orientation; it never changes unless manually edited.
- Q: When the user switches a slider from Horizontal to Vertical, should the system automatically swap the Width/Height? → A: Swap Dimensions: Automatically swap `colSpan` and `rowSpan` when the orientation toggle is flipped.
- Q: For a Vertical slider, should the labels and metrics be placed vertically or horizontally relative to the slider track? → A: Vertical Stack: Label (Top), Slider Track (Center), Current Value Metric (Bottom).
- Q: For a Vertical slider, should the thumb and track widths be identical to the horizontal variant's height and thickness, or should they be adjusted for better vertical grip? → A: Swap Dimensions: Thumb becomes 32dp wide and 24dp high; Track width is kept at 8dp.
- Q: Should the vertical slider include extra visual symbols (like "+" and "-" labels) to show direction? → A: None: Stick to Min/Max labels as defined in FR-007 to indicate direction.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Vertical Control Configuration (Priority: P1)

As an engineer designing a control panel, I want to place a vertical slider for parameters that feel more natural with a vertical motion (like tank levels or volume), so that the interface is more intuitive for the operator.

**Why this priority**: High. Vertical sliders are a standard UI element in industrial HMIs and are essential for many types of process control.

**Independent Test**: Can be fully tested by selecting "Vertical" in the widget configuration and verifying the slider renders vertically on the dashboard.

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I add a Slider and select "Vertical" orientation, **Then** the slider is rendered vertically within its grid cell.
2. **Given** a vertical slider is placed, **When** I save the layout and restart the app, **Then** the slider remains vertical.

---

### User Story 2 - Vertical Interaction (Priority: P1)

As an operator, I want to slide the thumb up and down to adjust values, with upward motion increasing the value, so that I can precisely control the process.

**Why this priority**: High. This is the core functional interaction for the feature.

**Independent Test**: Can be tested by dragging the vertical slider thumb and verifying that the tag value updates correctly.

**Acceptance Scenarios**:

1. **Given** a vertical slider with a range of 0 to 100, **When** I drag the thumb towards the top of the widget, **Then** the value increases towards 100.
2. **Given** a vertical slider, **When** I drag the thumb towards the bottom, **Then** the value decreases towards 0.

---

### User Story 3 - Responsive Labeling (Priority: P2)

As a designer, I want the labels and metrics to be positioned logically around the vertical slider so that they don't overlap and remain easy to read.

**Why this priority**: Medium. Ensures clarity and follows the "Clarity by Design" principle.

**Independent Test**: Can be tested by changing the widget size (colSpan/rowSpan) and verifying that the label, slider, and metric maintain a clean vertical stack.

**Acceptance Scenarios**:

1. **Given** a vertical slider, **When** it is rendered, **Then** the label is at the top, the vertical slider fills the middle, and the current value metric is at the bottom.

---

### Edge Cases

- **Narrow Width**: What happens if a vertical slider is placed in a 1-column cell? (The thumb and track must remain usable/touchable).
- **Extreme Aspect Ratios**: How does the vertical slider behave if the cell is very wide but short? (Orientation should dictate the primary axis of motion regardless of cell shape).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support two orientation modes for Slider widgets: `HORIZONTAL` and `VERTICAL`. These MUST be fixed configuration properties chosen by the user in settings and MUST NOT automatically change based on device orientation.
- **FR-002**: System MUST allow users to toggle between orientations in the `WidgetConfigDialog`. When a user toggles orientation, the system MUST automatically swap the widget's `colSpan` and `rowSpan` (e.g., 4x1 becomes 1x4) to maintain the intended aspect ratio.
- **FR-003**: Vertical Sliders MUST increase in value when dragged upwards and decrease when dragged downwards.
- **FR-004**: System MUST persist the orientation setting in the `WidgetConfiguration`.
- **FR-005**: Vertical Sliders MUST maintain the "Stitch" design system aesthetics. The thumb MUST be 32dp wide and 24dp high (swapped from horizontal), and the active track MUST be 8dp wide.
- **FR-006**: Vertical Sliders MUST use a vertical layout stack: Label (Top), Slider Track (Center), and Current Value Metric (Bottom).
- **FR-007**: System MUST ensure that Min/Max labels for Vertical Sliders are placed at the Bottom (Min) and Top (Max) of the track area respectively, and remain upright.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements (slider thumb) MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: UI MUST exclude non-functional decorative elements or gimmicks.

### Key Entities *(include if feature involves data)*

- **WidgetOrientation (Enum)**: Represents the orientation of the widget (`HORIZONTAL`, `VERTICAL`).
- **WidgetConfiguration (Updated)**: Includes an `orientation` field of type `WidgetOrientation`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can toggle between horizontal and vertical orientations in under 5 seconds.
- **SC-002**: 100% of vertical sliders correctly update their bound PLC tags when manipulated.
- **SC-003**: Labels and metrics remain 100% legible in both orientations across all supported font sizes.
- **SC-004**: Zero regressions in horizontal slider functionality.
