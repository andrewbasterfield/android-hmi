# Quickstart: Stitch Design System Integration (Industrial Precision HMI)

## Implementation Guide: The Kinetic Cockpit

This feature applies the **"Rugged Functionalism"** design system from Stitch to the Android UI.

### 1. Update the Theme
Define the `StitchTheme` using the obsidian obsidian (#131313) and Status tokens.
- **Color**: Implement `StitchColorPalette` with the "Void" background.
- **Shapes**: Override `MaterialTheme.shapes` to `RectangleShape` (0dp).
- **Typography**: Load `Space Grotesk` (Headlines) and `Inter` (Body).

### 2. Implement the "Inverse Video" Indication
Create a custom `Indication` and `IndicationInstance` that:
- Captures the `Pressed` state from `InteractionSource`.
- Inverts `onPrimary` and `primary` colors immediately.
- Triggers `HapticFeedback` on the transition to `Pressed`.

### 3. Build the Base Components
- `IndustrialButton`: Ensure a minimum height of 64px and apply the custom `Indication`.
- `TelemetryCard`: Use a modular `Box` with a 4px left-accent bar using the `HealthStatus` colors.
- `EmergencyHUD`: Wrap the root screen with a `PeripheralGlow` composable that pulses when the machine state is `CRITICAL`.

### 4. Verification
- Verify high contrast ratios (WCAG AAA).
- Verify 64px touch target alignment in a grid.
- Verify "Inverse Video" provides immediate mechanical feedback on low-end hardware.
