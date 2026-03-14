# Implementation Plan: Custom Labels and Dynamic Attribute Updates

**Branch**: `010-widget-dynamic-attributes` | **Date**: 2026-03-14 | **Spec**: [specs/010-widget-dynamic-attributes/spec.md](specs/010-widget-dynamic-attributes/spec.md)
**Input**: Feature specification from `/specs/010-widget-dynamic-attributes/spec.md`

## Summary

This feature adds the ability to override PLC tag addresses with human-readable labels in the UI and allows for dynamic, session-only attribute updates (label and color) via the communication protocol using `TAG.ATTRIBUTE:VALUE` syntax.

## Technical Context

**Language/Version**: Kotlin (Latest stable)  
**Primary Dependencies**: Jetpack Compose, Kotlin Coroutines, Hilt, Jetpack DataStore  
**Storage**: Jetpack DataStore (persistent configuration), In-memory StateFlow (transient session overrides)  
**Testing**: Unit tests for protocol parsing, Compose UI tests for label/color updates  
**Target Platform**: Android (API 24+)  
**Project Type**: Mobile App (Android)  
**Performance Goals**: <100ms latency from protocol message to UI update  
**Constraints**: Volatile protocol updates (session-only), "Last Change Wins" conflict resolution  

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Utilizing Jetpack Compose for UI updates and dynamic styling.
- [x] **Unidirectional Data Flow**: Attributes flowing from `PlcCommunicator` -> `DashboardViewModel` -> UI.
- [x] **Test-First**: Unit tests for extended protocol parsing logic.
- [x] **Accessibility**: Implementing contrast checks for dynamic background colors.
- [x] **Modular Architecture**: Integrating into existing `data`, `protocol`, and `dashboard` packages.

## Project Structure

### Documentation (this feature)

```text
specs/010-widget-dynamic-attributes/
├── plan.md              # This file
├── research.md          # Protocol parsing and transient state management
├── data-model.md        # Updated WidgetConfiguration
├── quickstart.md        # Testing protocol attribute updates
└── tasks.md             # Implementation steps
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── data/
│   └── WidgetConfiguration.kt # Add customLabel field
├── protocol/
│   ├── PlcCommunicator.kt     # Extend to handle attribute updates
│   └── RawTcpPlcCommunicator.kt # Parse TAG.ATTR:VALUE syntax
├── dashboard/
│   ├── DashboardViewModel.kt  # Manage transient session overrides
│   └── DashboardScreen.kt     # Update UI to respect overrides
└── widgets/
    └── ColorUtils.kt          # Contrast logic for dynamic colors
```

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

(No violations identified)
