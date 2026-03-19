# Data Model: UI Animations and Gauge Improvement

## Entities

### DashboardLayout (Modified)
Represents the overall dashboard configuration and settings.
- **id**: String (UUID)
- **name**: String
- **canvasColor**: Long (RGB)
- **widgets**: List<WidgetConfiguration>
- **isDarkThemeMigrated**: Boolean
- **hapticFeedbackEnabled**: Boolean (Default: true) - Global toggle for tactile feedback.

### WidgetConfiguration (Modified)
Represents a single widget on the dashboard.
- **id**: String (UUID)
- **type**: WidgetType (BUTTON, SLIDER, GAUGE)
- **column/row/span**: Grid coordinates
- **tagAddress**: PLC tag reference
- **customLabel**: Optional display text
- **backgroundColor**: Long (RGB)
- **fontSizeMultiplier**: Float
- **textColorOverride**: String?
- **minValue/maxValue**: Range (for Slider and Gauge)
- **colorZones**: List<GaugeZone> (New, GAUGE only) - Defines colored arcs for status.

### GaugeZone (New)
Defines a specific range and color for a Gauge widget's arc.
- **startValue**: Float
- **endValue**: Float
- **color**: Long (RGB/Hex)
- **label**: String? (Optional, e.g., "Critical")

## State Transitions
- **Button**: Idle (Elevation 4dp, Scale 1.0f) → Pressed (Elevation 1dp, Scale 0.95f) → Released (Spring-back with slight overshoot).
- **Gauge**: Value update → Animate needle rotation on a 270° arc (135° to 405°).
- **Haptics**: Button Press → Check `hapticFeedbackEnabled` and hardware capability → Trigger short vibration pulse.
