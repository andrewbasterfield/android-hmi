# Feature Specification: Dark Mode UI Refinement

**Feature Branch**: `012-dark-mode-ui-refinement`  
**Created**: 2026-03-16  
**Status**: Draft  
**Input**: User description: "I would just like it to look nicer. I would like it to have dark mode default colour scheme, and nicer fonts and colours. Firstly the background, can it be black by default, and the text on the widgets also black, and black removed as an option from the widget colour pickers?"

## Clarifications

### Session 2026-03-16
- Q: Should existing layouts be automatically migrated to the new black background/black text defaults, or should they remain unchanged? → A: Auto-Migrate All: Update all existing layouts to use the new black background and black text immediately.
- Q: Does "nicer fonts" imply using a specific premium/custom font file or standard high-quality system font families? → A: Standard System Sans: Use the best available system "sans-serif" font (e.g., Roboto) optimized for clarity (cockpit-style).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Default Dark Aesthetics (Priority: P1)

As a dashboard user, I want the application to open with a high-contrast dark theme by default so that it looks professional and reduces eye strain in industrial environments.

**Why this priority**: This is the core request and sets the visual foundation for all subsequent refinements.

**Independent Test**: Opening the app for the first time (or after a reset) shows a black background and dark-themed UI components.

**Acceptance Scenarios**:

1. **Given** a new installation, **When** the app starts, **Then** the dashboard canvas background is pure black by default.
2. **Given** the dashboard is visible, **When** viewing the interface, **Then** all default system fonts and colors reflect a modern "dark mode" aesthetic.
3. **Given** existing layouts created before this feature, **When** the app is updated, **Then** those layouts are automatically migrated to use the new black background and default dark theme.

---

### User Story 2 - Widget Visual Consistency (Priority: P2)

As a dashboard designer, I want widgets to have consistent black text and restricted color options so that the UI remains readable and adheres to the new design language.

**Why this priority**: Ensures that the content within the dashboard remains legible and aesthetically aligned with the dark theme.

**Independent Test**: Adding or editing a widget shows black text on the widget face, and the color picker no longer offers "Black" as a selectable background color for the widget itself.

**Acceptance Scenarios**:

1. **Given** any widget on the dashboard, **When** it displays a label or value, **Then** the text color is black.
2. **Given** the widget configuration dialog, **When** selecting a background color for a widget, **Then** "Black" is not available as an option.

---

### User Story 3 - Enhanced Typography and Color Palette (Priority: P3)

As a user, I want the fonts and color accents to feel "nicer" and more modern compared to the standard system defaults.

**Why this priority**: Adds the final layer of "polish" requested by the user.

**Independent Test**: Comparing the new UI to the previous version shows distinct, high-quality typography and a more refined color palette for non-black elements.

**Acceptance Scenarios**:

1. **Given** the dashboard, **When** text is displayed, **Then** it uses a modern, highly readable sans-serif typeface optimized for clarity and HMI display (e.g., Roboto).
2. **Given** interactive elements (buttons, sliders), **When** they are rendered, **Then** they use a refined color palette that complements the black background.

---

### Edge Cases

- **Contrast with Black Text**: If a user selects a very dark color (other than black) for a widget background, the black text might become unreadable. (Assumption: We will use a `HmiPalette` that avoids near-black colors).
- **Existing Layouts**: How do we handle layouts created before this change that might have different background colors? → All existing layouts will be automatically migrated to the new black background and black text defaults upon first launch after the update, as detected by the absence of the theme version flag in `DataStore`.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST use `HmiTheme` to enforce a pure black (#000000) background for the dashboard canvas by default.
- **FR-002**: All widgets MUST display their primary text (labels, values) in black (#000000).
- **FR-003**: The widget color picker MUST use the `HmiPalette` and MUST NOT include "Black" as a selectable option for widget backgrounds.
- **FR-004**: The system MUST apply the standard high-quality system sans-serif font family (e.g., Roboto) to all dashboard elements, optimized for cockpit-style clarity.
- **FR-005**: The system MUST provide a curated `HmiPalette` for widgets that ensures high contrast with black text.
- **FR-006**: The system MUST automatically migrate all existing layouts to the new black background and black text defaults on first run, detected via a version flag in DataStore.
- **FR-007**: The system MUST provide a per-widget `fontSizeMultiplier` (0.5x - 2.5x) adjustable via the widget configuration dialog.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **A11Y-004**: The system MUST ensure a minimum contrast ratio of 4.5:1 for all text against widget backgrounds, even with the "black text" requirement.

### Key Entities *(include if feature involves data)*

- **Theme Configuration**: Represents the global visual settings (default background, font families, curated palette).
- **Widget Visual Style**: The set of visual attributes applied to a widget (background color, text color, font weight).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of new dashboards initialize with a black background.
- **SC-002**: Zero widgets can be configured with a black background color via the UI.
- **SC-003**: 100% of text elements on widgets are rendered in black.
- **SC-004**: Users report a perceived "higher quality" or "more modern" look compared to the previous version in qualitative feedback.
- **SC-005**: All text elements meet or exceed WCAG AA contrast standards (4.5:1) against their respective backgrounds.
