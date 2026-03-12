# Research: Smooth Grid Snapping

This document outlines the research and technical implementation details for smooth dragging, ghosting, and spring animations.

## 1. Decoupling Logic (Ghosting)

**Decision**: Use transient local state (`remember { mutableStateOf(...) }`) within `DashboardScreen` to track the current drag offset in pixels, while keeping the `WidgetConfiguration` (grid cells) as the source of truth for the stable state.

**Rationale**:
- **Smoothness**: Updating the ViewModel on every pixel move is expensive and causes "jumpiness."
- **Ghosting**: We can use the transient pixel offset to calculate the "snap target" cells in real-time and render a separate translucent `Box` at those coordinates.

## 2. Spring Animations

**Decision**: Use `animateIntOffsetAsState` with a `spring` spec for the final snap transition.

**Rationale**:
- **Feel**: Springs feel more natural and industrial than linear or ease-in-out curves.
- **Spec**: `spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)` provides a high-quality "slide-to-fit" effect.

## 3. Implementation Patterns

**Dragging**:
- `Modifier.offset { IntOffset(...) }` for the active widget.
- `Modifier.offset { IntOffset(ghostColumn * 80, ghostRow * 80) }` for the ghost preview.

**Resizing**:
- Similar logic: Decouple visual width/height from grid `colSpan/rowSpan` during the gesture.
- Ghost preview shows the predicted final grid dimensions.

## 4. Performance

**Optimization**: Use `graphicsLayer` or `offset { ... }` (lambda version) to avoid recomposition of the entire dashboard during drag. Lambda offset only triggers a re-layout/draw at the specific node level.
