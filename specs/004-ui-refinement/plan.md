# Implementation Plan: UI Refinement

**Branch**: `004-ui-refinement` | **Date**: 2026-03-12 | **Spec**: [specs/004-ui-refinement/spec.md]
**Input**: Feature specification from `/specs/004-ui-refinement/spec.md`

## Summary

The UI Refinement feature implements a DPI-aware fixed-cell grid system (80dp cells) for the HMI dashboard. All widgets (Buttons, Sliders, Gauges) will be wrapped in a consistent square-edged "Widget Container" with a 1dp contrasting border and user-selectable background colors. The system will support drag-and-drop movement and resizing in grid units, with all layout changes persisted to DataStore.

## Technical Context

**Language/Version**: Kotlin (Latest stable)
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences) storing JSON via GSON.
**Testing**: JUnit 4 (Unit tests), Compose UI Test (Instrumented tests)
**Target Platform**: Android
**Project Type**: mobile-app
**Performance Goals**: 60 fps during drag and resize interactions.
**Constraints**: Minimum 48x48dp touch targets (enforced by 80dp grid).
**Scale/Scope**: Dashboard layout supporting dynamic grid based on device screen size.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Does the plan utilize Jetpack Compose for UI? (Yes, using Box and custom Modifiers for grid).
- [x] **Unidirectional Data Flow**: Is state managed in ViewModels with events flowing up? (Yes, DashboardViewModel will handle grid state).
- [x] **Test-First**: Are testing strategies (Unit, UI, Screenshot) explicitly defined? (Yes, unit tests for grid math and UI tests for interactions).
- [x] **Accessibility**: Are accessibility requirements included (touch targets, dynamic text, content descriptions)? (Yes, 80dp grid ensures targets > 48dp).
- [x] **Modular Architecture**: Which module(s) will this feature live in or create? (Lives in the `app` module).

## Project Structure

### Documentation (this feature)

```text
specs/004-ui-refinement/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (N/A for this internal UI feature)
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── dashboard/
│   ├── GridSystem.kt (New: Grid calculation logic)
│   ├── WidgetContainer.kt (New: Shared container UI)
│   ├── DashboardScreen.kt (Updated: Grid layout implementation)
│   └── DashboardViewModel.kt (Updated: Grid state management)
├── data/
│   └── WidgetConfiguration.kt (Updated: Grid coordinates)
└── widgets/
    ├── ButtonWidget.kt (Updated: Simplified to remove local container)
    ├── SliderWidget.kt (Updated: Simplified)
    └── GaugeWidget.kt (Updated: Simplified)
```

**Structure Decision**: Single project (Standard Android app structure).

## Complexity Tracking

*No violations.*
