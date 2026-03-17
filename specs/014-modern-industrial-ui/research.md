# Research: Modern Industrial UI

## Decision: Global Shape Management
- **Choice**: Implement a helper function `WidgetShapes.getShapeForSize(colSpan, rowSpan)` that returns `RoundedCornerShape(8.dp)` for most widgets and `RoundedCornerShape(4.dp)` for 1x1 widgets.
- **Rationale**: Centralizes the "Adaptive Radius" decision logic, making it easier to adjust global aesthetics later.
- **Alternatives considered**: Hardcoding shapes in each widget (rejected as it causes maintenance drift).

## Decision: Contrast Logic Override
- **Choice**: Modify `ColorUtils.getContrastColor` to accept an optional `forceBlack: Boolean` parameter, or create a specialized `IndustrialColorUtils` that always returns Black if the background luminance is above the new safety floor.
- **Rationale**: Directly fulfills **FR-003** and **FR-004** by prioritizing the "Black Text" look while ensuring legibility.
- **Alternatives considered**: Reverting all widgets to white text (rejected as it violates the core aesthetic request).

## Decision: Background Luminance Floor
- **Choice**: Implement a clamping function in `ColorUtils` that converts dark colors to a minimum of 20% lightness (HSL) or a specific luminance threshold before applying them to widget backgrounds.
- **Rationale**: Ensures that even if a user tries to pick a near-black color, the "Black Text" mandate remains accessible (WCAG 4.5:1).
- **Alternatives considered**: Blocking the color picker selection (rejected as it provides a worse UX than automatic clamping).
