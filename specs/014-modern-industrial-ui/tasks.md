# Tasks: Modern Industrial UI

**Input**: Design documents from `specs/014-modern-industrial-ui/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 [P] Verify project structure for theme and shape integration
- [X] T002 [P] Create `app/src/main/java/com/example/hmi/ui/theme/Shape.kt` for corner radius constants
- [X] T003 [P] Define `IndustrialShape` constants (8dp, 4dp) in `app/src/main/java/com/example/hmi/ui/theme/Shape.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T004 [P] Implement `WidgetShapes.getShapeForSize(colSpan, rowSpan)` in `app/src/main/java/com/example/hmi/ui/theme/Shape.kt`
- [X] T005 [P] Implement `ColorUtils.getIndustrialContrastColor(backgroundColor)` in `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt` (using 0.2 threshold with vibrant priority for Cherry Red #D2042D)
- [X] T006 Implement unit tests for `getShapeForSize` in `app/src/test/java/com/example/hmi/ui/theme/ShapeLogicTest.kt`
- [X] T007 Implement unit tests for `getIndustrialContrastColor` in `app/src/test/java/com/example/hmi/widgets/IndustrialContrastTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Rounded Interactive Elements (Priority: P1) 🎯 MVP

**Goal**: Widgets and dialogs exhibit rounded corners (8dp standard, 4dp for 1x1).

**Independent Test**: Verify that all widgets on the dashboard and all dialogs have visible rounded corners.

### Tests for User Story 1

- [X] T008 [US1] Create instrumentation test to verify rounded corner presence in `app/src/androidTest/java/com/example/hmi/dashboard/IndustrialUiTest.kt`

### Implementation for User Story 1

- [X] T009 [US1] Update `WidgetContainer` to apply `Modifier.clip` with adaptive shape in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [X] T010 [P] [US1] Update `ButtonWidget` to use `RoundedCornerShape(8.dp)` and ensure 48dp touch target in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [X] T011 [US1] Update DashboardSettingsDialog, WidgetPalette, and AddWidgetDialog to use rounded shapes and verify content descriptions in `app/src/main/java/com/example/hmi/dashboard/`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - High-Contrast Black Typography (Priority: P2)

**Goal**: Use black text for vibrant backgrounds, falling back to white only for very dark colors (L < 0.2).

**Independent Test**: Set a widget to Cherry Red and verify text is Black. Set it to Navy Blue and verify text is White.

### Tests for User Story 2

- [X] T012 [US2] Create instrumentation test for hybrid contrast behavior in `app/src/androidTest/java/com/example/hmi/widgets/HybridContrastTest.kt`

### Implementation for User Story 2

- [X] T013 [P] [US2] Update `ButtonWidget` to use `ColorUtils.getIndustrialContrastColor` in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [X] T014 [P] [US2] Update `SliderWidget` to use `ColorUtils.getIndustrialContrastColor` in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [X] T015 [P] [US2] Update `GaugeWidget` to use `ColorUtils.getIndustrialContrastColor` in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`

**Checkpoint**: User Story 2 is functional and testable independently.

---

## Phase 5: User Story 3 - Visual Depth & Border Polish (Priority: P3)

**Goal**: Subtle borders that follow the rounded corner path.

**Independent Test**: Verify that borders on widgets align perfectly with the new rounded corners.

### Implementation for User Story 3

- [X] T016 [US3] Update `WidgetContainer` border implementation to use the adaptive shape in `app/src/main/java/com/example/hmi/dashboard/WidgetContainer.kt`
- [X] T017 [US3] Ensure `ButtonWidget` border (if any) uses the new 8dp shape in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final refinements and documentation

- [X] T018 [P] Update `README.md` with "Modern Industrial UI" aesthetic details
- [X] T019 [P] Final audit of touch targets and content descriptions on rounded elements
- [X] T020 [P] Verify dynamic text scaling (A11Y-003) for rounded widgets in `app/src/androidTest/java/com/example/hmi/dashboard/IndustrialUiTest.kt`
- [X] T021 Run `quickstart.md` validation steps manually

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundation. US1, US2, and US3 can proceed in parallel once Foundation is complete.
- **Polish (Final Phase)**: Depends on completion of all stories.

### Parallel Opportunities

- T001, T002, T003 can run in parallel.
- T010 and T011 (rounded buttons/dialogs) can run in parallel.
- T013, T014, T015 (widget contrast updates) can run in parallel.
- US1 and US2 can be implemented simultaneously.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (Setup).
2. Complete Phase 2 (Foundation).
3. Complete Phase 3 (US1 - Rounded Interactive Elements).
4. **STOP and VALIDATE**: Verify the "Modern Industrial" shapes are correctly applied.

### Incremental Delivery

1. Foundation ready.
2. Add Rounded Corners (MVP!).
3. Add Hybrid Contrast (Black Text focus).
4. Add Border Polish.
5. Final Polish.
