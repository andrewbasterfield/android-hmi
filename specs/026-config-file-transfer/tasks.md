# Tasks: Config File Transfer

**Feature**: Config File Transfer
**Branch**: `026-config-file-transfer`
**Plan**: [plan.md](plan.md)
**Spec**: [spec.md](spec.md)

## Implementation Strategy

We follow an MVP-first approach, prioritizing the foundational data transfer and validation logic before implementing the user-facing export/import flows. Each user story is designed to be independently testable.

1.  **Foundational**: Project setup (dependencies, FileProvider) and core transfer manager logic with schema validation.
2.  **User Story 1**: Enable basic dashboard layout file transfer.
3.  **User Story 2 & 4**: Enable connection profile transfer and sharing capabilities.
4.  **User Story 3**: Implement the consolidated backup and selection UI.
5.  **Polish**: Validation testing and accessibility.

## Phase 1: Setup

- [X] T001 Add JitPack repository and `org.everit.json.schema` dependency to `app/build.gradle.kts` (Note: Ensure compatibility with Android's built-in `org.json`)
- [X] T002 Create FileProvider configuration in `app/src/main/res/xml/file_paths.xml`
- [X] T003 Register `FileProvider` and `application/json` intent filters in `app/src/main/AndroidManifest.xml`

## Phase 2: Foundational

- [X] T004 Create `FullBackupPackage` data model in `app/src/main/java/com/example/hmi/data/FullBackupPackage.kt`
- [X] T005 [P] Implement `ConfigTransferManager` skeleton in `app/src/main/java/com/example/hmi/data/ConfigTransferManager.kt`
- [X] T006 [P] Add `full-backup.schema.json` to project assets in `app/src/main/assets/schemas/full-backup.schema.json`
- [X] T007 Implement JSON Schema loading and validation logic in `ConfigTransferManager.kt`
- [X] T008 Implement `mergeProfiles(profiles: List<PlcConnectionProfile>)` in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [X] T009 [P] Provide `ConfigTransferManager` via Hilt in `app/src/main/java/com/example/hmi/di/DataModule.kt`

## Phase 3: [US1] Dashboard Layout Transfer

**Story Goal**: Export and import dashboard layouts via system file picker.
**Independent Test**: Export current layout to file, delete a widget, import file, verify widget is restored.

- [X] T010 [P] [US1] Add `exportLayout(uri: Uri)` and `importLayout(uri: Uri)` to `ConfigTransferManager.kt`
- [X] T011 [US1] Update `DashboardViewModel.kt` to handle layout transfer, trigger navigation to Dashboard on success (FR-013), and provide success/failure visual feedback via Snackbar (FR-007)
- [X] T012 [US1] Add "Save to File" and "Import from File" buttons to `JsonTransferDialog` in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [X] T013 [US1] Implement `ActivityResultLauncher` for `CreateDocument` and `OpenDocument` in `DashboardSettingsDialog.kt`

## Phase 4: [US2] Connection Profiles Transfer

**Story Goal**: Export and import connection profiles via system file picker.
**Independent Test**: Export profiles to file, delete a profile, import file, verify profile is restored.

- [X] T014 [P] [US2] Add `exportProfiles(uri: Uri)` and `importProfiles(uri: Uri)` to `ConfigTransferManager.kt`
- [X] T015 [US2] Update `ConnectionViewModel.kt` to handle profile transfer, trigger navigation to Dashboard on success (FR-013), and provide success/failure visual feedback via Snackbar (FR-007)
- [X] T016 [US2] Add "Export All" and "Import" buttons to Saved Profiles section in `app/src/main/java/com/example/hmi/connection/ConnectionScreen.kt`

## Phase 5: [US4] Sharing & External Intents

**Story Goal**: Share configuration to other apps and handle incoming share intents.
**Independent Test**: Share layout to Google Drive; Open a JSON file from File Manager and verify app launches with import prompt.

- [X] T017 [P] [US4] Implement `shareConfig(context: Context, content: String, filename: String)` in `ConfigTransferManager.kt` using `ACTION_SEND`
- [X] T018 [US4] Implement intent handling in `MainActivity.kt` to capture `ACTION_VIEW` and `ACTION_SEND` for JSON files
- [X] T019 [US4] Add "Share to Cloud" button to `JsonTransferDialog` in `DashboardSettingsDialog.kt`
- [X] T020 [US4] Add "Share Profiles" button to `ConnectionScreen.kt`

## Phase 6: [US3] Full Configuration Backup

**Story Goal**: Generate consolidated backup and provide selection UI on import.
**Independent Test**: Perform full backup, open file, uncheck "Layout", import, verify only profiles are updated.

- [X] T021 [P] [US3] Implement `exportFullBackup(uri: Uri)` and `importFullBackup(uri: Uri)` in `ConfigTransferManager.kt`
- [X] T022 [US3] Create `ImportSelectionDialog.kt` Compose component in `app/src/main/java/com/example/hmi/dashboard/`
- [X] T023 [US3] Integrate `ImportSelectionDialog` for incoming intents and ensure redirection to Dashboard on completion (FR-013)
- [X] T024 [US3] Add "Generate Full Backup" button to `DashboardSettingsDialog.kt`

## Phase 7: Polish & Validation

- [X] T025 Implement unit tests for `ConfigTransferManager` schema validation in `app/src/test/java/com/example/hmi/data/ConfigTransferManagerTest.kt`
- [ ] T026 Implement UI tests for `DashboardSettingsDialog` file picker triggers in `app/src/androidTest/java/com/example/hmi/dashboard/DashboardTransferTest.kt`
- [X] T027 Verify 48dp touch targets and content descriptions in all new UI components
- [X] T028 [P] Ensure error messages correctly identify non-conformant JSON elements per FR-006
- [X] T029 [P] Verify import performance for 20+ widget layouts is < 500ms per SC-002
- [X] T030 Add `Scaffold` with `SnackbarHost` to `MainActivity.kt` and establish a global `SnackbarManager` or event flow for feedback (FR-007)
- [X] T031 Implement schema versioning and "unknown field" tolerance in `ConfigTransferManager.kt` to ensure forward and backwards compatibility (FR-014)
- [X] T032 Create unit tests for schema migration and compatibility (FR-014)

## Dependencies

- Phase 1 & 2 must be completed before any User Story.
- US1, US2, and US4 can be developed in parallel after Foundational phase.
- US3 depends on both US1 and US2 logic being available in `ConfigTransferManager`.

## Parallel Execution Examples

- **Foundational**: T005, T006, and T009 can be started simultaneously.
- **US1**: T010 can be developed while UI tasks (T012, T013) are prepared.
- **US4**: T017 can be implemented independently of UI integration.
