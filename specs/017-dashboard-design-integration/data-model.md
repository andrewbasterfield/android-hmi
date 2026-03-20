# Data Model: Dashboard Design Integration (Kinetic Cockpit)

**Feature**: `017-dashboard-design-integration` | **Status**: Complete
**Input**: Feature requirements and research.md

## Entities & Relationships

### `DashboardLayout` (Migration Update)

The existing layout entity will track its migration status to the Kinetic Cockpit language.

| Field | Type | Validation | Rationale |
|-------|------|------------|-----------|
| `isKineticCockpitMigrated` | `Boolean` | Default: `false` | One-time flag to trigger the Obsidian/OSHA color migration. |

### `WidgetConfiguration` (Typography Update)

Existing fields for `label`, `backgroundColor`, and `fontSizeMultiplier` remain but are constrained by new design tokens.

| Field | Type | Rule | Rationale |
|-------|------|------|-----------|
| `fontSizeMultiplier` | `Float` | Minimum: `1.0f` | Ensures no font ever scales below the "Industrial Utility" baseline of 16sp. |

## State Transitions

### Layout Migration Flow

1. **Detection**: App launch checks `isKineticCockpitMigrated`.
2. **Transformation**:
    - `canvasColor` -> Obsidian (#131313).
    - `widget.backgroundColor` -> Sanitized via `ColorSanitizer`.
3. **Commit**: Set `isKineticCockpitMigrated = true` and save to DataStore.
