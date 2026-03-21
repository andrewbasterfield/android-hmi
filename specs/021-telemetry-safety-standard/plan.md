# Implementation Plan: Telemetry Safety Standard (SI Compliance & ISA-18.2)

**Feature Branch**: `021-telemetry-safety-standard`  
**Status**: Ready for Implementation  
**Created**: 2026-03-21  

## Technical Context

- **Current State**: `GaugeWidget` and `TelemetryCard` use standard Compose `MaterialTheme` for shapes and basic unit formatting. Units are currently force-capitalized in some areas. Alarms are partially implemented using full-block color shifts.
- **Goal**: Transition to "Hard Industrial" 2px border radius, strict SI unit case-sensitivity, and ISA-18.2 compliant alarm signaling (static text, flashing bounding box).

## Constitution Check (v1.1.0)

- **Compose-First (I)**: ✅ New SI Formatter and Alarm Pulse logic will be built in Compose.
- **Unidirectional Data Flow (II)**: ✅ Alarm acknowledgement will flow up to `DashboardViewModel`.
- **Clarity by Design (VI)**: ✅ Directly addresses SI unit safety and alarm readability.
- **Low Cognitive Load (VII)**: ✅ Flash suppression reduces operator overwhelm during faults.
- **No Gimmicks (VIII)**: ✅ 4Hz pulse serves a clear functional safety purpose (ISA-18.2).

## Phase 0: Research & Outline

- **Output**: [research.md](./research.md)
- **Status**: Complete. 
- **Decisions**: Session-scoped acknowledgement, `infiniteTransition` for pulsing, SI symbol dictionary implementation.

## Phase 1: Design & Contracts

- **Data Model**: [data-model.md](./data-model.md)
- **Contracts**: [contracts/SiFormatter.md](./contracts/SiFormatter.md)
- **Quickstart**: [quickstart.md](./quickstart.md)
- **Status**: Complete.

## Phase 2: Implementation Roadmap

### 1. Core UI Tokens (Surgical)
- **Action**: Update `core:ui`'s `Shape.kt` and `Color.kt` for the 2px radius and SI-compliant text tokens.
- **Validation**: Verify all cards and buttons across the dashboard reflect the 2px radius.

### 2. SI Unit Safety Logic
- **Action**: Implement `SiFormatter` utility in `core:ui`.
- **Action**: Refactor `GaugeWidget` and `TelemetryCard` to use the formatter.
- **Validation**: Unit Test `SiFormatterTest.kt` for all symbols in the dictionary.

### 3. ISA-18.2 Alarm Protocol
- **Action**: Update `WidgetConfiguration` to include `AlarmState`.
- **Action**: Create `AlarmPulse` composable in `core:ui/components`.
- **Action**: Implement `acknowledgeAlarm` in `DashboardViewModel`.
- **Validation**: Instrumented test for 3-5Hz pulse visibility and interaction logic.

## Gate Check

1. **[X]** Does it avoid implementation details in the spec? (Yes)
2. **[X]** Are all [NEEDS CLARIFICATION] resolved? (Yes)
3. **[X]** Does it adhere to the Industrial Design Constitution? (Yes)
