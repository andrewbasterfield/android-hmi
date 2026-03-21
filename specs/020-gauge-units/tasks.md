# Tasks: Gauge Units Support

**Input**: Design documents from `/specs/020-gauge-units/`
**Prerequisites**: plan.md, spec.md, data-model.md

**Tests**: Unit tests for serialization and UI tests for unit rendering are included.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Verify project structure and branch `020-gauge-units`
- [X] T002 [P] Sync Gradle files to ensure `app` module dependencies are resolved

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data model updates that MUST be complete before ANY user story can be implemented

- [X] T003 Update `WidgetConfiguration` data model with `units: String? = null` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T004 [P] Add unit tests for `WidgetConfiguration` serialization including the `units` field in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Contextual Data Readouts (Priority: P1) 🎯 MVP

**Goal**: Display configured units next to the gauge numeric value.

**Independent Test**: Configure a gauge with units "PSI", verify readout shows "75.5 PSI".

### Tests for User Story 1

- [X] T005 [P] [US1] Create UI test to verify units are appended to the numeric readout in `app/src/androidTest/java/com/example/hmi/widgets/GaugeUnitsTest.kt` (should fail)

### Implementation for User Story 1

- [X] T006 [US1] Update `GaugeWidget` parameters and rendering logic to display `units` appended to the value in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T007 [US1] Implement typography refinement for units (slightly smaller/lighter) in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T008 [US1] Add "Units" input field to the Gauge configuration section in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T009 [US1] Verify that empty units do not render extra whitespace or suffixes

**Checkpoint**: User Story 1 is functional. Units are visible and configurable.

---

## Phase 4: User Story 2 - Real Estate Optimization (Priority: P2)

**Goal**: Ensure units are on the same line as the value to maximize gauge arc size.

**Independent Test**: Verify gauge arc size remains constant when units are added/removed.

### Implementation for User Story 2

- [X] T010 [US2] Audit `GaugeWidget` layout to ensure the readout remains centered and doesn't push other elements in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T011 [US2] Verify layout stability during real-time preview in `WidgetPalette` configuration

**Checkpoint**: User Story 2 verified. Real estate usage optimized.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and cleanup

- [X] T012 [P] Verify special character support (e.g., "°C", "m²") in the readout
- [X] T013 Run comprehensive UI tests for all widget types to ensure no regressions
- [X] T014 [P] Cleanup any temporary testing artifacts

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1. Blocks all story phases.
- **User Story 1 (P1)**: Depends on Phase 2.
- **User Story 2 (P2)**: Depends on User Story 1.
- **Polish (Phase 5)**: Depends on completion of all stories.

### Parallel Opportunities

- T002 (Sync) and T001 (Structure).
- T004 (Model tests) can run alongside T003.
- T005 (UI Test) can be drafted while implementing T003.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 & 2.
2. Implement User Story 1 (Readout + Config UI).
3. Verify with UI tests. This delivers the core value.

### Incremental Delivery

1. Deliver Basic Unit Support (US1).
2. Refine Layout & Stability (US2).
