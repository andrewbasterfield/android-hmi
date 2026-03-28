# Tasks: Widget Duplication

**Input**: Design documents from `/specs/028-duplicate-widget/`
**Prerequisites**: plan.md, spec.md, data-model.md, research.md

**Tests**: Unit tests for the duplication logic in `DashboardViewModel` are REQUIRED per the implementation plan.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify environment and foundational structures

- [X] T001 [P] Verify current `WidgetConfiguration` and `DashboardLayout` data classes in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` and `app/src/main/java/com/example/hmi/data/DashboardLayout.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core logic in the ViewModel to handle duplication

**⚠️ CRITICAL**: ViewModel logic MUST be verified with unit tests before UI integration

- [X] T002 [P] Create/Update unit tests for duplication logic in `app/src/test/java/com/example/hmi/dashboard/DashboardViewModelTest.kt` (MUST fail initially)
- [X] T003 Implement `duplicateWidget(widgetId: String)` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt` following the state transition logic in `data-model.md`
- [X] T004 Ensure `duplicateWidget` correctly handles UUID generation, position offset (+1, +1), and Z-Order increment in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`

**Checkpoint**: Foundation ready - duplication logic verified via unit tests

---

## Phase 3: User Story 1 - Single Widget Duplication (Priority: P1) 🎯 MVP

**Goal**: Allow users to duplicate a single widget from the configuration dialog

**Independent Test**: Open a widget's config dialog in Edit Mode, click "Duplicate", and verify a copy appears with the correct offset and identical settings (except ID).

### Implementation for User Story 1

- [X] T005 [P] [US1] Add "Duplicate" button to the `WidgetConfigDialog` footer (adjacent to the "Delete" button) in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T006 [US1] Update `WidgetConfigDialog` parameters and `DashboardScreen.kt` call-site to pass the duplication event to the ViewModel in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T008 [US1] Add screen reader announcement "Widget duplicated" using `LiveRegion` or `Semantics` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T009 [US1] Verify 48dp touch target and high-contrast styling for the new "Duplicate" button per A11Y and UI requirements in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: User Story 1 functional and testable independently

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and cleanup

- [X] T010 [P] Validate full duplication flow against `quickstart.md` scenarios
- [X] T011 [P] Ensure layout persistence is correctly triggered and saved in DataStore after duplication in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [X] T012 Code cleanup and removal of any temporary debug logs

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately
- **Foundational (Phase 2)**: Depends on Phase 1 - BLOCKS Phase 3 UI integration
- **User Story 1 (Phase 3)**: Depends on Foundational phase logic
- **Polish (Final Phase)**: Depends on US1 completion

### Parallel Opportunities

- T001 and T002 can run in parallel
- T005 can start in parallel with T003/T004 (layout vs logic)
- T010 and T011 can be verified in parallel during final testing

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (ViewModel & Tests)
3. Complete Phase 3: User Story 1 (UI Integration)
4. **STOP and VALIDATE**: Verify duplication works and persists correctly

### Incremental Delivery

1. Verify duplication logic independently with unit tests.
2. Integrate the button and verify the widget appears on the canvas.
3. Add selection shift and accessibility feedback.

---

## Notes

- Every duplication action must assign a new unique ID to avoid state collisions.
- Position offset is strictly (+1, +1) grid units.
- Z-Order must be the highest in the layout to ensure the duplicate is on top.
