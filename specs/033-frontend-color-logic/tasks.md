# Tasks: Frontend-Driven Color Management

**Input**: Design documents from `/specs/033-frontend-color-logic/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/ProtocolChanges.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- File paths are relative to the repository root.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 [P] Verify project structure and linter configuration for Kotlin and Compose
- [ ] T002 [P] Review existing `ColorUtils.kt` to identify integration points for local resolution

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T003 Implement `ColorResolver` logic in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt` to map values to `ColorZones`, including support for `exactMatch` string/boolean evaluation.
- [x] T004 Update `DashboardViewModel.kt` to ignore `.color` attributes in `plcCommunicator.attributeUpdates` collection loop
- [x] T005 [P] Add unit tests for `ColorResolver` in `app/src/test/java/com/example/hmi/widgets/ColorUtilsTest.kt`

**Checkpoint**: Foundation ready - local color resolution logic is in place and protocol updates are being filtered.

---

## Phase 3: User Story 1 - Value-Based Dynamic Coloring (Priority: P1) 🎯 MVP

**Goal**: Widgets change color based on tag values and local `ColorZones` configuration.

**Independent Test**: Configure a gauge with a color zone (e.g., 0-50 Green) and verify it displays Green even without a `.color` message from the server.

### Tests for User Story 1 (Requested in Plan) ⚠️

- [x] T006 [P] [US1] Create unit test for Gauge color resolution in `app/src/test/java/com/example/hmi/widgets/GaugeColorLogicTest.kt`
- [x] T007 [P] [US1] Create Compose UI tests for widget color state changes (Gauge, Indicator, Button) in `app/src/androidTest/java/com/example/hmi/widgets/WidgetColorUiTest.kt`

### Implementation for User Story 1

- [x] T008 [US1] Update `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt` to resolve color locally using `ColorUtils.resolvePointerColor` (removing dependency on container overrides)
- [x] T009 [US1] Update `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt` to resolve color locally based on tag value and zones.
- [x] T010 [US1] Update `app/src/main/java/com/example/hmi/core/ui/components/IndustrialIndication.kt` (or equivalent Indicator widget) to resolve color locally.
- [x] T011 [US1] Update `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` to remove the `.color` override logic from `WidgetRenderingNode`
- [x] T012 [US1] Verify that `WidgetContainer.kt` correctly falls back to `MaterialTheme.colorScheme.background` when `backgroundColor` is null
- [x] T013 [US1] Verify Accessibility & UI Standards (high contrast for industrial environments) for color transitions

**Checkpoint**: User Story 1 is functional - widgets now drive their own colors based on data.

---

## Phase 4: User Story 2 - Protocol Simplification (Priority: P2)

**Goal**: The Demo PLC Server stops broadcasting `.color` attributes.

**Independent Test**: Connect to the demo server and verify no `.color` messages appear in the TCP stream.

### Implementation for User Story 2

- [x] T014 [US2] Remove color broadcasting logic from the simulation loop in `core/protocol/src/main/java/com/example/hmi/protocol/DemoPlcServer.kt`
- [x] T015 [P] [US2] Update `RawTcpPlcCommunicatorTest.kt` to ensure `.color` messages (if any) are handled as standard attributes or ignored gracefully

**Checkpoint**: Protocol is simplified - backend is no longer concerned with presentation.

---

## Phase 5: User Story 3 - Frontend Color Configuration (Priority: P3)

**Goal**: Dashboard designers can modify color thresholds in the widget editor.

**Independent Test**: Use the Widget Palette to add a new color zone and see it reflected immediately.

### Implementation for User Story 3

- [x] T016 [US3] Update `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt` to allow users to enter an `exactMatch` string value when configuring a ColorZone.
- [x] T017 [P] [US3] Ensure `ColorZone` entity in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` matches the updated data model

**Checkpoint**: All user stories are independently functional and integrated.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T018 [P] Update `docs/connection-guide.md` to reflect the removal of `.color` support
- [x] T019 Run `quickstart.md` validation to ensure end-to-end user journey is correct
- [x] T020 Final regression test of the dashboard with all widgets types
- [x] T021 Code cleanup and removal of any unused color-related constants in `core/ui`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** and **Foundational (Phase 2)** must be completed first.
- **User Story 1 (P1)** is the MVP and should be completed before P2/P3.
- **User Story 2 (P2)** and **User Story 3 (P3)** can proceed in parallel after Phase 2.

### Parallel Opportunities

- T001, T002 (Setup)
- T005, T006, T007 (Testing)
- T015, T017, T018 (Maintenance/Data updates)

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Foundational logic (T003-T005).
2. Implement local resolution in `GaugeWidget` (T008-T009).
3. Validate that gauges change color based on thresholds without server input.

### Incremental Delivery

1. Foundation -> Core logic ready.
2. User Story 1 -> MVP delivery (Gauges working).
3. User Story 2 -> Backend simplification.
4. User Story 3 -> Full customization support.
