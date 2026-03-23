# Data Model: Arc-Filling Gauge Support

## Enums

### GaugeStyle
Defines the visual representation of the gauge's current value.

| Value | Description |
|-------|-------------|
| `POINTER` | (Default) Renders a triangular caret/chevron pointing to the current value. |
| `ARC_FILL` | Renders a solid arc segment that fills up from the start of the scale to the current value. |

## Entities

### WidgetConfiguration (Updated)
Represents the persistent state of a dashboard widget.

| Field | Type | Description |
|-------|------|-------------|
| `pointerColor` | `Long?` | Static color for the pointer or filling arc (formerly `needleColor`). |
| `isPointerDynamic` | `Boolean` | Whether the color should match the active zone (formerly `isNeedleDynamic`). |
| `gaugeStyle` | `GaugeStyle` | The selected visual style for the gauge. Defaults to `POINTER`. |

## State Transitions
1.  **Switching Styles**: When `gaugeStyle` is changed in the configuration UI, the `GaugeWidget` recomposes immediately, toggling between the caret and the filling arc.
2.  **Color Resolution**: Both styles share the same color resolution logic (Priority: Active Zone > Static Pointer Color > Default Theme Content Color).
