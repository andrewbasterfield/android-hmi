# Data Model: Modern Industrial UI

## Entities

### WidgetConfiguration (Existing)
Used to determine the shape and color of widgets.
- **Attributes used**: `colSpan`, `rowSpan`, `backgroundColor`.

## Validation Rules

- **Adaptive Radius Rule**: 
    - Radius = 4dp IF `colSpan == 1` AND `rowSpan == 1`.
    - Radius = 8dp OTHERWISE.
- **Luminance Floor Rule**:
    - Minimum background luminance threshold: 0.2 (20%).
    - If `backgroundColor` has luminance < 0.2, it MUST be clamped to 0.2.
- **Contrast Rule**: 
    - All foreground text on widgets MUST be `#000000` (Black).
