# Research: Config File Transfer

This document outlines the research and technical decisions for the Config File Transfer feature.

## 1. JSON Schema Validation

- **Decision**: Use `org.everit.json.schema:org.everit.json.schema:1.14.1` via JitPack.
- **Rationale**:
    - Supports JSON Schema Draft 7.
    - Provides detailed validation exceptions with path information for non-conformant elements (Requirement FR-006).
    - Lightweight enough for Android, although it requires careful handling of the `org.json` namespace collision (use a modern version that bridges to Android's built-in `org.json`).
- **Implementation**:
    - Add to `build.gradle.kts` (app module).
    - Cache the `Schema` object in a `ConfigTransferManager` singleton for performance.
    - Map `ValidationException` messages to user-friendly Toast/Snackbar messages.

## 2. File Sharing (Share Out)

- **Decision**: Use `Intent.ACTION_SEND` with a `FileProvider` URI.
- **Rationale**:
    - Standard Android pattern for sharing files with other apps (Google Drive, Email, etc.).
    - Ensures security by granting temporary read permission to the receiving app.
- **Implementation**:
    - Define a `FileProvider` in `AndroidManifest.xml`.
    - Use `FileProvider.getUriForFile()` to share files from the app's `cacheDir`.

## 3. "Open With" Integration (Share In)

- **Decision**: Register `intent-filter` for `ACTION_VIEW` and `ACTION_SEND` with `mimeType="application/json"` in `MainActivity`.
- **Rationale**:
    - Allows the app to appear in the "Open with" list when a user taps a JSON file in a file manager or shares it from another app.
- **Implementation**:
    - Update `AndroidManifest.xml`.
    - Override `onNewIntent` in `MainActivity` to handle incoming files when the app is already running.
    - Use `contentResolver.openInputStream(uri)` to read the shared content.

## 4. Full Backup Structure & Selection

- **Decision**: A consolidated JSON object containing `version`, `layout`, and `profiles`.
- **Rationale**:
    - `version`: Allows for future schema evolution and backwards compatibility checks (Requirement FR-014).
    - `layout` & `profiles`: Keeps all system state in one file.
- **Selection UI**:
    - A custom Compose Dialog (`ImportSelectionDialog`) will show checkboxes for "Dashboard Layout" and "Connection Profiles" if both are present in the imported file.

## 5. Conflict Resolution (Profiles)

- **Decision**: Overwrite-by-name (Upsert).
- **Rationale**:
    - Simplest and most predictable behavior for technicians.
    - If a profile with the same name exists, it's updated; otherwise, it's added.
