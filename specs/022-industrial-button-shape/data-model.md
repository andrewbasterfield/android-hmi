# Data Model: Industrial Button Component

## Entity: IndustrialButton

... (same properties)

## Entity: IndustrialInput

The `IndustrialInput` is a heavy-duty text field designed for high-contrast data entry in industrial dashboards.

### Properties

| Property | Description | Range/Constraint |
|----------|-------------|------------------|
| `value` | Current text content. | String |
| `label` | Uppercase descriptive label above the input. | String |
| `shape` | Corner geometry of the background. | **Linked to "small" system shape (2dp)** |
| `height` | Minimum vertical size for touch targets. | **Min 64px (A11Y-001)** |
| `shelf` | Bottom-border "shelf" for visual data entry cues. | **4px width (FR-005)** |
| `background` | High-contrast container color. | `surfaceContainerHighest` |

### State Transitions

| Initial State | Event | Target State | Visual Response |
|---------------|-------|--------------|-----------------|
| Inactive | Focus | Focused | Increase Border Intensity / Cursor Active |
| Focused | Unfocus| Inactive | Restore Standard Colors |

### Validation Rules

1. **Machined Edge Rule**: The corner radius MUST NOT be 0dp.
2. **Tactile Buffer Rule**: The input MUST maintain a minimum 64px height.
3. **High-Contrast Rule**: The cursor and text color MUST utilize the system's "Primary" Green (#00E639).
