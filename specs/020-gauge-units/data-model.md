# Data Model: Gauge Units Support

## WidgetConfiguration (Updated)

| Field | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `units` | `String?` | `null` | Optional measurement unit suffix (e.g., "PSI", "°C"). |

### Resolution Logic
- **Readout**: `val displayValue = if (units.isNullOrBlank()) "%.1f".format(value) else "%.1f %s".format(value, units)`
- **Styling**: The unit suffix should use a slightly smaller font size or lighter weight to ensure the numeric value remains the visual "hero".
