# Implementation Plan: Dashboard Design Integration (Kinetic Cockpit)

**Branch**: `017-dashboard-design-integration` | **Date**: 2026-03-20 | **Spec**: [/specs/017-dashboard-design-integration/spec.md](/specs/017-dashboard-design-integration/spec.md)
**Input**: Feature specification from `/specs/017-dashboard-design-integration/spec.md`

## Summary
Transform the existing functional dashboard from a generic HMI style into the "Kinetic Cockpit" design language. This involves refactoring core UI containers (`WidgetContainer`) and widgets (`Gauge`, `Button`, `Slider`) to enforce 0dp corners, 2px structural bezels, and the Obsidian color palette. Key requirements include removing widget headers to increase information density, adding high-contrast tactile resize handles in Edit Mode, and scaling typography for industrial legibility.

## Technical Context

**Language/Version**: Kotlin 1.9+  
**Primary Dependencies**: Jetpack Compose, Hilt, :core:ui, :protocol (Live PLC Data)  
**Storage**: Jetpack DataStore (existing layout persistence)  
**Testing**: Compose UI Tests (Interaction timing), Screenshot Testing (Visual validation)  
**Target Platform**: Android (Ruggedized Tablets)
**Project Type**: Mobile App (Industrial HMI)  
**Performance Goals**: 60 fps UI rendering, <50ms interaction feedback latency  
**Constraints**: Absolute 0dp radius, 2px bezels, 64px targets, WCAG AAA contrast, Base Font >= 16sp  
**Scale/Scope**: Refactor 1 container, 3 core widgets, 1 main operational screen

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Refactoring existing Jetpack Compose widgets.
- [x] **Unidirectional Data Flow**: State flows from `DashboardViewModel` to the ruggedized widgets.
- [x] **Test-First**: UI tests will verify the <50ms state-swap latency.
- [x] **Accessibility**: Enforces 64px targets and Industrial-Scale typography (FR-013).
- [x] **Clarity by Design**: Geometric typography and monospaced numerical readouts.
- [x] **Low Cognitive Load**: HUD triggers only on `CRITICAL` states; header removal increases data focus.
- [x] **No Gimmicks**: Every element (including the new resize handles) serves a functional purpose.
- [x] **Modular Architecture**: Utilizes `:core:ui` for shared design tokens.

## Project Structure

### Documentation (this feature)

```text
specs/017-dashboard-design-integration/
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
│   ├── DashboardScreen.kt   # Wrap in StitchTheme, Bind HUD to live data
│   └── WidgetContainer.kt   # Force 0dp, 2px bezel, Resize Handles, No Headers
└── widgets/
    ├── GaugeWidget.kt       # Force Space Grotesk + Monospaced data + Legible Scale
    ├── ButtonWidget.kt      # Replace with IndustrialButton logic
    └── SliderWidget.kt      # Force 0dp track/thumb geometry + Legible Scale
```

**Structure Decision**: Refactoring existing classes in the `app` module while leveraging the `:core:ui` design tokens. 

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Modular Architecture (V) | Dashboard and Widgets are currently deeply coupled with legacy navigation and PLC communication logic in the `app` module. | A full module migration would exceed the scope of "Design Integration" and risk breaking core functional utility. |

## Phase 0: Outline & Research

1. **Best Practices for Resize Handles**: Research ergonomic and tactile patterns for industrial touchscreen resize handles.
2. **Typography Scale Audit**: Verify standard "Industrial Utility" font sizes for 10-inch tablets at 1-meter viewing distance.
3. **Migration Logic**: Patterns for mapping legacy user-defined colors to OSHA/Kinetic tokens without data loss.

## Phase 1: Design & Contracts

1. **Data Model**: Update `WidgetConfiguration` if necessary for legibility overrides.
2. **Quickstart**: Scenarios for verifying ruggedization and typography scale.

## Phase 2: Implementation Tasks

*Tasks will be generated via speckit.tasks.*
