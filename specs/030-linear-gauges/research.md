# Research: Linear Gauges (030)

## Problem Statement
The current `GaugeWidget` is monolithic and tightly coupled to the `ARC` representation. To support `LINEAR_HORIZONTAL` and `LINEAR_VERTICAL` axes with interchangeable `FILL` and `POINTER` indicators, a decoupled "Painter" architecture is required.

## Decision: Painter-Based Decoupling
**Decision**: Refactor `GaugeWidget.kt` to use specialized "Painter" logic for different axis types while sharing common telemetry handling.

### Rationale
- **Maintainability**: Separating Arc logic from Linear logic prevents `if/else` bloat within a single `Canvas` block.
- **Extensibility**: Future axis types (e.g., Logarithmic or Circular) can be added by implementing a new Painter without touching existing UI code.
- **Performance**: Pre-calculating geometry (ticks, zones) remains critical for 60 FPS performance on industrial hardware.

### Alternatives Considered
1. **Modifier-based Rotation**:
   - *Pros*: Could reuse a single Linear gauge for both Horiz/Vert.
   - *Cons*: Similar to the slider issue, rotating text/labels requires complex coordinate transforms and often leads to blurry or incorrectly positioned metrics. A native coordinate-aware painter is cleaner.
2. **Standard Material LinearProgressIndicator**:
   - *Pros*: Fast implementation.
   - *Cons*: Does not support industrial "Stitch" aesthetics (ticks, specific color zones, triangular pointers) or vertical orientation with upright labeling.

## Implementation Details

### Coordinate Mapping (Linear)
- **Vertical**: Map `minValue..maxValue` to `(canvasHeight - margin)..(0 + margin)`.
- **Horizontal**: Map `minValue..maxValue` to `(0 + margin)..(canvasWidth - margin)`.

### Indicator Rendering
- **FILL**: A rectangle or thick line starting from the `minValue` coordinate to the `currentValue` coordinate. Thickness will be 3x the base track (standardized with Arc implementation).
- **POINTER**: A triangle caret (`Path`) drawn adjacent to the track.
  - *Vertical Pointer*: Left of track, pointing Right.
  - *Horizontal Pointer*: Above track, pointing Down.

### Layout Consistency
- **Ticks & Labels**: Placed on the side opposite the Pointer (e.g., Right side for Vertical gauges). This ensures the indicator never obscures the scale.

## References
- [Jetpack Compose Canvas API](https://developer.android.com/develop/ui/compose/graphics/draw/overview)
- [Industrial HMI Design: Linear Indicators](https://www.nngroup.com/articles/slid-standard-controls/)
