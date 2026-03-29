# Data Model: Linear Gauges (030)

## Entities

### GaugeAxis (Enum)
Represents the path/axis along which the gauge data is visualized.
- `ARC`: Standard curved axis (Default).
- `LINEAR_HORIZONTAL`: Flat horizontal axis.
- `LINEAR_VERTICAL`: Flat vertical axis.

### GaugeIndicator (Enum)
Represents the visual indicator style used to show the current value.
- `POINTER`: A triangle caret moving along the axis (Default).
- `FILL`: A solid bar filling from minimum to current value.

### WidgetConfiguration (Updated)
Existing widget configuration extended for decoupled gauges.
- `id`: String (UUID)
- `type`: WidgetType (e.g., `GAUGE`)
- `gaugeAxis`: GaugeAxis (Default: `ARC`)
- `gaugeIndicator`: GaugeIndicator (Default: `POINTER`)
- ... (existing fields: `minValue`, `maxValue`, `colorZones`, `targetTicks`)

## State Transitions

### Configuration Update
- **Trigger**: User selects a new Axis or Indicator in `WidgetConfigDialog`.
- **Logic**:
  - If Axis switches from `ARC` to `LINEAR_*`, current `arcSweep` property is ignored but preserved in data.
  - If Axis switches to `LINEAR_VERTICAL`, `colSpan` and `rowSpan` are NOT automatically swapped (unlike Slider), as gauges often fit square or rectangular cells equally well. User must manually resize if needed.
- **Persistence**: Saved to `DataStore` via `DashboardViewModel`.

## Validation Rules
- **Orthogonality**: All 6 combinations of `GaugeAxis` x `GaugeIndicator` MUST be supported.
- **Consistency**: Linear axes MUST respect the same `colorZones` and `targetTicks` logic as the Arc axis.
