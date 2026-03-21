# Implementation Plan: Gauge Color Customization

**Branch**: `019-customize-gauge-colors` | **Date**: 2026-03-21 | **Spec**: [/specs/019-customize-gauge-colors/spec.md](../spec.md)
**Input**: Feature specification from `/specs/019-customize-gauge-colors/spec.md`

## Summary

This feature extends the `GaugeWidget` to support custom needle and scale colors, along with a "Dynamic Needle Color" mode that synchronizes the needle color with the active `GaugeZone` (threshold). Implementation involves updating the `WidgetConfiguration` data model, enhancing the `GaugeWidget` rendering logic, and updating the `WidgetConfigDialog` UI.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore (Preferences), GSON, Kotlin Coroutines (StateFlow)  
**Storage**: Jetpack DataStore (Preferences storing JSON via GSON)  
**Testing**: JUnit 4 (Unit), Compose UI Testing (Instrumentation)  
**Target Platform**: Android (Modern Industrial HMI)
**Project Type**: Mobile App / Industrial Dashboard  
**Performance Goals**: 60 fps (Smooth needle animations), <16ms latency for dynamic color switches.  
**Constraints**: 48dp min touch targets, WCAG 2.1 Contrast compliance.  
**Scale/Scope**: App module (Dashboard), Core:UI module (Widgets).

## Constitution Check

- [x] **Compose-First**: `GaugeWidget` uses `@Composable` with `Canvas` for efficient rendering.
- [x] **Unidirectional Data Flow**: State flows from `DashboardViewModel` -> `WidgetConfiguration` -> `GaugeWidget`.
- [x] **Test-First**: Unit tests for color selection logic and UI tests for color application are planned.
- [x] **Accessibility**: Minimum touch targets (48dp) and contrast checks are included.
- [x] **Clarity by Design**: Threshold-based dynamic coloring improves operator situational awareness.
- [x] **Low Cognitive Load**: Color settings integrated into existing widget configuration context.
- [x] **No Gimmicks**: Colors serve functional status signaling and accessibility purposes.
- [x] **Modular Architecture**: Feature integrated within existing dashboard and widget modules.

## Project Structure

### Documentation (this feature)

```text
specs/019-customize-gauge-colors/
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
│   └── WidgetPalette.kt      # Configuration Dialog updates
├── data/
│   └── WidgetConfiguration.kt # Data model updates
└── widgets/
    └── GaugeWidget.kt        # Rendering logic updates

core/ui/src/main/java/com/example/hmi/
└── widgets/
    └── ColorUtils.kt         # Color mapping/parsing extensions
```

**Structure Decision**: Single project structure with feature integration across existing dashboard and widget layers.

## Complexity Tracking

*No constitution violations identified.*
