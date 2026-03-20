# Tasks: Stitch Design System Integration (Industrial Precision HMI)

**Input**: Design documents from `/specs/016-stitch-design-system/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: UI and Screenshot tests are requested in the implementation plan to verify touch targets and color inversion.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 [P] Create `:core:ui` module and configure `build.gradle.kts`
- [X] T002 [P] Create `:feature:diagnostics` module and configure `build.gradle.kts`
- [X] T003 [P] Load "Space Grotesk" and "Inter" font families in `core/ui/src/main/res/font/`
- [X] T004 [P] Define "The Void" color tokens (#131313, Status colors) in `core/ui/src/main/java/com/example/hmi/core/ui/theme/Color.kt`
- [X] T005 [P] Define specific hex tokens for `surface-container-low` and `surface-container-high` in `Color.kt`
- [X] T006 [P] Define Rectangular shapes (0dp radius) in `core/ui/src/main/java/com/example/hmi/core/ui/theme/Shape.kt`
- [X] T007 [P] Configure Typography with Space Grotesk (Headlines) and Inter (Body) in `core/ui/src/main/java/com/example/hmi/core/ui/theme/Type.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [X] T008 Implement `StitchTheme` and `StitchThemeTokens` provider in `core/ui/src/main/java/com/example/hmi/core/ui/theme/StitchTheme.kt`
- [X] T009 [P] Create `IndicationInstance` for "Inverse Video" immediate color swap in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialIndication.kt`
- [X] T010 Implement `IndustrialIndication` that triggers on `InteractionSource` pressed state in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialIndication.kt`
- [X] T011 [P] Integrate Haptic Feedback trigger into `IndustrialIndication` in `IndustrialIndication.kt`

**Checkpoint**: Foundation ready - Theme and interaction logic can now be used for user stories.

---

## Phase 3: User Story 1 - High-Contrast Telemetry Monitoring (Priority: P1) 🎯 MVP

**Goal**: Implement high-contrast dark-themed telemetry readouts with stenciled-style data and status indicators.

**Independent Test**: Verify legibility of a `TelemetryCard` and presence of status icons for accessibility.

### Tests for User Story 1

- [X] T012 [P] [US1] Create Screenshot test for `TelemetryCard` contrast in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/TelemetryCardTest.kt`
- [X] T013 [P] [US1] Create UI test to verify `Space Grotesk` and status icons are present in `TelemetryCardTest.kt`

### Implementation for User Story 1

- [X] T014 [P] [US1] Create `TelemetryData` data model in `feature/diagnostics/src/main/java/com/example/hmi/feature/diagnostics/model/TelemetryData.kt`
- [X] T015 [P] [US1] Implement `TelemetryCard` with 4px health accent bar and status icons in `core/ui/src/main/java/com/example/hmi/core/ui/components/TelemetryCard.kt`
- [X] T016 [US1] Implement monospaced alignment for numerical values in `TelemetryCard.kt`
- [X] T017 [US1] Implement the rigid modular grid layout system in `feature/diagnostics/src/main/java/com/example/hmi/feature/diagnostics/DiagnosticsScreen.kt`
- [X] T018 [US1] Apply `StitchTheme` to `DiagnosticsScreen.kt` and display telemetry data.

**Checkpoint**: User Story 1 functional - High-contrast telemetry cards with icons are visible in a modular grid.

---

## Phase 4: User Story 2 - Gloved-Hand Tactile Interaction (Priority: P2)

**Goal**: Implement large touch targets with immediate visual "Inverse Video" feedback and Industrial Input.

**Independent Test**: Verify `IndustrialButton` and `IndustrialInput` height/targets for gloved use.

### Tests for User Story 2

- [X] T019 [P] [US2] Create UI test to verify `IndustrialButton` and `IndustrialInput` targets are at least 64px in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/IndustrialComponentTest.kt`
- [X] T020 [US2] Create UI test to verify color inversion on `Pressed` state in `IndustrialComponentTest.kt`

### Implementation for User Story 2

- [X] T021 [P] [US2] Implement `IndustrialButton` with 2px solid bezel in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialButton.kt`
- [X] T022 [P] [US2] Implement `IndustrialInput` with 4px bottom-border "shelf" in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialInput.kt`
- [X] T023 [US2] Apply `IndustrialIndication` to `IndustrialButton` in `IndustrialButton.kt`
- [X] T024 [US2] Add tactile controls and inputs to `feature/diagnostics/src/main/java/com/example/hmi/feature/diagnostics/DiagnosticsScreen.kt`

**Checkpoint**: User Story 2 functional - Tactile buttons and inputs with mechanical-style feedback are operational.

---

## Phase 5: User Story 3 - Peripheral Emergency Signaling (Priority: P3)

**Goal**: Implement a pulsing red peripheral HUD and backdrop blur for critical system failures.

**Independent Test**: Trigger a `CRITICAL` state and verify peripheral pulse and modal blur.

### Tests for User Story 3

- [X] T025 [P] [US3] Create UI test to verify pulsing alpha and `BackdropBlur` in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/EmergencyHUDTest.kt`

### Implementation for User Story 3

- [X] T026 [P] [US3] Implement `PeripheralGlow` composable in `core/ui/src/main/java/com/example/hmi/core/ui/components/EmergencyHUD.kt`
- [X] T027 [P] [US3] Implement `BackdropBlur` with 12px blur in `core/ui/src/main/java/com/example/hmi/core/ui/components/BackdropBlur.kt`
- [X] T024 [US3] Wrap `DiagnosticsScreen.kt` with `EmergencyHUD` and integrate with critical telemetry state.
- [X] T024a [US3] Implement priority logic in `core/ui/src/main/java/com/example/hmi/core/ui/components/EmergencyHUD.kt` to determine glow color based on highest active `TelemetryData` status.

**Checkpoint**: User Story 3 functional - Peripheral emergency alerts and blurs are integrated.


---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements, accessibility verification, and success criteria validation.

- [X] T029 [P] Verify WCAG AAA contrast ratios for all color combinations in `core/ui/`.
- [X] T030 [P] Conduct manual verification of `SC-001` (legibility at 5m) and `SC-004` (viewing angle) on physical hardware.
- [X] T030a [P] Verify SC-004 legibility at 10% device brightness on physical hardware to confirm "Low-Battery" robustness.
- [X] T031 [P] Ensure all technical labels and units are forced to UPPERCASE across all components.
- [X] T032 [P] Update `README.md` with "Kinetic Cockpit" modular architecture guidelines.
- [X] T033 Run full test suite across all modules.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1 (Module creation and core tokens).
- **User Stories (Phase 3+)**: All depend on Foundational (Phase 2).

### Parallel Opportunities

- Module setup and core token definition (T001-T007) can run in parallel.
- US1 and US2 can proceed in parallel once Phase 2 is complete.
