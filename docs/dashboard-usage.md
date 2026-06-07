# Dashboard Usage Guide

The Dashboard is where you monitor and control your HMI. It's built around a flexible, grid-based canvas that supports 2D paging and easy customization.

## Navigating the Dashboard

### Management Hub (Drawer)
The **Management Hub** is the central navigation point for your operational environment. Access it by:
- **Swiping from the left edge** of the screen.
- Tapping the **Menu (hamburger)** icon in the top toolbar.

From the Hub, you can:
- **Switch System Profiles** (one-tap launch).
- **View real-time connection status** (Connected, Reconnecting, or Lost).
- **Browse your Library** of saved connections and layouts.
- **Save the current environment** as a new preset.

### 2D Paging
Your dashboard isn't limited to a single screen. You can spread your layout across multiple pages:
- **Swipe Left/Right** to move between horizontal pages.
- **Swipe Up/Down** to move between vertical pages.

Place widgets beyond the initial viewport and swipe over to them whenever you need to.

### Global Status (Emergency HUD)

The dashboard keeps an eye on all gauge color zones and escalates to a full-screen visual alert when a value enters a zone with a reserved label:

| Zone Label   | Effect                                                    |
|--------------|-----------------------------------------------------------|
| `CAUTION`    | Amber pulsing glow around screen edges (1 Hz)             |
| `CRITICAL`   | Red pulsing glow around screen edges (2 Hz) + backdrop blur |

The global status always reflects the worst case across all widgets -- if any single gauge enters a `CRITICAL` zone, the entire screen pulses red.

> **Note**: There's currently no UI for setting a label on a color zone. For now, you'll need to edit the layout JSON directly (set `"label"` to `"CAUTION"` or `"CRITICAL"` on a zone entry). Without a label, the Emergency HUD won't activate.

---

## System Profiles (Presets)

A **System Profile** is an "opinionated binding" that links a specific technical connection (IP/Protocol) to a specific visual layout.

### Using Profiles
1. Open the **Management Hub**.
2. Tap a preset under **Systems (Presets)**.
3. The app will automatically connect to the target machine and load the correct dashboard in a single step.

### Creating a Profile
1. Connect to your desired machine.
2. Load or design the visual layout you want to use.
3. Open the Management Hub and tap **Save as System Profile**.
4. Give it a name (e.g., "Line 1 Production").

### Library (Mix-and-Match)
The **Library** section in the Management Hub allows you to manually swap parts of your environment:
- **Connections** -- Change the data source while keeping the same visual UI.
- **Layouts** -- Change the visual design while remaining connected to the same machine.

If you deviate from a saved preset, the Hub will show a **MODIFIED** badge in the "Current State" card.

---

## Edit Mode

To start customizing your dashboard, toggle **Edit Mode** using the floating action button (pencil icon).

### Adding Widgets
1. Tap the **Add Widget** button.
2. Pick a widget type (Gauge, Slider, or Button) and configure its settings.
3. The widget lands on the grid, ready to be dragged into position.

### Managing Widgets
- **Move** -- Long-press and drag a widget to a new spot.
- **Resize** -- Grab the handle at the bottom-right corner to change its grid span.
- **Configure** -- Tap a widget in Edit Mode to open its settings (see [Widget Configuration](widget-configuration.md)).
- **Duplicate** -- Tap "Duplicate" in the configuration dialog to create a copy offset by one grid cell. All settings carry over.
- **Delete** -- Tap the "Delete" button inside the configuration dialog.

---

## Layout Settings

Tap the gear icon in Edit Mode to access global layout settings:

- **Layout Name** -- Give your dashboard a descriptive name.
- **Canvas Background Color** -- Defaults to near-black (`#131313`), which reduces OLED smearing during animations and is easier on the eyes than pure black. Change it per layout to match your environment or branding.
- **Orientation Mode:**
  - **AUTO** -- Follows the device's physical orientation.
  - **LANDSCAPE** -- Locks the HMI to horizontal mode.
  - **PORTRAIT** -- Locks the HMI to vertical mode.
- **Haptic Feedback** -- Toggle tactile vibrations for button presses and slider movements.

---

## Interaction Tips

- **Grid Snapping** -- Widgets always align to the grid. While dragging, a translucent ghost shows you exactly where the widget will land.
