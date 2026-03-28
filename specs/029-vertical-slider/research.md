# Research: Vertical Slider Variant (029)

## Problem Statement
The current `SliderWidget` only supports horizontal orientation. Industrial HMIs frequently require vertical sliders for intuitive control of vertical processes (e.g., tank levels).

## Decision: Custom Vertical Slider Implementation
**Decision**: Implement a custom vertical slider instead of using `Modifier.rotate(-90f)` on the standard Material 3 `Slider`.

### Rationale
- **Layout Control**: Rotating a horizontal slider makes positioning labels (Top) and metrics (Bottom) difficult because the rotation happens after layout, leading to incorrect touch bounds and overlapping elements.
- **Aesthetics**: A custom implementation allows for easier integration of the "Stitch" design system (rugged rectangle thumb, specific end-ticks) in a vertical context.
- **Interaction**: Vertical sliders in Compose are best implemented using `Modifier.draggable` or `Modifier.pointerInput` to ensure the value increases upwards correctly and maps precisely to the vertical track.

### Alternatives Considered
1. **Material 3 Slider + Rotation**:
   - *Pros*: Uses standard component logic.
   - *Cons*: Significant layout "hacking" required to keep text upright; touch targets can become offset or non-intuitive if not handled perfectly.
2. **Third-Party Vertical Sliders**:
   - *Pros*: Saves development time.
   - *Cons*: Introduces external dependencies; may not align with the "Stitch" design system or project's lightweight philosophy.

## Implementation Details
- **Coordinate Mapping**: Map vertical Y-pixel offsets to the `minValue..maxValue` range.
- **Upward Increase**: Ensure `deltaY < 0` (upwards movement) increases the value.
- **Sizing**: Use `colSpan` and `rowSpan` to define the slider's bounding box. The dimension swapping logic (FR-002) will be handled in the `DashboardViewModel` or `DashboardSettingsDialog`.

## References
- [Jetpack Compose Draggable](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/drag-swipe-fling)
- [Industrial HMI Design Standards](https://www.nngroup.com/articles/slid-standard-controls/) (Standard: Up = Increase)
