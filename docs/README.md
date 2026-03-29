# HMI Control Panel Documentation

Welcome to the documentation for the HMI Control Panel. This guide will help you configure and use your Android-based industrial monitoring system.

## Getting Started

1.  **[Connection Guide](connection-guide.md)**: Learn how to connect to your PLC or MQTT Broker.
2.  **[Dashboard Usage](dashboard-usage.md)**: Master the interactive canvas and Edit Mode.

## Configuration & Customization

- **[Connection Guide](connection-guide.md)**: Detailed rules for MQTT topic addressing, Root Topic Prefixes, and Last Will and Testament.
- **[Widget Configuration](widget-configuration.md)**: Properties and settings for Gauges, Sliders, and Buttons.

## Maintenance & Migration

- **[System Transfer Guide](system-transfer.md)**: Backup and restore your layouts, profiles, and full system data.

---

## Key Features

- **Protocol Support:** RAW TCP and MQTT. (MODBUS TCP and OPC UA are defined but not yet implemented.)
- **Dynamic Grid:** Drag, resize, and snap widgets to a modular grid system.
- **2D Paging:** Infinite canvas expanding in four directions.
- **Dynamic Attributes:** Update widget labels and colors remotely via the protocol.
- **Alarm Signaling:** Pulsing visual alerts on gauges with tap-to-acknowledge.
- **Built-in Demo Server:** Test the full UI without external hardware using simulated tags.
- **Health Telemetry:** Built-in status monitoring for communication stability.
- **Industrial Design:** High-contrast UI with tactile haptic feedback.
