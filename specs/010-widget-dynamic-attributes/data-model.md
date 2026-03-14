# Data Model: Custom Labels and Dynamic Attribute Updates

## Entities

### WidgetConfiguration (Updated)
Represents the persistent state of a dashboard widget.
- **id**: UUID String (Primary Key)
- **type**: Enum (BUTTON, SLIDER, GAUGE)
- **tagAddress**: String
- **customLabel**: String? (Optional override for the display name)
- **backgroundColor**: Long? (Persistent base color)
- **...** (existing spatial fields)

### WidgetSessionOverrides (Transient)
In-memory state representing protocol-driven changes during the current session.
- **tagAddress**: String (Identifier)
- **labelOverride**: String?
- **colorOverride**: Long?

## Validation Rules
- **customLabel**: Maximum 50 characters for UI safety.
- **colorOverride**: Must be a valid 6 or 8-digit hex string if coming via protocol.

## State Transitions
1. **Initial**: Widget displays `tagAddress`.
2. **User Overrides**: User sets `customLabel` in Edit Mode -> Saved to DataStore -> UI updates to `customLabel`.
3. **Protocol Overrides**: Server sends `TAG.label:VALUE` -> Stored in session memory -> UI updates to `VALUE` (overriding both `tagAddress` and `customLabel`).
4. **App Restart**: Session memory cleared -> UI reverts to `customLabel` (persistent) or `tagAddress`.
