# Research: Built-in Demo Server Integration

## Technical Context & Decisions

### Decision 1: Server Lifecycle Management
**Decision**: Start the `DemoPlcServer` in `HmiApplication.onCreate()` (Always On).
**Rationale**: 
- Simplifies connection logic; the server is always ready at `127.0.0.1:9999`.
- Minimizes delay when a user selects "Demo Mode".
- The resource overhead of an idle `ServerSocket` is negligible on modern Android devices.
**Alternatives considered**: 
- **On-Demand**: Start only when "Demo Mode" is selected. Rejected to ensure the fastest possible "Aha!" moment for new users.

### Decision 2: Connection UI Integration
**Decision**: Add a dedicated "Connect to Local Demo Server" button in `ConnectionScreen`.
**Rationale**: 
- Provides a clear, one-tap entry point for new users.
- Distinct from the manual IP/Port entry, reducing confusion.
- Follows the specification's requirement for a seamless switch between modes.
**Alternatives considered**: 
- **Pre-filling IP/Port**: Automatically filling "127.0.0.1:9999". Rejected because it requires the user to still press "Connect", which is one extra tap compared to a dedicated button.

### Decision 3: Simulated Tag Behavior
**Decision**: Implement a background loop in `DemoPlcServer` that updates tags containing "temp", "pressure", or "level" every 1000ms.
**Rationale**: 
- Provides immediate visual feedback (moving needles/bars) without user interaction.
- Uses existing `broadcast` mechanism to notify all connected clients.
- Tags specified in the FR (e.g., `SIM_TEMP`) will be explicitly handled or matched by pattern.
**Alternatives considered**: 
- **Static values only**: Only update when the user interacts. Rejected because it looks "dead" to a first-time user.

### Decision 4: Communication Protocol
**Decision**: Stick to the existing "TAG:VALUE" line-based TCP protocol.
**Rationale**: 
- Reuses `RawTcpPlcCommunicator` without modification.
- Proven to work with existing widgets.
- Minimal complexity.
**Alternatives considered**: 
- **Direct Memory Injection**: Bypassing the network stack for demo mode. Rejected because we want to test the *actual* communication stack, including serialization/deserialization.

## Best Practices (Android/Kotlin/Hilt)
- **Singleton Scoping**: `DemoPlcServer` must be a `@Singleton` to ensure only one instance is running per app process.
- **IO Dispatchers**: All socket operations must remain on `Dispatchers.IO`.
- **Coroutine Scoping**: Use a `SupervisorJob` in the server's scope to prevent one failed connection from killing the entire server.

## Unknowns Resolved
- **Port Conflicts**: If port 9999 is taken, the server will fail silently or log an error. Since it's loopback, this is unlikely unless another HMI app is running.
- **Reconnection**: The `RawTcpPlcCommunicator` handles reconnection naturally by closing the old socket and opening a new one to `127.0.0.1:9999`.
