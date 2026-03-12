# Tasks: UI Refinement

**Input**: Design documents from `/specs/004-ui-refinement/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure for grid logic

- [x] T001 [P] Define `GridConstants` (CELL_SIZE = 80dp) in `app/src/main/java/com/example/hmi/dashboard/GridSystem.kt`
- [x] T002 [P] Create unit test for grid math (coordinate to cell conversion) in `app/src/test/java/com/example/hmi/dashboard/GridSystemTest.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T003 Update `WidgetConfiguration` to use `Int` fields: `column`, `row`, `colSpan`, `rowSpan` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T004 Implement `GridSystem` utility functions for snapping and DP conversion in `app/src/main/java/com/example/hmi/dashboard/GridSystem.kt`
- [x] T005 [P] Create `WidgetContainer` composable with square edges and contrasting border in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - DPI-Aware Grid Layout (Priority: P1) 🎯 MVP

**Goal**: Dashboard displays widgets snapped to a fixed 80dp grid that scales based on screen size.

**Independent Test**: Verify that on a phone, fewer widgets fit horizontally than on a tablet, but they remain the same physical size (80dp units).

### Implementation for User Story 1

- [x] T006 [US1] Implement dynamic grid column/row calculation in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` using `LocalConfiguration`
- [x] T007 [US1] Update `DashboardScreen` to position widgets using grid coordinates (col * 80dp) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T008 [US1] Implement drag-and-drop snapping to grid cells in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: User Story 1 functional - widgets are grid-aligned and draggable.

---

## Phase 4: User Story 2 - Resize Widgets in Edit Mode (Priority: P1) 🎯 MVP

**Goal**: Users can resize widgets using grid-snapping drag handles.

**Independent Test**: Drag the bottom-right handle of a button; verify it expands in 80dp increments and maintains functionality.

### Implementation for User Story 2

- [x] T009 [US2] Implement `ResizeHandle` UI component in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [x] T010 [US2] Implement resize gesture logic that updates `colSpan` and `rowSpan` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T011 [US2] Update `AddWidgetDialog` to use grid units for initial size input in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: User Story 2 functional - widgets are resizable on the grid.

---

## Phase 5: User Story 3 - Persistent Grid Layout (Priority: P2)

**Goal**: Layout changes are saved to DataStore and restored correctly.

**Independent Test**: Move/resize a widget, restart the app, and confirm it remains in the same grid position and size.

### Implementation for User Story 3

- [x] T012 [US3] Update `DashboardViewModel.updateWidgetPosition` to accept grid coordinates in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T013 [US3] Implement `DashboardViewModel.updateWidgetSize` for persisting `colSpan/rowSpan` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T014 [US3] Verify GSON serialization of new `Int` grid fields in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`

**Checkpoint**: User Stories 1-3 are complete and persistent.

---

## Phase 6: User Story 4 - Consistent Widget Containers (Priority: P2)

**Goal**: All widgets use the uniform `WidgetContainer` with borders and colors.

**Independent Test**: Verify Sliders and Gauges have the same square boundary and border behavior as Buttons.

### Implementation for User Story 4

- [x] T015 [US4] Wrap all widget types (Button, Slider, Gauge) in `WidgetContainer` within `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T016 [US4] Remove internal Padding/Card logic from `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`, `SliderWidget.kt`, and `GaugeWidget.kt` to avoid "double-borders"
- [x] T017 [US4] Implement `ColorUtils.getContrastColor` for the container border in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`

**Checkpoint**: All user stories functional and visually consistent.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final accessibility and performance verification

- [x] T018 [P] Add accessibility `contentDescription` to resize handles in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [x] T019 [P] Verify minimum 48x48dp touch target for all interactive elements in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T020 [P] Run `specs/004-ui-refinement/quickstart.md` validation on physical device

---

## Phase 8: User Story 5 - Edit and Delete Widgets (Priority: P1)

**Goal**: Users can modify or delete existing widgets via an Edit Dialog triggered by a settings icon.

**Independent Test**: Add a widget, click the edit icon, change its color, and then delete it.

### Implementation for User Story 5

- [x] T021 [US5] Implement `Edit` icon button in the top-right corner of `WidgetContainer` in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [x] T022 [US5] Add `onDelete` and `onUpdate` functions to `DashboardViewModel` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T023 [US5] Refactor `AddWidgetDialog` into a reusable `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T024 [US5] Implement `EditWidgetDialog` with a Delete button and 'Save' functionality in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T025 [US5] Connect the container's Edit icon to the `EditWidgetDialog` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

---

## Dependencies & Execution Order
...
### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup.
- **User Story 1 & 2 (Phases 3-4)**: Depend on Foundation. Can be done in parallel if T008 and T010 are coordinated.
- **User Story 3 (Phase 5)**: Depends on US1/US2.
- **User Story 4 (Phase 6)**: Can be done in parallel with US1/US2 but best after Foundation.
- **Polish (Phase 7)**: Depends on all other phases.

### Parallel Opportunities

- Unit tests (T002) can be written while T001 is being defined.
- `WidgetContainer` UI (T005) can be developed independently of the Grid math (T004).
- Accessibility (T018) and final validation (T020) can be done in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 & 2 Only)

1. Complete Setup and Foundational phases.
2. Implement Grid rendering and dragging (US1).
3. Implement Resizing (US2).
4. Verify on-screen behavior before adding persistence (US3).

### Incremental Delivery

- Grid Alignment -> Resizing -> Persistence -> Visual Containers -> Polish.
