# Implementation Plan: Widget Duplication

**Branch**: `028-duplicate-widget` | **Date**: 2026-03-28 | **Spec**: [/specs/028-duplicate-widget/spec.md](spec.md)
**Input**: Feature specification from `/specs/028-duplicate-widget/spec.md`

## Summary

The objective is to implement a "Duplicate" action for single widgets within the dashboard's Edit Mode. The implementation will involve adding a "Duplicate" button to the `WidgetConfigDialog`, which, when triggered, will create a new `WidgetConfiguration` instance. This new instance will inherit all properties from the source widget (labels, data sources, styling) but will be assigned a new unique ID (UUID) and the highest Z-Order in the current layout. The duplicated widget will be positioned with a (+1, +1) grid offset and immediately persisted to the `DashboardLayout` via `DashboardViewModel`.

## Technical Context

**Language/Version**: Kotlin 1.9.22
**Primary Dependencies**: Jetpack Compose, Hilt, Kotlin Coroutines (StateFlow), Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences storing JSON via GSON)
**Testing**: JUnit 4 (Unit), Compose Test Library (UI)
**Target Platform**: Android
**Project Type**: Mobile App
**Performance Goals**: Duplication processing and rendering in under 200ms
**Constraints**: Must maintain (+1, +1) offset even if it causes canvas overflow (handled by existing mechanics)
**Scale/Scope**: Single widget duplication per action; supports all widget types (BUTTON, SLIDER, GAUGE)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Implementation uses Jetpack Compose for the "Duplicate" button and layout updates.
- [x] **Unidirectional Data Flow**: State updates flow from `DashboardViewModel` to `DashboardLayout` StateFlow.
- [x] **Test-First**: Unit tests will be added to `DashboardViewModelTest` to verify duplication logic.
- [x] **Accessibility**: "Duplicate" button will adhere to 48dp touch targets and provide screen reader feedback.
- [x] **Clarity by Design**: The (+1, +1) offset ensures the user can clearly see the new widget and its relation to the source.
- [x] **Low Cognitive Load**: No extra visual feedback (flashes/toasts) is used; the appearance of the new widget is sufficient.
- [x] **No Gimmicks**: Every UI change (offset, Z-Order) serves a functional purpose for usability.
- [x] **Modular Architecture**: Feature is integrated into the existing `dashboard` and `data` packages within the `app` module.

## Project Structure

### Documentation (this feature)

```text
specs/028-duplicate-widget/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── data/
│   ├── WidgetConfiguration.kt    # ID and positioning data class
│   ├── DashboardLayout.kt        # Aggregate layout structure
│   └── DashboardRepository.kt    # Persistence management
├── dashboard/
│   ├── DashboardViewModel.kt     # Duplication and state management
│   ├── DashboardScreen.kt        # Edit mode UI and navigation
│   └── WidgetPalette.kt          # WidgetConfigDialog implementation
```

**Structure Decision**: Integration within the `app` module's existing `dashboard` and `data` packages.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
