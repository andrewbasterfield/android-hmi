# Quickstart: MQTT JSON Payload Support

## 1. Extracting Data from a JSON Topic

To display a single value from a JSON blob on a Gauge:

1.  **MQTT Connection:** Select **MQTT** as the protocol for your connection profile.
2.  **Add Gauge:** Place a Gauge on the dashboard and open its configuration.
3.  **Tag Address:** Set the MQTT topic (e.g., `zigbee2mqtt/living_room`).
4.  **JSON Path:** Enter the key to extract (e.g., `temperature`).
5.  **Nested Data:** For complex JSON like `{"sensors": {"temp": 22.5}}`, use `sensors.temp`.

## 2. Sending Commands with JSON Templates

To control a device that requires a structured JSON command:

1.  **Add Slider/Button:** Place a Slider or Button on the dashboard.
2.  **Tag Address:** Set the command topic (e.g., `homeassistant/light/set`).
3.  **Write Template:** Enter the JSON structure with the `$VALUE` token.
    -   Example: `{"brightness": $VALUE}`
    -   Example for string values: `{"state": "$VALUE"}`
4.  **Save:** When you move the slider or press the button, the HMI will wrap the value in your template before publishing.

## 3. Advanced: Shared Topic Optimization

The HMI automatically optimizes subscriptions. If you have three Gauges on the same `zigbee2mqtt/sensor` topic reading `humidity`, `temp`, and `voc_index`, only **one** subscription is created. Each Gauge will independently extract its value from the shared JSON blob.
