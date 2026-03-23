# Research: Latching Buttons and Indicator Lights

## Decisions

### 1. Interaction Type Selection UI
- **Decision**: Implement a row of `FilterChip` components for selecting `MOMENTARY`, `LATCHING`, and `INDICATOR`.
- **Rationale**: Matches the existing patterns for `GaugeStyle` selection in `WidgetPalette.kt`, ensuring UI consistency and low cognitive load.
- **Alternatives considered**:
    - **Dropdown**: Cleaner if there were 5+ options, but more clicks required.
    - **Standard Radio Buttons**: Standard but less visually integrated into the current "industrial" aesthetic than the chip pattern.

### 2. Write Failure & Latency Strategy
- **Decision**: Optimistic UI update on button click, followed by synchronization with the `tagValues` flow from the backend.
- **Rationale**: Aligns with the current slider implementation in `DashboardViewModel.kt`. By updating the local `_tagValues` map immediately, the user gets instant feedback (<500ms). If the backend write fails or the state is changed externally, the next flow update from `plcCommunicator.observeTag` will naturally correct the UI to the source of truth.
- **Alternatives considered**:
    - **Wait-for-Ack**: High latency; the button would feel "broken" if the backend is slow.
    - **Distinct Error State**: Not currently implemented for other widgets; deferred to prevent "gimmicks" or unnecessary complexity until a global error pattern is established.

### 3. Logic Inversion Implementation
- **Decision**: Handle logic inversion at the UI layer (`ButtonWidget.kt` or `DashboardScreen.kt`).
- **Rationale**: Keeping the protocol/data layer clean (where `true` always means `true` from the PLC) and only inverting the visual presentation. This makes diagnostics easier.
- **Implementation**: `visualIsActive = if (isInverted) !actualValue else actualValue`.

## Best Practices
- **Compose State Management**: Use `derivedStateOf` or simple logic within the composable to resolve the visual state from the raw tag value and the `isInverted` configuration.
- **Data Persistence**: GSON in `DashboardRepository` will automatically handle the new fields in `WidgetConfiguration` as long as they are non-null or have defaults.
