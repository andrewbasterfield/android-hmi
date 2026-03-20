# Implementation Plan: Configurable Gauge Tick Density

**Branch**: `018-gauge-tick-config` | **Date**: 2026-03-20 | **Spec**: [/specs/018-gauge-tick-config/spec.md](/specs/018-gauge-tick-config/spec.md)
**Input**: Feature specification from `/specs/018-gauge-tick-config/spec.md`

## Summary
Enable user-configurable tick density for Gauge widgets. This involves updating the `WidgetConfiguration` data model to persist a `targetTicks` value (default 6, range 2-20), refactoring the scale generation logic in `ScaleUtils` to handle dynamic targets, and implementing a "Scale Assistance" UI in the widget configuration dialog for Gauges to provide real-time feedback on the resulting increments.

## Technical Context

**Language/Version**: Kotlin 1.9+  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON  
**Storage**: Jetpack DataStore (Preferences storing JSON via GSON)  
**Testing**: JUnit 4 (Unit tests for algorithm), Compose UI Tests (UI feedback)  
**Target Platform**: Android 10+
**Project Type**: Mobile App (Industrial HMI)  
**Performance Goals**: 60 fps UI updates during slider adjustment; <100ms layout recalculation  
**Constraints**: strictly 0dp corners, 2px structural bezels (Kinetic Cockpit standards)  
**Scale/Scope**: Affects Gauge widgets ONLY; persisted per-widget.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Implementation is strictly in Jetpack Compose.
- [x] **Unidirectional Data Flow**: Configuration changes flow through `DashboardViewModel` to `DataStore`.
- [x] **Test-First**: Unit tests for `ScaleUtils` required to verify "Nice Number" outcomes.
- [x] **Accessibility**: Minimum 48dp touch targets for configuration sliders; high-contrast labels.
- [x] **Clarity by Design**: "Scale Assistance" ensures operators understand the resulting increments.
- [x] **Low Cognitive Load**: Defaults to 6 ticks to prevent immediate over-configuration.
- [x] **No Gimmicks**: Every tick mark serves a data-granularity purpose.
- [x] **Modular Architecture**: Reuses `:core:ui` tokens; logic encapsulated in widget layer.

## Project Structure

### Documentation (this feature)

```text
specs/018-gauge-tick-config/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
└── quickstart.md        # Phase 1 output
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── data/
│   └── WidgetConfiguration.kt   # Add targetTicks field (Used by Gauge)
├── widgets/
│   ├── ScaleUtils.kt            # Update calculateNiceStep to accept dynamic targetTicks
│   └── GaugeWidget.kt           # Pass targetTicks to ScaleUtils
└── dashboard/
    └── WidgetPalette.kt         # Update WidgetConfigDialog with Tick Density slider + Scale Assistance (Gauge only)
```

**Structure Decision**: Refactor existing `app` module classes to minimize overhead, as Gauges are core to the primary dashboard experience.

## Phase 0: Outline & Research

1. **Algorithm Validation**: Verify `ScaleUtils.calculateNiceStep` produces logical increments (1, 2, 5, 10 bases) when `targetTicks` ranges from 2 to 20.
2. **UI Placement**: Research optimal layout for "Scale Assistance" readout (e.g., inline label vs. dynamic slider tooltip).

## Phase 1: Design & Contracts

1. **Data Model**: Finalize `WidgetConfiguration` schema update.
2. **Quickstart**: Scenarios for high-precision (20 ticks) vs. clean (2 ticks) gauge layouts.
3. **Agent context update**: Run script to register new configuration capabilities.
