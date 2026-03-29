# Implementation Plan: MQTT JSON Payload Support

**Branch**: `031-mqtt-json-payload-support` | **Date**: 2026-03-29 | **Spec**: [/specs/031-mqtt-json-payload-support/spec.md]
**Input**: Feature specification from `/specs/031-mqtt-json-payload-support/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implement per-widget JSON extraction and write templates for MQTT connections. This feature moves JSON parsing logic from the global connection profile to individual widgets, allowing multiple widgets to subscribe to a single JSON topic and extract different nested values using dot-notation. It also introduces `$VALUE` substitution for outgoing JSON payloads.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Kotlin Coroutines (StateFlow), GSON (for persistence), kotlinx.serialization (for MQTT parsing), HiveMQ MQTT Client (v1.3.3)  
**Storage**: Jetpack DataStore (Preferences with JSON serialization via GSON)  
**Testing**: JUnit 4, Mockito, Espresso, Compose UI Testing  
**Target Platform**: Android 8.0+ (API 26+)
**Project Type**: Mobile App (Industrial HMI)  
**Performance Goals**: JSON parsing and extraction < 50ms latency  
**Constraints**: 100% backward compatibility for existing dashboards; < 200ms p95 for telemetry updates  
**Scale/Scope**: Integration into `core:protocol` (parsing) and `app` (UI/Data models)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: All UI changes to `WidgetConfigDialog` will use Jetpack Compose.
- [x] **Unidirectional Data Flow**: Widget state managed via `DashboardViewModel` with events flowing from UI to `PlcCommunicator`.
- [x] **Test-First**: Unit tests for `extractJsonPath` and integration tests for MQTT topic sharing are required.
- [x] **Accessibility**: Minimum 48dp touch targets for new input fields; content descriptions for JSON status icons.
- [x] **Clarity by Design**: Clear labeling and placeholder examples for JSON Path and Write Template fields.
- [x] **Low Cognitive Load**: JSON fields only visible when MQTT protocol is active.
- [x] **No Gimmicks**: Functional implementation of LWT and payload extraction.
- [x] **Modular Architecture**: Logic encapsulated in `:core:protocol` and `:app`.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
core/protocol/src/main/java/com/example/hmi/protocol/
├── MqttPlcCommunicator.kt          # Shared topic cache, per-widget extraction, write template substitution
├── PlcCommunicator.kt              # Interface: observeTag signature update
├── PlcCommunicatorDispatcher.kt    # Pass-through for updated interface
├── RawTcpPlcCommunicator.kt       # No-op for jsonPath (TCP has no JSON mode)
└── utils/
    └── JsonPathUtils.kt            # New: dot-notation JSON traversal

app/src/main/java/com/example/hmi/
├── data/
│   └── WidgetConfiguration.kt      # Add jsonPath, writeTemplate fields
├── dashboard/
│   ├── DashboardViewModel.kt       # Pass jsonPath/writeTemplate through observe/write calls
│   ├── DashboardScreen.kt          # Pass widget config through to ViewModel
│   └── WidgetPalette.kt            # JSON Path and Write Template input fields
└── data/
    └── LayoutMigrationManager.kt   # Auto-migrate global jsonKey to per-widget jsonPath

app/src/test/java/com/example/hmi/
└── dashboard/
    └── DashboardViewModelTest.kt   # New: unit tests for write template substitution

core/protocol/src/test/java/com/example/hmi/protocol/
├── utils/
│   └── JsonPathUtilsTest.kt        # New: unit tests for path traversal
└── MqttPlcCommunicatorTest.kt      # Integration tests for shared subscriptions
```

**Structure Decision**: Changes span `:core:protocol` (parsing logic, interface changes) and `:app` (data model, UI, ViewModel plumbing). The new `JsonPathUtils` utility lives in `:core:protocol` alongside the communicators that use it.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
