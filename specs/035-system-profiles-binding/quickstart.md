# Quickstart: System Profiles and Binding (Import/Export)

## Setup for Developers

1. **Entities**: Define `SystemProfile` and `SystemProfileBundle` in `com.example.hmi.data`.
2. **Repository**: Implement "Upsert" logic in `DashboardRepository.kt` for profiles and layouts.
3. **Transfer**: 
   - Update `FullBackupPackage.kt` with new fields.
   - Update `CURRENT_VERSION` to 2 in `ConfigTransferManager`.
   - Update `full-backup.schema.json` in assets.
4. **UI**: Add "Share Bundle" action to individual System Profiles in the Management Hub drawer.

## Key Interactions

### Full Backup
- Use "System Transfer Center" to export everything.
- Resulting file contains ALL layouts, ALL connections, and ALL presets.
- Importing on another device restores the entire environment using "Upsert" logic.

### Standalone Profile Sharing
- Long-press or tap "Share" on a specific Preset in the drawer.
- App bundles that preset + its layout + its connection IP/settings.
- Receiver imports the bundle to add that specific machine config to their library.

### Automatic Merging
- If you import a backup with a preset named "Line 1" that already exists locally, the local one is UPDATED with the incoming configuration.
