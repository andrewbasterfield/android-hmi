# Tasks: Latching Buttons and Indicator Lights

**Input**: Design documents from `/specs/024-button-switch-indicator/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: Unit tests for serialization and UI tests for latching/indicator behavior are included.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Paths assume the Android project structure as specified in `plan.md`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Data model initialization for new button interaction modes

- [x] T001 Define `InteractionType` enum (`MOMENTARY`, `LATCHING`, `INDICATOR`) in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T002 Update `WidgetConfiguration` with `interactionType` and `isInverted` fields in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core UI component updates for state-driven buttons

- [x] T003 [P] Update `IndustrialButton` to support `isChecked` (visual active state) and `enabled` (disables clicks, haptics, and ripple for UI-003) parameters in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialComponents.kt`
- [x] T004 [P] Update `ButtonWidget` to accept `isChecked` and `isInteractive` and pass them to `IndustrialButton` in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`

**Checkpoint**: Core data models and UI components are ready to support state-driven behaviors.

---

## Phase 3: User Story 1 - Toggle a Latching Switch (Priority: P1) 🎯 MVP

**Goal**: Support latching behavior (toggle switch) with optimistic UI updates.

**Independent Test**: Configure a button as `LATCHING` (can be done via code for now or after US3 is complete). Clicking the button should toggle its visual "Active" state and write the toggled value to the PLC.

### Tests for User Story 1

- [x] T005 [P] [US1] Add unit tests for `WidgetConfiguration` GSON serialization in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`

### Implementation for User Story 1

- [x] T006 [US1] Update `DashboardViewModel.onButtonPress` to toggle tag value for `LATCHING` mode in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T007 [US1] Update `DashboardScreen.kt` to resolve and pass `isChecked` state to `ButtonWidget` based on `tagValues` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: Latching buttons are functional and maintain state synced with the backend.

---

## Phase 4: User Story 2 - Monitor Status via Indicator Light (Priority: P2)

**Goal**: Support read-only indicator mode for status visualization.

**Independent Test**: Configure a button as `INDICATOR`. Clicking it should do nothing (no signal sent). Updating the backend tag value should change the button's visual state.

### Implementation for User Story 2

- [x] T008 [US2] Update `DashboardScreen.kt` to set `isInteractive = false` (disabling all feedback) for `INDICATOR` buttons and resolve their state in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: Indicator buttons correctly reflect backend state without allowing user interaction.

---

## Phase 5: User Story 3 - Configure Button Interaction Mode (Priority: P3)

**Goal**: Allow designers to select button behavior in the configuration dialog.

**Independent Test**: Open the Widget Configuration dialog for a button. Select `LATCHING` or `INDICATOR` and save. Verify the button on the dashboard adopts the selected behavior.

### Implementation for User Story 3

- [x] T009 [US3] Add `InteractionType` selection using `FilterChip` row to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

---

## Phase 6: User Story 4 - Invert Logic for Visual Feedback (Priority: P3)

**Goal**: Support "Active Low" logic via a configurable inversion flag.

**Independent Test**: Enable "Invert Logic" in configuration. Verify that `false` = Active (Identity Swap) and `true` = Inactive.

### Implementation for User Story 4

- [x] T010 [US4] Add "Invert Logic" toggle switch to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T011 [US4] Update state resolution logic in `DashboardScreen.kt` to respect the `isInverted` flag in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: All button behaviors and logic options are fully configurable and functional.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and UI refinements.

- [x] T012 [P] Add UI tests for latching behavior and indicator updates in `app/src/androidTest/java/com/example/hmi/widgets/ButtonWidgetTest.kt`
- [x] T013 [P] Run `quickstart.md` validation scenarios.
- [x] T014 [P] Perform Accessibility & UI Audit (touch targets, descriptions) and verify Performance Goals (SC-001, SC-002)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Foundations for data structures.
- **Phase 2 (Foundational)**: Core UI infrastructure required by all stories.
- **Phases 3 & 4 (US1, US2)**: Core functional requirements. US1 is the MVP.
- **Phases 5 & 6 (US3, US4)**: Configuration UI. Can be implemented earlier to facilitate manual testing of US1/US2.

### User Story Dependencies

- **US1 & US2** depend on Phase 2 UI updates.
- **US3 & US4** provide the UI to configure US1/US2, but US1/US2 can be verified via hardcoded configs or unit tests first.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 & 2.
2. Implement US1 (Latching).
3. Validate US1 with unit tests and manual code-level configuration change.

### Parallel Opportunities

- T003 and T004 (Core UI updates) can be done in parallel.
- Serialization tests (T005) can be done in parallel with ViewModel logic (T006).
- UI tests (T012) can be developed alongside the feature implementation.
