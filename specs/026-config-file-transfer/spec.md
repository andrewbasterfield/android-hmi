# Feature Specification: Config File Transfer

**Feature Branch**: `026-config-file-transfer`  
**Created**: 2026-03-24  
**Status**: Draft  
**Input**: User description: "I would like to be able to save json config object to the filesystem on the device or load it from there also. Connection profiles also."

## Clarifications

### Session 2026-03-24
- Q: Should the system apply any protection or warnings to exported files that contain connection parameters? → A: Option D (No extra protection; treat as standard system configuration files).
- Q: How should the app handle importing from a 'Full Backup' file that contains both a layout and connection profiles? → A: Option B (Selection Menu: Let the user check boxes for "Layout" and/or "Profiles" before importing).
- Q: Where should the user be taken after a successful import is processed? → A: Option A (Dashboard).
- Q: Should we use formal JSON Schema for import validation? → A: Option C (Schema-based Validation) with detailed error reporting for non-conformant elements and strict backwards compatibility for future schema versions.
- Q: Should we use a custom file extension for configuration files? → A: Option B (Standard JSON). The app handles `.json` files and relies on schema validation to reject non-HMI files.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Export/Import Dashboard Layout (Priority: P1)

As an engineer, I want to export my current dashboard layout to a file so that I can back it up or share it with other devices. I also want to be able to import a layout file to quickly configure a new device.

**Why this priority**: Core functionality for configuration management. It allows for backup and distribution of the HMI design.

**Independent Test**: Can be tested by creating a layout, exporting it to a file, deleting the layout from the app, and then importing the file to restore the original state.

**Acceptance Scenarios**:

1. **Given** a configured dashboard layout, **When** the user selects "Export Layout", **Then** the system prompts for a save location using the system file picker and writes the layout as a JSON file.
2. **Given** a dashboard layout JSON file on the device, **When** the user selects "Import Layout", **Then** the system replaces the current layout with the one from the file (after validation).

---

### User Story 2 - Export/Import Connection Profiles (Priority: P2)

As an engineer, I want to export my saved connection profiles to a file so that I don't have to manually re-enter IP addresses and ports on every device.

**Why this priority**: High value for large-scale deployments where multiple devices share the same PLC infrastructure.

**Independent Test**: Can be tested by saving several connection profiles, exporting them to a file, clearing the saved profiles, and then importing them back from the file.

**Acceptance Scenarios**:

1. **Given** multiple saved connection profiles, **When** the user selects "Export Profiles", **Then** the system saves all profiles into a single JSON file.
2. **Given** a profiles JSON file, **When** the user selects "Import Profiles", **Then** the system overwrites existing profiles with the same name and adds new ones.

---

### User Story 3 - Full Configuration Backup (Priority: P3)

As an administrator, I want to perform a full backup of both the layout and all connection profiles in a single action to ensure the entire system state is preserved.

**Why this priority**: Convenience feature for disaster recovery and total system cloning.

**Independent Test**: Can be tested by performing a full export, then importing the resulting file(s) into a fresh app installation.

**Acceptance Scenarios**:

1. **Given** a complete system state (layout + profiles), **When** the user selects "Full Backup", **Then** the system generates a single consolidated JSON file containing both entities.
2. **Given** a "Full Backup" file, **When** the user opens it for import, **Then** the system MUST display a selection menu allowing the user to choose which components (Layout, Connection Profiles) to import.

---

### User Story 4 - Share Configuration with Other Apps (Priority: P2)

As a technician, I want to share my configuration directly to Google Drive or via Email, and open configuration files sent to me by others, so that I don't have to manually manage files in the filesystem.

**Why this priority**: Significant UX improvement for collaboration and cloud backups.

**Independent Test**: 
- **Share Out**: Select "Share Layout", verify the system share sheet appears, and successfully upload to a cloud service.
- **Share In**: Open a JSON file from an external File Manager or Google Drive and verify the app launches with an import prompt.

**Acceptance Scenarios**:

1. **Given** a configuration in the app, **When** the user selects "Share", **Then** the system opens the Android Share Sheet.
2. **Given** the app is installed, **When** the user opens a compatible `.json` file from an external app, **Then** the app appears in the "Open with" list and handles the import flow.

---

### Edge Cases

- **Invalid JSON**: System MUST validate schema before import and show a clear error message.
- **Duplicate Names**: Connection profiles with identical names are overwritten by the incoming file data.
- **Permission Denial**: System MUST gracefully handle cases where the user cancels the file picker or denies storage access.
- **Incompatible Schema**: If a file from an older version of the app is imported, the system MUST attempt migration or reject it safely.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow exporting the current `DashboardLayout` to a JSON file via the **System File Picker**.
- **FR-002**: System MUST allow importing a `DashboardLayout` from a JSON file, replacing the current active layout.
- **FR-003**: System MUST allow exporting all saved `PlcConnectionProfile` entities to a JSON file.
- **FR-004**: System MUST allow importing `PlcConnectionProfile` entities from a JSON file, using an **overwrite-by-name** strategy for conflicts.
- **FR-005**: System MUST validate JSON structure using a formal JSON Schema before import.
- **FR-006**: In case of validation failure, the system MUST identify and display the specific non-conformant element or field to the user.
- **FR-007**: System MUST provide visual feedback (Toast/Snackbar) upon success or failure.
- **FR-008**: System MUST support a "Full Backup" export that consolidates layout and profiles into a single JSON file.
- **FR-009**: System MUST support "Sharing Out" configurations via the Android `ACTION_SEND` Intent (Share Sheet).
- **FR-010**: System MUST support "Sharing In" by registering an `intent-filter` for `application/json` files.
- **FR-011**: Exported files MUST NOT be encrypted; they are standard system configuration files.
- **FR-012**: Importing a "Full Backup" file MUST present a selection menu to the user to choose between importing the Layout, Connection Profiles, or both.
- **FR-013**: Upon successful import of a layout or full backup, the system MUST navigate the user to the Dashboard screen to verify the changes.
- **FR-014**: The system MUST ensure backwards compatibility for configuration files generated by older versions of the application.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **A11Y-002**: Meaningful images and icons MUST include content descriptions for screen readers.
- **A11Y-003**: UI MUST support dynamic text scaling and adapt to device font sizes.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-002**: UI MUST prioritize essential data and use progressive disclosure.

### Key Entities *(include if feature involves data)*

- **DashboardLayout**: The configuration of the HMI screen.
- **PlcConnectionProfile**: Connection parameters (IP, Port, Name).
- **FullBackupPackage**: A consolidated object containing both of the above for a complete system restore.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can export or share a configuration in under 3 interactions.
- **SC-002**: Import operations for large layouts complete in under 500ms.
- **SC-003**: 100% of validly exported files are successfully re-imported with full attribute fidelity.
- **SC-004**: System correctly handles incoming "Share" intents from at least 2 external apps (e.g., File Manager, Google Drive).
