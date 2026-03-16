# Quickstart: Dark Mode UI Refinement

## Core UI Verification

### Dashboard Background
1. Start the application.
2. Observe the dashboard canvas background. It MUST be pure black (#000000).
3. Open "Dashboard Settings" and verify that "Canvas Color" is set to black by default.

### Widget Appearance
1. Add a new widget (Button, Slider, or Gauge).
2. Observe that the text color (label and value) is black (#000000).
3. Verify that "Black" is NOT an option in the color picker for widget background.

### Font Clarity
1. Observe the typography on all labels and values.
2. It MUST be a clear, high-contrast sans-serif font (Roboto) optimized for readability.

### Adjustable Font Size
1. Enter "Edit Mode".
2. Open the configuration dialog for any widget.
3. Use the "Font Size" slider to adjust the size (0.5x to 2.5x).
4. Verify that the widget's text scales immediately in the preview and on the dashboard after saving.

## Automated Testing Strategy

### Unit Tests
- **Contrast Check**: Ensure the curated color palette used in the picker passes the 4.5:1 contrast ratio against black text.
- **Data Model Migration**: Verify that old layouts are correctly migrated to black background and black text on first load.
- **Font Multiplier**: Verify that the `fontSizeMultiplier` is clamped correctly within the 0.5-2.5 range in the view model.

### Instrumentation (UI) Tests
- Verify that opening the app displays a black background.
- Verify that the widget config dialog no longer shows "Black" in the color grid.
- Verify that sliding the font size multiplier actually changes the visual size of the text.
