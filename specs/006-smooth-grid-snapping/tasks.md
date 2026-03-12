# Tasks: Smooth Grid Snapping

**Input**: Design documents from `/specs/006-smooth-grid-snapping/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Tests are OPTIONAL. We will focus on manual verification of interaction smoothness and UI logic tests for coordinate mapping.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure for animations

- [X] T001 [P] Add animation constants (STIFFNESS, DAMPING) to `app/src/main/java/com/example/hmi/dashboard/GridSystem.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T002 Refactor `DashboardScreen.kt` to introduce transient `dragOffset` and `resizeOffset` state (decoupled from grid coordinates)
- [X] T003 [P] Update `WidgetContainer` to support an `alpha` parameter for ghost mode in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Smooth Dragging with Ghosting (Priority: P1) 🎯 MVP

**Goal**: Widgets move smoothly with the touch while a translucent ghost indicates the snap target.

**Independent Test**: Drag a button; verify it moves smoothly with your finger while a ghost box stays aligned to the 80dp grid beneath it.

### Implementation for User Story 1

- [X] T004 [US1] Implement ghost cell calculation (clamped to grid) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T005 [US1] Render the ghost `WidgetContainer` at the calculated snap coordinates in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T006 [US1] Update `pointerInput` in `DashboardScreen.kt` to modify pixel `offsetX/Y` during drag rather than updating grid coordinates immediately
- [X] T007 [US1] Update `onDragEnd` to commit the ghost coordinates to the `ViewModel`

**Checkpoint**: User Story 1 functional - dragging is smooth and visual feedback is present.

---

## Phase 4: User Story 2 - Spring Animation on Snap (Priority: P2)

**Goal**: Widgets slide into their final position using spring physics when released.

**Independent Test**: Release a widget after dragging; verify it performs a smooth sliding animation into its final cell.

### Implementation for User Story 2

- [X] T008 [US2] Wrap widget positioning in `animateIntOffsetAsState` within `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T009 [US2] Apply `Spring` animation spec to the offset transition for an "industrial" feel
- [X] T010 [US2] Implement similar smooth resizing logic for `colSpan` and `rowSpan` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: User Story 2 complete - all grid transitions are animated.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Performance and accessibility refinement

- [X] T011 [P] Use `Modifier.graphicsLayer` or lambda-based `offset` to optimize drag performance in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T012 [P] Ensure ghost boxes are ignored by accessibility services (set `semantics { invisibleToUser() }`) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T013 Run a final validation of `specs/006-smooth-grid-snapping/quickstart.md` on device

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** & **Foundational (Phase 2)**: MUST be complete before starting User Story 1.
- **User Story 1 (Phase 3)**: Core interaction model.
- **User Story 2 (Phase 4)**: Enhances the transition after Story 1 is released.
- **Polish (Phase 5)**: Performance tweaks after all logic is in place.

### Parallel Opportunities

- T001 and T003 can be done in parallel as they are in different files.
- Performance optimization (T011) can be done anytime after Story 1 is functional.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Decouple drag from grid.
2. Render ghost box.
3. Verify snapping logic on release.

### Incremental Delivery

- Transient State -> Ghost UI -> Smooth Drag -> Release Animation -> Polish.
