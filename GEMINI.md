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
- 011-layout-json-transfer: Added Kotlin (Latest stable) + Jetpack Compose, GSON, Hilt, Jetpack DataStore
- 010-widget-dynamic-attributes: Added Kotlin (Latest stable) + Jetpack Compose, Kotlin Coroutines, Hilt, Jetpack DataStore
- 009-builtin-demo-server: Added Kotlin (Latest stable) + Kotlin Coroutines, Jetpack Compose, Hilt


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
