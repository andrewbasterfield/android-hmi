# Data Model: Colored Buttons

This document defines the data structures and relationships for the "Colored Buttons" feature.

## 1. Entities

### `WidgetConfiguration` (Updated)

Represents the configuration for an individual HMI widget on the dashboard.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Unique identifier (UUID). |
| `type` | `WidgetType` | Type of widget (BUTTON, SLIDER, GAUGE). |
| `x` | `Float` | X-coordinate on the dashboard. |
| `y` | `Float` | Y-coordinate on the dashboard. |
| `width` | `Float` | Width of the widget. |
| `height` | `Float` | Height of the widget. |
| `tagAddress` | `String` | PLC tag address the widget is bound to. |
| `backgroundColor` | `Long?` | **[NEW]** ARGB value of the background color. If null, use system primary. |
| `minValue` | `Float?` | Minimum value for range-based widgets. |
| `maxValue` | `Float?` | Maximum value for range-based widgets. |

### `ColorPalette` (New)

A static collection of colors available for the user to choose from.

| Field | Type | Description |
|-------|------|-------------|
| `name` | `String` | Human-readable name (e.g., "Red", "Green"). |
| `value` | `Long` | ARGB value of the color. |

## 2. Validation Rules

1. **Color Selection**: Users must only be able to select from the predefined `ColorPalette` in the UI, although the data model supports any `Long` value for future flexibility.
2. **Persistence**: The `backgroundColor` must be stored as a `Long` in the dashboard layout configuration.
3. **Contrast**: The system MUST automatically calculate the text color (Black or White) based on the `backgroundColor` before rendering the `ButtonWidget`.
