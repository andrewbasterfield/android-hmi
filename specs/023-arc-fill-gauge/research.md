# Research: Arc-Filling Gauge Support

## Terminology: Pointer vs. Needle

- **Decision**: Rename all "Needle" related code to "Pointer" (e.g., `needleColor` → `pointerColor`, `isNeedleDynamic` → `isPointerDynamic`).
- **Rationale**: User request to follow aviation/industrial terminology ("Pointer", "Bug", "Caret", "Chevron"). "Needle" is considered legacy or non-technical in modern HMI contexts.
- **Alternatives considered**: "Chevron", but "Pointer" is more generic and encompasses both the existing triangle shape and future pointer types.

## GSON Migration Strategy

- **Decision**: Use a default value for the new `GaugeStyle` enum in the `WidgetConfiguration` data class. GSON will assign the default if the field is missing from JSON.
- **Rationale**: Ensures backward compatibility with existing layout configurations stored in DataStore without needing complex migration logic. Existing gauges will naturally default to `POINTER` style.
- **Alternatives considered**: Manual JSON parsing/mapping, but GSON's default handling is sufficient for simple enum additions.

## Arc Fill Rendering (Industrial HMI)

- **Decision**: Render a background "track" arc using the theme's `contentColor` at 10-15% opacity, followed by a foreground "fill" arc.
- **Rationale**: Provides high contrast for the active value while still clearly indicating the full scale range. Avoids "gimmicks" and prioritizes readability.
- **Implementation**: Use `Canvas.drawArc` for both. The fill sweep angle will be `(currentValue - minValue) / (maxValue - minValue) * arcSweep`.
- **Alternatives considered**: Solid background fill, but that would conflict with tick visibility and clutter the UI.

## Renaming Audit (Pointer vs Needle)

Affected Files:
1. `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` (fields)
2. `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt` (params and semantics)
3. `app/src/main/java/com/example/hmi/widgets/ColorUtils.kt` (methods)
4. `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt` (labels and state)
5. Multiple UI and unit tests (e.g., `GaugeColorTest.kt`, `GaugeDynamicColorTest.kt`)
