# Implementation Plan: Industrial Button Shape Refinement

**Branch**: `022-industrial-button-shape` | **Date**: 2026-03-21 | **Spec**: [/specs/022-industrial-button-shape/spec.md](spec.md)
**Input**: Feature specification from `/specs/022-industrial-button-shape/spec.md`

## Summary

Refactor the `IndustrialButton` and `IndustrialInput` components in the `:core:ui` module to replace `RectangleShape` (0dp corners) with `MaterialTheme.shapes.small` (2dp corners). This change aligns the components with the "Industrial Precision HMI" design specification, which mandates "hard but machined" edges. The refactor will ensure consistency across all interactive states (Normal, Pressed, Focused) and preserve existing tactile requirements (64px height, 2px border for buttons, 4px "shelf" for inputs). Existing placeholder tests in `IndustrialComponentTest.kt` will be implemented to verify these constraints.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt  
**Storage**: N/A  
**Testing**: JUnit 4, Compose UI Test (connectedDebugAndroidTest)  
**Target Platform**: Android (Rugged Tablet/Industrial Display)
**Project Type**: Mobile-app (HMI)  
**Performance Goals**: 60 fps UI rendering  
**Constraints**: Industrial Precision: 2dp corners, 2px border width, 64px min touch target  
**Scale/Scope**: Component-level refactor in `:core:ui` module (IndustrialComponents.kt)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Components are built with Jetpack Compose.
- [x] **Unidirectional Data Flow**: State is passed via parameters; events flow up.
- [x] **Test-First**: Plan includes implementing actual verification in `IndustrialComponentTest.kt`.
- [x] **Accessibility**: 64px touch target (A11Y-001) is preserved and tested.
- [x] **Clarity by Design**: High-contrast Obsidian/Green palette (UI-002) is preserved.
- [x] **Low Cognitive Load**: Aesthetic refinement for ergonomics; no changes to info hierarchy.
- [x] **No Gimmicks**: 2dp radius serves a functional ergonomic purpose.
- [x] **Modular Architecture**: localized to the `:core:ui` module.

## Project Structure

### Documentation (this feature)

```text
specs/022-industrial-button-shape/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Verification of theme tokens
├── data-model.md        # UI component definition
├── quickstart.md        # Verification guide
└── checklists/
    └── requirements.md  # Quality validation
```

### Source Code (repository root)

```text
core/ui/src/main/java/com/example/hmi/core/ui/
├── components/
│   └── IndustrialComponents.kt   # Component implementation
└── theme/
    ├── Shape.kt                  # Theme shape definitions
    └── StitchTheme.kt            # Theme entry point

core/ui/src/androidTest/java/com/example/hmi/core/ui/
└── IndustrialButtonTest.kt       # Verification tests
```

**Structure Decision**: Single project modular structure. The change is confined to the `:core:ui` module.

## Complexity Tracking

*No violations detected.*
