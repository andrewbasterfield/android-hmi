# Tasks: Custom Labels and Dynamic Attribute Updates

**Input**: Design documents from `specs/010-widget-dynamic-attributes/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Verify project structure for widget dynamic attributes integration

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Update `WidgetConfiguration` to include `customLabel: String? = null` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T003 Extend `PlcValue` or create `PlcAttributeUpdate` to support non-numeric attribute updates in `app/src/main/java/com/example/hmi/protocol/PlcCommunicator.kt`
- [x] T004 Implement attribute parsing logic using `lastIndexOf('.')` in `app/src/main/java/com/example/hmi/protocol/RawTcpPlcCommunicator.kt`
- [x] T005 [P] Create unit tests for attribute parsing logic in `app/src/test/java/com/example/hmi/protocol/RawTcpPlcCommunicatorTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Manual Label Override (Priority: P1) 🎯 MVP

**Goal**: Allow users to set a custom display name for widgets in Edit Mode.

**Independent Test**: Setting a custom label in the widget config dialog replaces the tag address on the dashboard.

### Implementation for User Story 1

- [x] T006 Add "Custom Label" field to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T007 Update `ButtonWidget` to display `customLabel` if present in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [x] T008 Update `SliderWidget` to display `customLabel` if present in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T009 Update `GaugeWidget` to display `customLabel` if present in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [x] T010 [P] [US1] Create instrumentation test for manual label override in `app/src/androidTest/java/com/example/hmi/dashboard/LabelOverrideTest.kt`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Dynamic Attribute Updates via Protocol (Priority: P2)

**Goal**: Enable real-time updates to widget labels and colors via the protocol.

**Independent Test**: Sending `TAG.color:#FF0000` via protocol immediately changes the widget's background color.

### Implementation for User Story 2

- [x] T011 Implement `sessionOverrides` StateFlow in `DashboardViewModel` to store transient attributes in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T012 Update `DashboardViewModel` to observe attribute updates from `PlcCommunicator` in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T013 Update `DashboardScreen` to merge persistent config with transient overrides for label and color in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T014 Implement hex color parsing and contrast logic in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt`
- [x] T015 [P] [US2] Create instrumentation test for protocol-driven attribute updates in `app/src/androidTest/java/com/example/hmi/protocol/DynamicAttributeTest.kt`

**Checkpoint**: User Story 2 is functional and testable independently.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and documentation

- [x] T016 [P] Update `README.md` with protocol attribute update examples
- [x] T017 Final code cleanup and refactoring
- [x] T018 Verify "Last Change Wins" conflict resolution manually per `quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational phase completion.
  - US1 (P1) is the priority.
  - US2 (P2) can be implemented after or in parallel with US1 once foundation is in.

### User Story Dependencies

- **User Story 1 (P1)**: Independent of US2.
- **User Story 2 (P2)**: Reuses foundation but can run in parallel with US1 UI work.

### Parallel Opportunities

- T005 (Tests) can run in parallel with T002-T004.
- T007-T009 (Widget UI updates) can run in parallel.
- T010 and T015 (Tests) can run in parallel with implementation.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2 (Foundational).
2. Complete Phase 3 (User Story 1).
3. **STOP and VALIDATE**: Verify manual labels work and persist.

### Incremental Delivery

1. Foundation ready.
2. Add Manual Labels (MVP!).
3. Add Dynamic Protocol Overrides.
4. Final Polish.

---

## Notes

- Protocol updates are session-only (volatile).
- Conflict resolution: "Last Change Wins" for the current session.
- Manual edits in Edit Mode update the persistent DataStore.
