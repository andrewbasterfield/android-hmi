# Feature Specification: Gauge Color Customization

**Feature Branch**: `019-customize-gauge-colors`  
**Created**: 2026-03-21  
**Status**: Draft  
**Input**: User description: "Hi gemini I would love to be able to change the colours of a gauge. Hi gemini please read the existing gauge and the colour threshold to understand the starting point."

## Starting Point Analysis

The existing `GaugeWidget` (and `GaugeZone` model) provides the following "Threshold" (Zone) capabilities:
- **Color Zones**: A list of ranges (`startValue` to `endValue`) that render as colored segments on the gauge arc.
- **Static Colors**: The needle, ticks, and labels currently use the system's `contentColor` (derived from the widget's background contrast).
- **Manual Thresholds**: Users can already define multiple colored zones via the `WidgetConfigDialog`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Custom Indicator Styling (Priority: P1)

As a dashboard designer, I want to manually set a specific color for the gauge's needle so that it stands out against the background regardless of the current value.

**Why this priority**: Core styling requirement. Decoupling the indicator color from the system default allows for better branding and emphasis.

**Independent Test**: Can be fully tested by opening the gauge configuration, selecting a "Needle Color," and seeing the needle update on the dashboard.

**Acceptance Scenarios**:

1. **Given** a Gauge widget is being edited, **When** I select a custom needle color, **Then** the needle MUST use that color instead of the system default.
2. **Given** a custom needle color is set, **When** the gauge value changes, **Then** the needle MUST retain its custom color.

---

### User Story 2 - Value-Driven Color Thresholds (Priority: P2)

As an operator, I want the gauge needle to automatically change color based on the value it is currently pointing at (following the defined color zones) so that I can immediately recognize critical states from a distance.

**Why this priority**: High value for industrial safety. It transforms the gauge from a static visual to an active status indicator.

**Independent Test**: Define a green zone (0-80) and a red zone (80-100). Move the gauge value from 50 to 95 and verify the needle flips from green to red.

**Acceptance Scenarios**:

1. **Given** a gauge has defined Color Zones, **When** the "Follow Zone Color" option is enabled, **Then** the needle MUST match the color of the zone corresponding to the current value.
2. **Given** the current value is outside all defined zones, **When** "Follow Zone Color" is enabled, **Then** the needle MUST fallback to its default/manual color.

---

### User Story 3 - Scale & Tick Customization (Priority: P3)

As a dashboard designer, I want to customize the color of the scale ticks and labels independently of the needle and background so that I can create a more refined visual hierarchy.

**Why this priority**: Advanced styling. Allows for subtle UI refinements like "faded" scales that don't distract from the primary data.

**Independent Test**: Change the "Scale Color" to a light grey and verify that only the ticks and numeric labels are affected.

**Acceptance Scenarios**:

1. **Given** a Gauge configuration, **When** a custom Scale Color is selected, **Then** all ticks and value labels MUST use that color.

## Edge Cases

- **Overlapping Zones**: If two zones overlap, the needle should follow the color of the *last* defined zone that contains the current value (standard stack behavior).
- **Contrast Conflicts**: If a user selects a red needle and it moves over a red zone segment, the needle may become invisible. The system should ideally provide a subtle stroke or shadow for the needle to maintain visibility.
- **Dark/Light Mode**: Custom absolute colors (HEX) should remain fixed, but "Default" colors should still adapt to theme changes.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to specify a static `needleColor` for Gauge widgets.
- **FR-002**: System MUST allow users to specify a static `scaleColor` (applying to ticks and labels) for Gauge widgets.
- **FR-003**: System MUST provide a toggle for "Dynamic Needle Color" which forces the needle to match the color of the current value's zone.
- **FR-004**: System MUST persist these new color attributes in the `WidgetConfiguration` (DataStore).
- **FR-005**: The `GaugeWidget` MUST prioritize dynamic coloring over static manual colors if the "Dynamic Needle Color" toggle is active and a matching zone exists.
- **FR-006**: System MUST allow resetting individual colors to "Default" (Theme-based).

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **UI-001**: Color controls MUST be integrated into the existing `WidgetConfigDialog` for Gauges.
- **UI-002**: The UI MUST use the `HmiColorPicker` (from `013-custom-color-picker`) for selection.
- **UI-003**: The gauge preview in the config dialog MUST reflect color changes in real-time.

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Updated to include `needleColor: Long?`, `scaleColor: Long?`, and `isNeedleDynamic: Boolean`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can enable "Dynamic Needle Color" in under 5 seconds.
- **SC-002**: Needle color updates MUST reflect zone transitions in under 16ms (60fps target).
- **SC-003**: 100% of custom colors are correctly persisted across application restarts.
- **SC-004**: Manual color selections MUST NOT break the accessibility contrast of the underlying dashboard canvas.
