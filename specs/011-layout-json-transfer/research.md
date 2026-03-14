# Research: JSON Import/Export

## Technical Context & Decisions

### Decision 1: Serialization Library
**Decision**: Use **GSON**.
**Rationale**: GSON is already integrated into the project (referenced in `GEMINI.md` and used in `DashboardRepository.kt`). Introducing another serialization library like Kotlin Serialization would add unnecessary weight and complexity.
**Alternatives considered**: 
- Kotlin Serialization: Offers better type safety and Kotlin support, but GSON is already established here.

### Decision 2: Clipboard Interaction
**Decision**: Use `LocalClipboardManager` in Jetpack Compose.
**Rationale**: This is the idiomatic way to interact with the system clipboard in Compose, providing a clean abstraction over the Android `ClipboardManager`.
**Alternatives considered**: 
- Native `android.content.ClipboardManager`: Requires context and is more verbose.

### Decision 3: Validation Strategy
**Decision**: Two-tier validation.
1.  **Structural**: GSON will attempt to parse the string into a `DashboardLayout` object. If it fails, the JSON is malformed.
2.  **Logical**: Verify that the parsed layout contains a valid name and at least a valid (even if empty) list of widgets.
**Rationale**: Prevents app crashes or corrupted state from applying invalid layouts.

### Decision 4: UI Placement
**Decision**: Add a "JSON Transfer" section within the existing `DashboardSettingsDialog`.
**Rationale**: Keeps the UI clean by grouping layout-level operations together. Avoids adding more buttons to the main top bar.

## Best Practices
- **Compose Text Field**: Use `OutlinedTextField` with `maxLines` or a fixed height for the JSON input to handle large strings gracefully.
- **Feedback**: Use `Snackbar` or `Toast` to confirm "Copied to Clipboard" and "Import Successful".

## Unknowns Resolved
- **Coroutines**: DataStore updates should happen on `Dispatchers.IO` via the `DashboardViewModel`.
- **Error Handling**: Use `try-catch` around GSON parsing to catch syntax errors and display user-friendly messages.
