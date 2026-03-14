# Implementation Plan: Built-in Demo Server Integration

**Branch**: `009-builtin-demo-server` | **Date**: 2026-03-14 | **Spec**: [specs/009-builtin-demo-server/spec.md](specs/009-builtin-demo-server/spec.md)
**Input**: Feature specification from `/specs/009-builtin-demo-server/spec.md`

## Summary

This plan outlines the integration of an internal `DemoPlcServer` into the Android application to allow for "Always On" standalone testing. The server will launch with the application and be accessible via a dedicated "Connect to Local Demo Server" button on the connection screen.

## Technical Context

**Language/Version**: Kotlin (Latest stable)  
**Primary Dependencies**: Kotlin Coroutines, Jetpack Compose, Hilt  
**Storage**: N/A (Server state is in-memory for the demo session)  
**Testing**: Unit tests for server logic, UI tests for connection flow  
**Target Platform**: Android (API 24+)
**Project Type**: Mobile App (Android)  
**Performance Goals**: Minimal CPU impact (<5%) while active.  
**Constraints**: Must work offline (localhost loopback).  
**Scale/Scope**: Single process simulation server.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: Uses existing Compose UI for the Connection Screen update.
- [x] **Unidirectional Data Flow**: Connection state managed by `ConnectionViewModel`.
- [x] **Test-First**: Unit tests planned for `DemoPlcServer` tag logic and broadcast mechanism.
- [x] **Accessibility**: New button will have 48x48dp touch target and clear label.
- [x] **Modular Architecture**: Lives within the `app` module, specifically in the `protocol` and `connection` packages.

## Project Structure

### Documentation (this feature)

```text
specs/009-builtin-demo-server/
├── plan.md              # This file
├── research.md          # Decisions on lifecycle and UI integration
├── data-model.md        # Entities for simulated tags
├── quickstart.md        # Guide for demo mode
└── tasks.md             # Implementation tasks
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── HmiApplication.kt          # Start/Stop demo server lifecycle
├── connection/
│   └── ConnectionScreen.kt    # Add "Demo Mode" button
├── protocol/
│   └── DemoPlcServer.kt       # The simulation server (already exists, will be updated)
```

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

(No violations identified)
