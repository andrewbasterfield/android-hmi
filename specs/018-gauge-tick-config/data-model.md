# Data Model: Configurable Gauge Tick Density

**Feature**: `018-gauge-tick-config` | **Status**: Complete
**Input**: Feature specification and plan.md

## Entities & Relationships

### `WidgetConfiguration` (Update)

The existing widget configuration model will be extended to support the density preference.

| Field | Type | Validation | Rationale |
|-------|------|------------|-----------|
| `targetTicks` | `Int` | Default: 6, Range: [2, 20] | Represents the desired number of major intervals on the scale. Affects Gauge dial background. |

## Serialization

The `targetTicks` field will be serialized into the existing JSON layout structure stored in Jetpack DataStore.

**Scope Note**: This field will only be utilized by widgets of type `GAUGE`. For other widget types, the field will be persisted but ignored by the rendering logic.

**GSON Note**: As a primitive `Int`, it will naturally fall back to the default value (6) when parsing older layouts that lack the field.
