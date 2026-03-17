# Feature Specification: Modern Industrial UI

**Feature Branch**: `014-modern-industrial-ui`  
**Created**: 2026-03-16  
**Status**: Draft  
**Input**: User description: "Apply modern industrial aesthetic: implement rounded corners (8dp) for all widgets and ensure black text is used consistently on vibrant backgrounds as per unnamed.png"

## Clarifications

### Session 2026-03-16
- Q: How should the system protect the "Black Text" aesthetic when a user picks a background that is too dark? → A: Hybrid Contrast (Use Black text by default for vibrant/light colors, but switch to White text if luminance is below 0.2 to ensure legibility).
- Q: Should we use a fixed 8dp radius regardless of widget size, or scale it down for smaller elements? → A: Adaptive Radius (Scale the radius down, e.g., to 4dp, for the smallest widget sizes).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Rounded Interactive Elements (Priority: P1)

As a dashboard operator, I want the widgets to have rounded corners so that the interface feels modern and the interactive zones are clearly distinguishable from the background.

**Why this priority**: High. This is the primary visual change that defines the "Modern Industrial" look requested by the user.

**Independent Test**: Can be fully tested by looking at any widget or dialog on the dashboard and verifying they have soft corners (8dp) instead of sharp right angles.

**Acceptance Scenarios**:

1. **Given** a widget on the dashboard, **When** it is rendered, **Then** its container MUST have rounded corners with a radius of 8dp (or 4dp for small widgets).
2. **Given** a configuration dialog, **When** it appears, **Then** its container and buttons MUST have matching rounded corners.

---

### User Story 2 - High-Contrast Black Typography (Priority: P2)

As a dashboard designer, I want all text on vibrant widget backgrounds to be black so that the dashboard has a consistent, authoritative, and physical feel.

**Why this priority**: Medium. Ensures the aesthetic consistency seen in the reference image (unnamed.png) where even the red button uses black text.

**Independent Test**: Select a vibrant background color (like Cherry Red) and verify the text remains Black, overriding standard contrast-checking defaults if necessary.

**Acceptance Scenarios**:

1. **Given** a widget with a vibrant background color (Red, Green, Blue, etc.), **When** text is displayed, **Then** the text color MUST be Black (#000000).

---

### User Story 3 - Visual Depth & Border Polish (Priority: P3)

As a user, I want the widgets to have a subtle border that complements the rounded corners, making them pop against the pure black background.

**Why this priority**: Low. Fine-tuning the visual polish to ensure the rounded shapes are clearly visible on the black canvas.

**Independent Test**: Observe widgets on a black background and verify the border is visible and follows the rounded corner path.

**Acceptance Scenarios**:

1. **Given** a widget on a pure black background, **When** viewed, **Then** it MUST have a subtle contrasting border following the adaptive 8dp/4dp curve.

### Edge Cases

- **Very Dark Custom Colors**: If a user selects a background color with luminance < 0.2, the system MUST automatically switch the text color to White (#FFFFFF) to maintain legibility while allowing the dark background.
- **Scaling Radius**: Small widgets (e.g. 1x1 grid size) MUST use a reduced 4dp radius to avoid looking overly circular or distorted, while larger widgets use the standard 8dp.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST apply a standard corner radius of 8dp to widgets and buttons.
- **FR-002**: System MUST apply a reduced corner radius of 4dp for "small" widgets (defined as 1x1 grid size).
- **FR-003**: System MUST default all widget foreground content to Black (#000000) for backgrounds with luminance >= 0.2.
- **FR-004**: System MUST switch widget foreground content to White (#FFFFFF) for backgrounds with luminance < 0.2.
- **FR-005**: System MUST prioritize Black text (#000000) for vibrant colors, but MUST switch to White text (#FFFFFF) if the 4.5:1 contrast ratio (WCAG AA) cannot be met, overriding the 0.2 threshold if necessary for accessibility.
- **FR-006**: System MUST apply a 1dp subtle border (Color: #FFFFFF with 0.15 alpha) to all widgets to ensure visibility on pure black backgrounds.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **A11Y-004**: System MUST ensure a minimum 4.5:1 contrast ratio (WCAG AA) by dynamically toggling between black and white text, prioritizing accessibility over the 0.2 luminance aesthetic threshold.

### Key Entities *(include if feature involves data)*

- **AdaptiveShapeDefinition**: The logic determining corner radius based on widget size (8dp standard, 4dp small).
- **HybridContrastLogic**: The threshold-based logic (0.2 luminance) for toggling between black and white foregrounds.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of widgets on the dashboard exhibit rounded corners matching their size-specific radius.
- **SC-002**: 100% of widgets with vibrant backgrounds (L >= 0.2) use Black (#000000) text.
- **SC-003**: 100% of widgets with very dark backgrounds (L < 0.2) use White (#FFFFFF) text.
- **SC-004**: Zero accessibility violations regarding touch target size on new rounded components.
