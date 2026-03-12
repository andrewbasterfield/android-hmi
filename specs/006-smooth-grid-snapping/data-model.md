# Data Model: Smooth Grid Snapping

This feature does not introduce new persistent entities or modify the database schema. It utilizes transient UI state to manage interaction smoothness.

## 1. Transient UI State (In-Memory)

The following state is maintained in the `DashboardScreen` during active gestures:

| Field | Type | Description |
|-------|------|-------------|
| `draggingWidgetId` | `String?` | ID of the widget currently being moved. |
| `dragOffset` | `Offset` | Raw pixel accumulation of the current drag gesture. |
| `ghostColumn` | `Int` | Predicted snap column based on `dragOffset`. |
| `ghostRow` | `Int` | Predicted snap row based on `dragOffset`. |
| `isResizing` | `Boolean` | Flag to indicate an active resize operation. |

## 2. Validation Rules

1.  **Ghost Constraining**: `ghostColumn` and `ghostRow` must always be clamped within the range of `[0, maxColumns - colSpan]` and `[0, maxRows - rowSpan]`.
2.  **Animation Trigger**: Persistence (ViewModel update) is only triggered on `onDragEnd` or `onResizeEnd`. This ensures animations complete before the "stable" grid state is rewritten.
