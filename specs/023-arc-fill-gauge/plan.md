# Implementation Plan: Arc-Filling Gauge Support

**Branch**: `023-arc-fill-gauge` | **Date**: 2026-03-23 | **Spec**: [/specs/023-arc-fill-gauge/spec.md](spec.md)
**Input**: Feature specification from `/specs/023-arc-fill-gauge/spec.md`

## Summary
Add support for an "Arc Fill" gauge style where the arc completes as the reading rises, alongside the existing "Pointer" style. This involves extending the `WidgetConfiguration` data model, updating the `GaugeWidget` rendering logic to support both styles, and adding a style selector to the configuration UI.

## Technical Context

**Language/Version**: Kotlin 1.9.22
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences storing JSON via GSON)
**Testing**: JUnit 4, Compose Test Rule (UI Tests)
**Target Platform**: Android (Industrial HMI)
**Project Type**: Mobile App
**Performance Goals**: 60 fps rendering for smooth gauge animations
**Constraints**: Must adhere to "Clarity by Design" (high contrast)
**Scale/Scope**: Update `GaugeWidget.kt`, `WidgetConfiguration.kt`, `WidgetPalette.kt`, and `DashboardScreen.kt`.

## Constitution Check

- [X] **Compose-First**: Utilizing Jetpack Compose for the new gauge rendering logic.
- [X] **Unidirectional Data Flow**: State managed via `DashboardViewModel` and passed down to `GaugeWidget`.
- [X] **Test-First**: Implementation includes UI tests for both styles and unit tests for configuration serialization.
- [X] **Accessibility**: Maintaining content descriptions and touch targets (48dp).
- [X] **Clarity by Design**: The "Arc Fill" style provides a clear, high-contrast alternative to the pointer.
- [X] **Low Cognitive Load**: Style selection is logically grouped in the Gauge configuration section.
- [X] **No Gimmicks**: Both styles serve functional monitoring purposes.
- [X] **Modular Architecture**: Feature resides in the existing `app` module, following established patterns.

## Project Structure

### Documentation (this feature)

```text
specs/023-arc-fill-gauge/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── data/
│   └── WidgetConfiguration.kt    # Update for GaugeStyle enum
├── widgets/
│   └── GaugeWidget.kt           # Add Arc Fill rendering logic
├── dashboard/
│   ├── DashboardScreen.kt       # Pass style to widget
│   └── WidgetPalette.kt         # Add style selector to dialog
└── ui/
    └── theme/                   # (Reference existing theme)

app/src/androidTest/java/com/example/hmi/
└── widgets/
    └── GaugeStyleTest.kt        # New UI tests for styles
```

**Structure Decision**: Integrated into the existing `app` module following the project's feature-by-layer structure.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | | |
