# Data Model: UI Refinement (Grid System)

This document defines the updated data structures for the grid-based HMI layout.

## 1. Entities

### `WidgetConfiguration` (Updated)

Represents the settings for an individual HMI widget, now using grid-based coordinates.

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Unique identifier (UUID). |
| `type` | `WidgetType` | Type of widget (BUTTON, SLIDER, GAUGE). |
| `column` | `Int` | **[NEW]** Starting column index in the grid. |
| `row` | `Int` | **[NEW]** Starting row index in the grid. |
| `colSpan` | `Int` | **[NEW]** Number of grid columns the widget spans (default 1). |
| `rowSpan` | `Int` | **[NEW]** Number of grid rows the widget spans (default 1). |
| `tagAddress` | `String` | PLC tag address the widget is bound to. |
| `backgroundColor` | `Long?` | ARGB value for the background. |
| `minValue` | `Float?` | Minimum value for range-based widgets. |
| `maxValue` | `Float?` | Maximum value for range-based widgets. |

## 2. Layout Logic

1. **Cell Size**: Each cell is exactly 80dp x 80dp.
2. **Dynamic Constraints**: The number of available columns and rows is calculated based on screen size: `floor(screenWidthDp / 80)`.
3. **Snapping**: During editing, all `x, y` and `width, height` changes are immediately converted to the nearest `column, row, colSpan, rowSpan` values.
4. **Validation**: Minimum `colSpan` and `rowSpan` are 1. Widgets cannot be placed outside the current grid boundaries.

## 3. Persistence

- Data is persisted via DataStore in JSON format using GSON.
- Older layouts will need to be migrated or reset to use the new grid-based coordinate system.
