# Tasks: System Profiles and Binding

**Feature**: System Profiles and Binding
**Plan**: [specs/035-system-profiles-binding/plan.md](plan.md)
**Branch**: `035-system-profiles-binding`

## Implementation Strategy

We will implement this feature in phases, starting with the updated data models and foundational repository logic to support "Upsert" operations. We will then build the logic for creating and launching profiles, followed by the "Management Hub" UI (Navigation Drawer). Finally, we will refine the import/export functionality to support Version 2 backups and standalone profile bundles.

- **Phase 1**: Setup (Entities and Schema)
- **Phase 2**: Foundational Repository Logic
- **Phase 3 [US1]**: Create a System Profile
- **Phase 4 [US2]**: One-Tap Launch
- **Phase 5 [US3]**: Management Hub UI
- **Phase 6 [FR-008/010]**: Refined Import/Export
- **Phase 7**: Polish & Demo Mode

## Phase 1: Setup (Entities and Schema)

Goal: Define the updated data structures and schema for System Profiles and comprehensive backups.

- [x] T001 Define `SystemProfile` and `SystemProfileBundle` entities in `app/src/main/java/com/example/hmi/data/SystemProfile.kt`
- [x] T002 Update `FullBackupPackage` data class with library fields in `app/src/main/java/com/example/hmi/data/FullBackupPackage.kt`
- [x] T003 Update `full-backup.schema.json` to Version 2 in `app/src/main/assets/schemas/full-backup.schema.json`

## Phase 2: Foundational Repository Logic

Goal: Implement the "Upsert" merge strategy and multi-entity persistence.

- [x] T004 Implement `Upsert` persistence logic for Connections, Layouts, and System Profiles in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [x] T005 [P] Add unit tests for `Upsert` logic and conflict resolution in `app/src/test/java/com/example/hmi/data/DashboardRepositoryTest.kt`

## Phase 3: Create a System Profile (US1)

Goal: Allow users to bind the current connection and layout into a named preset.

- [x] T006 Implement `saveCurrentAsSystemProfile(name: String)` logic in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T007 [P] Implement `saveSystemProfile` persistence method in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`

## Phase 4: One-Tap Launch (US2)

Goal: Automatically establish connection and load layout from a single action.

- [x] T008 Implement `launchSystemProfile(profile: SystemProfile)` orchestration in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T009 Ensure `plcCommunicator` connection triggers are properly handled from the `DashboardViewModel` during profile launch in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`

## Phase 5: Management Hub UI (US3)

Goal: Provide an intuitive, drawer-based entry point for all environment management.

- [x] T010 [P] [US3] Create `ManagementHubDrawer` UI component with 48dp touch targets in `app/src/main/java/com/example/hmi/dashboard/components/ManagementHubDrawer.kt`
- [x] T011 [US3] Integrate `ModalNavigationDrawer` into the dashboard root in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T012 [US3] Add a "Menu" navigation icon to the `TopAppBar` to toggle the drawer in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T013 [P] [US3] Implement real-time connection status indicator within the drawer in `app/src/main/java/com/example/hmi/dashboard/components/ManagementHubDrawer.kt`

## Phase 6: Refined Import/Export (FR-008/010)

Goal: Enable sharing of individual machine configurations and full environment backups.

- [x] T014 Update `ConfigTransferManager` to handle Version 2 comprehensive backups in `app/src/main/java/com/example/hmi/data/ConfigTransferManager.kt`
- [x] T015 Implement `exportSystemProfileBundle` to create standalone packages in `app/src/main/java/com/example/hmi/data/ConfigTransferManager.kt`
- [x] T016 [P] Add unit tests for `SystemProfileBundle` serialization and dependency bundling in `app/src/test/java/com/example/hmi/data/ConfigTransferManagerTest.kt`
- [x] T017 Add "Share" action to System Profile list items in the drawer in `app/src/main/java/com/example/hmi/dashboard/components/ManagementHubDrawer.kt`

## Phase 7: Polish & Demo Mode

Goal: Ensure data safety and providing a zero-config starting point.

- [x] T018 Implement deletion protection for resources referenced by System Profiles in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [x] T019 Implement "Unsaved Changes" (isModified) visual indicator in the Management Hub in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T020 Create built-in, read-only "Demo System" profile that binds Demo Server to Demo Layout in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [x] T021 [P] [US3] Add Compose UI test for ManagementHubDrawer visibility and section navigation in `app/src/androidTest/java/com/example/hmi/dashboard/ManagementHubTest.kt`
- [x] T022 [P] Verify SC-001 (switching speed < 3s) and SC-004 (visual identification of layout changes) via manual benchmarks on device

## Dependencies

1. Phase 1 & 2 are foundational and block all subsequent phases.
2. US1 and US2 (Logic) should be completed before US3 (UI) to enable full functional testing of the drawer.
3. Import/Export refinement (Phase 6) depends on the foundational logic from Phase 2.

## Parallel Execution

- T005 (Repository tests) can run while implementing T004.
- T010 (UI Component) can be developed in parallel with Phase 3 & 4 logic.
- T013 (Connection Status UI) can be developed once the basic drawer (T010) is defined.
- T016 (Transfer tests) can run while implementing T014/T015.
