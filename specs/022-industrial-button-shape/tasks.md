# Tasks: Industrial Component Shape Refinement

**Input**: Design documents from `/specs/022-industrial-button-shape/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Tests**: Tests are included as the spec requires "Independent Test" scenarios and placeholder tests in `IndustrialComponentTest.kt` must be implemented.

**Organization**: Tasks are grouped by component/user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and validation of the theme configuration.

- [X] T001 Verify `MaterialTheme.shapes.small` is defined as `RoundedCornerShape(2.dp)` in `core/ui/src/main/java/com/example/hmi/core/ui/theme/Shape.kt`
- [X] T002 [P] Confirm `StitchTheme` correctly applies `Shapes` in `core/ui/src/main/java/com/example/hmi/core/ui/theme/StitchTheme.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core testing infrastructure setup.

- [X] T003 Setup `IndustrialComponentTest.kt` with necessary imports and `createComposeRule` in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/IndustrialComponentTest.kt`

**Checkpoint**: Testing infrastructure ready.

---

## Phase 3: User Story 1 - Tactile Hardware Aesthetic (Priority: P1) 🎯 MVP

**Goal**: Refactor `IndustrialButton` and `IndustrialInput` to use 2dp rounded corners.

**Independent Test**: Visually verify 2dp corners on buttons and inputs; run instrumented tests for height and shape.

### Tests for User Story 1

- [X] T004 [P] [US1] Implement `industrialButton_hasMinHeight64dp` and verify it fails (if not already 64dp) or passes in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/IndustrialComponentTest.kt`
- [X] T005 [P] [US1] Implement `industrialInput_hasMinHeight64dp` in `core/ui/src/androidTest/java/com/example/hmi/core/ui/components/IndustrialComponentTest.kt`
- [X] T006 [US1] Create `industrialButton_usesSmallShape` test case to verify `MaterialTheme.shapes.small` is applied to the button Surface in `IndustrialComponentTest.kt`
- [X] T007 [US1] Create `industrialInput_usesSmallShape` test case to verify `MaterialTheme.shapes.small` is applied to the input background in `IndustrialComponentTest.kt`

### Implementation for User Story 1

- [X] T008 [US1] Refactor `IndustrialButton` to use `MaterialTheme.shapes.small` instead of `RectangleShape` for its `Surface` shape in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialComponents.kt`
- [X] T009 [US1] Refactor `IndustrialInput` to apply `MaterialTheme.shapes.small` to its background modifier in `core/ui/src/main/java/com/example/hmi/core/ui/components/IndustrialComponents.kt`
- [X] T010 [US1] Verify that `IndustrialButton` color inversion (Pressed state) correctly maintains the 2dp corner radius in `IndustrialComponents.kt`
- [X] T011 [US1] Ensure `IndustrialInput` bottom-border "shelf" (4px) is correctly aligned with the 2dp rounded corners in `IndustrialComponents.kt`

**Checkpoint**: `IndustrialButton` and `IndustrialInput` now have 2dp corners and meet minimum height requirements.

---

## Phase 4: User Story 2 - Visual Fatigue Reduction (Priority: P2)

**Goal**: Ensure high-contrast elements do not exhibit "stair-step" aliasing.

**Independent Test**: Compare 0dp vs 2dp corners on high-contrast obsidian background.

### Implementation for User Story 2

- [X] T012 [US2] Audit `IndustrialButton` border implementation to ensure it uses the same `MaterialTheme.shapes.small` to prevent overlapping or sharp internal corners in `IndustrialComponents.kt`
- [X] T013 [US2] Verify `IndustrialInput` background color (`surfaceContainerHighest`) correctly fills the 2dp rounded area in `IndustrialComponents.kt`

**Checkpoint**: Components are visually optimized for high-contrast industrial environments.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and documentation.

- [X] T014 [P] Run all tests in `core/ui` module: `./gradlew :core:ui:connectedDebugAndroidTest`
- [X] T015 Perform manual visual audit on `DashboardScreen.kt` to ensure `ButtonWidget` and `WidgetContainer` bezels look consistent with the new 2dp button corners.
- [X] T016 Update `quickstart.md` with final verification results in `specs/022-industrial-button-shape/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Phase 1.
- **User Story 1 (Phase 3)**: Depends on Phase 2.
- **User Story 2 (Phase 4)**: Depends on User Story 1.
- **Polish (Phase 5)**: Depends on all previous phases.

### Parallel Opportunities

- T001 and T002 can run in parallel.
- T004 and T005 can run in parallel.
- T014 and T016 can run in parallel.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup and Foundational phases.
2. Implement User Story 1 refactors and tests.
3. Validate with instrumented tests.

### Incremental Delivery

1. Foundation ready (Testing setup).
2. Refactor Button → Test.
3. Refactor Input → Test.
4. Visual Polish Audit.
