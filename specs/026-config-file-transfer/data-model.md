# Data Model: Config File Transfer

This document defines the data structures and validation rules for the Config File Transfer feature.

## 1. Full Backup Package

The `FullBackupPackage` is the consolidated object used for full system backups.

| Field | Type | Description |
|---|---|---|
| `version` | `Int` | Schema version (currently 1). Used for backwards compatibility checks. |
| `layout` | `DashboardLayout?` | Optional: The dashboard layout configuration. |
| `profiles` | `List<PlcConnectionProfile>?` | Optional: A list of saved connection profiles. |

### Validation Rules:
- **Version**: MUST be a positive integer.
- **Content**: MUST contain at least one of `layout` or `profiles`.
- **Layout**: MUST conform to the `DashboardLayout` JSON schema.
- **Profiles**: MUST conform to the `PlcConnectionProfile` list JSON schema.

## 2. Import Selection State

This transient object is used by the UI to track user choices during a "Full Backup" import.

| Field | Type | Description |
|---|---|---|
| `importLayout` | `Boolean` | Whether the user has selected to import the dashboard layout. |
| `importProfiles` | `Boolean` | Whether the user has selected to import the connection profiles. |

## 3. Transfer Manager Events

The `ConfigTransferManager` will emit events to notify the UI of operation results.

| Event | Data | Description |
|---|---|---|
| `ImportReady` | `FullBackupPackage` | The file has been successfully validated and is ready for selection. |
| `ValidationError` | `String` | Validation failed. Contains the specific non-conformant element/field. |
| `Success` | `String` | Operation (Export/Import/Share) completed successfully. |
| `Error` | `String` | General error (I/O, Permission, etc.). |
