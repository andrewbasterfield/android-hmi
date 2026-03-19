# Research: UI Animations and Gauge Improvement

## Decision: 3D Button "Press" Animation
- **Choice**: Use `MutableInteractionSource` to track `isPressed`. Combine `animateFloatAsState` (Scale: 1.0f -> 0.95f) with `animateDpAsState` (Elevation: 4.dp -> 1.dp).
- **Rationale**: Elevating the button and then decreasing that elevation (and shadow) during a scale-down simulates a physical 3D displacement. This fulfills the "more obvious" requirement while maintaining the "tactile" feel via a `spring` spec.
- **Interpolation**: Use `Spring(dampingRatio = 0.5f, stiffness = 150f)` for the return animation to create a subtle mechanical "bounce."

## Decision: High-Fidelity Circular Gauge (270°)
- **Choice**: Re-implement `GaugeWidget` using `Canvas`. 
  - **Arc Span**: Fixed 270° starting from 135° (bottom-left) to 405° (bottom-right).
  - **Needle**: Path-based drawing with a "hub" circle.
  - **Value Animation**: `animateFloatAsState` with a `tween` duration of 300ms to ensure 60fps movement.
- **Rationale**: 270° is the "cockpit" standard. Canvas provides precise control over arc segments for the flexible color zones.

## Decision: "Nice Number" Tick Algorithm
- **Choice**: Implement a utility function using `log10` to determine the magnitude of the range, then select a step size from the set `{1, 2, 5, 10}` multiplied by that magnitude.
- **Rationale**: This ensures ticks always fall on logical decimal intervals (e.g., 0, 0.2, 0.4... or 0, 50, 100...) rather than messy irrational divisions like 14.28...
- **Scale Density**: Target ~5-10 major ticks per gauge to prevent visual clutter.

## Decision: Flexible Color Zones
- **Choice**: Store zones as a `List<GaugeZone>` in the widget configuration.
- **Drawing**: Zones are drawn as secondary arcs underneath the main scale using `drawArc` with the specific start/end angles mapped from values.

## Decision: Layout-Level Haptic Toggle
- **Choice**: Store `hapticFeedbackEnabled` in `DashboardLayout`.
- **Implementation**: In the button's `onClick`, check the layout flag before calling `hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)`.
- **Hardware Check**: Use `Vibrator` system service to ensure no crashes on hardware without motors.
