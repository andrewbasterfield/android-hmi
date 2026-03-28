# Tasks: Orientation Management (027)

**Input**: Design documents from `/specs/027-orientation-management/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: This task list includes Unit and UI tests as requested in the implementation plan to ensure grid reflow correctness and orientation locking stability.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and dependency verification

- [X] T001 Verify Jetpack Compose Pager dependencies in `app/build.gradle.kts`
- [X] T002 Configure baseline test runner and Hilt rules in `app/src/androidTest/java/com/example/hmi/HmiTestRunner.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data structures and mapping logic for the 2D VirtualGrid

**⚠️ CRITICAL**: All user stories depend on these grid and orientation definitions.

- [X] T003 [P] Add `OrientationMode` enum (AUTO, LANDSCAPE, PORTRAIT) to `app/src/main/java/com/example/hmi/data/DashboardLayout.kt`
- [X] T004 Update `DashboardLayout` data class to include `orientationMode` and persistable bounds in `app/src/main/java/com/example/hmi/data/DashboardLayout.kt`
- [X] T005 [P] Update `DashboardRepository.kt` to support `OrientationMode` persistence via DataStore in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [X] T006 [P] Implement `GridReflowLogic.kt` for mapping global coordinates to 2D pages and offsets in `app/src/main/java/com/example/hmi/dashboard/GridReflowLogic.kt`
- [X] T007 Create Unit Tests for `GridReflowLogic` in `app/src/test/java/com/example/hmi/dashboard/GridReflowLogicTest.kt`

**Checkpoint**: Foundation ready - orientation state and grid mapping logic are verified.

---

## Phase 3: User Story 1 - Orientation Locking (Priority: P1) 🎯 MVP

**Goal**: Lock the HMI to Landscape/Portrait/Auto mode and persist the choice.

**Independent Test**: Verify orientation stays locked when device rotates physically in `OrientationLockTest`.

### Tests for User Story 1

- [X] T008 [P] [US1] Create UI Test for Orientation Locking behavior in `app/src/androidTest/java/com/example/hmi/dashboard/OrientationLockTest.kt`

### Implementation for User Story 1

- [X] T009 [US1] Update `DashboardViewModel` to expose and update `OrientationMode` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [X] T010 [US1] Implement orientation enforcement in `MainActivity.kt` using `requestedOrientation` based on observed ViewModel state
- [X] T011 [US1] Add Orientation Mode selection toggle to `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [X] T012 [US1] Verify Accessibility (48dp touch targets) for the new orientation toggle in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`

**Checkpoint**: User Story 1 complete - Orientation locking is functional and persisted.

---

## Phase 4: User Story 2 - Multi-Page Swiping & Overflow (Priority: P1)

**Goal**: Implement a 2D pager that renders an infinite canvas and supports edge-swiping.

**Independent Test**: Swipe in all four directions and see widgets spanning boundaries in `TwoDPagingTest`.

### Tests for User Story 2

- [X] T013 [P] [US2] Create UI Test for 2D navigation and cross-page widget rendering in `app/src/androidTest/java/com/example/hmi/dashboard/TwoDPagingTest.kt`
- [X] T014 [P] [US2] Create UI Test for Edge-Swiping during widget drag in `app/src/androidTest/java/com/example/hmi/dashboard/EdgeSwipingTest.kt`

### Implementation for User Story 2

- [X] T016 [US2] Refactor `DashboardScreen.kt` to replace single-grid with nested `VerticalPager` and `HorizontalPager`
- [X] T017 [US2] Integrate `GridReflowLogic` into the Pager to handle dynamic column/row counts and negative coordinate offsets in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T018 [US2] Implement boundary-spanning rendering (disabling clipping) for widgets in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T019 [US2] Implement Edge-Swiping logic to flip pages when dragging widgets near screen boundaries in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T020 [US2] Ensure all 2D pager components meet "Clarity by Design" standards (Stitch tokens) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T021 [US2] Verify manual resolution of overlapping widgets via drag-and-drop in Edit Mode in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: User Story 2 complete - 2D navigation on an infinite canvas is fully functional.

---

## Phase 5: User Story 3 - Persistence of Settings (Priority: P2)

**Goal**: Ensure all layout and orientation settings survive app restarts and rotations.

**Independent Test**: Change layout/orientation, restart app, verify state in `LayoutPersistenceTest`.

### Implementation for User Story 3

- [X] T022 [US3] Verify that `DashboardRepository` persists all new grid metadata and widget coordinates in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [X] T023 [US3] Ensure transient UI states (dialogs) survive orientation reflow via `rememberSaveable` or ViewModel state in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: All user stories are functional and state-persistent.

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T024 [P] Update `DEVELOPMENT_OVERVIEW.md` with 2D VirtualGrid and Pager documentation
- [ ] T025 [P] Run `detekt` and `ktlint` to ensure code style compliance across all new files
- [ ] T026 Performance audit: Verify <500ms orientation switch time on target hardware
- [ ] T027 Final validation of `quickstart.md` scenarios

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on T001 completion. BLOCKS all user stories.
- **User Stories (Phases 3 & 4)**: Depend on Foundational (Phase 2) completion. US1 and US2 can be worked on in parallel once `DashboardViewModel` and `GridReflowLogic` are ready.
- **Polish (Phase 6)**: Depends on all user stories being complete.

### Parallel Opportunities

- T003, T005, and T006 can be implemented in parallel.
- UI tests (T008, T013, T014) can be written in parallel with their implementation counterparts.
- Documentation (T023) and Linting (T024) can run in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 & Foundational)

1. Complete Phase 1 & 2.
2. Complete Phase 3 (US1: Orientation Locking).
3. **Validate**: Demonstrate that the UI locks to Landscape/Portrait and persists.

### Incremental Delivery

1. Add Phase 4 (US2: 2D Paging).
2. **Validate**: Demonstrate swiping in 2D and widgets spanning pages.
3. Add Phase 5 (US3: Persistence).
4. Final Polish.

---

## Notes

- Pager API: Ensure `androidx.compose.foundation.pager.*` is used (requires Compose 1.4.0+).
- Negative Coords: Coordinate mapping in `GridReflowLogic` is critical for index-to-page translation.
- Overlap: FR-009 explicitly permits visual overlap; do not add collision detection logic yet.
