# Research: Colored Buttons

This document outlines the research and technical decisions for implementing solid color customization for buttons in the HMI application.

## 1. Color Serialization

**Decision**: Store colors as ARGB `Long` values in `WidgetConfiguration`.
**Rationale**: 
- `androidx.compose.ui.graphics.Color` can be easily converted to/from `Long` using `Color.toArgb()` and `Color(Int)`.
- `Long` is a primitive type that is easily serialized by DataStore (both Preferences and Protobuf).
- It allows for future flexibility if we decide to allow custom colors beyond the predefined palette.

**Alternatives Considered**:
- Hex Strings (e.g., "#FF0000"): Human-readable in storage but requires more parsing logic.
- Enum IDs: Type-safe for the palette, but less flexible if colors are added or changed.

## 2. Automatic Text Contrast (Luminance)

**Decision**: Use Compose's built-in `Color.luminance()` method to determine text color.
**Rationale**:
- `Color.luminance()` calculates the relative luminance of a color based on the W3C formula.
- A threshold of `0.5` is standard for switching between light and dark text.
- Implementation: `if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White`.

## 3. Predefined Color Palette

**Decision**: Provide a curated set of 8 industrial-standard colors.
**Rationale**: Industrial HMIs benefit from consistent, high-visibility colors.

| Color | Hex | Purpose |
|-------|-----|---------|
| Primary | Theme | Default |
| Red | `#D32F2F` | Stop, Emergency, Error |
| Green | `#388E3C` | Start, Run, Success |
| Yellow | `#FBC02D` | Warning, Caution |
| Blue | `#1976D2` | Info, Manual Mode |
| Gray | `#757575` | Inactive, Neutral |
| Black | `#000000` | High Contrast |
| White | `#FFFFFF` | High Contrast |

*Note: Using Material Design "700" shades for better visibility than pure #FF0000/etc.*

## 4. UI for Color Selection

**Decision**: Add a "Color Picker" section to the widget configuration dialog.
**Rationale**: 
- A horizontal scrollable `Row` of color swatches is touch-friendly and intuitive.
- Each swatch will show a checkmark or border when selected.
- This will be integrated into the existing `AddWidgetDialog` and a new `EditWidgetDialog` (planned for 002).

## 5. Persistence via DataStore

**Decision**: Update `WidgetConfiguration` and ensure the `DashboardRepository` handles the new field.
**Rationale**: Existing infrastructure already uses DataStore for `DashboardLayout`. Adding a field to `WidgetConfiguration` is a low-risk change.
