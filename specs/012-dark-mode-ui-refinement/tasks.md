# Tasks: Dark Mode UI Refinement

**Input**: Design documents from `specs/012-dark-mode-ui-refinement/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Verify project structure for theme and typography integration
- [x] T002 [P] Create `app/src/main/java/com/example/hmi/ui/theme/` directory
- [x] T003 [P] Create `HmiPalette` with curated high-contrast colors in `app/src/main/java/com/example/hmi/ui/theme/HmiPalette.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 Update `WidgetConfiguration` to include `fontSizeMultiplier: Float = 1.0f` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T005 Update `DashboardViewModel` to implement legacy migration logic (auto-migrate to black background/text) in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T006 Implement unit tests for migration logic and palette contrast in `app/src/test/java/com/example/hmi/dashboard/ThemeMigrationTest.kt`
- [x] T007 [P] Implement `HmiTheme` with pure black background in `app/src/main/java/com/example/hmi/ui/theme/HmiTheme.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Default Dark Aesthetics (Priority: P1) 🎯 MVP

**Goal**: Application opens with a high-contrast dark theme and black background by default.

**Independent Test**: Opening the app shows a black background and dark-themed UI components. Existing layouts are auto-migrated.

### Implementation for User Story 1

- [x] T008 Update `MainActivity.kt` to use the new `HmiTheme` in `app/src/main/java/com/example/hmi/MainActivity.kt`
- [x] T009 Update `DashboardScreen.kt` to ensure default `canvasColor` is black in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T010 [US1] Create instrumentation test to verify black background on startup in `app/src/androidTest/java/com/example/hmi/dashboard/ThemeVerificationTest.kt`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Widget Visual Consistency (Priority: P2)

**Goal**: Widgets have consistent black text and restricted color options (no black background for widgets).

**Independent Test**: Widgets show black text; color picker does not offer black for widget backgrounds.

### Implementation for User Story 2

- [x] T011 [P] [US2] Update `ButtonWidget` to hardcode text color to black in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [x] T012 [P] [US2] Update `SliderWidget` to hardcode text color to black in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T013 [P] [US2] Update `GaugeWidget` to hardcode text color to black in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [x] T014 [US2] Update `DashboardSettingsDialog` to use restricted `HmiPalette` for canvas color if needed, or enforce black default in `app/src/main/java/com/example/hmi/dashboard/DashboardSettingsDialog.kt`
- [x] T015 [US2] Update `WidgetConfigDialog` (in `DashboardScreen.kt`) to exclude black from the color picker and use `HmiPalette` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T016 [US2] Create instrumentation test to verify black text and restricted color picker in `app/src/androidTest/java/com/example/hmi/widgets/WidgetVisualTest.kt`

**Checkpoint**: User Story 2 is functional and testable independently.

---

## Phase 5: User Story 3 - Enhanced Typography and Font Scaling (Priority: P3)

**Goal**: Use clear cockpit-style fonts and allow per-widget font size adjustment.

**Independent Test**: Typography is clear (Roboto); font size slider in edit dialog scales widget text.

### Implementation for User Story 3

- [x] T017 [US3] Configure `HmiTheme` typography to use Roboto with cockpit-style adjustments in `app/src/main/java/com/example/hmi/ui/theme/HmiTheme.kt`
- [x] T018 [US3] Add "Font Size" slider (0.5x - 2.5x) to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T019 [P] [US3] Update `ButtonWidget` to apply `fontSizeMultiplier` in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [x] T020 [P] [US3] Update `SliderWidget` to apply `fontSizeMultiplier` in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T021 [P] [US3] Update `GaugeWidget` to apply `fontSizeMultiplier` in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [x] T022 [US3] Create instrumentation test for font scaling behavior in `app/src/androidTest/java/com/example/hmi/widgets/FontScalingTest.kt`

**Checkpoint**: User Story 3 is functional and testable independently.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and documentation

- [x] T023 [P] Update `README.md` with new UI theme and font scaling instructions
- [x] T024 [P] Verify accessibility (touch targets 48x48dp) for new font size slider
- [x] T025 [P] Audit all widgets and interactive elements for appropriate content descriptions (A11Y-002)
- [x] T026 [P] Final code cleanup and refactoring across changed files
- [x] T027 Run `quickstart.md` validation steps manually

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational phase completion.
  - US1 (P1) is the MVP and should be completed first.
  - US2 (P2) and US3 (P3) depend on foundation but can be worked on in parallel once foundation is ready.

### User Story Dependencies

- **User Story 1 (P1)**: Independent of US2/US3.
- **User Story 2 (P2)**: Independent of US1 UI but requires migration logic from foundation.
- **User Story 3 (P3)**: Independent of US2 UI but requires the new theme from foundation.

### Parallel Opportunities

- T001, T002, T003 can run in parallel.
- T011, T012, T013 (Widget text updates) can run in parallel.
- T019, T020, T021 (Widget font scaling) can run in parallel.
- US2 and US3 implementation can overlap once foundation (T004-T007) is done.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (Setup).
2. Complete Phase 2 (Foundation).
3. Complete Phase 3 (US1 - Default Dark Aesthetics).
4. **STOP and VALIDATE**: Verify the app opens with a black background and clean theme.

### Incremental Delivery

1. Foundation ready.
2. Add Default Dark Theme (MVP!).
3. Add Black Text & Restricted Palette.
4. Add Cockpit Typography & Font Scaling.
5. Final Polish.
