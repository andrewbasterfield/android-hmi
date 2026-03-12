# Tasks: Dashboard Canvas Color

**Input**: Design documents from `/specs/008-dashboard-canvas-color/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Tests are OPTIONAL. We will focus on manual verification of UI consistency and persistence.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Update the data model to support canvas colors

- [X] T001 Update `DashboardLayout` data class in `app/src/main/java/com/example/hmi/data/DashboardLayout.kt` to include `canvasColor: Long? = null`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core logic for updating and persisting the canvas color

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T002 Add `updateDashboardSettings(name: String, canvasColor: Long?)` to `DashboardViewModel` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [X] T003 [P] Verify GSON serialization logic in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt` to ensure the new field is persisted

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Customize Dashboard Background (Priority: P1) 🎯 MVP

**Goal**: Users can change the dashboard background color in Edit Mode.

**Independent Test**: Enter Edit Mode, click the settings icon, select "Blue", and verify the dashboard canvas turns blue immediately.

### Implementation for User Story 1

- [X] T004 [P] [US1] Create `DashboardSettingsDialog` composable in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt` using `ColorPicker`
- [X] T005 [US1] Add a settings icon button to the `TopAppBar` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` (visible only in Edit Mode)
- [X] T006 [US1] Implement dialog visibility state and connection to `DashboardSettingsDialog` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T007 [US1] Apply `dashboardLayout.canvasColor` to the root background modifier in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: User Story 1 functional - canvas color can be updated live.

---

## Phase 4: User Story 2 - Persistent Canvas Color (Priority: P2)

**Goal**: Selected canvas color is saved and restored correctly.

**Independent Test**: Set canvas color to "Gray", restart the app, and verify it remains Gray.

### Implementation for User Story 2

- [X] T008 [US2] Verify end-to-end persistence by performing a manual "Set Color -> Restart App" test

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Accessibility and final verification

- [X] T009 [P] Add accessibility `contentDescription` to the dashboard settings icon in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T010 Run final validation against `specs/008-dashboard-canvas-color/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup.
- **User Story 1 (Phase 3)**: Depends on Foundation.
- **User Story 2 (Phase 4)**: Depends on US1 completion.
- **Polish (Phase 5)**: Final step.

### Parallel Opportunities

- T003 and T004 can be developed in parallel as they are in separate files.
- Accessibility polish (T009) can be done alongside the main UI work.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Add `canvasColor` to data model and ViewModel.
2. Create the `DashboardSettingsDialog`.
3. Link the dialog to the top bar and apply the background color.

### Incremental Delivery

- Data Model -> Update Logic -> Settings UI -> Persistence -> Polish.
