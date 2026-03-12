# Feature Specification: Keep Screen On

**Feature Branch**: `007-keep-screen-on`  
**Created**: 2026-03-12  
**Status**: Draft  
**Input**: User description: "can you keep the screen on like youtube or other video players that are not expecting interaction?"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Continuous Dashboard Monitoring (Priority: P1)

As an Operator using the HMI, I want the screen to stay on while the dashboard is visible, so that I can monitor live data and alerts without the device timing out and requiring me to unlock it repeatedly.

**Why this priority**: Core requirement for an industrial HMI. Operators need to see the machine state at all times without physical interaction.

**Independent Test**: Can be tested by navigating to the dashboard and waiting longer than the system's screen timeout period to verify the screen remains active.

**Acceptance Scenarios**:

1. **Given** I am on the Dashboard screen, **When** no touch interaction occurs for a period exceeding the device's screen timeout, **Then** the screen MUST remain on and fully visible.
2. **Given** the screen is being kept on by the dashboard, **When** I navigate back to the Setup/Connection screen, **Then** the screen MUST follow standard system timeout rules again.
3. **Given** the app is in the background, **When** the dashboard is not visible, **Then** the screen MUST NOT be prevented from timing out.

---

### User Story 2 - User Control Over Screen Behavior (Priority: P2)

As an Engineer, I want the option to enable or disable the "Keep Screen On" feature, so that I can conserve battery when continuous monitoring is not required. This is a persistent setting that defaults to 'Enabled'.

**Why this priority**: Essential for portable devices where battery life is a concern.

**Independent Test**: Can be tested by toggling the setting in the Setup screen and verifying the screen timeout behavior changes accordingly.

**Acceptance Scenarios**:

1. **Given** the "Keep Screen On" setting is disabled in the Setup screen, **When** I am on the Dashboard, **Then** the screen MUST time out according to system settings.
2. **Given** a new installation, **When** I first navigate to the Dashboard, **Then** the "Keep Screen On" feature MUST be active by default.

---

### Edge Cases

- **Battery Level**: Should the system automatically allow timeout if the battery drops below a certain percentage (e.g., 15%)?
- **External Power**: Should the screen always stay on when connected to a charger, regardless of the dashboard visibility? (Reasonable default: Only manage it via dashboard visibility for now).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST prevent the device screen from dimming or turning off while the Dashboard screen is in the foreground.
- **FR-002**: System MUST allow standard screen timeout behavior when the application is backgrounded or on non-dashboard screens.
- **FR-003**: System MUST provide a toggle in the Setup (Connection) screen to enable/disable this behavior.
- **FR-004**: The "Keep Screen On" feature MUST be enabled by default.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-001**: Visual feedback SHOULD be provided if the "Keep Screen On" state changes (e.g., an icon in the top bar).
- **A11Y-002**: If the battery is critically low, the system SHOULD notify the user before allowing the screen to time out.

### Key Entities *(include if feature involves data)*

- **AppPreferences**: Stores the user's choice for the "Keep Screen On" setting.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of devices remain active for at least 60 minutes of zero-interaction time while the dashboard is visible (if enabled).
- **SC-002**: Device returns to normal timeout behavior within 1 second of navigating away from the dashboard.
- **SC-003**: Battery consumption increase is documented and proportional to the extended screen-on time.
