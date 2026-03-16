# Data Model: Custom Color Picker

## Entities

### CustomColor (Attribute)
A 32-bit ARGB value (represented as a `Long` in Kotlin) that defines a custom color.
- **Persistence**: Stored in `WidgetConfiguration.backgroundColor` and `DashboardLayout.canvasColor`.
- **Validation**:
    - MUST be opaque (Alpha = 255/FF).
    - MUST pass contrast validation: automatically determines `contentColor` based on luminance.

### RecentColorsList (Global)
A persistent collection of recently used custom colors.
- **Data Structure**: `List<Long>` (Ordered by recency).
- **Limit**: Exactly 8 unique colors.
- **Scope**: Global (Shared across all widgets and dashboard canvas).

## Storage Strategy

### DataStore Persistence
A new key will be added to the `DashboardRepository` for tracking recent colors:
- Key: `recent_colors`
- Format: JSON array of `Long` values.

### Widget/Layout Updates
The existing `backgroundColor` and `canvasColor` fields in `WidgetConfiguration` and `DashboardLayout` already support `Long?`. No structural changes are needed to these entities, but the logic for picking these colors will be updated to include the custom options.
