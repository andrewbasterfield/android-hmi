# Feature Specification: Setup Navigation Rename

**Feature Branch**: `005-setup-navigation-rename`  
**Created**: 2026-03-12  
**Status**: Draft  
**Input**: User description: "Rename 'Back' Button: Change to 'Setup' or 'Connection' to better reflect its destination (PLC configuration). Add a settings icon for clarity."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Clear Navigation Intent (Priority: P1)

As an Engineer using the HMI, I want the button that returns to the PLC configuration screen to be clearly labeled "Setup" and have an intuitive icon, so that I immediately understand its destination without trial and error.

**Why this priority**: Improves user experience and reduces cognitive load by making navigation explicit.

**Independent Test**: Can be fully tested by observing the button on the Dashboard top bar and verifying it uses the new label and icon.

**Acceptance Scenarios**:

1. **Given** I am on the Dashboard screen, **When** I look at the top bar, **Then** I see a button labeled "Setup" instead of "Back".
2. **Given** the "Setup" button is visible, **When** I look at its content, **Then** it includes a settings/cog icon alongside the text.
3. **Given** I click the "Setup" button, **When** the transition completes, **Then** I am navigated back to the PLC Connection/Configuration screen.

---

### Edge Cases

- **Icon Position**: Should the icon be to the left or right of the text? (Default: Left for standard Material Design patterns).
- **Text Length**: Does "Setup" fit well on smaller phone screens without crowding the "Edit Mode" toggle? (Must ensure sufficient spacing).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST rename the existing "Back" button in the Dashboard top bar to "Setup".
- **FR-002**: System MUST add a standard settings/cog icon to the "Setup" button.
- **FR-003**: The button MUST retain its existing functionality of navigating back to the `ConnectionScreen`.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: The "Setup" button MUST maintain a minimum touch target of 48x48dp.
- **A11Y-002**: The settings icon MUST have a content description (e.g., "Go to Setup").
- **A11Y-003**: The combination of icon and text MUST be rendered using the theme's standard button colors for consistency.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of Dashboard users see the "Setup" label instead of the generic "Back" label.
- **SC-002**: Navigation behavior remains 100% consistent with the previous "Back" functionality.
- **SC-003**: No layout breakages occur on screens as small as 320dp width due to the new button content.
