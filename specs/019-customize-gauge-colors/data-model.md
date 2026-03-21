# Data Model: Gauge Color Customization

## WidgetConfiguration (Updated)

| Field | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `needleColor` | `Long?` | `null` | Color override for the gauge needle (internal Compose value). |
| `scaleColor` | `Long?` | `null` | Color override for scale ticks and numeric labels. |
| `isNeedleDynamic` | `Boolean` | `false` | If true, the needle matches the color of the current active `GaugeZone`. |

### Logic Transitions
1.  **Needle Color (Active)**:
    *   If `isNeedleDynamic` is TRUE and `currentValue` falls within a `GaugeZone`: use `GaugeZone.color`.
    *   Else if `needleColor` is NOT NULL: use `needleColor`.
    *   Else: use `LocalContentColor.current`.
2.  **Scale Color (Active)**:
    *   If `scaleColor` is NOT NULL: use `scaleColor`.
    *   Else: use `LocalContentColor.current.copy(alpha = 0.8f)`.
