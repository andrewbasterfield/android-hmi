# Quickstart: Configuring Frontend Color Thresholds

This guide explains how to set up dynamic color shifts for your widgets now that backend `.color` support has been removed.

## Step 1: Open Widget Settings
1. Enter **Edit Mode** on your dashboard.
2. Click the gear icon on the widget you want to configure.

## Step 2: Define Color Zones
In the configuration panel, look for the **Color Zones** section.

1. Click **Add Zone**.
2. Set the **Start Value** and **End Value** for the range.
3. Select a **Color** for this range.
4. (Optional) Provide a **Label** (e.g., "DANGER").

## Step 3: Verify in Run Mode
1. Exit Edit Mode.
2. Watch the widget as the tag value changes. It will now automatically shift colors based on your defined zones.

## Example Configuration: Temperature Gauge
- **Zone 1**: 0.0 to 40.0 -> Blue (Cold)
- **Zone 2**: 40.1 to 80.0 -> Green (Normal)
- **Zone 3**: 80.1 to 100.0 -> Red (Critical)

*Note: The backend protocol command `SIM_TEMP.color:#FF0000` will no longer work.*
