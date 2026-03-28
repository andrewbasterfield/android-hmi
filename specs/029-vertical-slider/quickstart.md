# Quickstart: Vertical Slider Variant (029)

This guide helps you test and use the new vertical slider variant.

## Configuring a Vertical Slider

1.  Enter **Edit Mode** on the dashboard.
2.  Tap **Add Widget** and select **Slider**.
3.  In the configuration dialog, find the **Orientation** toggle.
4.  Select **Vertical**.
    *   Observe: The `colSpan` and `rowSpan` (Width and Height) automatically swap.
5.  Set your desired `tagAddress` and `minValue/maxValue`.
6.  Tap **Save**.

## Using the Vertical Slider

1.  Switch to **Run Mode**.
2.  Locate the vertical slider.
3.  Drag the thumb **Up** to increase the value and **Down** to decrease it.
4.  Observe: The current value is displayed in the metric at the bottom, and the label is at the top.

## For Developers

### Custom Component: `VerticalSliderWidget`
The vertical slider is a custom implementation using Compose's `Modifier.draggable` to ensure precise upward value mapping and stable label positioning.

### Sizing Logic
Dimension swapping `(4x1 -> 1x4)` happens in `WidgetConfigDialog` when the orientation is toggled. This ensures the slider maintains its travel distance when rotated.

## Testing

### Unit Tests
-   Verify `WidgetConfiguration` serialization including the `orientation` field.
-   Verify the dimension swapping logic in the configuration dialog.

### UI/Interaction Tests
-   Verify the vertical slider increases value on upward drag.
-   Verify labels and metrics are correctly stacked in vertical mode.
