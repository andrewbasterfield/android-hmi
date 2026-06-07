# Implementation Plan: System Profiles and Binding (Refined)

**Branch**: `035-system-profiles-binding` | **Date**: 2026-06-07 | **Spec**: [specs/035-system-profiles-binding/spec.md](spec.md)
**Input**: Feature specification from `/specs/035-system-profiles-binding/spec.md` (updated with Import/Export)

## Summary
The goal is to implement a "System Profile" entity that binds a Connection Profile to a Dashboard Layout, managed via a "Management Hub" drawer. This refinement adds comprehensive "Full Backup" support (exporting the entire library) and "Standalone Profile Bundles" for sharing individual machine configurations with all their visual and technical dependencies.

## Technical Context

**Language/Version**: Kotlin 1.9+, Java 17  
**Primary Dependencies**: Jetpack Compose, Hilt, DataStore, Kotlin Serialization, JSON Schema Validation (Everit)  
**Storage**: Jetpack DataStore (Preferences) with JSON serialization  
**Transfer Format**: JSON (Bundles and Backups)  
**Testing**: JUnit 5, MockK, Compose UI Tests  
**Performance Goals**: Environment switching < 3s, Large backup parsing < 1s  

## Constitution Check

- [x] **Compose-First**: Management Hub and Share dialogs use Compose.
- [x] **Unidirectional Data Flow**: Transfer events and state managed via ViewModels and SharedFlows.
- [x] **Test-First**: Unit tests required for new "Upsert" logic and Bundle serialization.
- [x] **Clarity by Design**: Export/Import feedback must be unambiguous and immediate.
- [x] **No Gimmicks**: Backup format is standard JSON for interoperability.

## Project Structure

### Documentation (this feature)

```text
specs/035-system-profiles-binding/
├── plan.md              # This file
├── research.md          # Import/Export rationale and decisions
├── data-model.md        # Bundle and Backup entity definitions
├── quickstart.md        # Import/Export interaction guide
└── tasks.md             # Updated tasks (Phase 2)
```

### Source Code Updates

```text
app/src/main/java/com/example/hmi/
├── data/
│   ├── SystemProfile.kt         # UPDATED: Added SystemProfileBundle
│   ├── FullBackupPackage.kt     # UPDATED: Added library fields
│   ├── DashboardRepository.kt   # UPDATED: Added Upsert logic for all entities
│   └── ConfigTransferManager.kt # UPDATED: Version 2 schema, Bundle handling
└── assets/
    └── schemas/
        └── full-backup.schema.json # UPDATED: V2 validation rules
```

## Complexity Tracking

*Schema migration risk from V1 to V2 is mitigated by using nullable lists and default values.*
