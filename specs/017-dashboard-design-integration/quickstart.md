# Quickstart: Dashboard Design Integration (Kinetic Cockpit)

**Feature**: `017-dashboard-design-integration` | **Status**: Complete
**Input**: Specification and user stories from `/specs/017-dashboard-design-integration/spec.md`

## Overview
This feature integrates the **Kinetic Cockpit** design system directly into the existing functional dashboard. It transforms current generic HMI widgets into rugged, high-clarity industrial controls using the Obsidian palette and 0dp geometric forms.

## Verification Scenarios

### Scenario 1: Automatic Ruggedization Migration (US1)

- **Given**: A legacy dashboard layout with light colors and rounded corners.
- **When**: The app is launched with the Kinetic Cockpit feature enabled.
- **Then**: 
    - The canvas background MUST become Obsidian (#131313).
    - All widgets MUST have 0dp (rectangular) corners.
    - Legacy colors MUST be sanitized to high-contrast OSHA-compliant tokens.

### Scenario 2: Industrial Typography Scale (US1)

- **Given**: A Gauge or Slider widget displaying live PLC data.
- **When**: Viewed on a tablet at a distance of 1 meter.
- **Then**:
    - The numerical readout MUST be at least 24sp.
    - All numeric data MUST use Tabular (Monospaced) figures to prevent layout "jumping."
    - All labels MUST use Space Grotesk Bold and be at least 16sp.

### Scenario 3: Tactile Interaction & Management (US2)

- **Given**: A dashboard button or the dashboard in Edit Mode.
- **When**: The button is pressed OR a widget corner is touched for resizing.
- **Then**:
    - Button: Swaps to "Inverse Video" immediately (<50ms).
    - Edit Mode: A high-contrast corner handle MUST be visible and responsive to touch resizing.
