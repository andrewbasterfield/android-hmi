# Quickstart: Arc-Filling Gauge Support

## Overview
Learn how to use the new "Arc Fill" style for Gauges in the android-ui dashboard.

## Configuring a Gauge for Arc Fill

1.  **Open Dashboard**: Launch the app.
2.  **Add a Gauge**: Click "Add Widget" and select "GAUGE".
3.  **Set Style**: Scroll to the Gauge settings and locate "Display Style".
4.  **Select Arc Fill**: Toggle from "Pointer" to "Arc Fill".
5.  **Configure Colors**: 
    - Enable "Pointer matches Zone Color" for dynamic state feedback.
    - Define "Color Zones" (e.g., 0-80 green, 80-100 red).
6.  **Save**: Click "Save". The gauge now fills up instead of using a pointer.

## For Developers: Testing the New Style

### Unit Tests
Run `WidgetConfigurationTest.kt` to verify that existing layouts migrate correctly to the `POINTER` default and that new `ARC_FILL` configs persist correctly.

### UI Tests
Run `GaugeStyleTest.kt` to verify:
- "Pointer" style renders a chevron.
- "Arc Fill" style renders a filling arc.
- Content descriptions accurately reflect the state for both styles.
