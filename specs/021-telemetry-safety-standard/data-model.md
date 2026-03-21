# Data Model: Telemetry Safety Standard

## Entities

### 1. Telemetry Point
A single stream of industrial data that adheres to SI unit compliance.

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `tagAddress` | String | Unique identifier in the PLC (e.g., "TANK_001_VOLT") | Non-blank |
| `value` | Float | Current numerical readout | N/A |
| `units` | String | SI unit symbol (e.g., "mV", "kW") | Strict case-sensitivity (FR-001) |
| `state` | AlarmState | The current safety status of the point | N/A |

### 2. AlarmState (Enum)
Represents the lifecycle of a safety fault according to ISA-18.2.

- **Normal**: Value is within expected operating thresholds. No visual alerts.
- **Unacknowledged**: A fault has been detected but not yet tapped by the operator. **Visual behavior**: Flashing 2px bounding box (3-5Hz).
- **Acknowledged**: The operator has tapped the alert. The flashing has stopped, but the fault remains unresolved. **Visual behavior**: Persistent Solid Red background/border.

## State Transitions

| From | Event | To | Action |
|------|-------|----|--------|
| Normal | Value exceeds thresholds | Unacknowledged | Start 4Hz pulse on bounding box |
| Unacknowledged | Operator Tap | Acknowledged | Stop pulse; Maintain solid red |
| Unacknowledged/Acknowledged | Value returns to normal | Normal | Clear all visual alerts |
| Acknowledged | Session End (App Restart) | Unacknowledged | Re-trigger pulse (Safety reset) |

## Validation Rules
- **SI Check**: Units MUST be verified against a known SI symbol list. Any attempt to `toUpperCase()` or `toLowerCase()` units in the data layer is a violation of functional safety.
- **Stale Data**: If no update is received for > 5s, the value MUST be rendered as `---` while preserving the unit `units`.
