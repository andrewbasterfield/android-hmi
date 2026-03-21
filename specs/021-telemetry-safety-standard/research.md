# Research: Telemetry Safety Standards (SI Compliance & ISA-18.2)

## Decisions

### 1. SI Unit Compliance Formatting
- **Decision**: Implement a custom `SiFormatter` utility to handle strict case-sensitivity for SI symbols.
- **Rationale**: Strict compliance with SI (International System of Units) is a safety requirement to prevent misinterpretation of magnitudes (e.g., `mV` vs `MV`). Standard Android/Kotlin string capitalization functions are insufficient.
- **Alternatives considered**: 
    - Using `String.toUpperCase()` (Rejected: Violates safety protocol for units).
    - Hardcoding units in strings (Rejected: Difficult to maintain and validate).

### 2. ISA-18.2 Alarm Protocol Implementation
- **Decision**: Introduce a `LocalAlarmState` within `GaugeWidget` and `TelemetryCard` that tracks `Normal`, `Unacknowledged`, and `Acknowledged`. 
- **Rationale**: Compliance with ISA-18.2 requires separating the "conspicuity" (flashing) from the "status" (color). The text must remain static to ensure readability during a crisis.
- **Alarm Pulse**: Use a Compose `infiniteTransition` with a 3-5Hz (200ms - 333ms) cycle for the bounding box.
- **Flash Suppression**: Tapping the widget will transition the state to `Acknowledged` in the ViewModel.

### 3. "Hard Industrial" 2px Border Radius
- **Decision**: Update `core:ui`'s `Shape.kt` and `StitchTheme` to use `RoundedCornerShape(2.dp)` for small and medium tokens.
- **Rationale**: Aligns with `@DESIGN.md` "Rugged Functionalism" North Star. 2px provides a "machined" look that avoids the cheapness of a raw 90-degree digital corner while maintaining professional rigidity.

### 4. Touch Target & Clearance
- **Decision**: Maintain a 64px (48dp + padding) baseline for buttons but strictly enforce a `spacing-4` (16dp) minimum dead space between interactive widgets in the `DashboardGrid`.
- **Rationale**: Prevents accidental triggers without requiring a full recalculation of absolute physical metrics for the current Hilt-based dependency injection scope.

## Unknowns & Clarifications

### [RESOLVED] Alarm Persistence
- **Decision**: Alarm acknowledgement is **session-scoped**. If the app is restarted while a fault persists, the alarm returns to the `Unacknowledged` (flashing) state.
- **Rationale**: Safety first. An operator must re-acknowledge a fault upon taking control (new session).
