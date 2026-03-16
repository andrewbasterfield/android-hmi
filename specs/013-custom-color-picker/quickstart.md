# Quickstart: Custom Color Picker

## Core UI Verification

### Color Selection Tabs
1. Open the "Widget Settings" or "Dashboard Settings" dialog.
2. Observe the new "Color Picker" interface with three tabs: **Palette**, **Spectrum**, and **Hex**.
3. Verify that you can switch between tabs.

### Custom Hex Entry
1. Switch to the **Hex** tab.
2. Enter a valid 6-digit hex code (e.g., `FF00FF`).
3. Verify the color preview updates to Magenta.
4. Observe the "Contrast Indicator": It should show whether Black or White text is being used.
5. Save the widget and verify the color is applied.

### Visual Spectrum Selection
1. Switch to the **Spectrum** tab.
2. Drag the slider or color wheel to pick a color.
3. Verify that the color updates in real-time in the preview.

### Recent Colors History
1. Pick a few custom colors (at least 3 different ones).
2. Open the picker again for a different widget.
3. Observe the "Recent Colors" row.
4. Verify that it contains the previously picked colors.
5. Pick a color from the "Recent Colors" row and verify it is applied.

## Automated Testing Strategy

### Unit Tests
- **Contrast Logic**: Verify the luminance calculation and text color selection for various backgrounds (e.g., `#000000` -> White text, `#FFFFFF` -> Black text).
- **Recent Colors Logic**: Verify that adding a color to the history maintains a limit of 8 and moves the most recent to the front.
- **Hex Validation**: Verify the regex-based validation for 3, 6, and 8-digit hex codes.

### UI Tests (Espresso/Compose)
- Verify that clicking each tab (Palette, Spectrum, Hex) shows the correct content.
- Verify that entering an invalid hex code disables the "Save" button and shows an error message.
- Verify that selecting a color from the "Recent Colors" row updates the selection.
