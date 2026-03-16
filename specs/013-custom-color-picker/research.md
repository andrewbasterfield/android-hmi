# Research: Custom Color Picker

## Decision: Color Selection UI
- **Choice**: Implement a custom Spectrum picker using a Canvas-based Hue/Saturation/Value (HSV) selector, alongside a standard `OutlinedTextField` for Hex input.
- **Rationale**: While third-party libraries exist, a Canvas-based HSV picker in Compose is lightweight and provides the exact control needed for the "Tabbed Interface" (Palette, Spectrum, Hex) required by the spec. It avoids adding external dependencies for a relatively small UI component.
- **Alternatives considered**: 
    - `ClassicColorPicker` library: Rejected to keep the dependency graph small.
    - Android `ColorPickerDialog` (Legacy): Rejected as it violates the **Compose-First** principle.

## Decision: Contrast Calculation
- **Choice**: Use the WCAG relative luminance formula: `L = 0.2126 * R + 0.7152 * G + 0.0722 * B`.
- **Rationale**: This is the industry standard for determining color contrast. If `L > 0.5`, the text should be Black; otherwise, it should be White. This directly fulfills **FR-006**.
- **Alternatives considered**: 
    - Simple grayscale average: Rejected as it doesn't account for human eye sensitivity to different colors (especially green).

## Decision: Persistence of Recent Colors
- **Choice**: Store the `RecentColorsList` as a JSON-serialized string of `Long` values in `Jetpack DataStore`.
- **Rationale**: The project already uses `DataStore` with `GSON` for layout persistence. Adding a global key for `recent_colors` is consistent with the existing architecture and satisfies the "Global History" requirement.
- **Alternatives considered**: 
    - Room Database: Rejected as overkill for a simple list of 8 colors.

## Decision: Tabbed Interface Implementation
- **Choice**: Use Compose `TabRow` and `HorizontalPager` (or simple `Box` switching).
- **Rationale**: Standard Material 3 pattern for organizing multiple selection modes.
- **Alternatives considered**: 
    - Vertical scrolling: Rejected as it consumes too much screen real estate in a dialog.
