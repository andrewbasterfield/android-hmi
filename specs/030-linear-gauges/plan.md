# Implementation Plan: Linear Gauges (030)

**Branch**: `030-linear-gauges` | **Date**: 2026-03-28 | **Spec**: [specs/030-linear-gauges/spec.md]
**Input**: Feature specification from `/specs/030-linear-gauges/spec.md`

## Summary
Implement a decoupled gauge architecture supporting `ARC`, `LINEAR_HORIZONTAL`, and `LINEAR_VERTICAL` axes with `FILL` and `POINTER` (triangle caret) indicators. This provides 6 orthogonal combinations for process visualization while maintaining "Stitch" design system consistency.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, Kotlinx Serialization  
**Storage**: Jetpack DataStore (Preferences with JSON serialization)  
**Testing**: JUnit 4 (Unit), Compose Test Rule (UI/Interaction)  
**Target Platform**: Android (Industrial HMI)
**Project Type**: Mobile App / Industrial Dashboard  
**Performance Goals**: 60 FPS UI responsiveness, near-zero lag PLC tag updates  
**Constraints**: Industrial "Stitch" aesthetics, high-contrast, offline-capable  
**Scale/Scope**: Dashboard widget system extension

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Utilizing Jetpack Compose `Canvas` for all gauge rendering.
- [x] **Unidirectional Data Flow**: State flows from `DashboardViewModel` to `GaugeWidget`.
- [x] **Test-First**: Unit tests for `WidgetConfiguration` and interaction tests for all 6 gauge combinations.
- [x] **Accessibility**: Minimum 48dp touch targets for config, content descriptions for all gauge states.
- [x] **Clarity by Design**: High-contrast linear scales, upright labels, and distinct pointer carets.
- [x] **Low Cognitive Load**: Orthogonal selection of axis/indicator reduces configuration complexity.
- [x] **No Gimmicks**: Every visual element (ticks, zones, pointers) serves a functional monitoring purpose.
- [x] **Modular Architecture**: Implementation partitioned between `:app:widgets` and `:app:dashboard`.

## Project Structure

### Documentation (this feature)

```text
specs/030-linear-gauges/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── data/
│   └── WidgetConfiguration.kt    # Updated with Axis/Indicator enums
├── dashboard/
│   └── WidgetPalette.kt          # Config UI for new options
└── widgets/
    ├── GaugeWidget.kt            # Core logic delegating to axis/indicator drawing
    ├── LinearGaugeWidget.kt      # (New) Optimized component for linear rendering
    └── ArcGaugeWidget.kt         # (Extracted) Current arc logic refactored
```

**Structure Decision**: Refactor existing `GaugeWidget.kt` to delegate to specific axis drawers (`Arc` vs `Linear`) while sharing indicator logic where possible.
