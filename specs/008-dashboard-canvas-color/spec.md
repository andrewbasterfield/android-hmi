# Feature Specification: Dashboard Canvas Color

**Feature Branch**: `008-dashboard-canvas-color`  
**Created**: 2026-03-12  
**Status**: Draft  
**Input**: User description: "Can we set the background canvas colour?"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Customize Dashboard Background (Priority: P1)

As an Engineer using the HMI, I want to set a custom background color for the entire dashboard canvas via a settings dialog in Edit Mode, so that I can improve visual contrast or follow branding guidelines.

**Why this priority**: Essential for visual customization and making the dashboard usable in different lighting environments.

**Independent Test**: Can be fully tested by clicking a "Dashboard Settings" button in Edit Mode, selecting a color, and verifying the background updates.

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I open the 'Dashboard Settings' and select a new background color, **Then** the dashboard canvas MUST update immediately.
2. **Given** a custom background color is set, **When** I view the dashboard, **Then** all widgets MUST remain visible and usable against the new background.

---

### User Story 2 - Persistent Canvas Color (Priority: P2)

As an Engineer using the HMI, I want my selected canvas color to be saved so that the dashboard looks consistent every time I open the app.

**Why this priority**: Ensures a consistent user experience and professional feel.

**Independent Test**: Can be tested by setting a color, restarting the app, and verifying the color is retained.

**Acceptance Scenarios**:

1. **Given** I have set the canvas color to 'Dark Gray', **When** I close and reopen the app, **Then** the dashboard background MUST remain 'Dark Gray'.

---

### Edge Cases

- **Contrast with Widgets**: If a user selects a background color that matches the widget containers, the borders and content must remain distinguishable. (Automatic border contrast should handle this).
- **Default State**: New layouts should default to the standard Material theme background color.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to select a background color for the dashboard canvas.
- **FR-002**: System MUST use the existing `ColorPalette` for canvas color selection.
- **FR-003**: System MUST persist the selected canvas color in the `DashboardLayout` data model.
- **FR-004**: The canvas color selection UI MUST be located in a 'Dashboard Settings' dialog accessible only in Edit Mode.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: The dashboard MUST maintain sufficient contrast between the canvas and widget elements.
- **A11Y-002**: Color selection UI MUST be accessible and follow the existing patterns used for widget color selection.

### Key Entities *(include if feature involves data)*

- **DashboardLayout**: Updated to include a `canvasColor` (Long?) field.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can change the canvas color in under 15 seconds.
- **SC-002**: 100% of selected colors are correctly persisted and restored upon app restart.
- **SC-003**: No degradation in dashboard performance (60fps) occurs after changing the background color.
