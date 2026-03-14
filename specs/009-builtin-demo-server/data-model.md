# Data Model: Built-in Demo Server

## Entities

### DemoTag
Represents a simulated tag within the internal demo server.
- **Name**: String (e.g., "SIM_TEMP", "SIM_STATUS")
- **Type**: Derived from value (Float, Int, Boolean)
- **CurrentValue**: String (serialized format "TAG:VALUE")
- **SimulationRule**: (Optional) Strategy for auto-updating the value (e.g., RandomDrift, Toggle)

### ConnectionProfile (Pre-defined)
A standard connection profile used to target the internal server.
- **IP Address**: `127.0.0.1`
- **Port**: `9999`
- **Protocol**: `RAW_TCP`

## Validation Rules
- **Tag Names**: MUST NOT contain colons (reserved as protocol delimiters).
- **Values**: MUST be serializable to strings that `RawTcpPlcCommunicator` can parse (floats, ints, or booleans).

## State Transitions
1. **Disconnected**: `DemoPlcServer` is running but has zero active socket connections.
2. **Connected**: `DemoPlcServer` has one or more active loopback connections from `RawTcpPlcCommunicator`.
3. **Simulating**: Background coroutine is periodically updating specific tag values and broadcasting them.
