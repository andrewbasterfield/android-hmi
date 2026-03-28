# Data Model: Widget Duplication

## Entities

### WidgetConfiguration
Represents the persistent state of a single UI component on the dashboard.

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | `String` | Unique identifier (UUID). Re-generated for duplicates. |
| `type` | `WidgetType` | ENUM: BUTTON, SLIDER, GAUGE. Copied to duplicate. |
| `column` | `Int` | X-coordinate in the grid. Incremented by 1 for duplicates. |
| `row` | `Int` | Y-coordinate in the grid. Incremented by 1 for duplicates. |
| `colSpan` | `Int` | Horizontal size in grid cells. Copied to duplicate. |
| `rowSpan` | `Int` | Vertical size in grid cells. Copied to duplicate. |
| `tagAddress` | `String` | PLC tag mapping. Copied to duplicate. |
| `customLabel` | `String?` | Optional label. Copied identically to duplicate. |
| `backgroundColor` | `Long?` | Hex color. Copied to duplicate. |
| `zOrder` | `Int` | Depth ordering. Incremented to `max(zOrder) + 1` for duplicates. |
| `...` | `Any` | All other widget-specific configurations (scaling, interaction modes) are copied. |

### DashboardLayout
Aggregate container for the dashboard configuration.

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | `String` | Dashboard name. |
| `widgets` | `List<WidgetConfiguration>` | Collection of all widgets. Duplication appends a new instance here. |

## State Transitions

### Duplication Logic
1. **User Event**: User clicks "Duplicate" in `WidgetConfigDialog`.
2. **ViewModel Interaction**: `DashboardViewModel.duplicateWidget(widgetId)` is invoked.
3. **Copy Operation**:
   - `source = widgets.find { it.id == widgetId }`
   - `duplicate = source.copy(id = UUID.randomUUID().toString(), column = source.column + 1, row = source.row + 1, zOrder = widgets.maxOf { it.zOrder } + 1)`
4. **State Update**: `_dashboardLayout.update { layout -> layout.copy(widgets = layout.widgets + duplicate) }`.
5. **Persistence**: `DashboardRepository.saveLayout(newLayout)` is triggered.
6. **UI Refresh**: `DashboardScreen` recomposes, selecting the new widget.
