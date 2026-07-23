# Research: System Profiles and Binding (Import/Export)

## Decision: Comprehensive Backup Schema
**Rationale**: Users need to move their entire "environment" (all presets and library items) to new devices.
**Decision**: Update `FullBackupPackage` to include:
- `libraryLayouts: List<DashboardLayout>?`
- `systemProfiles: List<SystemProfile>?`
The existing `layout` (active) and `profiles` (saved connections) fields will be retained for backward compatibility.

## Decision: Standalone Profile Sharing
**Rationale**: Sharing a specific machine configuration shouldn't require sending the whole library.
**Decision**: Create a `SystemProfileBundle` entity.
```kotlin
@Serializable
data class SystemProfileBundle(
    val profile: SystemProfile,
    val layout: DashboardLayout,
    val connection: PlcConnectionProfile
)
```
This bundle will be serialized into a single JSON file for sharing.

## Decision: Import Merge Strategy (Upsert)
**Rationale**: Industrial users often update existing configurations via import. Overwriting by ID/Name is safer than duplicating or wiping.
**Decision**:
- Connections: Match by `name` (existing behavior in `mergeProfiles`).
- Layouts: Match by `id`.
- System Profiles: Match by `id`.
The `DashboardRepository` will be updated to handle this "Upsert" logic for all three entity types.

## Decision: Schema Versioning
**Rationale**: New fields in `FullBackupPackage` require a schema update.
**Decision**: 
1. Bump `CURRENT_VERSION` to 2 in `ConfigTransferManager`.
2. Update `full-backup.schema.json` to include the new nullable lists.
3. Ensure the app can still parse Version 1 files by treating new fields as null.

## Alternatives Considered
- **Strict Overwrite**: Rejected because it could cause data loss of local-only configurations.
- **Multiple Files (ZIP)**: Rejected to keep the transfer process simple (single JSON file).
- **Manual Resolution UI**: Rejected for MVP to minimize cognitive load, but noted as a potential future improvement.
