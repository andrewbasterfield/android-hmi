# Quickstart: Configurable Gauge Tick Density

**Feature**: `018-gauge-tick-config` | **Status**: Complete
**Input**: Feature specification and design artifacts

## Overview
This feature allows dashboard engineers to tune the information density of Gauge widgets. High-density settings provide precision for technical monitoring, while low-density settings offer a cleaner visual for "at-a-glance" status panels.

## Verification Scenarios

### Scenario 1: High-Density Gauge Precision (US1)

- **Given**: A Gauge widget with range 0 to 100.
- **When**: I enter Edit Mode and set "Tick Density" to 20.
- **Then**: 
    - The "Scale Assistance" label should show: `Outcome: ~21 Ticks (Steps of 5.0)`.
    - The gauge should render small ticks every 5 units.

### Scenario 2: Low-Density Gauge Clarity (US1)

- **Given**: A Gauge widget with range 0 to 100.
- **When**: I enter Edit Mode and set "Tick Density" to 2.
- **Then**:
    - The "Scale Assistance" label should show: `Outcome: ~3 Ticks (Steps of 50.0)`.
    - The gauge dial should only show tick marks at 0, 50, and 100.

### Scenario 3: Scale Assistance Guidance (FR-006)

- **Given**: The Widget Configuration Dialog is open for any Gauge.
- **When**: I move the density slider from 2 to 20.
- **Then**: The "Outcome" label MUST update immediately to reflect the calculated tick count and step size based on the current range.
