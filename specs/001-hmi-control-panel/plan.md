# Implementation Plan: HMI Control Panel

**Branch**: `001-hmi-control-panel` | **Date**: 2026-03-10 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-hmi-control-panel/spec.md`

## Summary

Build an Android application that serves as an easily customizable HMI/SCADA control panel. It allows operators to connect to PLCs via TCP/IP and provides an "Edit Mode" for engineers to intuitively configure dashboards with buttons, sliders, and gauges bound to PLC data tags.

## Technical Context

**Language/Version**: Kotlin (Latest stable)
**Primary Dependencies**: Jetpack Compose, Kotlin Coroutines (StateFlow/SharedFlow), Hilt
**Storage**: Jetpack DataStore (for persisting dashboard layout and connection profile)
**Testing**: JUnit 4/5, MockK, Jetpack Compose UI Tests
**Target Platform**: Android (Phones and Tablets, API 24+)
**Project Type**: Mobile Application
**Performance Goals**: UI updates with perceived latency < 200ms; fluid 60fps drag-and-drop in edit mode.
**Constraints**: Must maintain stable TCP/IP connection; robust error handling for network drops.
**Scale/Scope**: Local device storage, single PLC connection active at a time.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Does the plan utilize Jetpack Compose for UI? (Yes, fully declarative UI)
- [x] **Unidirectional Data Flow**: Is state managed in ViewModels with events flowing up? (Yes, ViewModels will hold `DashboardState` and `ConnectionState` as StateFlows)
- [x] **Test-First**: Are testing strategies (Unit, UI, Screenshot) explicitly defined? (Yes, unit testing ViewModels and UI testing Compose widgets)
- [x] **Accessibility**: Are accessibility requirements included (touch targets, dynamic text, content descriptions)? (Yes, widgets will adhere to Android accessibility guidelines)
- [x] **Modular Architecture**: Which module(s) will this feature live in or create? (Starting in `app` module, with a clean separation of the `protocol` package for easy future extraction)

## Project Structure

### Documentation (this feature)

```text
specs/001-hmi-control-panel/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/hmi/
│   │   │   ├── connection/    # PLC Connection logic and UI
│   │   │   ├── dashboard/     # Dashboard rendering and Edit Mode logic
│   │   │   ├── widgets/       # Button, Slider, Gauge Composables
│   │   │   ├── data/          # Models and Repositories (DataStore)
│   │   │   └── protocol/      # PlcCommunicator interface and implementations
│   └── test/                  # Unit tests for ViewModels and Repositories
│   └── androidTest/           # Compose UI tests
```

**Structure Decision**: A standard Android single-app module structure to start. The `protocol` package will act as the abstraction layer for FR-009, making it easy to extract into a separate Gradle module (`:core:protocol`) later to enforce the modular architecture principle strictly as the project grows.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |