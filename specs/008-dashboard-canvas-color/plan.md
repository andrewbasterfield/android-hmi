# Implementation Plan: Dashboard Canvas Color

**Branch**: `008-dashboard-canvas-color` | **Date**: 2026-03-12 | **Spec**: [specs/008-dashboard-canvas-color/spec.md]
**Input**: Feature specification from `/specs/008-dashboard-canvas-color/spec.md`

## Summary

This feature allows users to customize the background color of the HMI dashboard canvas. It involves updating the `DashboardLayout` data model to store a `canvasColor`, providing a "Dashboard Settings" dialog in Edit Mode for color selection (using the existing `ColorPalette`), and ensuring the selected color is persisted and applied to the dashboard's root container.

## Technical Context

**Language/Version**: Kotlin (Latest stable)
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences) storing JSON via GSON.
**Testing**: JUnit 4, Compose UI Test
**Target Platform**: Android
**Project Type**: mobile-app
**Performance Goals**: 60 fps; instantaneous background color updates.
**Constraints**: Must use existing `ColorPalette` and maintain contrast with widgets.
**Scale/Scope**: Impacts `DashboardLayout`, `DashboardViewModel`, `DashboardScreen`, and `DashboardRepository`.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Does the plan utilize Jetpack Compose for UI? (Yes, the dashboard background and settings dialog are Compose-based).
- [x] **Unidirectional Data Flow**: Is state managed in ViewModels with events flowing up? (Yes, `DashboardViewModel` will hold the canvas color state).
- [x] **Test-First**: Are testing strategies (Unit, UI, Screenshot) explicitly defined? (Yes, unit tests for layout updates and UI tests for color application).
- [x] **Accessibility**: Are accessibility requirements included (touch targets, dynamic text, content descriptions)? (Yes, specified in FR-002/A11Y-002).
- [x] **Modular Architecture**: Which module(s) will this feature live in or create? (Main `app` module).

## Project Structure

### Documentation (this feature)

```text
specs/008-dashboard-canvas-color/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── dashboard/
│   ├── DashboardScreen.kt (Updated: Apply canvas background)
│   ├── DashboardViewModel.kt (Updated: Handle canvas color updates)
│   └── DashboardSettingsDialog.kt (New: Color selection for canvas)
├── data/
│   ├── DashboardLayout.kt (Updated: Add canvasColor field)
│   └── DashboardRepository.kt (Updated: Handle persistence of updated layout)
```

**Structure Decision**: Single project (Standard Android app structure).

## Complexity Tracking

*No violations.*
