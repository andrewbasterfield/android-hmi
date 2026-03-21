# Research: Gauge Color Customization

## Decision: Dynamic Needle Color Implementation
- **Decision**: Calculate the active zone color directly within the `Canvas` draw scope using the `animatedValue`.
- **Rationale**: This avoids frequent recompositions that would occur if the color was calculated in the `@Composable` body using `remember(animatedValue)`. Since the needle is already being redrawn every frame during animation, calculating its color at the same time is highly efficient.
- **Implementation**:
  ```kotlin
  val activeZone = colorZones.find { animatedValue >= it.startValue && animatedValue <= it.endValue }
  val currentNeedleColor = when {
      isNeedleDynamic && activeZone != null -> ColorUtils.toColor(activeZone.color)
      needleColor != null -> ColorUtils.toColor(needleColor)
      else -> contentColor
  }
  ```

## Decision: HmiColorPicker Integration
- **Decision**: Reuse `HmiColorPicker` as-is for Needle and Scale color selection.
- **Rationale**: Research confirms that `HmiColorPicker` already handles `null` values by providing a "Reset to Default" visual (using the `Icons.Default.Palette` icon). This aligns perfectly with the requirement to allow resetting individual colors to "Default" (FR-006).

## Decision: Data Model Persistence
- **Decision**: Update `WidgetConfiguration` with nullable `needleColor: Long?`, `scaleColor: Long?`, and `isNeedleDynamic: Boolean = false`.
- **Rationale**: Using nullable `Long` maintains compatibility with the existing JSON persistence via GSON. If the fields are missing in old configurations, they will default to `null` (or `false` for the boolean), ensuring graceful degradation and a "Standard" starting point for existing gauges.

## Decision: Scale and Label Coloring
- **Decision**: Apply `scaleColor` to both the tick marks (Canvas) and the numeric labels (Text components).
- **Rationale**: Provides a consistent visual "Scale" theme as requested in US3. Labels will use the custom color if provided, falling back to `LocalContentColor` with appropriate alpha.

## Alternatives Considered
- **Alternative**: Pre-calculating the needle color in the ViewModel.
- **Rejected**: This would add unnecessary complexity to the data layer for a purely visual/transient state derived from the live value. The UI-layer animation already has access to the value and zones.
