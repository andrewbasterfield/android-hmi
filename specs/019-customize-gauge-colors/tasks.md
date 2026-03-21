# Tasks: Gauge Color Customization

**Input**: Design documents from `/specs/019-customize-gauge-colors/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Unit tests for color selection logic and UI tests for color application are included as per the implementation plan's constitution check.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Verify project structure and branch `019-customize-gauge-colors`
- [X] T002 [P] Sync Gradle files to ensure `core:ui` and `app` dependencies are available

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and data model updates that MUST be complete before ANY user story can be implemented

- [X] T003 Update `WidgetConfiguration` data model with `needleColor`, `scaleColor`, and `isNeedleDynamic` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T004 [P] Add unit tests for `WidgetConfiguration` serialization/deserialization with new fields in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Custom Indicator Styling (Priority: P1) 🎯 MVP

**Goal**: Allow users to manually set a specific color for the gauge needle.

**Independent Test**: Open gauge configuration, select a "Needle Color," and verify the needle updates on the dashboard.

### Tests for User Story 1

- [X] T005 [P] [US1] Create UI test to verify needle color update in `app/src/androidTest/java/com/example/hmi/widgets/GaugeColorTest.kt` (should fail)

### Implementation for User Story 1

- [X] T006 [US1] Update `GaugeWidget` parameters and rendering logic to use `needleColor` in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T007 [US1] Add "Needle Color" selection using `HmiColorPicker` to the gauge configuration section in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T008 [US1] Verify UI standards for the new color picker integration (48dp touch targets, clarity)

**Checkpoint**: User Story 1 is functional. Needle can be colored manually.

---

## Phase 4: User Story 2 - Value-Driven Color Thresholds (Priority: P2)

**Goal**: Gauge needle automatically changes color based on the active `GaugeZone`.

**Independent Test**: Define a green zone (0-80) and a red zone (80-100). Move gauge value to 90 and verify needle turns red.

### Tests for User Story 2

- [X] T009 [P] [US2] Add unit test for needle color calculation logic in `app/src/test/java/com/example/hmi/widgets/GaugeColorLogicTest.kt` (should fail)
- [X] T010 [P] [US2] Add UI test for dynamic needle color transitions in `app/src/androidTest/java/com/example/hmi/widgets/GaugeDynamicColorTest.kt` (should fail)

### Implementation for User Story 2

- [X] T011 [US2] Implement dynamic needle color calculation logic within the `Canvas` draw scope in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T012 [US2] Add "Dynamic Needle Color" toggle to the gauge configuration section in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T013 [US2] Ensure `GaugeWidget` prioritizes dynamic coloring over static colors when toggle is ON (as per FR-005)

**Checkpoint**: User Story 2 is functional. Needle reacts to zone thresholds.

---

## Phase 5: User Story 3 - Scale & Tick Customization (Priority: P3)

**Goal**: Customize color of scale ticks and labels independently.

**Independent Test**: Change "Scale Color" and verify ticks and labels reflect the new color while needle remains unaffected.

### Tests for User Story 3

- [X] T014 [P] [US3] Add UI test to verify scale and label color updates in `app/src/androidTest/java/com/example/hmi/widgets/GaugeScaleColorTest.kt` (should fail)

### Implementation for User Story 3

- [X] T015 [US3] Update `GaugeWidget` to apply `scaleColor` override to ticks and labels in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T016 [US3] Add "Scale Color" selection using `HmiColorPicker` to the gauge configuration section in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: All user stories functional. Gauge colors are fully customizable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and cleanup

- [X] T017 [P] Verify contrast compliance for manual color selections against dashboard backgrounds
- [X] T018 Run `quickstart.md` validation steps
- [X] T019 [P] Remove any temporary debug logs or testing artifacts

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1. Blocks all story phases.
- **User Story 1 (P1)**: Depends on Phase 2.
- **User Story 2 (P2)**: Depends on Phase 2. Integrates with US1 components.
- **User Story 3 (P3)**: Depends on Phase 2.
- **Polish (Phase 6)**: Depends on completion of all stories.

### Parallel Opportunities

- T002 (Gradle sync) and T001 (Structure verification).
- T004 (Data tests) can run alongside T003 (Data model) once the model is drafted.
- T005, T009, T010, T014 (all tests) can be drafted in parallel.
- Once Foundational (Phase 2) is complete, US1 and US3 could technically be implemented in parallel if needed, as they affect different parameters of the `GaugeWidget`.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 & 2.
2. Implement User Story 1 (Manual Needle Color).
3. Verify with UI tests. This provides the first layer of customization value.

### Incremental Delivery

1. Deliver Manual Needle Color (US1).
2. Deliver Dynamic Thresholds (US2) - adds high safety value.
3. Deliver Scale Customization (US3) - adds final aesthetic polish.
