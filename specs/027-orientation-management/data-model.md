# Data Model: Orientation Management (027)

## Entities

### OrientationMode (Enum)
Represents the enforced orientation of the application.
- `AUTO`: Follow the device's physical sensor.
- `FORCE_LANDSCAPE`: Lock the UI to landscape mode.
- `FORCE_PORTRAIT`: Lock the UI to portrait mode.

### DashboardLayout (Updated)
Existing layout configuration extended for orientation and grid management.
- `id`: String (UUID)
- `name`: String
- `orientationMode`: OrientationMode (default: `AUTO`)
- `widgets`: List<WidgetConfiguration>
- `canvasColor`: Long?
- `hapticFeedbackEnabled`: Boolean

### WidgetConfiguration (Existing/Referenced)
- `id`: String (UUID)
- `type`: WidgetType
- `column`: Int (Global X-axis, can be negative)
- `row`: Int (Global Y-axis, can be negative)
- `colSpan`: Int
- `rowSpan`: Int
- ... (other existing fields)

## Relationships
- **DashboardLayout** contains many **WidgetConfiguration** objects.
- All **WidgetConfiguration** coordinates are relative to the **VirtualGrid** defined in the **DashboardLayout**.

## Validation Rules
- **Grid Bounds**: The `DashboardLayout` must automatically extend its virtual boundaries to include the min/max coordinates of all its widgets.
- **Overlap**: Visual overlap is permitted, but the system must not allow widgets to be "lost" (coordinates outside reachable pages).
- **Persistence**: Both `OrientationMode` and `WidgetConfiguration` changes must be persisted immediately to `DataStore`.

## State Transitions
1. **Orientation Change**:
   - Triggered by: Sensor (AUTO) or User Toggle (FORCE_*).
   - Result: `Viewport` dimensions update -> `Reflow` of the `VirtualGrid` into new `Page` boundaries.
2. **Page Navigation**:
   - Triggered by: Swipe or Edge-Swipe in Edit Mode.
   - Result: `PagerState` updates -> Viewport scrolls to show the next segment of the `VirtualGrid`.
3. **Widget Movement**:
   - Triggered by: Drag-and-drop.
   - Result: `WidgetConfiguration.column/row` updates in the global `Layout`.
