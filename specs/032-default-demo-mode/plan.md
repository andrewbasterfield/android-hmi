# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Add a default demo mode that displays a built-in dashboard with simulated local data when the application is launched without an existing layout configuration.

## Technical Context

**Language/Version**: Kotlin (Android)
**Primary Dependencies**: Jetpack Compose, ViewModels (AAC)
**Storage**: DataStore (via `DashboardRepository`)
**Testing**: JUnit, Compose UI tests
**Target Platform**: Android
**Project Type**: Mobile Application
**Performance Goals**: Loading demo mode takes <1 second.
**Constraints**: Must not save demo layout to persistent storage automatically; operates in memory.
**Scale/Scope**: Impacts dashboard loading logic and widget configuration (adds `TEXT` widget type).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Does the plan utilize Jetpack Compose for UI? (Yes, `DashboardScreen` is in Compose)
- [x] **Unidirectional Data Flow**: Is state managed in ViewModels with events flowing up? (Yes, via `DashboardViewModel`)
- [x] **Test-First**: Are testing strategies (Unit, UI, Screenshot) explicitly defined? (Yes, unit tests for ViewModel loading logic)
- [x] **Accessibility**: Are accessibility requirements included (touch targets, dynamic text, content descriptions)? (Yes)
- [x] **Clarity by Design**: Does the UI provide high contrast and clear typography? (Yes)
- [x] **Low Cognitive Load**: Is information prioritized with progressive disclosure? (Yes, onboarding text provided)
- [x] **No Gimmicks**: Are all UI elements and animations functional rather than decorative? (Yes, simulated data used functionally)
- [x] **Modular Architecture**: Which module(s) will this feature live in or create? (Modifies `app` and `core/protocol` modules)

## Project Structure

### Documentation (this feature)

```text
specs/032-default-demo-mode/
├── plan.md              
├── research.md          
├── data-model.md        
├── quickstart.md        
└── tasks.md             
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── dashboard/
│   ├── DashboardViewModel.kt
│   └── DashboardScreen.kt
└── data/
    ├── DashboardRepository.kt
    ├── DashboardLayout.kt
    └── WidgetConfiguration.kt

core/protocol/src/main/java/com/example/hmi/protocol/
└── DemoPlcServer.kt
```

**Structure Decision**: Logic will reside within the existing single-app module (`app/`), integrating with existing data structures and view models. No new feature modules are required.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
