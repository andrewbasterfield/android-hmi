# Tasks: Vertical Slider Variant (029)

**Input**: Design documents from `/specs/029-vertical-slider/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Update common data models and types used across all stories.

- [x] T001 [P] Define `WidgetOrientation` enum in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T002 Update `WidgetConfiguration` data class to include `orientation` field in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Ensure persistence and basic handling of the new orientation field.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T003 [P] Add unit test for `WidgetConfiguration` serialization with orientation in `app/src/test/java/com/example/hmi/data/WidgetConfigurationTest.kt`
- [x] T004 Verify GSON handles the new `orientation` field correctly during layout loading in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Vertical Control Configuration (Priority: P1) 🎯 MVP

**Goal**: Allow users to select "Vertical" orientation and automatically swap dimensions.

**Independent Test**: Open `WidgetConfigDialog` for a slider, toggle to "Vertical", and verify `colSpan` and `rowSpan` values swap immediately in the UI.

- [x] T005 [P] [US1] Add Orientation toggle to `WidgetConfigDialog` in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T006 [US1] Implement dimension swapping logic (colSpan <-> rowSpan) in `WidgetConfigDialog` when orientation changes in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`
- [x] T007 [US1] Update `DashboardScreen.kt` to pass `widget.orientation` from the layout configuration to the `SliderWidget` call
- [x] T008 [US1] Verify Accessibility & UI Standards (48dp touch target for toggle) in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: User Story 1 functional (Configurable Orientation).

---

## Phase 4: User Story 2 - Vertical Interaction (Priority: P1)

**Goal**: Implement the custom vertical slider logic and upward value increase.

**Independent Test**: Add a vertical slider to the dashboard and drag the thumb upwards; verify the bound PLC tag value increases.

- [x] T009 [US2] Implement `VerticalSlider` custom component in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt` using `Modifier.draggable`
- [x] T010 [US2] Implement vertical track (8dp wide) and thumb (32dp x 24dp) rendering with "Stitch" aesthetics in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T011 [US2] Map upward drag gestures to value increase in `VerticalSlider` in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T012 [P] [US2] Add interaction test for vertical slider value updates in `app/src/androidTest/java/com/example/hmi/widgets/WidgetTest.kt`

**Checkpoint**: User Story 2 functional (Interactive Vertical Slider).

---

## Phase 5: User Story 3 - Responsive Labeling (Priority: P2)

**Goal**: Position labels and metrics correctly for the vertical slider variant.

**Independent Test**: Render a vertical slider and verify the label is at the top, track in the center, and metric at the bottom, all upright.

- [x] T013 [US3] Implement vertical layout stack (Label-Track-Metric) for vertical orientation in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T014 [US3] Implement upright Min/Max label positioning at the bottom and top of the vertical track in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T015 [US3] Verify "Clarity by Design" (no overlapping text), content descriptions (A11Y-002), and dynamic text support (A11Y-003) for vertical sliders across different font sizes in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`

**Checkpoint**: User Story 3 functional (Responsive Vertical Layout).

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and documentation.

- [x] T016 [P] Run `quickstart.md` validation on a physical device or emulator
- [x] T017 [P] Verify cross-page boundary rendering for vertical sliders in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T018 Final code cleanup, documentation update in `DEVELOPMENT_OVERVIEW.md`, and final audit for Low Cognitive Load (UI-002) and No Gimmicks (UI-003) principles.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational phase completion.
- **User Story 2 (Phase 4)**: Depends on User Story 1 (needs selectable orientation).
- **User Story 3 (Phase 5)**: Depends on User Story 2 (needs vertical slider structure).
- **Polish (Final Phase)**: Depends on all desired user stories being complete.

### Implementation Strategy

- **MVP First**: User Story 1 (Configuration) and User Story 2 (Interaction) form the core MVP.
- **Incremental Delivery**: Each story adds a layer of refinement (Config -> Interaction -> Layout).

### Parallel Opportunities

- T001 and T002 are closely related but T001 defines the type T002 uses.
- T003 can be written in parallel with T002.
- T012 can be written in parallel with T009-T011.
- T016 and T017 can run in parallel.
