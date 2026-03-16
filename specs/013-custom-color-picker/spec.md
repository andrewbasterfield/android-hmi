# Feature Specification: Custom Color Picker

**Feature Branch**: `013-custom-color-picker`  
**Created**: 2026-03-16  
**Status**: Draft  
**Input**: User description: "Can I have a custom colour picker (a picker for custom colours)?"

## Clarifications

### Session 2026-03-16
- Q: How should the user transition between the existing hardcoded palette and the new custom picker (Hex/Spectrum)? → A: Tabbed Interface (Separate tabs for "Palette", "Spectrum", and "Hex").
- Q: How many recently used custom colors should be displayed to the user? → A: 8 colors (Fits well in one row on mobile).
- Q: How should the UI respond when the user types an invalid hex code? → A: Visual Error (Show an error message and disable "Save" button until fixed).
- Q: Should there be a "Reset to Default" or "Clear Color" option in the custom picker? → A: Clear Button (A dedicated button to remove the background color).
- Q: Should the "Recent Colors" history be shared across all widgets, or unique to each widget type? → A: Global History (Shared across all widgets and the dashboard canvas).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Pick a Custom Hex Color (Priority: P1)

As a dashboard editor, I want to enter a specific hexadecimal color code so that I can match the HMI precisely to my organization's brand or safety standards.

**Why this priority**: High. This is the core "custom" requirement. It allows power users to bypass the curated list.

**Independent Test**: Can be fully tested by entering "#FF5733" in a text field and seeing the widget background update immediately.

**Acceptance Scenarios**:

1. **Given** I am in the Widget Configuration dialog, **When** I enter a valid 6-digit hex code (e.g., "00FF00"), **Then** the color preview updates to that color.
2. **Given** I have entered a valid hex code, **When** I save the widget, **Then** the widget on the dashboard displays the custom color.

---

### User Story 2 - Visual Spectrum Selection (Priority: P2)

As a dashboard editor, I want to select a color from a visual gradient or spectrum so that I can quickly find a color that looks good without knowing hex codes.

**Why this priority**: Medium. Provides a better "creative" experience for non-technical users.

**Independent Test**: Can be tested by tapping a "Custom" button to open a color wheel/spectrum and selecting a point.

**Acceptance Scenarios**:

1. **Given** I am picking a color, **When** I drag my finger across a color spectrum, **Then** the selected color updates in real-time.

---

### User Story 3 - Recent/Favorite Custom Colors (Priority: P3)

As a dashboard editor, I want to see a list of my recently used custom colors so that I can quickly apply the same custom color to multiple widgets without re-typing hex codes.

**Why this priority**: Low. This is a "polish" feature for efficiency.

**Independent Test**: Can be tested by picking a custom color for Widget A, then seeing that color appear as a shortcut when configuring Widget B.

**Acceptance Scenarios**:

1. **Given** I have successfully applied a custom color to a widget, **When** I open the configuration for a different widget, **Then** the previous custom color appears in a "Recent Colors" row.

### Edge Cases

- **Invalid Hex Input**: If an invalid hex code (e.g., "G12345") is entered, the system MUST show an error message and disable the "Save" button until a valid format is provided.
- **Contrast Handling**: System will automatically calculate the luminance of the chosen color. If the color is too dark for black text (contrast < 4.5:1), the widget text MUST automatically switch to White to maintain readability.
- **Transparency Support**: The custom picker will only support opaque (100% alpha) colors to ensure visual consistency and readability across all dashboards.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a way to input a custom hexadecimal color code for both widget backgrounds and dashboard canvas.
- **FR-002**: System MUST validate hex inputs, display an error message for invalid formats, and disable the "Save" action until fixed.
- **FR-003**: System MUST provide a visual spectrum or slider-based picker (e.g., Hue/Saturation/Value) to discover colors.
- **FR-004**: System MUST store and persist custom color values alongside the widget configuration.
- **FR-005**: System MUST allow users to switch between the curated "High Contrast" palette and the "Custom" picker using a tabbed interface (Palette, Spectrum, Hex).
- **FR-006**: System MUST automatically toggle between Black and White text color based on the selected custom background's luminance to ensure at least 4.5:1 contrast.
- **FR-007**: Custom color picker MUST NOT support transparency/alpha channel adjustments.
- **FR-008**: System MUST persist a global history of the 8 most recently used custom colors across all widgets and the dashboard canvas.
- **FR-009**: System MUST provide a "Clear" or "Reset" button to remove the custom color and return to the default background state.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements (sliders, color wheel, text fields) MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **A11Y-004**: System MUST provide a visual contrast indicator showing which text color (Black or White) is being applied for the selected background.

### Key Entities *(include if feature involves data)*

- **CustomColor**: Represents an RGB value that is not part of the hardcoded `HmiPalette`.
- **RecentColorsList**: A persistent collection of the 8 most recently used custom colors, shared globally across all widgets.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can apply any valid RGB/Hex color to a widget in under 10 seconds.
- **SC-002**: 100% of valid hex codes entered result in the correct color being displayed on the dashboard.
- **SC-003**: Custom colors are successfully persisted and reloaded across app restarts.
- **SC-004**: 100% of custom colors maintain a minimum 4.5:1 contrast ratio by automatically switching text color.
