# Research: Custom Labels and Dynamic Attribute Updates

## Technical Context & Decisions

### Decision 1: Protocol Parsing for Attributes
**Decision**: Use `lastIndexOf('.')` to identify the attribute suffix in the incoming message.
**Rationale**: 
- The standard message is `TAG:VALUE`.
- The new format is `TAG.ATTR:VALUE`.
- If a tag is `SYSTEM.STATUS`, and the message is `SYSTEM.STATUS.color:#FF0000`, splitting by the *last* dot correctly identifies `color` as the attribute and `SYSTEM.STATUS` as the tag.
**Alternatives considered**: 
- Splitting by first dot (Rejected: fails for dotted tag names).
- Using a different separator like `@` (Rejected: User requested dot notation).

### Decision 2: Transient Session Overrides
**Decision**: Store protocol-driven updates in an in-memory `StateFlow` within `DashboardViewModel`.
**Rationale**: 
- Meets the requirement for "volatile" updates (Option A in specification).
- Allows UI to combine persistent `WidgetConfiguration` with transient `Overrides`.
- Simple implementation using a Map: `Map<String, Map<String, String>>` (Tag -> AttributeName -> Value).
**Alternatives considered**: 
- Saving to DataStore with a "temporary" flag (Rejected: too complex, adds disk I/O for volatile data).

### Decision 3: Hex Color Integration
**Decision**: Implement a robust hex-to-long conversion that handles `#RRGGBB` and `#AARRGGBB`.
**Rationale**: 
- PLC systems often use simple hex strings.
- Jetpack Compose `Color` uses `Long` or `ULong`.
- Must handle the case where the `#` is missing.
**Alternatives considered**: 
- Native `android.graphics.Color.parseColor` (Rejected: requires prefixing with `0xFF` for opaque colors and doesn't return the exact type needed for Compose easily without casting).

## Best Practices
- **Conflict Resolution**: "Last Change Wins" logic will be implemented by ensuring that when a user manually edits a widget, it updates the DataStore. The ViewModel should then clear any transient overrides for that specific attribute to ensure the manual edit is visible, or simply update both. Since manual edits are persistent, they will survive a restart, whereas protocol updates will not.

## Unknowns Resolved
- **Performance**: Protocol parsing is a simple string split and map lookup, well within the 100ms latency goal.
- **Accessibility**: The `ColorUtils.kt` already contains luminance logic from feature 003, which will be reused for dynamic background colors.
