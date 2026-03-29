# Widget Configuration Guide

## Common Settings (All Widgets)

| Setting              | Description                                                |
|----------------------|------------------------------------------------------------|
| Tag Address          | MQTT topic or TCP tag to read from                         |
| JSON Path            | Optional dot-notation path to extract from a JSON payload (e.g. `status.temp`) |
| Write Topic          | Optional separate topic for sending commands (sliders, buttons only) |
| Write Template       | Optional JSON template for outgoing commands (e.g. `{"val": $VALUE}`) |
| Custom Label         | Display name override (defaults to Tag Address)            |
| Width / Height       | Grid span in columns and rows                              |
| Label Font Size      | Scale multiplier for the label (0 = hidden, up to 2x)     |
| Show Outline         | Adds a visible border around the widget                    |
| Background Color     | Widget background from the color picker                    |
| Label Color          | Text color override (auto-detected from background if empty) |

## Alarms

Gauge widgets support an alarm state that provides visual signaling for critical conditions.

| State            | Visual                                            | Interaction                |
|------------------|---------------------------------------------------|----------------------------|
| Normal           | No border                                         | None                       |
| Unacknowledged   | Pulsing red border (4 Hz)                         | Tap the widget to acknowledge |
| Acknowledged     | Static red border                                 | None (requires manual reset) |

When a gauge is in the **Unacknowledged** state, tapping it transitions to **Acknowledged**, which stops the pulse but keeps the red border as a reminder.

> **Note**: There is currently no UI to set the alarm state on a widget. It can only be configured by editing the layout JSON directly (set `"alarmState"` to `"Normal"`, `"Unacknowledged"`, or `"Acknowledged"`).

## Gauge

Gauges display a read-only numeric value with a visual indicator.

| Setting            | Description                                              |
|--------------------|----------------------------------------------------------|
| Units              | Suffix displayed after the value (e.g. `PSI`, `°C`, `W`) |
| Decimal Places     | Number of decimal places for the metric display (0-4)    |
| Min / Max          | Scale range                                               |
| Metric Font Size   | Scale multiplier for the value readout (0 = hidden)      |
| Tick Density       | Target number of tick marks on the scale                  |
| Gauge Axis         | ARC (radial), HORIZONTAL, or VERTICAL                    |
| Arc Sweep          | Arc angle in degrees (90-270, arc gauges only)           |
| Gauge Indicator    | POINTER (needle/triangle) or FILL (bar fill)             |
| Pointer Color      | Static color, or dynamic (follows zone colors)           |
| Color Zones        | Colored ranges on the scale (e.g. green 0-80, red 80-100) |

## Slider

Sliders send a numeric value within a configurable range.

| Setting            | Description                                              |
|--------------------|----------------------------------------------------------|
| Units              | Suffix displayed after the value                         |
| Decimal Places     | Precision for both display and sent value (0-4)          |
| Min / Max          | Slider range                                              |
| Metric Font Size   | Scale multiplier for the value readout                   |
| Orientation        | HORIZONTAL or VERTICAL                                    |

The slider only publishes to the broker when the rounded value actually changes, avoiding duplicate messages during drag.

## Button

Buttons send a configured value on press and/or release.

| Setting            | Description                                              |
|--------------------|----------------------------------------------------------|
| Interaction Mode   | MOMENTARY (sends on press and release), LATCHING (toggles on tap), or INDICATOR (read-only) |
| Invert Logic       | Swaps the visual active/inactive state                   |
| True Values        | Comma-separated list of values recognized as "on". First value is sent on press/toggle-on. Default: `true, 1, on` |
| False Values       | Comma-separated list of values recognized as "off". First value is sent on release/toggle-off. Default: `false, 0, off` |

### True/False Value Matching

When a message arrives on the Tag Address, the raw payload is matched (case-insensitive) against the True Values and False Values lists to determine the button's visual state. This allows integration with systems that use non-standard payloads like `"running"` / `"stopped"` or `"ON"` / `"OFF"`.

If the payload doesn't match either list, the button falls back to numeric comparison (value > 0.5 = on).
