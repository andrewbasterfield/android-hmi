# Research: Frontend-Driven Color Management

## Overview
This research explores the transition from backend-driven color updates (via the protocol) to frontend-driven color resolution based on tag values and local configurations.

## Decisions

### Decision 1: UI-Side Color Resolution Mechanism
- **Choice**: Implement a `ColorResolver` utility (or enhance `ColorUtils`) that takes a `currentValue` and a list of `ColorZones` and returns the appropriate `Color`.
- **Rationale**: Centralizing this logic ensures consistency across all widget types (Gauges, Indicators, etc.) and makes it easily testable in isolation.
- **Alternatives considered**: Putting the logic directly in each widget (rejected: code duplication) or in the `DashboardViewModel` only (rejected: logic should be pure and usable in previews).

### Decision 2: Protocol Modification
- **Choice**: Remove `.color` broadcasting from `DemoPlcServer` and ignore `.color` updates in `RawTcpPlcCommunicator` or `DashboardViewModel`.
- **Rationale**: "Dropping support" means the frontend should no longer respond to these messages, even if sent by a real PLC, to enforce the new architectural boundary.
- **Alternatives considered**: Continuing to support `.color` as a "manual override" (rejected: contrary to the directive to make it a "frontend concern").

### Decision 3: Configuration Authority
- **Choice**: The `WidgetConfiguration` (and its `colorZones` property) is the sole authority for color logic.
- **Rationale**: Simplifies the mental model for users—what you see in the editor is what determines the behavior, regardless of what the backend sends.

## Best Practices
- **Compose State**: Use `derivedStateOf` in Compose widgets to recalculate colors only when the value or the zones list changes.
- **Color Consistency**: Ensure `ColorZones` labels (e.g., "CRITICAL", "CAUTION") match the `HealthStatus` enum for global dashboard health reporting.
- **Unit Testing**: Create a robust test suite for `ColorResolver` covering edge cases like overlapping zones, exact boundary values, and empty zone lists.
