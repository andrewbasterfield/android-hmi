# Data Model: Default Demo Mode

## 1. Demo Layout Configuration
The demo mode utilizes the existing `DashboardLayout` data model, populated with specific default `WidgetConfiguration` objects bound to the `DemoPlcServer` tags.

### Entity: `DemoLayout` (Instance of `DashboardLayout`)
- **name**: "Demo Layout"

### Demo Widgets:
1. **Instruction Text Widget** (Requires adding `TEXT` to `WidgetType`)
   - **type**: `WidgetType.TEXT`
   - **customLabel**: "Welcome to the HMI Demo. This dashboard is showing simulated data from the local server. To connect to a real device or create a custom layout, use the settings menu."
   - **row**: 0, **col**: 0, **colSpan**: 4, **rowSpan**: 1
2. **Temperature Gauge**
   - **type**: `WidgetType.GAUGE`
   - **tagAddress**: "SIM_TEMP"
   - **customLabel**: "Temperature"
   - **units**: "°C"
   - **minValue**: 0f, **maxValue**: 100f
   - **row**: 1, **col**: 0, **colSpan**: 2, **rowSpan**: 2
3. **Pressure Gauge**
   - **type**: `WidgetType.GAUGE`
   - **tagAddress**: "SIM_PRESSURE"
   - **customLabel**: "Pressure"
   - **units**: "kPa"
   - **minValue**: 0f, **maxValue**: 200f
   - **row**: 1, **col**: 2, **colSpan**: 2, **rowSpan**: 2
4. **Status Indicator**
   - **type**: `WidgetType.BUTTON`
   - **interactionType**: `InteractionType.INDICATOR`
   - **tagAddress**: "SIM_STATUS"
   - **customLabel**: "System Status"
   - **row**: 3, **col**: 1, **colSpan**: 2, **rowSpan**: 1

## Data Model Changes
- **`WidgetType` Enum**: Add `TEXT` as a supported widget type to accommodate the onboarding instructions.