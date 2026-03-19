# android-ui Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-10

## Active Technologies
- Kotlin (Latest stable) + Jetpack Compose, Kotlin Coroutines (StateFlow), Hilt, Jetpack DataStore (003-colored-buttons)
- Jetpack DataStore (Protobuf or Preferences) for layout persistence. (003-colored-buttons)
- Kotlin (Latest stable) + Jetpack Compose, Hilt, Jetpack DataStore, GSON (004-ui-refinement)
- Jetpack DataStore (Preferences) storing JSON via GSON. (004-ui-refinement)
- Kotlin (Latest stable) + Jetpack Compose (Animation, Gestures), Kotlin Coroutines (006-smooth-grid-snapping)
- Kotlin (Latest stable) + Kotlin Coroutines, Jetpack Compose, Hilt (009-builtin-demo-server)
- N/A (Server state is in-memory for the demo session) (009-builtin-demo-server)
- Kotlin (Latest stable) + Jetpack Compose, Kotlin Coroutines, Hilt, Jetpack DataStore (010-widget-dynamic-attributes)
- Jetpack DataStore (persistent configuration), In-memory StateFlow (transient session overrides) (010-widget-dynamic-attributes)
- Kotlin (Latest stable) + Jetpack Compose, GSON, Hilt, Jetpack DataStore (011-layout-json-transfer)
- Kotlin 1.9+ + Jetpack Compose, Hilt, Jetpack DataStore, GSON (012-dark-mode-ui-refinement)
- Jetpack DataStore (Preferences with JSON serialization) (012-dark-mode-ui-refinement)

- Kotlin (Latest stable) + Jetpack Compose, Kotlin Coroutines (StateFlow/SharedFlow), Hilt (001-hmi-control-panel)

## Project Structure

```text
src/
tests/
```

## Commands

- **Build Project**: `./gradlew build`
- **Assemble Debug APK**: `./gradlew :app:assembleDebug`
- **Run Unit Tests**: `./gradlew test`
- **Run Instrumentation Tests**: `./gradlew connectedDebugAndroidTest`
- **Clean Project**: `./gradlew clean`

## Code Style

Kotlin (Latest stable): Follow standard conventions

## Recent Changes
- 015-ui-animations-gauge-improvement: Implemented 3D tactile button feedback (Scale + Elevation + Offset), intelligent "Reverse-Video" color swap logic, aviation-style 270° Canvas gauges with dynamic color zones, and "Nice Number" scale tick algorithm. Added layout-level haptic feedback toggle. Updated project Constitution to v1.1.0.
- 014-modern-industrial-ui: Added [if applicable, e.g., PostgreSQL, CoreData, files or N/A]
- 013-custom-color-picker: Added [if applicable, e.g., PostgreSQL, CoreData, files or N/A]
- 012-dark-mode-ui-refinement: Added Kotlin 1.9+ + Jetpack Compose, Hilt, Jetpack DataStore, GSON


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
