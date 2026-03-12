# Feature Specification: HMI Widget Customization (Refinement)

**Feature Branch**: `002-hmi-widget-customization`  
**Created**: 2026-03-10  
**Status**: Draft  
**Input**: User description: "I want to design an app to be run on an android phone/tablet that I can easily customize to show controls such as buttons, sliders and also guages. This is an industrial control panel such as HMI / SCADA to interface with a PLC. The connection to PLC will be tcp/ip (TBD)"
**Refinement Request**: 
- Sliders should be resizable instead of full width
- Buttons should be resizable and different colours

## Assumptions

- **Existing Foundation**: The base HMI Control Panel (001-hmi-control-panel) is functional and has basic widget support.
- **Edit Mode**: Resizing and color changes are performed while the app is in "Edit Mode".
- **Persistence**: Any changes to size or color must be saved in the `DashboardLayout` via DataStore.

## User Scenarios & Testing

### User Story 1 - Resize Sliders and Buttons (Priority: P1)

As an engineer, I want to adjust the dimensions of sliders and buttons so that I can create a more compact or logically grouped interface.

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I select a Slider or Button, **Then** I see resize handles or a way to change its width and height.
2. **Given** I have resized a Slider to be 200dp wide, **When** I switch to Run Mode, **Then** the slider maintains its 200dp width and remains functional.

---

### User Story 2 - Customize Button Color (Priority: P1)

As an engineer, I want to change the color of buttons so that I can visually distinguish between different functions (e.g., Green for Start, Red for Stop).

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I select a Button, **Then** I am presented with a color selection menu (e.g., Red, Green, Blue, Yellow).
2. **Given** I have set a Button's color to Red, **When** I save the layout and restart the app, **Then** the button remains Red.

---

### User Story 3 - New Gauge Types: Bar Chart (Priority: P2)

As an operator, I want to see a bar-chart style display for certain process values so that I can easily monitor levels (e.g., tank levels) with appropriate color coding.

**Acceptance Scenarios**:

1. **Given** the app is in Edit Mode, **When** I select a "Bar Chart" from the control palette, **Then** a vertical or horizontal bar chart appears on the screen.
2. **Given** I am configuring a Bar Chart, **When** I choose a color (e.g., Blue for water, Green for healthy range), **Then** the bar itself reflects that color in Run Mode.

## Requirements

### Functional Requirements

- **FR-011**: System MUST allow users to specify width and height for Button widgets in Edit Mode.
- **FR-012**: System MUST allow users to specify width and height for Slider widgets in Edit Mode.
- **FR-013**: System MUST allow users to select a background color for Button widgets from a predefined list.
- **FR-014**: System MUST persist the dimensions (width, height) and color of widgets in the local `DashboardLayout` configuration.
- **FR-015**: Widgets MUST maintain their functionality (sending commands, displaying values) regardless of their size or color.
- **FR-016**: System MUST provide a "Bar Chart" gauge type that visually represents a single PLC value as a filled bar.
- **FR-017**: System MUST allow users to select the fill color for Bar Chart gauges.

### UI Requirements

- **UI-001**: Resize handles or dimension inputs MUST be intuitive and only accessible in Edit Mode.
- **UI-002**: Button colors MUST have sufficient contrast with text labels for readability.
- **A11Y-001**: Even when resized, interactive elements MUST maintain a minimum touch target of 48x48dp.

## Success Criteria

### Measurable Outcomes

- **SC-005**: An engineer can resize a widget and change its color in under 15 seconds while in Edit Mode.
- **SC-006**: The updated dimensions and colors are correctly reflected immediately in the UI and persisted upon saving.
