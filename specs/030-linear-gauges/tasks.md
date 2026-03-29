# Tasks: Linear Gauges (030)

**Input**: Design documents from `/specs/030-linear-gauges/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Update common data models and types used across all stories.

- [x] T001 Define `GaugeAxis` enum (`ARC`, `LINEAR_HORIZONTAL`, `LINEAR_VERTICAL`) in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T002 Define `GaugeIndicator` enum (`FILL`, `POINTER`) in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T003 Update `WidgetConfiguration` data class to include `gaugeAxis` and `gaugeIndicator` fields in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Refactor existing gauge logic to support the new decoupled architecture.

**⚠️ CRITICAL**: This refactoring blocks all user story implementation.

- [x] T004 [P] Update `WidgetConfigurationTest.kt` to verify serialization of new gauge axis and indicator fields in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`
- [x] T005 Define `GaugePainter` interface and extract current Arc rendering logic into `ArcGaugePainter.kt` in `app/src/main/java/com/example/hmi/widgets/`
- [x] T006 Refactor `GaugeWidget.kt` to delegate rendering to the appropriate `GaugePainter` based on configuration.

**Checkpoint**: Foundation ready - current Arc gauge functionality preserved under the new architecture.

---

## Phase 3: User Story 1 - Linear Fill Gauge (Priority: P1) 🎯 MVP

**Goal**: Implement the basic linear fill gauge for both horizontal and vertical axes.

**Independent Test**: Configure a gauge to `LINEAR_VERTICAL` + `FILL` and verify it fills from bottom to top as tag data changes.

- [x] T007 [P] [US1] Add Axis and Indicator selection rows (FilterChips) to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T008 [US1] Implement `LinearGaugePainter.kt` structure and coordinate mapping for horizontal/vertical scales in `app/src/main/java/com/example/hmi/widgets/`
- [x] T009 [US1] Implement `FILL` indicator rendering logic (3x track thickness) for `LINEAR_HORIZONTAL` axis in `LinearGaugePainter.kt`
- [x] T010 [US1] Implement `FILL` indicator rendering logic (3x track thickness) for `LINEAR_VERTICAL` axis in `LinearGaugePainter.kt`

**Checkpoint**: User Story 1 functional (Linear Fill Gauges).

---

## Phase 4: User Story 2 - Linear Pointer Gauge (Priority: P1)

**Goal**: Implement the triangle caret pointer for linear axes.

**Independent Test**: Configure a gauge to `LINEAR_HORIZONTAL` + `POINTER` and verify the triangle moves along the top of the track.

- [x] T011 [US2] Implement `POINTER` (triangle caret) drawing logic for linear axes in `LinearGaugePainter.kt`
- [x] T012 [US2] Implement edge-alignment logic for pointers (Left side for Vert, Top side for Horiz) pointing inward in `LinearGaugePainter.kt`

**Checkpoint**: User Story 2 functional (Linear Pointer Gauges).

---

## Phase 5: User Story 3 - Visual Consistency & Zones (Priority: P2)

**Goal**: Add ticks and color zones to the linear axes.

**Independent Test**: Define a high-value red zone for a linear gauge and verify it renders correctly on the track.

- [x] T013 [US3] Implement scale tick rendering along the linear track in `LinearGaugePainter.kt`
- [x] T014 [US3] Implement color zone segment rendering for linear axes in `LinearGaugePainter.kt`
- [x] T015 [US3] Implement opposite-side layout for ticks and labels (relative to pointer) to prevent visual crowding in `LinearGaugePainter.kt`

**Checkpoint**: User Story 3 functional (Full Linear Gauge Feature Set).

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and documentation.

- [x] T016 [P] Add UI interaction test for linear gauges in `app/src/androidTest/java/com/example/hmi/widgets/WidgetTest.kt`, verifying all 6 axis/indicator combinations render correctly.
- [x] T017 [P] Verify content descriptions (A11Y-002) and dynamic text scaling support (A11Y-003) for all 6 gauge combinations in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [x] T018 Final code cleanup, documentation update in `DEVELOPMENT_OVERVIEW.md`, and final audit for "Clarity by Design" and upright label (UI-003) principles.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all visual work.
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion.
- **User Story 2 (Phase 4)**: Depends on Phase 3 (Painter structure).
- **User Story 3 (Phase 5)**: Depends on Phase 4 (Layout alignment).
- **Polish (Final Phase)**: Depends on all desired user stories being complete.

### Implementation Strategy

- **MVP First**: User Story 1 (Linear Fill) provides immediate value for level monitoring.
- **Incremental Refinement**: Indicators are added first, then decorative/informational scale elements.

### Parallel Opportunities

- T004 (Tests) can be written in parallel with T003.
- T007 (Config UI) can start as soon as the enums are defined in Phase 1.
- T016 and T017 can run in parallel.
