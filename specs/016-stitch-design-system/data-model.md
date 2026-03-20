# Data Model: Stitch Design System Integration (Industrial Precision HMI)

## Entities

### TelemetryData
Represents a single point of machine data for the "Readout" card.
- `id`: String (Unique identifier)
- `label`: String (Uppercase technical label, e.g., "ENGINE I")
- `value`: String (Numerical or status text)
- `unit`: String (Uppercase unit of measure, e.g., "RPM")
- `status`: HealthStatus (Enum: NORMAL, CAUTION, CRITICAL)
- `trend`: Float? (Optional delta from previous value)

### StitchThemeTokens
The core visual tokens derived from the Stitch specification.
- `colorScheme`: Custom Dark Scheme (#131313 Void background)
- `typography`: 
    - `Display`: Space Grotesk (Stenciled style)
    - `Body`: Inter
- `shapes`: Rectangular (0dp radius)
- `interactiveBezel`: 2dp Solid stroke

### InteractionState
Represents the tactile state of any industrial component.
- `state`: UIState (IDLE, PRESSED, ACTIVE, ERROR, DISABLED)
- `hapticEnabled`: Boolean (From system settings)

## Enums

### HealthStatus
- **NORMAL**: Green (#00e639)
- **CAUTION**: Amber (#feaa00)
- **CRITICAL**: Red (#93000a)

### UIState
- **IDLE**: Standard state
- **PRESSED**: Immediate "Inverse Video" swap
- **ACTIVE**: Sustained ON state
- **ERROR**: Flashing or Red-bordered
- **DISABLED**: Muted with reduced alpha
