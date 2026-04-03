# Development Guide

A high-level overview of the project's architecture, key technologies, and common development patterns. Use this as a starting point for navigating the codebase and implementing changes.

## Architecture Overview

The project follows a modular, MVVM-based architecture with Hilt for dependency injection.

- **Presentation Layer** -- Jetpack Compose for all UI. State is managed via `ViewModels` and `StateFlow`.
- **Domain Layer** -- Implicitly defined through data models and interfaces in `core:protocol` and `app:data`.
- **Data Layer** -- `DashboardRepository` manages persistence using `Jetpack DataStore (Preferences)` with `GSON` serialization.
- **Communication Layer** -- A protocol layer (`core:protocol`) abstracts industrial PLC communication.
- **Transfer Layer** -- `ConfigTransferManager` handles layout/profile import/export via JSON with schema validation.

## Core Modules

- **`:app`** -- The main entry point. Contains:
    - `dashboard/` -- Main screen, manual 2D grid system, and widget configuration.
    - `widgets/` -- Individual UI widget implementations (Buttons, Gauges, Sliders).
    - `data/` -- Persistence logic, layout models, and configuration transfer managers.
    - `di/` -- Hilt modules for application-wide dependencies.
- **`:core:protocol`** -- Decouples the UI from industrial communication protocols.
    - `PlcCommunicator` -- Core interface for reading/writing PLC tags and attributes.
    - `PlcCommunicatorDispatcher` -- A singleton that switches between implementations (TCP, MQTT, Demo).
- **`:core:ui`** -- The design system.
    - `theme/` -- Design tokens, colors, typography, and shapes.
    - `components/` -- Shared industrial UI components (HUDs, Indicators, Telemetry Cards).
- **`:feature:diagnostics`** -- Independent feature module for system health and diagnostics.

## Key Technologies

| Technology | Usage |
| :--- | :--- |
| **Kotlin Coroutines** | Asynchronous operations, StateFlow for reactive UI updates. |
| **Jetpack Compose** | Declarative UI framework. |
| **Hilt (Dagger)** | Dependency injection. |
| **Jetpack DataStore** | Lightweight persistence for layouts and settings. |
| **Kotlinx Serialization** | JSON serialization for complex objects. |
| **HiveMQ Client** | MQTT protocol implementation. |
| **JSON Schema Validator** | Validates configuration files against `full-backup.schema.json`. |
| **Storage Access Framework** | Android SAF for importing and exporting configuration files. |

## How-To Guides

### Adding a New Widget
1.  **Define Type** -- Add a new entry to `WidgetType` enum in `app:data/WidgetConfiguration.kt`.
2.  **Update Config** -- Add any widget-specific properties to the `WidgetConfiguration` data class.
3.  **Implement UI** -- Create a new `@Composable` in `app:widgets/` that takes `WidgetConfiguration` and state.
4.  **Configuration Dialog** -- Update `WidgetConfigDialog` in `app:dashboard/WidgetPalette.kt` to include inputs for any new properties.
5.  **Register in Container** -- Update `DashboardScreen.kt`'s `WidgetRenderingNode` to switch on the new `WidgetType` and render your composable.
6.  **Palette Defaults** -- Ensure the widget can be created with sensible defaults from the `WidgetPalette`.

### Adding a New Protocol
1.  **Implement Interface** -- Create a new implementation of `PlcCommunicator` in `core:protocol`.
2.  **Update Dispatcher** -- Register the new implementation in `PlcCommunicatorDispatcher.kt`.
3.  **Update Profile** -- Add the protocol to the `Protocol` enum (usually in `PlcConnectionProfile.kt`).
4.  **DI** -- Provide the new implementation in a Hilt module.

### Persistence Patterns
- **Standard Persistence** -- Inject `DashboardRepository`.
- **Flow-based updates** -- Observe `dashboardLayoutFlow` or `connectionProfileFlow`.
- **Saving** -- Use `suspend` functions like `saveLayout(layout)` or `saveConnectionProfile(profile)`.
- Most complex objects are stored as JSON strings in `PreferencesDataStore`.

### Using the Theme
- Use `StitchTheme.tokens` for colors and typography to ensure consistency.
- Example: `color = StitchTheme.tokens.statusRed`
- Note: `HmiTheme` is legacy; `StitchTheme` is the current standard.

### Using the Painter Pattern (Visualization Widgets)
- For complex visualization widgets like Gauges, use the `Painter` abstraction to decouple axis/scale geometry from indicator/needle rendering.
- Implement the `GaugePainter` interface for new axis types (e.g., logarithmic or specialized linear scales).
- `GaugeWidget` acts as a coordinator: it manages telemetry and animation state, then delegates `Canvas` drawing to the selected `GaugePainter`.

## Important Files

| Location | Purpose |
| :--- | :--- |
| `app/.../dashboard/DashboardScreen.kt` | Main UI entry point. |
| `app/.../dashboard/DashboardViewModel.kt` | Core UI logic and state coordination. |
| `app/.../data/DashboardRepository.kt` | All persistence. |
| `app/.../data/ConfigTransferManager.kt` | SAF-based import/export and validation. |
| `core/protocol/.../PlcCommunicator.kt` | Contract for all PLC communication. |
| `core/ui/.../theme/StitchTheme.kt` | Design system entry point. |
| `specs/` | Technical specifications for every implemented feature. |

## Development Tips

- **Diagnostic Logging** -- Use the `:feature:diagnostics` module for debugging protocol issues.
- **Simulation** -- Use `DemoPlcServer` in `core:protocol` for UI development without a real PLC.
- **2D VirtualGrid** -- The layout uses a discrete coordinate system (Columns x Rows). Each widget has global `column` and `row` properties.
- **Manual 2D Paging** -- The dashboard uses a custom paging system based on `currentLogicalPageX/Y` and animated offsets. Navigation is constrained to pages that contain at least one widget (or part of a spanning widget) to prevent navigating into empty space.
- **Widget Variants:**
    - **Sliders** -- Horizontal or Vertical. Sizing logic automatically swaps `colSpan`/`rowSpan` when the variant changes to maintain the intended aspect ratio.
    - **Gauges** -- Decoupled `GaugeAxis` (Arc, Linear Horizontal, Linear Vertical) and `GaugeIndicator` (Fill, Pointer) provide 6 unique visualization combinations.
- **Grid Reflow** -- When orientation changes, the viewport dimensions update and the grid reflows into different page boundaries. See `GridReflowLogic.kt` for the mapping math.

## Build Troubleshooting

### `jlink executable ... does not exist`
This means you have the **JRE** installed instead of the full **JDK**. The Android build needs tools like `jlink` and `javac`.
- **Fix (Debian/Ubuntu)**: `sudo apt install openjdk-17-jdk`

### Invalid Escape in `settings.gradle.kts`
If you see script compilation errors about `Illegal escape: '\.'`, make sure package filtering regexes use double-backslashes (e.g., `"com\\.android.*"`).
