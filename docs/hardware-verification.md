# Hardware Verification Checklist

A QA checklist for verifying the industrial design system on a physical device.

1. **Rugged Aesthetic** -- All dashboard widgets should have `0dp` corners and a `2px` thick border using the `Outline` token.
2. **Stable Readouts** -- Numerical values in Gauges and Sliders should use **Tabular (Monospaced)** figures so digits don't jump around during live updates.
3. **Tactile Feedback** -- Press any button and confirm the **"Inverse Video"** color swap (Color/Black). Check that haptic feedback fires on press (if enabled in settings).
4. **Emergency Signaling** -- Force a `CRITICAL` tag value (e.g., SIM_FUEL < 10%). The header status icon should change to an Error symbol, and the **Emergency HUD** should pulse the screen edges in Red (#93000A) at 2Hz.
5. **Accessibility** -- Every widget should have a high-contrast status icon that conveys state independently of color.
