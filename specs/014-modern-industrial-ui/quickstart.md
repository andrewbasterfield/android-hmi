# Quickstart: Modern Industrial UI

## Visual Verification

### Rounded Corners
1. Start the app and connect to the dashboard.
2. Add a **Button** (default size 2x1). Verify corners are rounded (8dp).
3. Add a **Gauge** (default size 2x2). Verify corners are rounded (8dp).
4. Resize a widget to **1x1**. Verify the rounding reduces to 4dp.

### Black Text Mandate
1. Change a widget's background to **Cherry Red** (`#D2042D`).
2. Verify the text remains **Black**.
3. Verify the text is clearly legible against the red.

### Luminance Floor
1. Use the Custom Hex picker to set a background color to **Near-Black** (e.g., `#050505`).
2. Verify the app automatically brightens the color to a legible grey threshold.
3. Verify the text remains Black and is readable.

## Automated Testing Strategy

### Unit Tests
- `ColorUtilsTest`: Verify the clamping logic for the luminance floor.
- `ShapeLogicTest`: Verify that `getShapeForSize` returns the correct radius for 1x1 vs larger widgets.

### UI Tests
- `IndustrialUiTest`: Capture screenshots (if supported) or verify `RoundedCornerShape` properties via semantics if possible.
- Verify text color of widgets is `Color.Black` regardless of background.
