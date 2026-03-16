# Data Model: Dark Mode UI Refinement

## Entities

### WidgetConfiguration (Updated)

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | Unique UUID for the widget |
| `type` | WidgetType | BUTTON, SLIDER, or GAUGE |
| `column` | Int | Grid X position |
| `row` | Int | Grid Y position |
| `colSpan` | Int | Grid width in cells |
| `rowSpan` | Int | Grid height in cells |
| `tagAddress` | String | Underlying PLC tag address |
| `customLabel` | String? | Optional override for the tag address display |
| `backgroundColor` | Long? | ARGB color value (Restricted palette to ensure black text contrast) |
| `fontSizeMultiplier` | Float | Multiplier for text size (0.5 to 2.5, default 1.0) |
| `minValue` | Float? | For sliders and gauges |
| `maxValue` | Float? | For sliders and gauges |

### DashboardLayout (Existing)

| Field | Type | Description |
|-------|------|-------------|
| `name` | String | Dashboard name |
| `canvasColor` | Long? | ARGB background color (Defaulting to #000000) |
| `widgets` | List<WidgetConfiguration> | Collection of widgets |

## Validation Rules

- **Contrast Rule**: All `backgroundColor` values MUST provide at least 4.5:1 contrast against black text.
- **Font Size Range**: `fontSizeMultiplier` MUST be between 0.5f and 2.5f.
- **Default Background**: New `DashboardLayout` objects MUST initialize `canvasColor` to 0xFF000000.
