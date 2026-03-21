# Implementation Plan: Gauge Units Support

**Branch**: `020-gauge-units` | **Date**: 2026-03-21 | **Spec**: [/specs/020-gauge-units/spec.md](../spec.md)
**Input**: Feature specification from `/specs/020-gauge-units/spec.md`

## Summary

This feature adds support for displaying measurement units next to the Gauge numeric readout. It involves updating the `WidgetConfiguration` data model to persist a nullable `units` string, updating the `WidgetConfigDialog` to allow user input, and modifying the `GaugeWidget` rendering logic to append the units to the value string using a refined typography hierarchy.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore (Preferences), GSON  
**Storage**: Jetpack DataStore (GSON serialization)  
**Target Platform**: Android (Modern Industrial HMI)  
**Scale/Scope**: `app` module (Dashboard, Data, Widgets).

## Constitution Check

- [x] **Compose-First**: `GaugeWidget` is a `@Composable` component.
- [x] **Unidirectional Data Flow**: State managed via `DashboardViewModel` -> `WidgetConfiguration`.
- [x] **Test-First**: Unit tests for serialization and UI tests for unit rendering are planned.
- [x] **Clarity by Design**: Measurement units provide critical industrial context.
- [x] **No Gimmicks**: Units serve a direct functional purpose for data interpretation.

## Project Structure

### Documentation

```text
specs/020-gauge-units/
├── plan.md              # This file
├── data-model.md        # Updated fields
└── tasks.md             # Execution steps
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── dashboard/
│   └── WidgetPalette.kt      # Configuration UI
├── data/
│   └── WidgetConfiguration.kt # Data model update
└── widgets/
    └── GaugeWidget.kt        # Rendering logic
```

## Complexity Tracking

*No complexity violations identified.*
