# Tasks: Dashboard Design Integration (Kinetic Cockpit)

**Input**: Design documents from `/specs/017-dashboard-design-integration/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Compose UI tests are requested to verify 64px touch targets and "Inverse Video" interaction timing.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and base structure

- [X] T001 [P] Implement `ColorSanitizer` utility in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt` to map legacy colors to OSHA/Kinetic tokens
- [X] T002 [P] Define OSHA-compliant preset colors in `ColorUtils.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [X] T003 Refactor `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`:
    - Enforce `RectangleShape` (0dp radius).
    - Update border to 2.dp using `StitchTheme.tokens.outline`.
    - **Remove header row and label/status text (UI-002).**
    - **Implement tactile 32x32dp Resize Handle in bottom-right corner for Edit Mode (BUG-001, FR-012).**
- [X] T004 Update `app/src/main/java/com/example/hmi/MainActivity.kt` to wrap all operational routes in `StitchTheme`.
- [X] T005 [US1] Implement layout migration in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`:
    - Create `migrateToKineticCockpit()` function.
    - Map existing colors to OSHA tokens using `ColorSanitizer`.
    - Set default `canvasColor` to Obsidian (#131313).
    - Trigger migration on first launch detected in DataStore.

**Checkpoint**: Foundation ready - containers and theme are ruggedized.

---

## Phase 3: User Story 1 - Ruggedized Live Dashboard (Priority: P1) 🎯 MVP

**Goal**: Transform existing widgets to the rugged aesthetic with live data and legible scale.

**Independent Test**: Verify existing dashboard widgets render with 0dp corners and monospaced readouts at industrial scale.

### Implementation for User Story 1

- [X] T006 [US1] Refactor `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`:
    - Force `FontFamily.Monospace` for numerical values.
    - **Update Typography Scale: Readout >= 24sp, Label >= 16sp (BUG-002, FR-013).**
    - Set default background to `StitchTheme.tokens.surfaceContainerLow`.
- [X] T007 [US1] Refactor `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`:
    - Enforce `RectangleShape` for track and thumb.
    - Force `FontFamily.Monospace` for scale readouts.
    - **Update Typography Scale: Readout >= 18sp, Scale Labels >= 14sp (BUG-002, FR-013).**

**Checkpoint**: User Story 1 functional - the live dashboard is ruggedized and legible.

---

## Phase 4: User Story 2 - Tactile Feedback & Management (Priority: P2)

**Goal**: Implement "Inverse Video" and haptic feedback for functional controls.

**Independent Test**: Press a dashboard button and verify immediate color swap and vibration (if enabled).

### Tests for User Story 2

- [ ] T008 [P] [US2] Create UI test in `app/src/androidTest/java/com/example/hmi/widgets/ButtonWidgetTest.kt` to verify `ButtonWidget` state swap timing (<50ms).

### Implementation for User Story 2

- [ ] T009 [US2] Refactor `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`:
    - Replace internal logic with `IndustrialButton` from `:core:ui`.
    - Ensure 64px minimum touch target height.
    - Bind haptic feedback to `DashboardLayout.hapticFeedbackEnabled`.
- [ ] T010 [US2] Connect **Resize Handle** events in `WidgetContainer.kt` to `DashboardScreen.kt` resize logic.

**Checkpoint**: User Story 2 functional - buttons and resizing provide mechanical-style tactile confirmation.

---

## Phase 5: User Story 3 - Real-Time Emergency HUD (Priority: P3)

**Goal**: Connect the peripheral HUD glow to live PLC alarms.

**Independent Test**: Trigger a critical PLC tag and verify the 2Hz red peripheral pulse appears.

### Implementation for User Story 3

- [X] T011 [US3] Implement `derivedStateOf` logic in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` to calculate `globalStatus`.
- [X] T012 [US3] Wrap the main dashboard `Box` in `EmergencyHUD` and bind to `globalStatus`.

**Checkpoint**: User Story 3 functional - system provides screen-wide emergency signaling.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and success criteria validation.

- [ ] T013 [P] Verify WCAG AAA contrast for all OSHA color presets in `ColorUtils.kt`.
- [ ] T014 [P] Update `README.md` with "Functional Ruggedization" verification instructions.
- [ ] T015 Run full instrumentation test suite to validate success criteria SC-001 through SC-003.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1 completion.
- **User Stories (Phase 3+)**: All depend on Foundational (Phase 2).

### User Story Dependencies

- **US1 (P1)**: Independent after Phase 2.
- **US2 (P2)**: Independent after Phase 2.
- **US3 (P3)**: Depends on US1's widget refactors for status icons.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and 2.
2. Complete Phase 3 (Ruggedized Live Dashboard).
3. **Validate**: Verify live PLC data is monospaced, ruggedized, and legible at distance.

### Incremental Delivery

1. Add User Story 2 (Tactile Feedback & Management) to enable high-performance interaction.
2. Add User Story 3 (Emergency HUD) for situational awareness.
3. Perform final Polish and verification.
