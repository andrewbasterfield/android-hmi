# Data Model: System Profiles and Binding (Import/Export)

## Entities

### SystemProfile (Update)
| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique identifier (UUID) |
| name | String | User-visible name of the preset |
| connectionProfileName | String | Name of the bound `PlcConnectionProfile` |
| layoutId | String | ID of the bound `DashboardLayout` |
| isReadOnly | Boolean | If true, the profile cannot be deleted or renamed (e.g., Demo System) |

### SystemProfileBundle
A standalone package for sharing a single System Profile with all its dependencies.

| Field | Type | Description |
|-------|------|-------------|
| profile | SystemProfile | The preset metadata |
| layout | DashboardLayout | The visual design |
| connection | PlcConnectionProfile | The technical connection parameters |

### FullBackupPackage (Update)
| Field | Type | Description |
|-------|------|-------------|
| version | Int | Schema version (Bump to 2) |
| layout | DashboardLayout? | Currently active layout |
| profiles | List<PlcConnectionProfile>? | Saved connection profiles |
| libraryLayouts | List<DashboardLayout>? | **NEW**: All layouts in the user library |
| systemProfiles | List<SystemProfile>? | **NEW**: All saved system presets |

## Import Validation (Schema)

The JSON schema for imports MUST be updated to support the new optional collections:
- `libraryLayouts`: Array of layout objects.
- `systemProfiles`: Array of System Profile objects.

## Merge Logic (Upsert)

1. **Connections**: `currentList.removeAll { it.name == incoming.name }; currentList.add(incoming)`
2. **Layouts**: `currentList.removeAll { it.id == incoming.id }; currentList.add(incoming)`
3. **System Profiles**: `currentList.removeAll { it.id == incoming.id }; currentList.add(incoming)`
