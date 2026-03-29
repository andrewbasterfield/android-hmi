# Quickstart: Linear Gauges (030)

This guide helps you test and use the new decoupled linear gauges.

## Configuring a Linear Gauge

1.  Enter **Edit Mode** on the dashboard.
2.  Tap **Add Widget** and select **Gauge**.
3.  In the configuration dialog, you will see two new selection rows:
    *   **Axis**: Select `Arc`, `Horizontal`, or `Vertical`.
    *   **Indicator**: Select `Pointer` or `Fill`.
4.  Define your **Color Zones** (e.g., 0-80 Green, 80-100 Red).
5.  Set your **Target Ticks** (e.g., 5).
6.  Tap **Save**.

## Visual Verification Matrix

Ensure the following combinations render correctly:

| Axis | Indicator | Visual Expectation |
| :--- | :--- | :--- |
| **Arc** | **Pointer** | Current needle-style arc gauge. |
| **Arc** | **Fill** | Solid arc bar filling from start. |
| **Horiz** | **Fill** | Horizontal progress-style bar. |
| **Horiz** | **Pointer** | Scale with triangle pointing down from top. |
| **Vert** | **Fill** | Vertical tank-style bar filling from bottom. |
| **Vert** | **Pointer** | Scale with triangle pointing right from left. |

## For Developers

### Refactored Component: `GaugeWidget`
The main `GaugeWidget` now acts as a coordinator. It manages the common `AnimatedFloat` state and `Column` layout (Label -> Canvas -> Metric), then delegates the `Canvas` drawing to `ArcGaugePainter` or `LinearGaugePainter`.

### Coordinate Stability
Numeric labels and ticks for linear gauges are always placed on the opposite side of the pointer to ensure the UI remains clear even at high values.

## Testing

### Unit Tests
- Verify `WidgetConfiguration` serialization with new `gaugeAxis` and `gaugeIndicator` fields.
- Verify that `GaugeIndicator.FILL` correctly maps to the 3x track thickness.

### UI/Interaction Tests
- Verify that the triangle caret (pointer) correctly follows the tag value along all 3 axes.
- Verify that color zones align perfectly with the scale ticks on linear axes.
