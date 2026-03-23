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
- Kotlin 1.9+ + Jetpack Compose (UI), Hilt (DI), Kotlin Coroutines (Async), Space Grotesk & Inter Fonts (016-stitch-design-system)
- Jetpack DataStore (for persisting UI preferences like haptic feedback or layout overrides) (016-stitch-design-system)
- Kotlin 1.9+ + Jetpack Compose, Hilt, :core:ui (existing), :protocol (live data) (017-dashboard-design-integration)
- Jetpack DataStore (existing layout persistence) (017-dashboard-design-integration)
- Kotlin 1.9.22 + Jetpack Compose, Hilt, Kotlin Coroutines (StateFlow), :core:ui (Design Tokens), :protocol (PLC Data) (017-dashboard-design-integration)
- Kotlin 1.9+ + Jetpack Compose, Hilt, :core:ui, :protocol (Live PLC Data) (017-dashboard-design-integration)
- Jetpack DataStore (JSON via GSON) (018-gauge-tick-config)
- Jetpack DataStore (Preferences storing JSON via GSON) (018-gauge-tick-config)
- Kotlin 1.9.22 + Jetpack Compose, Hilt, Jetpack DataStore (Preferences), GSON, Kotlin Coroutines (StateFlow) (019-customize-gauge-colors)
- Kotlin 1.9.22 + Jetpack Compose, Hilt, Jetpack DataStore, GSON (023-arc-fill-gauge)

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
- 024-button-switch-indicator: Added Kotlin 1.9.22 + Jetpack Compose, Hilt, Jetpack DataStore, GSON
- 023-arc-fill-gauge: Added Kotlin 1.9.22 + Jetpack Compose, Hilt, Jetpack DataStore, GSON
- 022-industrial-button-shape: Added Kotlin 1.9.22 + Jetpack Compose, Hilt


<!-- MANUAL ADDITIONS START -->
You are a senior software engineer. Your primary goal is to produce thoughtful, high-quality, and maintainable code. Never rush to write a quick patch. 

You must strictly adhere to the following workflow for every request:

1. DIAGNOSE & PLAN: Analyze the problem, identify the root cause, and propose a high-level, step-by-step solution in plain text.
2. WAIT: Do NOT write or output any code blocks until the user explicitly approves the plan.
3. EXECUTE: Only after approval, output the required code.
4. REVERT & RETHINK: If the user reports that an experiment or approach failed, immediately discard that mental thread. Acknowledge that you are reverting to the last known working state. Analyze the failure and propose a fundamentally new approach. Do NOT attempt to patch a failed patch.
<!-- MANUAL ADDITIONS END -->
