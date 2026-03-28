# android-ui Development Overview

This document provides a high-level overview of the project's architecture, key technologies, and common development patterns. Use this as a starting point to navigate the codebase and understand how to implement new features or modify existing ones.

## Architecture Overview

The project follows a modular, MVVM-based architecture with Hilt for Dependency Injection.

- **Presentation Layer**: Jetpack Compose is used for the entire UI. State is managed via `ViewModels` and `StateFlow`.
- **Domain Layer**: Implicitly defined through data models and interfaces in `core:protocol` and `app:data`.
- **Data Layer**: `DashboardRepository` manages persistence using `Jetpack DataStore (Preferences)` with `GSON` serialization.
- **Communication Layer**: A specialized protocol layer (`core:protocol`) abstracts industrial PLC communication.
- **Transfer Layer**: `ConfigTransferManager` handles layout/profile import/export via JSON with schema validation.

## Core Modules

- **`:app`**: The main entry point. Contains:
    - `dashboard/`: Main screen, manual 2D grid system, and widget configuration.
    - `widgets/`: Individual UI widget implementations (Buttons, Gauges, Sliders).
    - `data/`: Persistence logic, layout models, and configuration transfer managers.
    - `di/`: Hilt modules for application-wide dependencies.
- **`:core:protocol`**: Decouples the UI from industrial communication protocols.
    - `PlcCommunicator`: Core interface for reading/writing PLC tags and attributes.
    - `PlcCommunicatorDispatcher`: A singleton that switches between different implementations (TCP, MQTT, Demo).
- **`:core:ui`**: The "Stitch" Design System.
    - `theme/`: Design tokens, colors, typography, and shapes.
    - `components/`: Shared industrial UI components (HUDs, Indicators, Telemetry Cards).
- **`:feature:diagnostics`**: An independent feature module for system health and diagnostics.

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

### 1. Adding a New Widget
1.  **Define Type**: Add a new entry to `WidgetType` enum in `app:data/WidgetConfiguration.kt`.
2.  **Update Config**: Add any widget-specific properties to `WidgetConfiguration` data class.
3.  **Implement UI**: Create a new `@Composable` in `app:widgets/` that takes `WidgetConfiguration` and state.
4.  **Configuration Dialog**: Update `WidgetConfigDialog` in `app:dashboard/WidgetPalette.kt` to include inputs for any new properties added in step 2.
5.  **Register in Container**: Update `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`'s `WidgetRenderingNode` to switch on the new `WidgetType` and render your new Composable.
6.  **Palette Defaults**: Ensure the widget can be created with sensible defaults from the `WidgetPalette`.

### 2. Adding a New Protocol
1.  **Implement Interface**: Create a new implementation of `PlcCommunicator` in `core:protocol`.
2.  **Update Dispatcher**: Register the new implementation in `PlcCommunicatorDispatcher.kt`.
3.  **Update Profile**: Add the protocol to the `Protocol` enum (usually in `PlcConnectionProfile.kt`).
4.  **DI**: Ensure the new implementation is provided in a Hilt module.

### 3. Persistence Patterns
- **Standard Persistence**: Inject `DashboardRepository`.
- **Flow-based updates**: Observe `dashboardLayoutFlow` or `connectionProfileFlow`.
- **Saving**: Use `suspend` functions like `saveLayout(layout)` or `saveConnectionProfile(profile)`.
- **Note**: Most complex objects are stored as JSON strings in `PreferencesDataStore`.

### 4. Using the Theme
- Always use `StitchTheme.tokens` for colors and typography to ensure consistency and support for future design system updates.
- Example: `color = StitchTheme.tokens.statusRed`
- Note: `HmiTheme` is legacy; `StitchTheme` is the current project standard.

## Important Files & Locations

| Location | Purpose |
| :--- | :--- |
| `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt` | The main UI entry point. |
| `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt` | Core UI logic and state coordination. |
| `app/src/main/java/com/example/hmi/data/DashboardRepository.kt` | Handles all persistence. |
| `app/src/main/java/com/example/hmi/data/ConfigTransferManager.kt` | Handles SAF-based import/export and validation. |
| `core/protocol/src/main/java/com/example/hmi/protocol/PlcCommunicator.kt` | The contract for all PLC communication. |
| `core/ui/src/main/java/com/example/hmi/core/ui/theme/StitchTheme.kt` | Design system entry point. |
| `specs/` | Comprehensive technical specifications for every implemented feature. |

## Development Tips
- **Diagnostic Logging**: Use the `:feature:diagnostics` module for debugging complex protocol issues.
- **Simulation**: Use `DemoPlcServer` in `core:protocol` for UI development without a real PLC.
- **2D VirtualGrid**: The layout uses a discrete coordinate system (Columns x Rows). Each widget has global `column` and `row` properties.
- **Manual 2D Paging**: The dashboard uses a custom paging system based on `currentLogicalPageX/Y` and animated offsets. Navigation is logically constrained to pages that contain at least one widget (or a portion of a spanning widget) to prevent "losing" the user in empty grid space. This avoids nested `Pager` complexity.
- **Widget Variants**: Certain widgets support multiple orientations (e.g., Sliders can be Horizontal or Vertical). Sizing logic often automatically adjusts (`colSpan`/`rowSpan` swap) when the variant is changed to maintain the intended aspect ratio.
- **Grid Reflow**: When orientation changes, the viewport dimensions update, causing the grid to logically "reflow" into a different set of page boundaries. See `GridReflowLogic.kt` for mapping math.
- **Edge-Swiping (Legacy/Draft)**: Previously mentioned automatic page flip during drag is currently not active; navigation between pages during drag is handled via `onNavigateToPage` upon drag completion if the widget crosses page boundaries.
es.
