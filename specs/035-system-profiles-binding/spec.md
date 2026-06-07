# Feature Specification: System Profiles and Binding

**Feature Branch**: `035-system-profiles-binding`  
**Created**: 2026-06-07  
**Status**: Draft  
**Input**: User description: "System Profiles and Binding - Connecting Layouts and Connection Profiles for intuitive management"

## Clarifications

### Session 2026-06-07

- Q: When deleting a Connection/Layout bound to a System Profile, what is the behavior? → A: Prevent deletion of the underlying resource, requiring the user to unbind/delete the System Profile first.
- Q: If a user manually overrides a layout while a System Profile is active, is it permanent? → A: It becomes a temporary "Unsaved Binding". The system restores the last explicitly selected state on launch.
- Q: Should there be a built-in "Demo" System Profile? → A: Yes, provide a read-only "Demo System" profile binding the built-in Demo Connection to the Demo Layout.
- Q: What is the dismissal behavior of the Management Hub (Drawer) on selection? → A: Auto-close: Selecting a profile immediately closes the drawer and applies the configuration.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create a System Profile (Priority: P1)

As an engineer, I want to bind a specific dashboard layout to a specific PLC connection profile as a "System Profile" so that I can launch a complete operational environment with a single action.

**Why this priority**: This is the core "opinionated" binding that simplifies the user experience from technical setup to operational readiness.

**Independent Test**: Select a connection profile (e.g., "Line 1") and a layout (e.g., "Operator View"), save them as a System Profile named "Line 1 Production", and verify it appears in the Presets list.

**Acceptance Scenarios**:

1. **Given** I have a connection profile and a layout selected, **When** I choose "Save as System Profile", **Then** I am prompted for a name.
2. **Given** a new System Profile is saved, **When** I view the Management Hub, **Then** the new profile is visible in the "Systems" or "Presets" section.

---

### User Story 2 - One-Tap Launch (Priority: P1)

As an operator, I want to tap a System Profile and have the app automatically connect to the PLC and load the correct layout so that I don't have to navigate multiple technical screens.

**Why this priority**: Essential for day-to-day usability in industrial environments where speed and simplicity are critical.

**Independent Test**: From a disconnected state, tap a "System Profile" and verify the app establishes the connection and displays the correct dashboard widgets.

**Acceptance Scenarios**:

1. **Given** the app is in the Management Hub, **When** I tap a System Profile preset, **Then** the app initiates a connection to the bound IP/Protocol AND switches the dashboard to the bound Layout.

---

### User Story 3 - Management Hub Navigation (Priority: P2)

As a user, I want a unified "Management Hub" accessible from the dashboard so that I can manage my environment without "disconnecting" or losing my current view.

**Why this priority**: Improves the "Edit Mode" experience by moving high-level management concerns into a persistent, accessible navigation component.

**Independent Test**: Open the side drawer from the Dashboard, verify I can see the current connection status, active layout name, and options to switch both.

**Acceptance Scenarios**:

1. **Given** I am on the Dashboard, **When** I open the Side Navigation Drawer, **Then** I see sections for "Current System", "Saved Systems (Presets)", and "Library (Connections/Layouts)".

---

### Edge Cases

- **Protected Resources**: If a user attempts to delete a Connection Profile or Dashboard Layout that is referenced by a System Profile, the system MUST prevent the deletion and alert the user that it is in use by one or more System Profiles.
- **Connection Failure**: If a System Profile is tapped but the connection fails, the Layout SHOULD still load (in offline/error mode) so the user can see the UI they expect while troubleshooting.
- **Unsaved Changes**: If a user modifies a layout while using a System Profile, the system MUST prompt to "Update Preset" or "Save as New" when leaving.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support a "System Profile" entity that holds references (IDs) to one Connection Profile and one Dashboard Layout.
- **FR-002**: System MUST provide a built-in, read-only "Demo System" profile that binds the internal Demo Server to the Demo Layout.
- **FR-003**: System MUST provide a unified "Management Hub" UI, preferably as a Navigation Drawer.
- **FR-004**: System MUST allow users to save the current combination of Connection + Layout as a named System Profile.
- **FR-005**: System MUST allow switching between System Profiles while the app is running (disconnecting from the old and connecting to the new).
- **FR-006**: System MUST allow "Mix-and-Match" where a user can manually pick any Connection Profile and apply any Layout to it independently.
- **FR-007**: System MUST persist the "Active System Profile" or "Active Manual Binding" so it is restored on app launch. Manual overrides to a System Profile (via Library) are considered transient and MUST NOT overwrite the profile reference unless explicitly saved.
- **FR-008**: System MUST update the "Full Backup" export functionality to include the entire repository state: all saved Connection Profiles, all Dashboard Layouts in the library, and all System Profiles (Presets).
- **FR-009**: System MUST implement a "Merge (Upsert)" strategy during import, where incoming items update existing ones if identifiers match, while new items are added to the local library.
- **FR-010**: System MUST allow users to export and share an individual "System Profile" as a standalone package that automatically includes its bound Dashboard Layout and Connection Profile.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements in the Management Hub MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Use clear iconography to distinguish between "Systems" (Bundles), "Connections" (Plugs), and "Layouts" (Screens).
- **UI-001**: The Management Hub MUST be accessible via a standard gesture (swipe from edge) or a "Menu" icon in the Top App Bar.
- **UI-002**: UI MUST show real-time connection status (Connected/Connecting/Error) directly within the Management Hub.
- **UI-003**: UI MUST use progressive disclosure (e.g., expandable sections) to prevent overloading the user with all Connections and Layouts at once.
- **UI-004**: The Management Hub (Drawer) MUST automatically close upon successful selection of a System Profile, returning the user to the Dashboard.

### Key Entities *(include if feature involves data)*

- **SystemProfile**: A binding entity. Attributes: `id`, `name`, `connectionProfileName`, `layoutId`.
- **ManagementHubState**: Tracks the currently active binding, whether it's from a preset or a manual selection.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can switch between two "System Profiles" (including connection establishment) in under 3 seconds (network latency permitting).
- **SC-002**: Launching a System Profile requires exactly ONE tap from the Management Hub.
- **SC-003**: Zero app-restarts or "Connection Screen" redirects are required to change configurations.
- **SC-004**: 90% of tested users can identify how to change the "Layout" without navigating away from the live data view.
