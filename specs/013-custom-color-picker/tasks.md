# Tasks: Custom Color Picker

**Input**: Design documents from `specs/013-custom-color-picker/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Tests**: Test tasks are included as requested in the implementation plan (Test-First approach).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create component directory `app/src/main/java/com/example/hmi/ui/components/`
- [x] T002 [P] Configure GSON for `Long` color serialization in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T003 Update `DashboardRepository` to include `recent_colors` DataStore key in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [x] T004 Implement contrast calculation and luminance logic in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt`
- [x] T005 [P] Implement unit tests for contrast logic in `app/src/test/java/com/example/hmi/widgets/ColorContrastTest.kt`
- [x] T006 [P] Implement unit tests for recent colors queue logic in `app/src/test/java/com/example/hmi/dashboard/RecentColorsTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Pick a Custom Hex Color (Priority: P1) 🎯 MVP

**Goal**: Users can enter a 6-digit hex code to apply a custom color with automatic contrast adjustment.

**Independent Test**: Enter "#FF0000" in the Hex tab and verify the widget turns red and text adjusts if needed.

### Tests for User Story 1

- [x] T007 [P] [US1] Create UI test for hex validation in `app/src/androidTest/java/com/example/hmi/ui/components/HexValidationTest.kt`

### Implementation for User Story 1

- [x] T008 [P] [US1] Create `HexEntryField` component with validation in `app/src/main/java/com/example/hmi/ui/components/HexEntryField.kt`
- [x] T009 [US1] Create `HmiColorPicker` container with "Palette" and "Hex" tabs in `app/src/main/java/com/example/hmi/ui/components/ColorPicker.kt`
- [x] T010 [US1] Integrate `HmiColorPicker` into `DashboardSettingsDialog.kt`
- [x] T011 [US1] Integrate `HmiColorPicker` into `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T012 [US1] Implement automatic text color toggle (Black/White) in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [x] T013 [US1] Implement "Clear/Reset" button logic in `app/src/main/java/com/example/hmi/ui/components/ColorPicker.kt`

**Checkpoint**: User Story 1 is fully functional and testable independently.

---

## Phase 4: User Story 2 - Visual Spectrum Selection (Priority: P2)

**Goal**: Users can select a color from a visual HSV spectrum/gradient.

**Independent Test**: Drag the spectrum selector and verify the preview color updates in real-time.

### Implementation for User Story 2

- [x] T014 [US2] Create `SpectrumPicker` using Compose Canvas in `app/src/main/java/com/example/hmi/ui/components/SpectrumPicker.kt`
- [x] T015 [US2] Add "Spectrum" tab to `HmiColorPicker` in `app/src/main/java/com/example/hmi/ui/components/ColorPicker.kt`
- [x] T016 [US2] Verify touch target (48x48dp) for spectrum handles in `app/src/main/java/com/example/hmi/ui/components/SpectrumPicker.kt`

**Checkpoint**: User Story 2 is fully functional and integrated with US1.

---

## Phase 5: User Story 3 - Recent/Favorite Custom Colors (Priority: P3)

**Goal**: Users see a persistent row of the 8 most recently used custom colors.

**Independent Test**: Pick a new custom color, close the dialog, reopen it, and verify the color appears in the "Recent" list.

### Implementation for User Story 3

- [x] T017 [US3] Implement `saveRecentColor(color)` logic in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T018 [US3] Create `RecentColorsRow` component in `app/src/main/java/com/example/hmi/ui/components/ColorPicker.kt`
- [x] T019 [US3] Integrate `RecentColorsRow` into all tabs of the color picker.
- [x] T020 [US3] Verify global history persistence across app restarts.

**Checkpoint**: All user stories are now independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and verification

- [x] T021 [P] Update `README.md` with custom color picker instructions
- [x] T022 [P] Audit content descriptions for all new picker components
- [x] T023 [P] Final code cleanup and refactoring
- [x] T024 Run `quickstart.md` validation steps manually

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1 completion - BLOCKS all user stories.
- **User Stories (Phase 3+)**: All depend on Phase 2 completion.
  - US1 (P1) is the MVP and MUST be completed first.
  - US2 (P2) and US3 (P3) can proceed in parallel once US1 core is ready.

### User Story Dependencies

- **User Story 1 (P1)**: Independent of US2/US3.
- **User Story 2 (P2)**: Integrates into the tabbed picker created in US1.
- **User Story 3 (P3)**: Depends on the color saving infrastructure but can be tested with any picked color.

### Parallel Opportunities

- T001, T002 (Setup)
- T005, T006 (Foundation Unit Tests)
- T007, T008 (US1 UI vs Testing)
- Phase 6 (Polish) tasks marked [P]

---

## Parallel Example: User Story 1

```bash
# Implement the input field and its test in parallel:
Task: "Create HexEntryField component with validation in app/src/main/java/com/example/hmi/ui/components/HexEntryField.kt"
Task: "Create UI test for hex validation in app/src/androidTest/java/com/example/hmi/ui/components/HexValidationTest.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup + Foundation.
2. Complete US1 (Hex Input & Tabs).
3. **STOP and VALIDATE**: Verify custom colors can be applied via Hex code.

### Incremental Delivery

1. MVP (Hex Input).
2. Add Visual Spectrum (US2).
3. Add Recent Colors History (US3).
4. Final Polish.
