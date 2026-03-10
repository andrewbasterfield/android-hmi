# Data Model: HMI Control Panel

## Entities

### `PlcConnectionProfile`
Stores the connection state, IP address, and port.
- `id`: String (UUID)
- `name`: String
- `ipAddress`: String
- `port`: Int
- `protocol`: Enum (RAW_TCP, MODBUS_TCP, OPC_UA)

### `DashboardLayout`
A collection of configured widgets representing the current HMI screen.
- `id`: String (UUID)
- `name`: String
- `widgets`: List<WidgetConfiguration>

### `WidgetConfiguration`
Defines a single UI element, its visual coordinates, and the PLC data tag.
- `id`: String (UUID)
- `type`: Enum (BUTTON, SLIDER, GAUGE)
- `x`: Float (relative position)
- `y`: Float (relative position)
- `width`: Float
- `height`: Float
- `tagAddress`: String (The PLC memory address or tag name)
- `minValue`: Float? (For sliders/gauges)
- `maxValue`: Float? (For sliders/gauges)