# Tasks: Colored Buttons

**Input**: Design documents from `/specs/003-colored-buttons/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are OPTIONAL. For this feature, we will focus on manual verification and UI consistency as per the spec.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Feature-specific initialization

- [X] T001 [P] Create `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt` for luminance-based contrast logic

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T002 Define `ColorPalette` object in `app/src/main/java/com/example/hmi/data/ColorPalette.kt` per `contracts/ColorPalette.kt`
- [X] T003 Update `WidgetConfiguration` data class in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` to include `backgroundColor: Long? = null`
- [X] T004 Implement `getContrastColor(backgroundColor: Color): Color` in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Customize Button Color (Priority: P1) 🎯 MVP

**Goal**: Allow users to select a color when adding a button and see it reflected in the UI with automatic text contrast.

**Independent Test**: Add a button in Edit Mode, select "Green", and verify the button is green in both Edit and Run modes. Verify text is black on Yellow and white on Red.

### Implementation for User Story 1

- [X] T005 [P] [US1] Update `ButtonWidget` to accept `backgroundColor: Long?` and apply it to the `Button` colors in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [X] T006 [US1] Use `ColorUtils.getContrastColor` in `ButtonWidget` to set the `contentColor` (text/icon color) in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [X] T007 [US1] Create a `ColorPicker` composable in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt` that displays the `ColorPalette` items
- [X] T008 [US1] Update `AddWidgetDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt` to include the `ColorPicker` and pass the selected color to the `onConfirm` callback
- [X] T009 [US1] Update `DashboardScreen` in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` to pass `widget.backgroundColor` to the `ButtonWidget` call
- [X] T010 [US1] Verify that selecting a color in the dialog correctly sets the `backgroundColor` in the new `WidgetConfiguration` within `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently (minus persistence if not already handled by DataStore auto-serialization).

---

## Phase 4: User Story 2 - Persistent Button Colors (Priority: P2)

**Goal**: Ensure selected colors are saved and restored across application restarts.

**Independent Test**: Set a button to "Blue", close the app, reopen it, and verify the button is still Blue.

### Implementation for User Story 2

- [X] T011 [US2] Verify that `DashboardRepository` (if using Protobuf) or the DataStore mapping correctly includes the new `backgroundColor` field in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [X] T012 [US2] Perform a manual end-to-end test: Add colored button -> Restart App -> Verify Color retention

**Checkpoint**: User Stories 1 AND 2 are now complete and persistent.

---

## Phase 5: User Story 3 - Default Button Color (Priority: P3)

**Goal**: New buttons use the system primary color by default.

**Independent Test**: Add a new button without selecting a specific color. Verify it uses the default theme color.

### Implementation for User Story 3

- [X] T013 [US3] Ensure the "Default" option in `ColorPicker` results in a `null` value for `backgroundColor` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T014 [US3] Confirm `ButtonWidget` defaults to `ButtonDefaults.buttonColors()` when `backgroundColor` is `null` in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`

**Checkpoint**: All user stories are now independently functional and consistent with the spec.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T015 [P] Verify touch target sizes for the new `ColorPicker` swatches (minimum 48x48dp) in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T016 [P] Ensure accessibility content descriptions are added for color swatches in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [X] T017 Run a final check against `specs/003-colored-buttons/quickstart.md` to ensure the user flow is accurate
- [X] T018 [P] [US1] Create unit tests for `ColorUtils.getContrastColor` in `app/src/test/java/com/example/hmi/widgets/ColorUtilsTest.kt`
- [ ] T019 [US1] Create UI tests for `ButtonWidget` color application in `app/src/androidTest/java/com/example/hmi/widgets/ButtonWidgetTest.kt`
- [ ] T020 [US1] Create UI tests for `ColorPicker` selection in `app/src/androidTest/java/com/example/hmi/dashboard/ColorPickerTest.kt`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** & **Foundational (Phase 2)**: MUST be complete before starting User Story 1.
- **User Story 1 (Phase 3)**: The MVP - provides the core UI and logic.
- **User Story 2 (Phase 4)**: Depends on US1 being implemented to test persistence.
- **User Story 3 (Phase 5)**: Can be verified alongside US1.

### Parallel Opportunities

- T001 and T002 can be done in parallel.
- Once T005 and T006 are done (ButtonWidget update), work on the Dialog (T007, T008) can proceed.
- Polish tasks T015 and T016 can be done in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Implement the foundation (ColorPalette and Data Model).
2. Update the `ButtonWidget` to support colors.
3. Update the `AddWidgetDialog` to allow color selection.
4. Verify the color appears on the dashboard.

### Incremental Delivery

- Foundation -> MVP UI -> Persistence -> Default Behavior -> Polish.

---

## Notes

- [P] tasks = different files, no dependencies.
- [Story] label maps task to specific user story for traceability.
- Automatic text contrast is handled centrally in `ColorUtils` and applied in `ButtonWidget`.
