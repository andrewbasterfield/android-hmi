# Quickstart: Industrial Button Shape Refinement

## Implementation Overview
Refine the `IndustrialButton` and `IndustrialInput` to use `MaterialTheme.shapes.small` (2dp corners) for both background and border across all interactive states.

## Verification Results (2026-03-21)

### 1. Manual Visual Inspection
- [X] **Dashboard Screen**: All `ButtonWidget` instances exhibit 2dp corners.
- [X] **Dashboard Screen**: `IndustrialInput` (search bars) exhibit 2dp corners on their obsidian backgrounds.
- [X] **Interactive States**: Pressing buttons confirms that "Inverse Video" state (Primary background) also exhibits 2dp corners.
- [X] **Consistency**: `WidgetContainer` bezels are aligned with the new 2dp component corners.

### 2. Instrumented UI Test
Ran the `core:ui` module tests:
```bash
./gradlew :core:ui:connectedDebugAndroidTest
```

**Results**:
- [X] `industrialButton_hasMinHeight64dp`: PASS
- [X] `industrialInput_hasMinHeight64dp`: PASS
- [X] `industrialButton_usesSmallShape`: PASS (Verified via `Shape" == "small"`)
- [X] `industrialInput_usesSmallShape`: PASS (Verified via `Shape" == "small"`)

### 3. Component Contract Verification
- [X] `IndustrialButton` function signature remains compatible.
- [X] `IndustrialInput` function signature remains compatible.

## Rollback Procedure
To revert the change, restore the `shape` parameter in `IndustrialButton` (Surface) and the `clip` modifier in `IndustrialInput` to `RectangleShape`.
