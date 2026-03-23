# Quickstart: MQTT Backend Support

## Setting Up a Test Broker (Local)

To test the HMI's MQTT functionality, it's recommended to run a local Mosquitto broker:

1. **Install Mosquitto**:
   ```bash
   # MacOS
   brew install mosquitto
   # Ubuntu/Debian
   sudo apt install mosquitto
   ```

2. **Run Mosquitto (Standard Port 1883)**:
   ```bash
   /usr/local/sbin/mosquitto -p 1883
   ```

## Configuring the HMI

1. Open the HMI Android Application.
2. Navigate to the **Connection Settings**.
3. Select **MQTT** as the Protocol.
4. Enter the Host (e.g., your development machine's local IP).
5. Port: `1883`.
6. Client ID: `hmi-test-1`.
7. (Optional) Set a Topic Prefix: `factory/line1/`.
8. Click **Connect**.

## Simulating Data (Telemetry)

Use `mosquitto_pub` to send data to the HMI:

**Plain Text Value**:
```bash
mosquitto_pub -h localhost -t "factory/line1/tank_level" -m "85.5"
```

**JSON Value**:
```bash
mosquitto_pub -h localhost -t "factory/line1/tank_level" -m '{"value": 92.1, "unit": "liters"}'
```

**Attribute Update (Dynamic UI)**:
```bash
mosquitto_pub -h localhost -t "factory/line1/tank_level/color" -m "#FF0000"
```

## Verifying Control (Egress)

Interact with a Button or Switch widget in the HMI and monitor the broker:

```bash
mosquitto_sub -h localhost -t "factory/line1/#" -v
```

You should see a message published to the tag's topic (e.g., `factory/line1/pump_start`) with the value `true` or `false`.
