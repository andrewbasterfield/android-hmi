# Quickstart: Gauge Color Customization

## Setting Up Custom Gauge Colors

1.  **Open Dashboard**: Launch the app and enter "Edit Mode."
2.  **Edit Gauge**: Long-press on any Gauge widget to open the configuration dialog.
3.  **Static Styling**:
    *   Find the **Needle Color** section. Choose a color from the palette or enter a Hex code.
    *   Find the **Scale Color** section. Choose a color for the ticks and labels.
4.  **Enable Dynamic Thresholds**:
    *   Toggle **Dynamic Needle Color** to ON.
    *   Ensure you have at least one **Color Zone** defined (e.g., Red for 80-100).
    *   Observe how the needle color switches to Red automatically when the value increases.
5.  **Reset**: Tap the "Palette" icon in the color picker to reset any color to the default theme-based style.

## Development Verification

To verify your changes:
1.  Run the app with `./gradlew installDebug`.
2.  Navigate to the main dashboard.
3.  Adjust a gauge's value (e.g., via the slider if bound to the same tag).
4.  Verify the needle color transition at the threshold points.
