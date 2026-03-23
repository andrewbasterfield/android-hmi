# Quickstart: Latching Buttons and Indicator Lights

This feature introduces latching and indicator behaviors for button widgets, allowing them to act as switches or status lights.

## For Dashboard Designers
1. Open the **Widget Configuration** dialog for a button.
2. Select an **Interaction Type**:
   - **MOMENTARY**: Standard push-button (default).
   - **LATCHING**: Toggle switch.
   - **INDICATOR**: Read-only status light.
3. Toggle **Invert Logic** if the backend uses Active-Low logic (e.g., `false` = Active).
4. Save the configuration.

## For Developers
### 1. Data Model
`WidgetConfiguration.kt` now includes `InteractionType` and `isInverted`. GSON handles the serialization automatically; default values ensure backward compatibility.

### 2. ViewModel Logic
`DashboardViewModel.onButtonPress` has been updated to check the `interactionType`. 
- If `LATCHING`, it toggles the current value in `_tagValues` and writes the new value to the PLC.
- If `INDICATOR`, clicks are ignored.

### 3. UI Layer
- `ButtonWidget.kt` now accepts `isChecked` and `isInteractive` parameters.
- `DashboardScreen.kt` resolves the `visualState` based on `tagValue` and `isInverted`.

## Testing the Feature
1. **Latching Test**: Configure a button as "LATCHING". Click it; verify it stays in the "Active" color state. Click again; verify it reverts.
2. **Indicator Test**: Configure a button as "INDICATOR". Update the tag via the demo server or diagnostics. Verify the button's color changes automatically without user interaction.
3. **Inversion Test**: Enable "Invert Logic". Verify a `false` tag state results in the "Active" (Identity Swap) visual state.
