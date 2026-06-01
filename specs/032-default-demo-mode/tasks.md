# Implementation Tasks: Default Demo Mode

**Feature**: Default Demo Mode
**Spec**: [spec.md](./spec.md)
**Plan**: [plan.md](./plan.md)

## Phase 1: Setup

- [x] T001 Define `TEXT` widget type in `WidgetType` enum located in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`

## Phase 2: Foundational

- [x] T002 Implement `createDemoLayout()` factory function inside `app/src/main/java/com/example/hmi/data/DashboardLayout.kt` (or as an extension) representing the 4 configured widgets (Text, Temp Gauge, Pressure Gauge, Status Button).

## Phase 3: Out-of-the-box Experience [US1]

**Goal**: Load the demo layout automatically when no valid configuration is found upon startup.

**Independent Test**: Perform a clean launch; verify the dashboard displays the Demo Layout instead of an empty screen.

- [x] T004 [US1] In `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`, modify `dashboardLayoutFlow`. If the stored JSON is null or empty, emit the output of `createDemoLayout()` instead of a blank `DashboardLayout()`.
- [x] T005 [US1] Update UI to support rendering the new `TEXT` widget type in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`.

## Final Phase: Polish & Cross-Cutting

- [x] T006 Verify UI styling of the `TEXT` widget complies with Clarity by Design principles (typography and contrast).
- [x] T007 Ensure the `TEXT` widget meets A11Y requirements (dynamic text scaling and minimum touch targets if interactive).
- [x] T008 Run a performance trace on a clean install to verify the Demo Layout loads and renders in under 1 second (SC-002).
- [x] T009 [US2] Write unit tests for `DashboardRepository` (create `app/src/test/java/com/example/hmi/data/DashboardRepositoryTest.kt`) to verify it emits `DemoLayout` when empty (US1), and correctly emits user configurations when saved (US2).

## Dependencies

- Phase 2 depends on Phase 1
- Phase 3 depends on Phase 2
- Final Phase depends on Phase 3

## Implementation Strategy

1. **MVP (Phase 3)**: Implement the core fallback logic in the repository. New users will see a functional dashboard instead of an empty screen, and normal save behavior will apply naturally.