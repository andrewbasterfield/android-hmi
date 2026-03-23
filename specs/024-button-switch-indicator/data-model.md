# Data Model: Latching Buttons and Indicator Lights

## Entities

### `InteractionType` (Enumeration)
Defines how a button widget responds to user input and reflects backend state.
- **`MOMENTARY`**: Standard push-button. Sends `true` on click. UI shows "Active" only while clicked (or briefly after).
- **`LATCHING`**: Switch/Toggle. Each click alternates the tag's boolean value. UI reflects the persistent state of the tag.
- **`INDICATOR`**: Status light. Ignores clicks. UI reflects the state of the tag.

### `WidgetConfiguration` (Updated)
The configuration object for a dashboard widget.
| Field | Type | Description |
|---|---|---|
| `interactionType` | `InteractionType` | The behavior mode for button widgets. Defaults to `MOMENTARY`. |
| `isInverted` | `Boolean` | If `true`, the UI state is the logical inverse of the backend tag value. Defaults to `false`. |

## State Transitions (LATCHING Mode)
1. **Initial State**: Tag is `false`, UI is "Inactive" (assuming `isInverted` = `false`).
2. **User Click**: 
   - UI immediately switches to "Active" (Optimistic).
   - `DashboardViewModel` calls `plcCommunicator.writeTag(address, true)`.
3. **Backend Sync**: 
   - `plcCommunicator` emits `true` for the tag.
   - `_tagValues` confirms `true`, UI remains "Active".
4. **Second User Click**:
   - UI immediately switches to "Inactive".
   - `DashboardViewModel` calls `plcCommunicator.writeTag(address, false)`.

## Logic Inversion Table
| Backend Value | `isInverted` | Visual State |
|---|---|---|
| `true` | `false` | **ACTIVE** (Identity Swap) |
| `false` | `false` | **INACTIVE** (Base Colors) |
| `true` | `true` | **INACTIVE** (Base Colors) |
| `false` | `true` | **ACTIVE** (Identity Swap) |
