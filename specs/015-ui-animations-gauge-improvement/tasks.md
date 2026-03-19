# Tasks: UI Animations and Gauge Improvement

**Input**: Design documents from `/specs/015-ui-animations-gauge-improvement/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md

## Phase 1: Foundational (Data & Infrastructure)

**Purpose**: Update the core data models and configuration to support haptics and gauge zones.

- [x] T001 [P] Create `GaugeZone` data class in `app/src/main/java/com/example/hmi/data/GaugeZone.kt`
- [x] T002 [P] Add `hapticFeedbackEnabled` to `DashboardLayout` in `app/src/main/java/com/example/hmi/data/DashboardLayout.kt`
- [x] T003 [P] Add `colorZones` list to `WidgetConfiguration` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T004 Implement `ScaleUtils.kt` with the "Nice Number" tick algorithm (log10 base) in `app/src/main/java/com/example/hmi/widgets/ScaleUtils.kt`
- [x] T005 Update `DashboardViewModel` to handle the new haptic feedback state and zone updates.

---

## Phase 2: User Story 1 - Tactile Button Feedback (Priority: P1)

**Goal**: Implement the 3D button press animation and optional haptic feedback.

- [x] T006 [P] [US1] Create a baseline UI test for button interaction states in `app/src/test/java/com/example/hmi/widgets/ButtonAnimationTest.kt`
- [x] T007 [US1] Implement `InteractionSource` and scale/elevation animations in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [x] T008 [US1] Integrate `LocalHapticFeedback` into `ButtonWidget` with the `hapticFeedbackEnabled` layout check.
- [x] T009 [US1] Add "Enable Haptic Feedback" toggle to `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`.

---

## Phase 3: User Story 2 - High-Precision 270° Gauge (Priority: P2)

**Goal**: Re-implement the Gauge using `Canvas` for a high-fidelity 270° dial with dynamic zones.

> **⚠️ NOTE: Write tests FIRST and ensure they FAIL before starting implementation (T011).**

- [x] T010 [P] [US2] Create unit tests for the "Nice Number" tick algorithm in `app/src/test/java/com/example/hmi/widgets/ScaleUtilsTest.kt`.
- [x] T011 [US2] Implement the `Canvas` based 270° arc and needle drawing in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt` (including appropriate semantics/content descriptions).
- [x] T012 [US2] Implement dynamic arc drawing for `colorZones` in the Gauge Canvas.
- [x] T013 [US2] Integrate `ScaleUtils` to draw logical tick marks and labels on the gauge face.
- [x] T014 [US2] Implement value interpolation (smooth needle movement) using `animateFloatAsState`.

---

## Phase 4: User Story 3 - Configuration UI (Priority: P3)

**Goal**: Provide the UI for administrators to configure haptics and gauge zones.

- [x] T015 [US3] Update `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt` to allow adding/removing `GaugeZone` entries for Gauge widgets.
- [x] T016 [US3] Add color selection (using `HmiColorPicker`) for each Gauge Zone in the configuration dialog.
- [x] T017 Verify all Accessibility & UI Standards (48dp targets, high contrast, no gimmicks) for the new configuration fields.

---

## Phase 5: Verification & Polish

- [x] T018 Perform a manual walkthrough of the `quickstart.md` scenarios (specifically verifying button press visibility from 1 meter distance per SC-001).
- [x] T019 [P] Run all unit and UI tests (`./gradlew test`).
- [x] T020 [P] Optimize Canvas drawing performance (verify 60fps on target hardware).
