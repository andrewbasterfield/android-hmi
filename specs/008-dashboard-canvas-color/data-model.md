# Data Model: Dashboard Canvas Color

This document defines the updated data structures for the dashboard layout.

## 1. Entities

### `DashboardLayout` (Updated)

Represents the overall configuration for a single HMI dashboard.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Unique identifier (UUID). |
| `name` | `String` | Human-readable name of the layout. |
| `canvasColor` | `Long?` | **[NEW]** ARGB value of the dashboard background. Null uses theme default. |
| `widgets` | `List<WidgetConfiguration>` | Collection of widgets contained in this layout. |

## 2. Validation Rules

1. **Color Source**: The `canvasColor` MUST be selected from the predefined `ColorPalette` in the UI to ensure consistency.
2. **Persistence**: Changes to `canvasColor` MUST be immediately reflected in the `DashboardViewModel` and saved via the `DashboardRepository`.

## 3. Migration

- **Compatibility**: GSON will default the `canvasColor` to `null` when loading older layout JSON files, ensuring zero breakage for existing users.
