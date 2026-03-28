# Implementation Plan: Vertical Slider Variant (029)

**Feature Branch**: `029-vertical-slider`  
**Created**: 2026-03-28  
**Status**: Planning  

## Technical Context

- **Tech Stack**: Kotlin 1.9.22, Jetpack Compose, Hilt, Jetpack DataStore, GSON.
- **Current State**: `SliderWidget.kt` uses Material 3 `Slider` (Horizontal only).
- **Goal**: Implement `VERTICAL` orientation for sliders with upward value increase and "Stitch" design aesthetics.

## Constitution Check

| Principle | Compliance | Rationale |
|-----------|------------|-----------|
| I. Compose-First | ✅ | Using a custom Compose slider with `Modifier.draggable`. |
| II. Unidirectional Data Flow | ✅ | ViewModel remains the source of truth for slider values. |
| III. Test-First & Coverage | ✅ | Adding unit tests for configuration logic and UI interaction tests. |
| IV. Accessibility | ✅ | Maintaining 48dp touch targets and content descriptions. |
| VI. Clarity by Design | ✅ | Upward increase is the industrial standard; labels remain upright. |
| VIII. No Gimmicks | ✅ | No unnecessary animations or decorative elements. |

## Phase 0: Research (Done)
- Decision: Custom Vertical Slider via `Modifier.draggable`.
- Rationale: Better layout control for upright labels/metrics compared to rotation.
- See `specs/029-vertical-slider/research.md` for details.

## Phase 1: Design (Done)
- Data Model: Added `WidgetOrientation` enum and `orientation` field.
- See `specs/029-vertical-slider/data-model.md` and `quickstart.md`.

## Phase 2: Implementation

1.  **Data Model & UI Integration**:
    *   Update `WidgetConfiguration.kt` with `WidgetOrientation` enum and field.
    *   Add orientation toggle to `WidgetConfigDialog` in `WidgetPalette.kt`.
    *   Implement dimension swapping logic in `WidgetConfigDialog`.

2.  **Custom Vertical Slider**:
    *   Refactor `SliderWidget.kt` to support `HORIZONTAL` (using current M3 Slider) and `VERTICAL` (using new custom component).
    *   Implement the custom vertical track and thumb logic in `SliderWidget.kt`.
    *   Ensure labels/metrics stack vertically for `VERTICAL` mode.

3.  **ViewModel & Screen Wiring**:
    *   Update `DashboardScreen.kt` to pass the `orientation` from the widget configuration to `SliderWidget`.

## Phase 3: Validation

1.  **Unit Tests**:
    *   Test serialization of the new `orientation` field.
    *   Test dimension swapping logic in the config dialog.
2.  **Interaction Tests**:
    *   Verify upward drag increases value on vertical sliders.
3.  **Visual Verification**:
    *   Verify labels and metrics are upright and correctly positioned.
