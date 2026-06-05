# PLC Protocol Contract: Color Management Update

## Overview
This document specifies the changes to the expected TCP protocol between the HMI and the PLC/Server.

## Deprecated Attributes
The following attribute suffixes are **DEPRECATED** and will be ignored by the HMI starting with version 0.33.

| Attribute | Example | Status | Action |
|-----------|---------|--------|--------|
| `.color`  | `TAG.color:#FF0000` | Deprecated | Ignore on Receipt |

## Active Attributes
The protocol remains primarily focused on raw data and basic labeling.

| Attribute | Example | Status | Description |
|-----------|---------|--------|-------------|
| (none)    | `TAG:25.5` | Active | Raw value update (Float/Int/Bool). |
| `.label`  | `TAG.label:Temp` | Active | Transient label override for the widget. |

## Implementation for Backend/PLC
- **DO NOT** send `.color` messages.
- Sending `.color` messages will not cause errors, but they will have no effect on the HMI UI.
- All threshold logic must be configured within the HMI application itself.
