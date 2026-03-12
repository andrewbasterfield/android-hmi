# Research: UI Refinement (DPI-Aware Grid)

This document outlines the research and technical decisions for the grid-based HMI layout system.

## 1. Coordinate System: Cells vs. Pixels

**Decision**: Store widget positions and sizes in **Grid Units (Int)**: `column`, `row`, `colSpan`, `rowSpan`.
**Rationale**:
- **Consistency**: A widget placed in cell (0,0) with size (2,2) will occupy the top-left corner on all devices, but may take up a different fraction of the screen depending on its density and resolution.
- **Persistence**: Storing `Int` cells simplifies JSON serialization and avoids floating-point precision issues during layout calculations.
- **Responsiveness**: The system will dynamically calculate the maximum columns and rows based on screen width/height in DP.

## 2. Grid Math in Jetpack Compose

**Approach**:
- `CellSize = 80.dp` (Fixed)
- `ScreenSizeDP = LocalConfiguration.current.screenWidthDp / screenHeightDp`
- `Columns = floor(ScreenWidthDp / 80)`
- `Rows = floor(ScreenHeightDp / 80)`

**Layout**: Use a `Box` with `Modifier.offset` where `x = column * CellSize` and `y = row * CellSize`.

## 3. Drag and Resize Interaction

**Dragging**:
- Capture the total `dragAmount` in DP.
- On drag end, snap to the nearest cell: `newColumn = (currentX + dragAmountX).toDp() / 80.dp`.

**Resizing**:
- Implement small circular handles (24dp) at the bottom-right corner of each widget in Edit Mode.
- Dragging the handle updates the `colSpan` and `rowSpan` in grid increments.
- Minimum size: `1x1` grid unit.

## 4. Widget Container Implementation

**Decision**: Implement a single `WidgetContainer` composable that wraps all widgets.
**Rationale**:
- **Uniformity**: Centralizes the implementation of square edges, 1dp contrasting border, and `ColorUtils.getContrastColor` logic.
- **Simplification**: Individual widgets (Button, Slider, Gauge) can now focus on their internal controls, assuming they are placed in a correctly sized container.
## 5. Color Palette & Serialization Fixes

**Decision**: Reorder the color palette to follow: White, Primary (Default), RGB, CMY, Gray, Black.
**Rationale**: Standard industrial color sequence for easier selection.

**Decision**: Explicitly cast `Long` color values to `Int` before passing to the `Color` constructor.
**Rationale**: Compose's `Color(Long)` expects a 64-bit internal format, while our storage uses 32-bit ARGB. Casting to `Int` ensures the leading alpha bits are correctly handled, preventing "White" or transparent rendering for certain colors like Purple.

## 6. Robust TCP Communication

**Decision**: Implement a line-based protocol (`TAG:VALUE\n`) with a background listener.
**Rationale**: Simplest way to drive HMI widgets from standard tools like `ncat`. 

**Improvements**:
- Added a 5-second connection timeout.
- Implemented a more resilient parsing logic for different number formats.
- Prevented "Connection Error" states when intentionally disconnecting.

## 7. Alternatives Considered
...
- **Why**: Percentage-based columns result in widgets of different physical sizes on different hardware. In an industrial context, button sizes should be consistent for reliable touch interactions.

### Free-form 8dp Snapping (Rejected)
- **Why**: While flexible, it makes it much harder to align complex layouts. A formal grid system enforces professionalism and simplifies the coordinate data model.
