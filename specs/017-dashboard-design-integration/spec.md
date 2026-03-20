# Feature Specification: Dashboard Design Integration (Kinetic Cockpit)

**Feature Branch**: `017-dashboard-design-integration`  
**Created**: 2026-03-20  
**Status**: Draft  
**Input**: User description: "Apply the 'Kinetic Cockpit' design language to the existing functional dashboard and widgets, ensuring live PLC data is rendered using the new rugged aesthetic."

## Clarifications

### Session 2026-03-20
- Q: Scope of Ruggedization → A: Strictly Run Mode: Only ruggedize the operational dashboard and widgets.
- Q: Emergency HUD Persistence → A: State-Driven: Pulse as long as any tag remains in a CRITICAL state.
- Q: Interaction Confirmation (Haptics) → A: Configurable: Respect the hapticFeedbackEnabled setting.
- Q: "Void" Canvas Choice → A: Obsidian (#131313) to prevent OLED smearing; migrate existing layouts automatically.
- Q: Status Indicator Accessibility → A: Dropped widget header concept; status to be integrated into widget content or borders.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ruggedized Live Dashboard (Priority: P1)

As a plant operator, I want my existing functional dashboard to use the "Kinetic Cockpit" aesthetic so that I can monitor live machine data with improved legibility and rugged feedback.

**Acceptance Scenarios**:
1. **Given** the dashboard is open, **When** I view any widget, **Then** it must have a 0px border radius and use the obsidian palette.
2. **Given** a Gauge widget, **When** the PLC tag updates, **Then** the value must be rendered in Space Grotesk with monospaced alignment.

---

### User Story 2 - Tactile Feedback & Management (Priority: P2)

As an operator, I want to easily resize my widgets and receive immediate feedback on press so that I can manage my workspace effectively even in high-vibration environments.

**Acceptance Scenarios**:
1. **Given** a functional dashboard button, **When** I press it, **Then** the colors must immediately invert and trigger haptic feedback (if enabled).
2. **Given** Edit Mode is active, **When** I view a widget, **Then** a high-contrast tactile resize handle MUST be visible in the bottom-right corner.

---


### User Story 3 - Real-Time Emergency HUD (Priority: P3)

As an operator, I want the pulsing peripheral HUD to trigger based on live PLC alarms so that I am alerted to real machine emergencies.

**Acceptance Scenarios**:
1. **Given** a live PLC tag exceeds its critical threshold, **When** the value is received, **Then** the screen periphery must pulse red.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST wrap the existing `DashboardScreen` in the `StitchTheme` (Run Mode only).
- **FR-002**: All existing widgets (Gauge, Button, Slider) MUST be refactored to use 0dp corners and "Kinetic Cockpit" typography.
- **FR-003**: `WidgetContainer` MUST be updated to follow the 2px bezel and obsidian surface rules.
- **FR-004**: The `EmergencyHUD` MUST be bound to the live `tagValues` flow in `DashboardViewModel`.
- **FR-005**: Color picker presets MUST be updated to align with OSHA Safety Standards (ANSI Z535.1) and Kinetic Cockpit status tokens. Custom color inputs MUST be sanitized to ensure WCAG AAA contrast ratios.
- **FR-006**: "Inverse Video" interaction MUST perform a State Swap: the primary background color becomes the text/content color, and the background becomes Obsidian (#131313) or Black.
- **FR-007**: The pulsing peripheral HUD (EmergencyHUD) MUST trigger and remain active as long as at least one active telemetry tag is in a `CRITICAL` (Red) state. It MUST NOT clear until all critical conditions are resolved. `CAUTION` (Amber) states should only reflect on the individual widget.
- **FR-008**: All numerical telemetry values MUST use Tabular (Monospaced) figures regardless of the font family to ensure layout stability during updates.
- **FR-009**: Ruggedization components (0dp radius, 2px bezels) MUST NOT be applied to Edit Mode elements (palette, drag handles) to maintain scope.
- **FR-010**: Haptic feedback for tactile interactions MUST respect the `hapticFeedbackEnabled` configuration in the `DashboardLayout`.
- **FR-011**: Existing dashboard layouts MUST be automatically migrated to the "Void" background and obsidian surface tokens upon first launch of the feature.
- **FR-012**: WidgetContainer MUST provide high-contrast tactile **Resize Handles** in the bottom-right corner when in Edit Mode.
- **FR-013**: Typography MUST be scaled to "Industrial Utility" levels (Base size >= 16sp) to ensure legibility on vibrating mounting arms.

### Accessibility & UI Requirements *(mandatory)*
- **UI-001**: System MUST use Obsidian (#131313) as the default canvas background to minimize OLED smearing while maintaining surface hierarchy.
- **UI-002**: UI MUST NOT utilize any header bars or title strips on widgets; information density MUST be prioritized by keeping labels within the widget content area.

## Success Criteria *(mandatory)*
- **SC-001**: 100% of functional dashboard widgets adhere to the "Rugged Functionalism" aesthetic.
- **SC-002**: "Inverse Video" feedback latency is under 50ms on the dashboard.
- **SC-003**: Emergency HUD accurately triggers within 100ms of receiving a critical PLC tag update.
