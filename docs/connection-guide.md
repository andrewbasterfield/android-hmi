# Connection Guide

This guide walks you through connecting to your PLCs or MQTT brokers using the HMI Control Panel.

## Connection Profiles

Profiles let you save connection settings for different machines or environments so you can switch between them quickly.

### Managing Profiles
- **Load Profile** -- Use the "Load" dropdown at the top to pick from your saved or built-in profiles.
- **Save Profile** -- Enter a unique name and click **Save**. If the name already exists, you'll be asked to overwrite it.
- **Delete Profile** -- Select a saved profile and click **Delete**. Built-in profiles can't be removed.
- **Built-in Profiles** -- The app ships with a "Local Demo Server" profile so you can test without any hardware.

## Basic Configuration

- **Profile Name** -- A friendly name for this connection.
- **Protocol:**
  - **RAW_TCP** -- Direct TCP connection (default port 9999).
  - **MQTT** -- Connect through an MQTT broker (default port 1883).
  - **MODBUS_TCP** -- Not yet implemented. Selecting this will fail to connect.
  - **OPC_UA** -- Not yet implemented. Selecting this will fail to connect.
- **IP Address / Broker Host** -- The network address of your PLC or MQTT broker.
- **Port** -- Automatically switches between 9999 (TCP) and 1883 (MQTT) when you change protocols.

## Raw TCP Settings

With **RAW_TCP** selected, the HMI communicates over a plain TCP socket using a simple line-based text protocol.

### Wire Format

Each message is a single newline-terminated line:

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

You can update a widget's appearance remotely by appending an attribute name after the tag with a `.` separator:

```
TAG.attribute:VALUE
```

| Attribute | What it does                              | Example                        |
|-----------|-------------------------------------------|--------------------------------|
| `label`   | Overrides the widget's display text       | `MOTOR_01.label:Main Pump`     |
| `color`   | Overrides the widget's background color   | `MOTOR_01.color:#FF0000`       |

These overrides are applied at runtime only -- they don't modify your saved widget configuration, and they last for the duration of the connection session.

### Connection Health

The HMI sends a heartbeat (an empty newline) every 5 seconds to detect half-open connections. If the server becomes unreachable, the connection state transitions to ERROR.

### Testing with ncat

You can try things out without a PLC using a simple TCP listener:

```bash
ncat -l 9999 -k --crlf
```

Type `TAG:VALUE` lines to simulate PLC data. The HMI will show the values and send writes back when you use sliders or buttons.

## MQTT Settings

When you select **MQTT**, a few additional settings appear:

- **Client ID** -- A unique identifier for the HMI on the broker. Leave it blank to auto-generate one.
- **Username / Password** -- Optional credentials for brokers that require authentication.
- **Root Topic Prefix** -- (Optional) A base path prepended to all relative tag addresses (e.g. `hmi/plant1`).

### Topic Addressing

Widget tag addresses can be **relative** or **absolute**:

- **Relative (default):** A plain tag address gets joined with the prefix. For example, `motor/speed` with prefix `hmi/plant1` becomes `hmi/plant1/motor/speed`.
- **Absolute:** Start the address with `/` to bypass the prefix entirely. `/zigbee2mqtt/sensor` stays exactly as-is.

### Connection Status (LWT)

The HMI automatically publishes its status to `[prefix]/status`:
- **online** -- Published when connected.
- **offline** -- Published on clean disconnect, or via **Last Will and Testament (LWT)** if the connection drops unexpectedly.

### Write Topics
By default, widgets read and write to the same topic. If your system uses separate state/command topics (common with Home Assistant), use the **Write Topic** field in the widget configuration to specify a dedicated command path.

## MQTT Advanced Features

### Per-Widget JSON Extraction

If your broker publishes data as JSON blobs (e.g. from Zigbee2MQTT), you can extract specific keys right in the widget configuration.

**JSON Path Syntax:** `key1.key2.leaf`

| Example Payload | JSON Path | Result |
|-----------------|-----------|--------|
| `{"temp": 22.5}` | `temp` | `22.5` |
| `{"motor": {"rpm": 1500}}` | `motor.rpm` | `1500` |

Multiple widgets can subscribe to the same topic with different paths. The HMI parses the blob once and fans out updates to all matching widgets efficiently.

### Structured Write Templates

If a device expects JSON-formatted commands, use the **Write Template** field on Sliders or Buttons.

**Template Variable:** `$VALUE` is replaced with the widget's current numeric or text value.

| Write Template | Widget Value | Published Payload |
|----------------|--------------|-------------------|
| `{"level": $VALUE}` | `75` | `{"level": 75}` |
| `{"cmd": "SET", "val": $VALUE}` | `42` | `{"cmd": "SET", "val": 42}` |
| `{"state": "$VALUE"}` | `on` | `{"state": "on"}` |

> **Tip**: The substitution is literal. If the destination expects a string, wrap `$VALUE` in quotes in your template.

## Advanced Options

- **Keep screen on while dashboard is active** -- Prevents your Android device from dimming or sleeping while you're monitoring.

## Demo Mode

The built-in **Local Demo Server** profile launches an in-process TCP server on `127.0.0.1:9999` that simulates a PLC with several tags. No external hardware or software needed -- just tap and go.

### Available Demo Tags

| Tag            | Behavior                                                         |
|----------------|------------------------------------------------------------------|
| `SIM_TEMP`     | Sine-wave oscillation between 20 and 100 over a 60-second cycle |
| `SIM_PRESSURE` | Random walk with small drift (+/-0.2 per second)                |
| `SIM_STATUS`   | Boolean that randomly toggles (10% chance per second)            |
| `USER_LEVEL`   | Writable value with moderate drift (+/-1.0 per second)          |

### Dynamic Attribute Demo

`SIM_TEMP` also generates dynamic attribute updates so you can see runtime widget customization in action:

| Temperature Range | Label        | Color                |
|-------------------|--------------|----------------------|
| < 40              | Cold         | `#0000FF` (Blue)     |
| 40 - 70           | Optimal      | `#00FF00` (Green)    |
| 70 - 90           | Warning      | `#FFA500` (Orange)   |
| >= 90             | CRITICAL     | `#FF0000` (Red)      |

To try it: create a Gauge widget with Tag Address `SIM_TEMP` and watch the label and background color change automatically as the temperature sweeps through its range.

## Connection States

The "Connect" button reflects the current status:
- **Connect** -- Ready to go.
- **Connecting...** -- Establishing the link.
- **Connected** -- You're live. The app will redirect you to the dashboard automatically.
- **Error** -- Something went wrong. Check the error message below the button for details.

## Troubleshooting

- **Connection Failed** -- Double-check the IP address and port. Make sure your device is on the same network as the PLC or broker.
- **MQTT Disconnects** -- Make sure your Client ID is unique. If another client connects with the same ID, the broker will kick the HMI off.
