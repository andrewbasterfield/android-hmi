# Research: Industrial Button Shape Verification

## Decision
Utilize `MaterialTheme.shapes.small` for the corner radius of the `IndustrialButton`.

## Rationale
The "Industrial Precision HMI" design specification mandates a "hard industrial" edge with a **2dp corner radius**. This prevents raw "digital sharpness" while maintaining a rugged aesthetic. 

Current project state verification:
- File: `core/ui/src/main/java/com/example/hmi/core/ui/theme/Shape.kt`
- Token: `small = RoundedCornerShape(2.dp)`

By using the theme token rather than a hardcoded 2dp value, the component remains part of the unified design system, allowing for global refinements if the "machined edge" standard evolves.

## Alternatives Considered
1. **RectangleShape (0dp)**: Currently used. Rejected as it creates harsh 90-degree corners that are prone to aliasing on low-res displays and increase visual fatigue in high-contrast Obsidian/Green palettes.
2. **Hardcoded 2dp RoundedCornerShape**: Provides the correct look but breaks the relationship with the design system's `small` shape token, making global updates harder.
