# Tasks: Telemetry Safety Standards (SI Compliance & ISA-18.2)

**Input**: Design documents from `/specs/021-telemetry-safety-standard/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/SiFormatter.md

**Tests**: Technical validation includes unit tests for the SI Formatter and instrumented tests for the alarm pulse logic.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and base utility setup

- [x] T001 Update `core/ui/src/main/java/com/example/hmi/core/ui/theme/Shape.kt` to set `small`, `medium`, and `large` shapes to `RoundedCornerShape(2.dp)` for total project consistency
- [x] T002 [P] Create `core/ui/src/main/java/com/example/hmi/core/ui/utils/SiFormatter.kt` following the `SiFormatter.md` contract
- [x] T003 [P] Create unit test `core/ui/src/test/java/com/example/hmi/core/ui/utils/SiFormatterTest.kt` to verify SI symbol case-sensitivity

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core model updates and alarm pulse infrastructure

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 Add `AlarmState` enum (Normal, Unacknowledged, Acknowledged) to `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T005 [P] Create `AlarmPulse` composable in `core/ui/src/main/java/com/example/hmi/core/ui/components/AlarmPulse.kt` that animate-flashes a 2px border at 3-5Hz
- [x] T006 Update `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt` to include an `acknowledgeAlarm(tagAddress: String)` method
- [x] T007 [P] Create `core/ui/src/main/java/com/example/hmi/core/ui/theme/HealthStatus.kt` if not present to define consistent "Warning" (StatusRed) background tokens

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Safety-Critical Monitoring (Priority: P1) 🎯 MVP

**Goal**: Ensure telemetry units are SI-compliant and unacknowledged alarms flash the bounding box without affecting text readability.

**Independent Test**: Trigger a fault for a voltage tag; verify unit is `mV` and bounding box pulses 3-5Hz while value `0.005` remains static.

### Implementation for User Story 1

- [x] T008 [US1] Refactor `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt` to use `SiFormatter` for unit display
- [x] T009 [US1] Wrap `GaugeWidget` content in `AlarmPulse` to handle `Unacknowledged` state (flashing 2px border)
- [x] T010 [P] [US1] Refactor `core/ui/src/main/java/com/example/hmi/core/ui/components/TelemetryCard.kt` to use `SiFormatter`
- [x] T011 [US1] Wrap `TelemetryCard` content in `AlarmPulse` for alarm signaling
- [x] T012 [US1] Verify that `GaugeWidget` and `TelemetryCard` text remains static (no color shift or flash) during the alarm pulse

**Checkpoint**: User Story 1 is functional: Correct units and conspicuous, readable alarms.

---

## Phase 4: User Story 2 - Alarm Acknowledgement (Priority: P2)

**Goal**: Allow operators to suppress the flashing strobe while maintaining the "Warning" color state.

**Independent Test**: Tap a flashing gauge; verify flashing stops immediately but the 2px border stays solid StatusRed.

### Implementation for User Story 2

- [x] T013 [US2] Update `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` to trigger `viewModel.acknowledgeAlarm()` on widget tap
- [x] T014 [US2] Modify `AlarmPulse.kt` to support a "Solid" (Acknowledged) state that displays a non-flashing StatusRed border
- [x] T015 [US2] Update `GaugeWidget.kt` and `TelemetryCard.kt` to pass the `Acknowledged` state to `AlarmPulse`
- [x] T016 [US2] Verify that app restart resets `Acknowledged` alarms to `Unacknowledged` (flashing) per safety research

**Checkpoint**: User Story 2 is functional: Operators can suppress visual noise while resolving faults.

---

## Phase 5: User Story 3 - High-Density Unit Scanning (Priority: P3)

**Goal**: Ensure all magnitudes (kilo, Mega, etc.) are correctly rendered across the entire dashboard.

**Independent Test**: View multiple widgets with `kW`, `MW`, `Hz`, and `mV`; verify case matches SI standards exactly.

### Implementation for User Story 3

- [x] T017 [US3] Review all `WidgetConfiguration` samples in `app/src/main/assets/` or `DashboardRepository` to ensure default units are SI-compliant
- [x] T018 [P] [US3] Create an instrumented test `app/src/androidTest/java/com/example/hmi/widgets/SiComplianceTest.kt` that renders all symbols from the dictionary and verifies case
- [x] T019 [US3] Verify 2px border radius consistency across all high-density telemetry blocks

**Checkpoint**: User Story 3 is functional: Dashboard-wide SI compliance and aesthetic consistency.

---

## Phase N: Polish & Cross-Cutting Concerns

- [x] T020 [P] Update `README.md` and `DESIGN.md` (if needed) with the new safety protocol documentation
- [x] T021 Code cleanup in `ColorUtils.kt` to remove any legacy force-capitalization logic
- [x] T022 Performance optimization: Ensure `AlarmPulse` infiniteTransition doesn't cause excessive recomposition when in `Normal` or `Acknowledged` states
- [x] T023 Run `quickstart.md` validation to confirm final implementation matches design intent

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: Depends on T001-T003 completion.
- **User Stories (Phase 3+)**: Depend on Foundational completion.
  - US1 (P1) is the MVP and should be completed first.
  - US2 and US3 can proceed once US1 is stable.

### User Story Dependencies

- **User Story 1 (P1)**: Foundation for all safety-critical monitoring.
- **User Story 2 (P2)**: Extends US1 with interaction logic.
- **User Story 3 (P3)**: Validates global compliance.

### Parallel Opportunities

- T002 and T003 (SI Formatter & Test) can run in parallel.
- T010 and T011 (TelemetryCard refactor) can run in parallel with GaugeWidget work.
- T018 (Instrumented test) can be developed in parallel with Story 3 implementation.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup + Foundational logic.
2. Refactor `GaugeWidget` for SI compliance and `AlarmPulse` (T008-T009).
3. **STOP and VALIDATE**: Verify SI unit case and 4Hz pulse on a single widget.

### Incremental Delivery

1. Foundation + US1 → "Safe Monitoring" MVP.
2. US2 → Adds "Operator Interaction" (Acknowledgement).
3. US3 → Finalizes "Global Compliance" and Polish.

---

## Notes

- **SI Safety**: NEVER use `.uppercase()` on unit strings.
- **Alarm Pulse**: 3-5Hz limit is a strict safety constraint (A11Y-001).
- **Hard Industrial**: 2px radius is the new project-wide standard for all `core:ui` blocks.
