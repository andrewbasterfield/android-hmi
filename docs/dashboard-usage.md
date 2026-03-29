# Dashboard Usage Guide

The Dashboard is the primary interface for monitoring and controlling your HMI. It features a flexible, grid-based canvas that supports 2D paging and intuitive customization.

## Navigating the Dashboard

### 2D Paging
The dashboard is not limited to a single screen. You can expand your layout in four directions:
- **Swipe Left/Right:** Move between horizontal pages.
- **Swipe Up/Down:** Move between vertical pages.

Place widgets beyond the initial viewport and swipe to navigate to them.

### Global Status (Emergency HUD)

The dashboard monitors all gauge color zones and escalates to a full-screen visual alert when a value enters a zone with a reserved label:

| Zone Label   | Effect                                                    |
|--------------|-----------------------------------------------------------|
| `CAUTION`    | Amber pulsing glow around screen edges (1 Hz)             |
| `CRITICAL`   | Red pulsing glow around screen edges (2 Hz) + backdrop blur |

The global status is the worst-case across all widgets — if any single gauge value enters a `CRITICAL` zone, the entire screen pulses red.

> **Note**: There is currently no UI to set a label on a color zone. Zone labels can only be set by editing the layout JSON directly (set `"label"` to `"CAUTION"` or `"CRITICAL"` on a zone entry). Without this, the Emergency HUD will never activate.

---

## Edit Mode

To customize your dashboard, toggle **Edit Mode** using the floating action button (pencil icon).

### Adding Widgets
1. Tap the **Add Widget** button.
2. Select a widget type (Gauge, Slider, or Button) and configure its settings.
3. The widget is placed on the grid and can be repositioned by dragging.

### Managing Widgets
- **Move:** Long-press and drag a widget to a new location.
- **Resize:** Use the handle at the bottom-right of a widget to change its grid span.
- **Configure:** Tap a widget in Edit Mode to open its specific settings (see [Widget Configuration](widget-configuration.md)).
- **Duplicate:** Tap "Duplicate" in the widget's configuration dialog to create a copy offset by one grid cell. All settings are copied.
- **Delete:** Tap the "Delete" button inside the widget's configuration dialog.

---

## Layout Settings

Tap the gear icon in Edit Mode to access global layout settings:

- **Layout Name:** Give your dashboard a unique name.
- **Canvas Background Color:** Customize the look of your HMI.
- **Orientation Mode:**
  - **AUTO:** Follows the device's physical orientation.
  - **LANDSCAPE:** Locks the HMI to horizontal mode.
  - **PORTRAIT:** Locks the HMI to vertical mode.
- **Haptic Feedback:** Toggle tactile vibrations for button presses and slider movements.

---

## Interaction Tips

- **Grid Snapping:** Widgets always align to the grid. A translucent ghost shows where the widget will land during a drag.
