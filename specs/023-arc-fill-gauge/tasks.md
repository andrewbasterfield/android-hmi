# Tasks: Arc-Filling Gauge Support

**Input**: Design documents from `/specs/023-arc-fill-gauge/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Tests**: Included as per implementation plan requirements for UI and serialization verification.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Renaming & Terminology)

**Purpose**: Align codebase with industrial terminology ("Pointer" vs "Needle")

- [X] T001 [P] Rename `needleColor` to `pointerColor` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T002 [P] Rename `isNeedleDynamic` to `isPointerDynamic` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T003 [P] Update `ColorUtils.resolveNeedleColor` to `resolvePointerColor` and rename parameters in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt`
- [X] T004 [P] Update `GaugeWidget` parameters and semantics to use "Pointer" terminology in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T005 [P] Update `WidgetPalette.kt` to use "Pointer" terminology in UI labels and state variables in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T006 [P] Update existing tests to reflect "Pointer" renaming in:
    - `app/src/androidTest/java/com/example/hmi/widgets/GaugeColorTest.kt`
    - `app/src/androidTest/java/com/example/hmi/widgets/GaugeDynamicColorTest.kt`
    - `app/src/androidTest/java/com/example/hmi/widgets/GaugeScaleColorTest.kt`
    - `app/src/androidTest/java/com/example/hmi/widgets/GaugeUnitsTest.kt`
    - `app/src/test/java/com/example/hmi/widgets/GaugeColorLogicTest.kt`

---

## Phase 2: Foundational (Data Model & Persistence)

**Purpose**: Core infrastructure for the new Gauge styles

- [X] T007 [P] Add `GaugeStyle` enum (POINTER, ARC_FILL) to `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T008 [P] Add `gaugeStyle` property to `WidgetConfiguration` with default `POINTER` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [X] T009 [P] Update `WidgetConfigurationTest.kt` to verify serialization of `gaugeStyle` and renamed pointer fields in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`

**Checkpoint**: Foundation ready - UI and rendering work can begin.

---

## Phase 3: User Story 1 - Configure Gauge Display Style (Priority: P1) 🎯 MVP

**Goal**: Allow users to choose between Pointer and Arc Fill styles in the configuration dialog.

**Independent Test**: Open a Gauge configuration, toggle style to "Arc Fill", and save. Pointer should disappear and be replaced by a placeholder or empty arc.

### Tests for User Story 1

- [X] T010 [P] [US1] Add test case for style selection UI in `app/src/androidTest/java/com/example/hmi/widgets/GaugeStyleTest.kt`

### Implementation for User Story 1

- [X] T011 [P] [US1] Add `GaugeStyle` selector (FilterChips) to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T012 [US1] Pass `gaugeStyle` from `DashboardScreen` to `GaugeWidget` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [X] T013 [US1] Update `GaugeWidget` to hide pointer when style is `ARC_FILL` and update `contentDescription` for a11y (A11Y-002) in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`

**Checkpoint**: Style selection is functional and persisted.

---

## Phase 4: User Story 2 - Dynamic Arc Filling (Priority: P1)

**Goal**: Render a filling arc that represents the current value.

**Independent Test**: Change value of an "Arc Fill" gauge and observe the arc length changing proportionally.

### Tests for User Story 2

- [X] T014 [P] [US2] Add test case for arc fill length verification and content description updates in `app/src/androidTest/java/com/example/hmi/widgets/GaugeStyleTest.kt`

### Implementation for User Story 2

- [X] T015 [US2] Implement background "track" arc rendering (low contrast, 10-15% opacity per UI-004/Research) in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T016 [US2] Implement foreground "fill" arc rendering proportional to value in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T017 [US2] Verify arc filling respects `arcSweep` and `startAngle` alignment, and perform visual accuracy check (SC-002: <1% error).

**Checkpoint**: Gauge accurately represents value using the filling arc.

---

## Phase 5: User Story 3 - Arc Color Customization (Priority: P2)

**Goal**: Apply existing color logic (zones/static) to the filling arc.

**Independent Test**: Configure color zones and verify the filling arc changes color as the value moves between zones.

### Tests for User Story 3

- [X] T018 [P] [US3] Add test case for arc fill color zones in `app/src/androidTest/java/com/example/hmi/widgets/GaugeStyleTest.kt`

### Implementation for User Story 3

- [X] T019 [US3] Update `ARC_FILL` rendering logic to use `resolvePointerColor` for the foreground arc in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T020 [US3] Verify consistency between Pointer color and Arc Fill color across all states.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and documentation

- [X] T021 [P] Ensure high contrast between background track and foreground fill (UI-001)
- [X] T022 [P] Update semantics and accessibility labels for new style in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [X] T023 [P] Update `README.md` or internal documentation if necessary
- [X] T024 Run `quickstart.md` validation on a real device/emulator
- [X] T025 Perform final linting and code cleanup

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 completion.
- **User Story 1 (Phase 3)**: Depends on Phase 2.
- **User Story 2 & 3 (Phases 4 & 5)**: Depend on Phase 3 (as they require the `gaugeStyle` parameter to be passed).
- **Polish (Phase 6)**: Depends on all user stories.

### Parallel Opportunities

- Renaming tasks (T001-T006) can mostly be done in parallel or as a single refactoring pass.
- Serialization tests (T009) can be written while UI logic is being developed.
- UI tests (T010, T014, T018) can be drafted ahead of implementation.

---

## Implementation Strategy

### MVP First (User Story 1 & 2)

1. Complete renaming and data model updates.
2. Implement style selection UI.
3. Implement basic arc filling (User Story 2).
4. **STOP and VALIDATE**: Verify a functional arc-fill gauge exists with correct semantics.

### Incremental Delivery

1. Foundation ready (Phase 1 & 2).
2. Style toggle functional (Phase 3).
3. Arc fill functional (Phase 4).
4. Color logic integrated (Phase 5).
5. Final polish (Phase 6).
