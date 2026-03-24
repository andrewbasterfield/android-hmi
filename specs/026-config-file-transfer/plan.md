# Implementation Plan: Config File Transfer

**Branch**: `026-config-file-transfer` | **Date**: 2026-03-24 | **Spec**: [specs/026-config-file-transfer/spec.md](spec.md)
**Input**: Feature specification from `/specs/026-config-file-transfer/spec.md`

## Summary

Implement a robust configuration transfer system for dashboard layouts and connection profiles. This includes file-based export/import using the Android Storage Access Framework (SAF), cloud sharing via the Android Share Sheet, and "Open With" integration via Intent Filters. Validation will be enforced using formal JSON schemas to ensure data integrity and provide detailed error feedback.

## Technical Context

**Language/Version**: Kotlin 1.9.22  
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON, Android SAF, Android Intent System, JSON Schema Validator (`org.everit.json.schema`)  
**Storage**: Jetpack DataStore (Preferences), External Files (via SAF)  
**Testing**: JUnit 4 (Unit), Compose Testing (UI), Mockk  
**Target Platform**: Android API 26+  
**Project Type**: Mobile App  
**Performance Goals**: Import processing < 500ms for standard layouts  
**Constraints**: Must handle external file URIs securely (FileProvider), schema-based validation required  
**Scale/Scope**: Impacts dashboard and connection settings; affects data persistence and UI dialogs

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Compose-First**: All UI changes will be in Jetpack Compose.
- [x] **Unidirectional Data Flow**: ViewModel will handle intent data and file streams; UI will observe state flows.
- [x] **Test-First**: Unit tests for schema validation and profile merging; UI tests for sharing triggers.
- [x] **Accessibility**: Minimum 48dp touch targets for export/import actions; content descriptions for icons.
- [x] **Clarity by Design**: High-contrast dialogs for import confirmations and error reporting.
- [x] **Low Cognitive Load**: Selection menu for full backup imports to prevent accidental overwrites.
- [x] **No Gimmicks**: Animations limited to standard Material transitions for dialogs.
- [x] **Modular Architecture**: Feature lives in `app` module; data models in `core:protocol`.

## Project Structure

### Documentation (this feature)

```text
specs/026-config-file-transfer/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
app/src/main/java/com/example/hmi/
├── MainActivity.kt             # Intent filter handling
├── data/
│   ├── DashboardRepository.kt   # Profile merge logic
│   └── ConfigTransferManager.kt # NEW: Centralized logic for file I/O and validation
├── dashboard/
│   ├── DashboardViewModel.kt    # Integration of transfer logic
│   └── DashboardSettingsDialog.kt # UI for file picking and sharing
├── connection/
│   ├── ConnectionViewModel.kt   # Profile transfer integration
│   └── ConnectionScreen.kt      # Profile export/import UI
└── di/
    └── DataModule.kt            # Hilt bindings for new manager
```

**Structure Decision**: Integrated into existing `app` module as it touches core repository and dashboard UI.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None      |            |                                     |
