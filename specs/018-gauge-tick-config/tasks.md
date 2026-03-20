# Tasks: Configurable Gauge Tick Density

**Input**: Design documents from `/specs/018-gauge-tick-config/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Unit tests for `ScaleUtils` are requested to verify algorithm behavior at density extremes (2 and 20 ticks).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Project Initialization)

**Purpose**: Update data entities to support the new configuration field.

- [X] T001 Update `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` to add `targetTicks: Int = 6` field.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core logic validation and algorithm verification.

- [X] T002 Create unit tests in `app/src/test/java/com/example/hmi/widgets/ScaleUtilsTest.kt` to verify `calculateNiceStep` with `targetTicks` ranging from 2 to 20.
- [X] T003 [P] Verify `app/src/main/java/com/example/hmi/widgets/ScaleUtils.kt` correctly handles dynamic `targetTicks` against various ranges.

**Checkpoint**: Core algorithm is verified and handles dynamic densities safely.

---

## Phase 3: User Story 1 - Adjustable Data Granularity (Priority: P1) 🎯 MVP

**Goal**: Enable real-time adjustment of gauge tick density via UI with Scale Assistance.

**Independent Test**: Open Gauge config, adjust density slider, and verify immediate "Outcome" label update and dashboard dial update.

### Implementation for User Story 1

- [X] T004 [US1] Update `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt` to pass `targetTicks` from `WidgetConfiguration` to `ScaleUtils.calculateNiceStep`.
- [X] T005 [US1] Refactor `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`:
    - Add "Tick Density" Slider (Range 2-20) below the Max Value field for Gauges.
    - Implement "Scale Assistance" label displaying calculated tick count and step size using current range and slider value.

**Checkpoint**: User Story 1 functional - tick density can be adjusted live with visual guidance.

---

## Phase 4: User Story 2 - Persistent Custom Density (Priority: P2)

**Goal**: Ensure custom density settings are saved across app restarts.

**Independent Test**: Configure a gauge to 15 ticks, restart the app, and verify the density is preserved.

### Implementation for User Story 2

- [X] T006 [US2] Verify `targetTicks` persistence by confirming successful GSON serialization/deserialization in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`.

**Checkpoint**: User Story 2 functional - configuration is durable.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and documentation.

- [X] T007 Perform visual audit of Gauge rendering:
    - Verify 0 overlapping marks at maximum density (20 ticks) on 1x1 widgets.
    - Confirm "immediate" (<100ms) visual update of dial during slider manipulation.
    - Validate "Scale Assistance" outcome matches the actual rendered tick count.
- [X] T008 [P] Update `specs/018-gauge-tick-config/checklists/requirements.md` to reflect completed verification.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Prerequisites for all subsequent phases.
- **Foundational (Phase 2)**: MUST be complete before UI integration to ensure algorithm safety.
- **User Stories (Phase 3 & 4)**: Phase 3 provides the UI to test Phase 4 persistence.

### User Story Dependencies

- **US1 (P1)**: Independent after Phase 2.
- **US2 (P2)**: Dependent on US1 for UI control.

---

## Parallel Execution Examples

- T002 (Tests) and T003 (Verification) can run in parallel if logic is already presumed stable.
- T008 (Checklist update) can run in parallel with final visual audit.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and 2.
2. Implement T004 and T005.
3. **Validate**: Verify the slider correctly updates the dial increments live with helpful guidance.

### Incremental Delivery

1. Verify persistence (US2).
2. Perform final visual Polish.
