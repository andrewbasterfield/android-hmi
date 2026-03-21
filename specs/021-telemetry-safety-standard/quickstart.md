# Quickstart: Telemetry Safety Standard

## 1. Implementation Checklist

### [ ] Update UI Tokens
- Set `Shapes.small` and `Shapes.medium` to `RoundedCornerShape(2.dp)` in `core:ui/Shape.kt`.

### [ ] SI Compliance
- Implement `SiFormatter.kt` in `core:ui/utils`.
- Replace all unit formatting in `GaugeWidget.kt` and `TelemetryCard.kt` with `SiFormatter.format(value, unit)`.

### [ ] Alarm Protocol (ISA-18.2)
- Add `AlarmState` (Normal, Unacknowledged, Acknowledged) to `WidgetConfiguration`.
- Implement `AlarmPulse(frequency: Float)` Composable wrapper.
- Wrap `GaugeWidget` and `TelemetryCard` contents with `AlarmPulse` that flashes the 2px border at 3-5Hz if `Unacknowledged`.

## 2. Code Example (Alarm State Toggle)

```kotlin
// In ViewModel
fun acknowledgeAlarm(tagAddress: String) {
    val widget = widgets.find { it.tagAddress == tagAddress }
    if (widget?.state == AlarmState.Unacknowledged) {
        updateWidgetConfig(widget.copy(state = AlarmState.Acknowledged))
    }
}
```

## 3. Design Validation
- **Requirement Check**: Units like `mV` MUST NOT be `MV`.
- **Readability Check**: Numeric values MUST remain static during the 3-5Hz pulse.
- **Aesthetic Check**: All blocks MUST have 2px rounded corners.
