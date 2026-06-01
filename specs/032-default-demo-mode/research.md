# Phase 0: Research

## Decisions & Rationale

### 1. Default Layout Injection Point
- **Decision**: The `DashboardRepository.dashboardLayoutFlow` will emit the built-in `DemoLayout` directly if no user configuration is found in the DataStore.
- **Rationale**: This is the simplest and most robust approach. It replaces the previous behavior of emitting a blank `DashboardLayout()`. By doing this, the rest of the application (like `DashboardViewModel`) doesn't need to know whether the layout is a demo or user-created; it just renders what the repository provides.
- **Alternatives considered**: Injecting the demo layout in `DashboardViewModel`. Rejected because it introduces unnecessary transient state tracking (`isDemoModeActive`) and complexity around intercepting save events.

### 2. Demo Data Source & Connection
- **Decision**: The Demo Layout will automatically establish a connection to the `DemoPlcServer` (localhost:9999) using a specific `PlcConnectionProfile`.
- **Rationale**: `DemoPlcServer` is already implemented and provides dynamic values for tags like `SIM_TEMP`, `SIM_PRESSURE`, `SIM_STATUS`. The Demo Layout will bind its widgets to these tags.
- **Alternatives considered**: Creating a mock data source in the ViewModel. Rejected because `DemoPlcServer` already exists and provides a more realistic end-to-end simulation.

### 3. Onboarding Text Widget
- **Decision**: The demo layout will include a Text widget containing instructions: "Welcome to the HMI Demo. This dashboard is showing simulated data from the local server. To connect to a real device or create a custom layout, use the settings menu."
- **Rationale**: Meets UI-004 specification for basic onboarding instructions.

### 4. Saving the Demo Layout
- **Decision**: Because the `DemoLayout` is emitted directly by the repository as the starting state, any user modifications (e.g., adding/moving widgets) will be saved normally by the existing app logic. If a new configuration is transferred, it will overwrite the existing configuration in the repository.
- **Rationale**: This aligns with the edge case assumption that modifying the demo makes it a normal user configuration. It removes the need for complex save-prevention logic, as the act of saving a modified demo layout is a valid user action.