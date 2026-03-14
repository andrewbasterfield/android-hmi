# Tasks: JSON Import/Export

**Input**: Design documents from `specs/011-layout-json-transfer/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Verify project structure for JSON Import/Export integration

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Implement `exportLayoutToJson()` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt` using GSON
- [x] T003 Implement `importLayoutFromJson(json: String)` with validation in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T004 Create unit tests for JSON serialization and validation logic in `app/src/test/java/com/example/hmi/dashboard/DashboardViewModelTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Export & Share (Priority: P1) 🎯 MVP

**Goal**: Allow users to copy their current layout JSON to the clipboard.

**Independent Test**: Opening Dashboard Settings shows the current layout as JSON, and clicking "Copy" puts it into the system clipboard.

### Implementation for User Story 1

- [x] T005 [P] [US1] Add "JSON Transfer" section and read-only Export text area to `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T006 [US1] Implement "Copy to Clipboard" button with `LocalClipboardManager` in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T007 [US1] Create instrumentation test for layout export flow in `app/src/androidTest/java/com/example/hmi/dashboard/JsonExportTest.kt`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Import & Restore (Priority: P2)

**Goal**: Allow users to paste a new layout JSON and apply it to the dashboard.

**Independent Test**: Pasting a valid JSON string into the import field and tapping "Apply" replaces the dashboard layout immediately.

### Implementation for User Story 2

- [x] T008 [P] [US2] Add Import text input field and "Apply Import" button to `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T009 [US2] Add success/failure feedback (Toasts or Snackbars) for import results in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T010 [US2] Create instrumentation test for layout import (valid vs invalid JSON) in `app/src/androidTest/java/com/example/hmi/dashboard/JsonImportTest.kt`

**Checkpoint**: User Story 2 is functional and testable independently.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and documentation

- [x] T011 [P] Update `README.md` with JSON Import/Export usage instructions
- [x] T012 [P] Verify accessibility (touch targets 48x48dp) for all new buttons in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T013 [P] Final code cleanup and refactoring across changed files

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational phase completion.
  - US1 (P1) is the MVP and should be completed first.
  - US2 (P2) depends on foundation but can be worked on in parallel with US1 UI once the view model is ready.

### User Story Dependencies

- **User Story 1 (P1)**: Independent of US2.
- **User Story 2 (P2)**: Independent of US1 UI but requires US1's "Copy" ability for easy end-to-end testing.

### Parallel Opportunities

- T004 (Tests) can run in parallel with T002/T003.
- T005 and T008 can be implemented in the same file sequentially.
- T007 and T010 can be developed in parallel once foundation is complete.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2 (Foundation).
2. Complete Phase 3 (Export UI).
3. **STOP and VALIDATE**: Verify users can backup their layout via clipboard.

### Incremental Delivery

1. Foundation ready.
2. Add Export UI (MVP!).
3. Add Import UI and validation feedback.
4. Final Polish and Documentation.

---

## Notes

- Uses existing GSON library for serialization.
- Validation must prevent app crashes on malformed JSON.
- All persistent updates must go through `DashboardViewModel` to ensure UDF compliance.
