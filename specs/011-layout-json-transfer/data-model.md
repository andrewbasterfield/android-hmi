# Data Model: JSON Import/Export

## Entities

### DashboardLayout (Serialized)
The primary entity for data transfer.
- **id**: String (UUID)
- **name**: String (Dashboard title)
- **canvasColor**: Long? (Optional background color)
- **widgets**: List of `WidgetConfiguration` objects

### WidgetConfiguration (Serialized)
Detailed settings for individual UI elements.
- **id**: String (UUID)
- **type**: Enum (BUTTON, SLIDER, GAUGE)
- **column/row**: Int (Grid position)
- **colSpan/rowSpan**: Int (Grid size)
- **tagAddress**: String (PLC communication endpoint)
- **customLabel**: String? (Optional UI override)
- **backgroundColor**: Long?
- **minValue/maxValue**: Float? (For sliders/gauges)

## Validation Rules
- **Structural**: Incoming JSON must parse into a `DashboardLayout` object without throwing `JsonSyntaxException`.
- **Integrity**: 
    - `name` MUST NOT be blank.
    - `widgets` MUST be a list (can be empty).
    - Each widget MUST have a valid `type` and `tagAddress`.

## State Transitions
1. **Idle**: User viewing the dashboard.
2. **Exporting**: `DashboardViewModel` serializes current `DashboardLayout` to JSON string -> UI displays in text field.
3. **Importing**: User pastes JSON string -> `DashboardViewModel` validates -> Overwrites DataStore -> UI refreshes.
