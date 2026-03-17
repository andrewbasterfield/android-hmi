# Implementation Plan: Modern Industrial UI

**Branch**: `014-modern-industrial-ui` | **Date**: 2026-03-16 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/014-modern-industrial-ui/spec.md`

## Summary
Implement a "Modern Industrial" aesthetic across the HMI dashboard. Key changes include moving from sharp rectangles to adaptive rounded corners (8dp standard, 4dp for 1x1 widgets) and enforcing a strict "Black Text" mandate for all vibrant and light widget content. To support this mandate while maintaining accessibility, a "Hybrid Contrast" strategy will be implemented to automatically switch to White text for very dark backgrounds (L < 0.2).

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Jetpack Compose (Foundation, Material 3)
**Storage**: Jetpack DataStore (Existing layout persistence)
**Testing**: JUnit 4, Compose UI Test
**Target Platform**: Android (API 24+)
**Project Type**: Mobile App
**Performance Goals**: Maintaining 60 FPS while applying clipping and borders to rounded widgets
**Constraints**: 
- Standard radius: 8dp
- Small radius (1x1): 4dp
- Foreground color: Black (L >= 0.2) / White (L < 0.2)
- Vibrant Priority: Force Black text for Cherry Red (#D2042D)

## Constitution Check

- [x] **Compose-First**: Utilizing `RoundedCornerShape` and `Modifier.clip` in Compose.
- [x] **Unidirectional Data Flow**: UI state remains derived from `WidgetConfiguration`.
- [x] **Test-First**: Unit tests for adaptive radius logic and hybrid contrast toggling.
- [x] **Accessibility**: Minimum 4.5:1 contrast enforced via dynamic Black/White text toggling at the 0.2 luminance threshold.
- [x] **Modular Architecture**: Feature applied within the `app` module.

## Project Structure

### Documentation (this feature)

```text
specs/014-modern-industrial-ui/
├── plan.md              # This file
├── research.md          # Decisions on shapes and contrast logic
├── data-model.md        # Adaptive radius and contrast rules
├── quickstart.md        # Verification steps
└── tasks.md             # Task breakdown (Phase 2)
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── ui/theme/
│   └── Shape.kt          # NEW: Define 8dp and 4dp corner constants
├── dashboard/
│   └── WidgetContainer.kt # Update to use adaptive RoundedCornerShape
├── widgets/
│   ├── ColorUtils.kt     # Update with hybrid contrast logic
│   ├── ButtonWidget.kt   # Update shape and use hybrid contrast
│   ├── SliderWidget.kt   # Use hybrid contrast
│   └── GaugeWidget.kt    # Use hybrid contrast
```

**Structure Decision**: Extending the existing `ui/theme` and `widgets` packages to house the new aesthetic constants and logic.

## Planning Phase 2: Implementation Steps

1.  **Phase 1: Foundation (Shapes & Utils)**
    *   Create `ui/theme/Shape.kt` with the new constants.
    *   Implement `ColorUtils.getIndustrialContrastColor(color)` with 0.2 threshold.
    *   Write unit tests for the new utility functions.

2.  **Phase 2: Core Refinement (Widgets)**
    *   Update `WidgetContainer.kt` to apply the adaptive `RoundedCornerShape` based on size.
    *   Update `ButtonWidget.kt` to use the 8dp rounded shape and hybrid contrast.
    *   Update `SliderWidget` and `GaugeWidget` to use hybrid contrast for labels/values.

3.  **Phase 3: Integration & Polish**
    *   Verify all dialogs use the new rounded aesthetic.
    *   Ensure hybrid contrast is applied when loading custom hex colors.
    *   Run visual verification steps from `quickstart.md`.
