# Feature Specification: Industrial Component Shape Refinement

**Feature Branch**: `022-industrial-button-shape`  
**Created**: 2026-03-21  
**Status**: Draft  
**Input**: User description: "Refactor IndustrialButton and IndustrialInput to use 2dp rounded corners per Design System Specification to reconcile the 'hard industrial' look with the 'avoid digital sharpness' mandate."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Tactile Hardware Aesthetic (Priority: P1)

As a plant operator, I want interactive buttons and inputs to look like physical, machined components rather than flat digital rectangles, so that the interface feels more like a reliable industrial control panel.

**Why this priority**: Core aesthetic requirement of the "Industrial Precision HMI" design system to balance rigidity with precision.

**Independent Test**: Can be tested by visually inspecting any screen containing an `IndustrialButton` or `IndustrialInput` and verifying that corners are slightly blunted (2dp) rather than perfectly sharp (0dp).

**Acceptance Scenarios**:

1. **Given** a dashboard with control buttons and search inputs, **When** the operator views the screen, **Then** all buttons and input backgrounds must exhibit a subtle 2dp corner radius.
2. **Given** an `IndustrialButton` or `IndustrialInput` is rendered, **When** compared to a 90-degree reference object, **Then** the component must appear "machined" rather than "digitally sharp."

---

### User Story 2 - Visual Fatigue Reduction (Priority: P2)

As a technician monitoring the system for long shifts, I want a UI that avoids harsh, pixel-perfect 90-degree corners on large interactive blocks, so that visual fatigue from high-contrast sharp edges is minimized.

**Why this priority**: Supports long-term operator focus and comfort in high-stakes environments.

**Independent Test**: Can be tested by comparing the current "Void" background contrast against 0dp corners versus 2dp corners; the 2dp corner should provide a smoother visual transition.

**Acceptance Scenarios**:

1. **Given** a high-contrast obsidian background (#131313), **When** a button or input is displayed, **Then** its corners must not create a "stair-step" aliasing effect associated with raw 90-degree angles.

## Edge Cases

- **Small Buttons**: How does a 2dp radius look on buttons that are at the minimum 64dp height but very narrow?
- **Nested Components**: If a component contains another outlined element, do the corners remain consistent?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: `IndustrialButton` and `IndustrialInput` MUST utilize a 2dp corner radius for their backgrounds and borders.
- **FR-002**: Components MUST link their shape to the system's "small" shape token to ensure visual consistency across the HMI.
- **FR-003**: The 2dp radius MUST be applied to all interactive states (Normal, Pressed, Focused).
- **FR-004**: The system MUST NOT use raw 90-degree (sharp) corners for primary interactive components.
- **FR-005**: All existing tactile interaction constraints (minimum 64px height, 2px border width, 4px bottom-border "shelf" for inputs) MUST be preserved.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: All interactive elements MUST have minimum touch targets of 64px height.
- **A11Y-002**: Meaningful icons within components MUST include content descriptions.
- **UI-001**: UI MUST follow the "Industrial Precision" principle: 2dp corners provide a "hard" but "machined" edge.
- **UI-002**: UI MUST maintain the "Void" background (#131313) and high-contrast text/border colors.

### Key Entities *(include if feature involves data)*

- **IndustrialButton**: Core interactive button component with a tactile physical toggle aesthetic.
- **IndustrialInput**: Heavy-duty text input field with a "shelf" bottom-border and machined-edge background.

## Assumptions

- **AS-001**: The "small" shape token in the design system is globally defined as a 2dp rounded corner.
- **AS-002**: Existing automated UI tests for component presence and interaction will remain valid after the shape change.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of `IndustrialButton` instances across all application screens exhibit a 2dp corner radius.
- **SC-002**: Zero 90-degree (0dp) corners remain on primary dashboard interactive elements.
- **SC-003**: Button interaction performance (press/release latency) remains unchanged or improves.
- **SC-004**: No regressions in touch target accuracy are observed (minimum 64dp height remains constant).
