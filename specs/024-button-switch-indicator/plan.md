# Implementation Plan: Latching Buttons and Indicator Lights

**Branch**: `024-button-switch-indicator` | **Date**: 2026-03-23 | **Spec**: [/specs/024-button-switch-indicator/spec.md](spec.md)
**Input**: Feature specification from `/specs/024-button-switch-indicator/spec.md`

## Summary
Support `LATCHING` and `INDICATOR` modes for button widgets, including an "Invert Logic" toggle. This involves updating the `WidgetConfiguration` model, enhancing the `WidgetConfigDialog` with new controls, and modifying `DashboardViewModel` to handle toggle logic and optimistic UI updates.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON  
**Storage**: Jetpack DataStore (Preferences storing JSON via GSON)  
**Testing**: JUnit 4, Compose Test Rule (Integration/UI)  
**Target Platform**: Android (Industrial HMI)
**Project Type**: Mobile app  
**Performance Goals**: <500ms visual feedback for toggles, <250ms for indicator updates  
**Constraints**: High-contrast Kinetic tokens, no decorative gimmicks  
**Scale/Scope**: Affects `app` module (Dashboard, Widgets, Data) and `core:ui` (Button components)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Utilizing Jetpack Compose for all UI changes in `WidgetConfigDialog` and `ButtonWidget`.
- [x] **Unidirectional Data Flow**: `DashboardViewModel` will hold the tag states; `DashboardScreen` will observe and pass them down.
- [x] **Test-First**: New test cases for `WidgetConfiguration` serialization and `ButtonWidget` behavior will be added.
- [x] **Accessibility**: Maintaining 48dp touch targets and dynamic text support.
- [x] **Clarity by Design**: Using "Identity Swap" for active states to ensure high visibility.
- [x] **Low Cognitive Load**: New configuration options are grouped logically in the existing dialog.
- [x] **No Gimmicks**: Animations are avoided; visual feedback is immediate and functional.
- [x] **Modular Architecture**: Changes primarily in `:app` (feature layer) and `:core:ui` (component layer).

## Project Structure

### Documentation (this feature)

```text
specs/024-button-switch-indicator/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── checklists/
    └── requirements.md  # Spec quality checklist
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── data/
│   ├── WidgetConfiguration.kt    # Add InteractionType and isInverted
│   └── DashboardRepository.kt    # (No change expected, GSON handles serialization)
├── dashboard/
│   ├── DashboardViewModel.kt     # Handle Latching logic & optimistic updates
│   ├── DashboardScreen.kt        # Pass state to ButtonWidget
│   └── WidgetPalette.kt          # Update WidgetConfigDialog with new UI
└── widgets/
    └── ButtonWidget.kt           # Add isChecked, isInteractive parameters

core/ui/src/main/java/com/example/hmi/core/ui/
├── components/
│   └── IndustrialComponents.kt   # Update IndustrialButton for latching state
└── theme/                        # (Reference existing tokens)
```

**Structure Decision**: Standard Android feature-based structure within the existing `:app` and `:core:ui` modules.

## Complexity Tracking

*No constitution violations identified.*
