# Research: Stitch Design System Integration (Industrial Precision HMI)

## Decision: Implementation Strategy for "Kinetic Cockpit" UI

### 1. Peripheral Pulse Logic
- **Decision**: Use a top-level `Box` with `Modifier.drawBehind` to render a gradient glow.
- **Rationale**: Efficiently pulses without causing full-screen recompositions. Uses `InfiniteTransition` for the pulsing alpha.
- **Alternatives considered**: A separate `Overlay` composable (rejected as too complex for simple peripheral glow).

### 2. Inverse Video Interaction
- **Decision**: Implement a custom `Indication` for the design system.
- **Rationale**: Allows all interactive components to automatically inherit the "Immediate Color Swap" behavior when pressed.
- **Alternatives considered**: Manual state-checking in every button (rejected as unmaintainable).

### 3. Monospaced Telemetry Alignment
- **Decision**: Use `FontFamily.Monospace` with `TextAlign.Right` for numerical data.
- **Rationale**: Ensures that decimal points align vertically, preventing layout "jitter" as values update.

### 4. 0dp Border Radius Strategy
- **Decision**: Override the standard `Shapes` in `MaterialTheme` and enforce `RectangleShape` for all components.
- **Rationale**: Guarantees consistency across all standard and custom components.

## Findings Summary

- **Touch Targets**: Standard Material3 components use 48dp. We will explicitly set `Modifier.heightIn(min = 64.dp)` for all tactile targets.
- **Typography**: Space Grotesk must be loaded as a custom font family in `ui/theme/Type.kt`.
- **Haptic Feedback**: The project already has a haptic toggle. We will integrate this into the custom `Indication` so that every "Inverse Video" swap triggers a physical vibration.
