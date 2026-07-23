# Feature Specification: Multiple Layout support

**Feature Branch**: `034-multiple-layouts`  
**Created**: 2026-06-07  
**Status**: Draft  
**Input**: User description: "Multiple Layout support"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create and Switch Layouts (Priority: P1)

As a user, I want to create multiple different dashboard layouts and switch between them so that I can have specialized dashboards for different tasks or machines.

**Why this priority**: This is the core functionality that enables users to move beyond a single static dashboard.

**Independent Test**: Create a new layout named "Maintenance", add a widget to it, switch back to the default layout, then switch back to "Maintenance" and verify the widget is still there.

**Acceptance Scenarios**:

1. **Given** the app is open on the default dashboard, **When** I select "Create New Layout" and enter "Production Line A", **Then** a new empty dashboard is displayed.
2. **Given** I have multiple layouts saved, **When** I select "Production Line A" from the layout list, **Then** the dashboard instantly updates to show the widgets configured for that layout.

---

### User Story 2 - Manage Layouts (Rename/Delete) (Priority: P2)

As a user, I want to rename or delete my saved layouts so that I can keep my dashboard list organized.

**Why this priority**: Essential for long-term usability; users need to be able to fix typos and remove old dashboards.

**Independent Test**: Rename an existing layout and verify the name update is reflected in the list and the dashboard header. Delete a layout and verify it no longer appears in the list.

**Acceptance Scenarios**:

1. **Given** a layout named "Test 1", **When** I rename it to "Calibration", **Then** the layout is persisted with the new name.
2. **Given** an unwanted layout, **When** I select "Delete" and confirm the action, **Then** the layout is permanently removed from storage.
3. **Given** I am deleting the currently active layout, **When** I confirm deletion, **Then** the system automatically switches to the next available layout (or the Demo layout if none remain).

---

### User Story 3 - Duplicate Layout (Priority: P3)

As a user, I want to duplicate an existing layout so that I can use it as a template for a new one without starting from scratch.

**Why this priority**: Significant efficiency improvement for users who need multiple similar dashboards with minor variations.

**Independent Test**: Select "Duplicate" on a complex layout and verify that the new layout contains identical widgets, positions, and configurations.

**Acceptance Scenarios**:

1. **Given** a layout "Station 1" with 10 widgets, **When** I select "Duplicate", **Then** a new layout "Station 1 (Copy)" is created with the exact same 10 widgets.

---

### Edge Cases

- **Last Layout Deletion**: If a user deletes their only layout, the system MUST automatically regenerate the default Demo Layout to prevent an empty/broken state.
- **Duplicate Names**: If a user tries to name a layout with an existing name, the system SHOULD automatically append a suffix (e.g., "(2)") or warn the user.
- **App Interruption**: If the app is closed while switching layouts, it MUST boot back into the last successfully selected layout.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to view a list of all saved layouts.
- **FR-002**: System MUST allow users to create a new, empty layout with a custom name.
- **FR-003**: System MUST allow users to switch the active dashboard to any saved layout.
- **FR-004**: System MUST persist all saved layouts independently using the existing DataStore infrastructure.
- **FR-005**: System MUST persist the identity of the currently active layout so it is restored on app launch.
- **FR-006**: System MUST allow users to rename an existing layout.
- **FR-007**: System MUST allow users to delete a layout (with a mandatory confirmation prompt).
- **FR-008**: System MUST provide a "Duplicate" function that creates a deep copy of a layout.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements (buttons, list items) MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes without breaking layouts.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.
- **UI-003**: UI MUST exclude non-functional decorative elements or gimmicks.
- **UI-004**: The layout management list MUST be accessible from the main dashboard screen (e.g., via a "Layouts" button or side drawer).

### Key Entities *(include if feature involves data)*

- **DashboardLayout**: (Existing) Represents the full configuration of a dashboard, including its name, canvas settings, and all widgets.
- **LayoutRegistry**: A new entity or data structure that maintains the list of available layout IDs/Names and tracks the "Active" layout.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can switch between any two layouts in under 1 second.
- **SC-002**: 100% of layout management actions (create, rename, delete, duplicate) are persisted immediately.
- **SC-003**: Users can successfully create and maintain at least 20 different layouts without performance degradation.
- **SC-004**: Zero data loss occurs when switching between or duplicating layouts.
