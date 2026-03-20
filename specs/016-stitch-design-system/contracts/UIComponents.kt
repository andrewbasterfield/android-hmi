# UI Contract: Stitch Design System Integration (Industrial Precision HMI)

## Component: IndustrialButton
Standard tactile target for system interactions.

### Signature
```kotlin
@Composable
fun IndustrialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    status: ButtonStatus = ButtonStatus.NORMAL,
    content: @Composable RowScope.() -> Unit = {}
)
```

### Constraints
- Height: Min 64px (forced)
- Radius: 0dp (forced)
- Bezel: 2dp solid
- Interaction: "Inverse Video" on press (Indication)

---

## Component: TelemetryCard
Modular data readout block with health accent.

### Signature
```kotlin
@Composable
fun TelemetryCard(
    label: String,
    value: String,
    unit: String,
    status: HealthStatus,
    modifier: Modifier = Modifier,
    onDetailsClick: (() -> Unit)? = null
)
```

### Constraints
- Border: 2dp bezel (outline token)
- Accent: 4px vertical bar (status color)
- Type: Monospaced numerical values
- Case: Uppercase labels and units
