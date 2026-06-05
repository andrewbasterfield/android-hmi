# Implementation Plan: Frontend-Driven Color Management

**Branch**: `033-frontend-color-logic` | **Date**: 2026-06-05 | **Spec**: [/specs/033-frontend-color-logic/spec.md]
**Input**: Feature specification from `/specs/033-frontend-color-logic/spec.md`

## Summary
The goal is to shift color management from the backend protocol to the frontend. This involves removing color-broadcasting logic from the `DemoPlcServer`, ignoring `.color` updates in the protocol layer, and ensuring UI widgets determine their colors locally based on value-driven `ColorZones` defined in their configuration.

## Technical Context

**Language/Version**: Kotlin 1.9.x  
**Primary Dependencies**: Jetpack Compose, Hilt, DataStore, Coroutines  
**Storage**: DataStore (via `DashboardRepository`)  
**Testing**: JUnit 4, Mockito, Compose UI Test  
**Target Platform**: Android (Min SDK 24, Target SDK 34)
**Project Type**: Mobile Application (Industrial HMI)  
**Performance Goals**: 60 fps for gauge animations, <100ms for color shift response to value change.  
**Constraints**: Must maintain high contrast for industrial environments.  
**Scale/Scope**: Impacts all widget types and the demo server.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: All widget coloring logic will be implemented within Compose components using `derivedStateOf`.
- [x] **Unidirectional Data Flow**: Tag values flow from `DashboardViewModel` to widgets, which then resolve colors locally.
- [x] **Test-First**: `ColorResolver` (or equivalent) will be unit tested for various range and value combinations.
- [x] **Accessibility**: Color shifts will be accompanied by label changes (where configured) to support users with color vision deficiencies.
- [x] **Clarity by Design**: High-contrast OSHA/ANSI-compliant colors will be prioritized in presets.
- [x] **Low Cognitive Load**: Colors will only change when significant value thresholds are crossed.
- [x] **No Gimmicks**: Color changes serve a direct functional purpose (status indication).
- [x] **Modular Architecture**: Changes will span `core:protocol`, `core:ui`, and the `app` module.

## Project Structure

### Documentation (this feature)

```text
specs/033-frontend-color-logic/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Research decisions
├── data-model.md        # Updated entities
├── quickstart.md        # Usage guide
├── contracts/           
│   └── ProtocolChanges.md # PLC protocol updates
└── checklists/          # Validation checklists
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── dashboard/
│   ├── DashboardViewModel.kt     # Ignore .color updates
│   └── DashboardScreen.kt        # Remove color override logic
├── widgets/
│   ├── ColorUtils.kt             # Add/Enhance color resolution logic
│   └── GaugeWidget.kt            # Update to use local resolution
└── protocol/
    └── DemoPlcServer.kt          # Remove color broadcasting
```

**Structure Decision**: Standard Android project structure with existing feature modules.
