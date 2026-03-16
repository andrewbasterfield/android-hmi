# Research: Dark Mode UI Refinement

## Decision: Theme Implementation
- **Choice**: Implement a custom `HmiTheme` using Compose `MaterialTheme` as a base, but with a hardcoded `darkColorScheme` where `surface` and `background` are pure black (#000000).
- **Rationale**: Ensures the requested "black by default" requirement is enforced at the theme level rather than just the initial data state.
- **Alternatives considered**: Manually passing colors to every component (rejected as non-scalable and prone to inconsistency).

## Decision: Typography
- **Choice**: Use System Sans-Serif (Roboto) with specific weight and spacing adjustments to mimic cockpit displays.
- **Rationale**: User requested clarity similar to an aircraft cockpit; Roboto is highly optimized for this on Android without the overhead of custom fonts.
- **Alternatives considered**: Bundling "Inter" or "Lexend" (rejected per user preference for B in clarifications).

## Decision: Adjustable Font Size
- **Choice**: Add `fontSizeMultiplier: Float` to `WidgetConfiguration` (range 0.5f to 2.5f). Use this multiplier against theme-provided `TextStyle` sizes in the widgets.
- **Rationale**: Fulfills the "adjustable font size" requirement while maintaining relative scaling for different text elements (labels vs. values).

## Decision: Curated Color Palette
- **Choice**: Define a `HmiPalette` object containing vibrant, high-value colors (e.g., Bright Green, Safety Orange, Sky Blue, Canary Yellow) that maintain >4.5:1 contrast against black text.
- **Rationale**: Since text MUST be black (FR-002), we must restrict widget backgrounds to colors light enough to ensure legibility and accessibility (A11Y-004).

## Decision: Legacy Migration
- **Choice**: Implement a migration function in `DashboardRepository` or `DashboardViewModel` that detects the version/flag and updates `canvasColor` to black and all widgets to have black text/multipliers if they are null/default.
- **Rationale**: Per user clarification to "wipe the state" or auto-migrate as there is no significant investment.
