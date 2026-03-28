# Data Model: Vertical Slider Variant (029)

## Entities

### WidgetOrientation (Enum)
Represents the orientation of the widget (primarily used by Sliders).
- `HORIZONTAL`: Standard horizontal layout (Default).
- `VERTICAL`: Vertical layout with upward increase.

### WidgetConfiguration (Updated)
Existing widget configuration extended for orientation.
- `id`: String (UUID)
- `type`: WidgetType
- ... (existing fields)
- `orientation`: WidgetOrientation (Default: `HORIZONTAL`)
- ... (existing fields)

## State Transitions
1. **Orientation Toggle**:
   - Triggered by: User selection in `WidgetConfigDialog`.
   - Effect: Swaps `colSpan` and `rowSpan`.
   - Persistence: Saved immediately to `DataStore` via `DashboardViewModel`.

## Validation Rules
- **Consistency**: The `orientation` field is respected for `SLIDER` widgets. For other types, it may be ignored or used for layout purposes (e.g., vertical button labels in the future).
- **Dimension Swap**: The swap logic `(newColSpan = oldRowSpan, newRowSpan = oldColSpan)` MUST be triggered only on the orientation toggle change event, not on every layout load. This is typically handled within the `DashboardViewModel` or `DashboardSettingsDialog` logic.
