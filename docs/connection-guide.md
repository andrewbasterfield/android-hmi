# Connection Guide

This guide explains how to configure and manage connections to your PLCs or MQTT brokers using the HMI Control Panel.

## Connection Profiles

Connection profiles allow you to save your configuration for different machines or environments.

### Managing Profiles
- **Load Profile:** Use the "Load" dropdown at the top to select from saved or built-in profiles.
- **Save Profile:** Enter a unique "Profile Name" and click **Save**. If the name already exists, you will be asked to overwrite it.
- **Delete Profile:** Select a saved profile and click **Delete**. Built-in profiles cannot be deleted.
- **Built-in Profiles:** The app includes a "Local Demo Server" profile for testing without physical hardware.

## Basic Configuration

- **Profile Name:** A friendly name for this connection.
- **Protocol:**
  - **RAW_TCP:** Direct TCP connection (Default port 9999).
  - **MQTT:** Connection via an MQTT Broker (Default port 1883).
  - **MODBUS_TCP:** Not yet implemented. Selecting this protocol will fail to connect.
  - **OPC_UA:** Not yet implemented. Selecting this protocol will fail to connect.
- **IP Address / Broker Host:** The network address of your PLC or MQTT Broker.
- **Port:** The network port (automatically switches between 9999 for TCP and 1883 for MQTT when changing protocols).

## Raw TCP Settings

When the **RAW_TCP** protocol is selected, the HMI communicates using a simple line-based text protocol over a TCP socket.

### Wire Format

Each message is a single line terminated by a newline character:

```
TAG:VALUE\n
```

**Inbound (server to HMI):**
```
TANK_LEVEL:75.5
PUMP_ENABLED:true
MOTOR_01.label:Main Pump
MOTOR_01.color:#FF0000
```

**Outbound (HMI to server):**
```
TANK_LEVEL:50.0
PUMP_ENABLED:true
```

### Value Types

Values are inferred automatically from the payload:

| Payload           | Parsed As   | Example               |
|-------------------|-------------|------------------------|
| `true` / `false`  | Boolean     | `PUMP:true`            |
| Contains `.`      | Float       | `TEMP:23.5`            |
| Integer string    | Integer     | `COUNT:42`             |
| Anything else     | Float (fallback) | `LEVEL:100`       |

### Dynamic Attributes

The Raw TCP protocol supports updating widget appearance remotely by appending an attribute name after the tag with a `.` separator:

```
TAG.attribute:VALUE
```

| Attribute | Effect                                    | Example                        |
|-----------|-------------------------------------------|--------------------------------|
| `label`   | Overrides the widget's display text       | `MOTOR_01.label:Main Pump`     |
| `color`   | Overrides the widget's background color   | `MOTOR_01.color:#FF0000`       |

These overrides are applied at runtime and do not modify the saved widget configuration. They persist for the duration of the connection session.

### Connection Health

The HMI sends a heartbeat (empty newline) every 5 seconds to detect half-open connections. If the server becomes unreachable, the connection state transitions to ERROR.

### Testing with ncat

You can test without a PLC using a simple TCP listener:

```bash
ncat -l 9999 -k --crlf
```

Type `TAG:VALUE` lines to simulate PLC data. The HMI will display values and respond with writes when sliders or buttons are used.

## MQTT Settings

When the **MQTT** protocol is selected, additional settings become available:

- **Client ID:** A unique identifier for the HMI on the broker. If left blank, a random ID is generated.
- **Username / Password:** Optional credentials for brokers requiring authentication.
- **Root Topic Prefix:** (Optional) A base path prepended to all relative tag addresses (e.g. `hmi/plant1`).

### Topic Addressing

Widget tag addresses can be **relative** or **absolute**.

- **Relative (default):** A plain tag address is joined with the prefix. `motor/speed` + `hmi/plant1` becomes `hmi/plant1/motor/speed`.
- **Absolute:** Start the address with a `/` to bypass the prefix entirely. `/zigbee2mqtt/sensor` stays `/zigbee2mqtt/sensor`.

### Connection Status (LWT)

The HMI automatically publishes its status to `[prefix]/status`.
- **online**: Published on connection.
- **offline**: Published on clean disconnect or via **Last Will and Testament (LWT)** if the connection is lost unexpectedly.

### Write Topics
By default, widgets read and write to the same topic. For systems like Home Assistant that use separate state/command topics, use the **Write Topic** field in the widget configuration to specify a dedicated command path.

## Advanced Options

- **Keep screen on while dashboard is active:** Prevents the Android device from dimming or sleeping while you are monitoring the HMI.

## Demo Mode

The built-in **Local Demo Server** profile launches an in-process TCP server on `127.0.0.1:9999` that simulates a PLC with several tags. No external hardware or software is required.

### Available Demo Tags

| Tag            | Behavior                                                         |
|----------------|------------------------------------------------------------------|
| `SIM_TEMP`     | Sine-wave oscillation between 20 and 100 over a 60-second cycle |
| `SIM_PRESSURE` | Random walk with small drift (±0.2 per second)                  |
| `SIM_STATUS`   | Boolean that randomly toggles (10% chance per second)            |
| `USER_LEVEL`   | Writable value with moderate drift (±1.0 per second)            |

### Dynamic Attribute Demo

`SIM_TEMP` also generates dynamic attribute updates to demonstrate runtime widget customization:

| Temperature Range | Label        | Color                |
|-------------------|--------------|----------------------|
| < 40              | Cold         | `#0000FF` (Blue)     |
| 40 - 70           | Optimal      | `#00FF00` (Green)    |
| 70 - 90           | Warning      | `#FFA500` (Orange)   |
| >= 90             | CRITICAL     | `#FF0000` (Red)      |

To try it: create a Gauge widget with Tag Address `SIM_TEMP` and watch the label and background color change automatically as the simulated temperature sweeps through its range.

## Connection States

The "Connect" button indicates the current status:
- **Connect:** Ready to attempt a connection.
- **Connecting...:** The app is currently establishing a link.
- **Connected:** The connection is active. You will be automatically redirected to the dashboard.
- **Error:** If a connection fails, an error message will appear below the button.

## Troubleshooting

- **Connection Failed:** Verify the IP address and Port are correct and that the device is on the same network as the PLC/Broker.
- **MQTT Disconnects:** Ensure your Client ID is unique. If another client connects with the same ID, the broker will disconnect the HMI.
