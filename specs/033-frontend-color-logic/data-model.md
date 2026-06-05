# Data Model: Frontend-Driven Color Management

## Entities

### ColorZone
Represents a user-defined value range and its associated visual feedback.

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID/String | Unique identifier for the zone. |
| `label` | String | User-friendly name (e.g., "High Temp", "CRITICAL"). |
| `startValue` | Float | The inclusive lower bound of the range. |
| `endValue` | Float | The inclusive upper bound of the range. |
| `color` | Long | The ARGB/Compose color value. |

### WidgetConfiguration (Updated)
The configuration object for a single HMI widget.

| Field | Type | Description |
|-------|------|-------------|
| `tagAddress` | String | The PLC tag to monitor. |
| `colorZones` | List<ColorZone> | The collection of ranges for dynamic coloring. |
| `backgroundColor`| Long? | Static fallback color if no zones match or are defined. |
| `isColorDynamic`| Boolean | Flag to enable/disable value-based color shifts. |

## Relationships
- A `DashboardLayout` contains many `WidgetConfigurations`.
- A `WidgetConfiguration` contains a list of `ColorZones`.

## Validation Rules
- **Range Integrity**: `startValue` must be less than or equal to `endValue`.
- **Unique Labels**: Within a single widget, `ColorZone` labels should ideally be unique for clarity.
- **First-Match Precedence**: The UI will evaluate zones in the order they appear in the `colorZones` list.
