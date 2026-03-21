# Feature Specification: Telemetry Safety Standards (SI Compliance & ISA-18.2)

**Feature Branch**: `021-telemetry-safety-standard`  
**Created**: 2026-03-21  
**Status**: Draft  
**Input**: User description: "Implement SI unit compliance and ISA-18.2 alarm protocol based on @DESIGN.md"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Safety-Critical Telemetry Monitoring (Priority: P1)

As an industrial operator, I need telemetry units to be accurately formatted and alarms to be conspicuous but readable, so that I can monitor system health without misinterpreting data or being blinded by flashing text.

**Why this priority**: Correct data interpretation (SI units) and readability during alarms are life-critical in high-stakes environments.

**Independent Test**: Can be fully tested by triggering an out-of-bounds value and verifying that the unit is correctly cased (e.g., `mV`) and the value remains static while the bounding box flashes.

**Acceptance Scenarios**:

1. **Given** a gauge monitoring voltage, **When** the value is `0.005` volts, **Then** the unit is displayed as `mV` (not `MV` or `mv`).
2. **Given** a telemetry block in a normal state, **When** a critical fault occurs, **Then** the 2px bounding box flashes at 3-5 Hz while the numeric value remains static.

---

### User Story 2 - Alarm Acknowledgement & Flash Suppression (Priority: P2)

As an operator, I need to acknowledge an active alarm to stop its flashing, so that I can focus on resolving the issue without visual distraction while still seeing the "Warning" state.

**Why this priority**: Continuous flashing during troubleshooting leads to cognitive overload and fatigue.

**Independent Test**: Can be tested by tapping a flashing alarm and verifying the flashing stops but the warning color (Solid Red) persists.

**Acceptance Scenarios**:

1. **Given** an active flashing alarm, **When** the operator acknowledges the alert, **Then** the flashing stops immediately.
2. **Given** an acknowledged alarm, **When** the operator views the dashboard, **Then** the affected telemetry block remains in a high-contrast "Warning" state (Solid Red background or border) until the fault is resolved.

---

### User Story 3 - High-Density Unit Scanning (Priority: P3)

As a technician, I need to scan multiple telemetry blocks rapidly to differentiate between magnitudes (e.g., kilo vs. mega), so that I can ensure all systems are within operating parameters.

**Why this priority**: Faster scanning improves operational efficiency and reduces the window of error.

**Independent Test**: Can be tested by displaying multiple units with different cases (kW, MW, Hz, mV) and verifying they match SI standards.

**Acceptance Scenarios**:

1. **Given** multiple telemetry blocks on screen, **When** they display SI units, **Then** symbols like `k` (kilo) remain lowercase and `M` (Mega) remain uppercase.

### Edge Cases

- **Multiple Concurrent Alarms**: If multiple telemetry points fail simultaneously, each module MUST independently execute its own flashing logic within its 2px bounding box.
- **Data Stream Loss**: If a telemetry value becomes null or stale, the numeric display MUST show `---` (placeholder), but the SI unit (e.g., `mV`) MUST remain visible to maintain context.
- **Automatic Fault Recovery**: If a telemetry value returns to a "Normal" state before an operator acknowledges the alarm, the flashing/warning state MUST automatically clear immediately.
- **Overlapping Interactions**: If an operator acknowledges an alarm at the exact moment a second fault occurs in the same module, the UI MUST prioritize the newest unacknowledged fault (re-trigger flashing).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST enforce case-sensitive SI unit symbols for all telemetry displays (e.g., `kW`, `mV`, `MHz`, `Hz`).
- **FR-002**: System MUST NOT automatically capitalize telemetry units for aesthetic consistency.
- **FR-003**: System MUST display critical alarms by flashing only the 2px bounding box or an adjacent icon, leaving the telemetry value static.
- **FR-004**: System MUST pulse the alarm bounding box at a frequency between 3Hz and 5Hz for unacknowledged critical failures.
- **FR-005**: System MUST provide a "Single Tap" interaction on the affected telemetry block for operators to "Acknowledge" an alarm (Flash Suppression).
- **FR-006**: System MUST maintain a persistent high-contrast "Warning" state (Solid Red) for acknowledged alarms until the underlying fault is cleared.

### Assumptions

- **Hardware Display Calibration**: Assumes the target display provides sufficient contrast for the obsidian palette and high-contrast red warning states.
- **Operator Training**: Assumes operators are trained to recognize that a static red bounding box indicates an acknowledged but unresolved fault.
- **NVIS Decoupling**: Assumes that true MIL-STD-3009 night vision compatibility is handled at the hardware layer and not through these specific software alarm protocols.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: Flashing elements MUST NOT exceed 5Hz to minimize the risk of photosensitive seizures (complying with MIL-STD-1472H).
- **A11Y-002**: Telemetry values MUST maintain a minimum contrast ratio of 7:1 against their background during all alarm states.
- **UI-001**: UI MUST use a "Hard Industrial" 2px border radius for all telemetry blocks and buttons.
- **UI-002**: UI MUST prioritize data density, utilizing arched or linear sweeps instead of circular gauges to maximize grid efficiency.
- **UI-003**: UI MUST exclude all tooltips; all necessary context (labels and SI units) MUST be visible on-screen.

### Key Entities *(include if feature involves data)*

- **Telemetry Point**: Represents a single stream of data (value, SI unit, current state).
- **Alarm State**: Represents the lifecycle of a fault (Normal, Unacknowledged Alarm, Acknowledged Alarm).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of displayed telemetry units match strict SI case-sensitivity formatting.
- **SC-002**: Telemetry text remains 100% legible (static) during active unacknowledged alarm states.
- **SC-003**: Operators can successfully transition an alarm from "Flashing" to "Suppressed" in a single interaction.
- **SC-004**: All interactive elements and data blocks adhere to the 2px "Hard Industrial" border radius.
