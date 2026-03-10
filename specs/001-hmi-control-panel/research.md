# Phase 0: Research & Technical Decisions

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **State Management**: ViewModels with Kotlin Coroutines (StateFlow/SharedFlow)
- **Dependency Injection**: Hilt (Standard for Android)
- **Persistence**: Jetpack DataStore (for connection settings and simple widget configurations) or Room if layout complexity grows. We'll start with DataStore for simple JSON-like serialization of the dashboard.
- **TCP/IP Communication**: Raw Java Sockets (`java.net.Socket`) wrapped in Kotlin Coroutines for non-blocking I/O.

## Protocol Abstraction (FR-009)

- **Decision**: Define a `PlcCommunicator` interface that abstracts the connection, disconnection, reading, and writing of tags.
- **Rationale**: This satisfies FR-009 and allows future implementations (Modbus, OPC UA) to be plugged in without changing the UI or core ViewModel logic.
- **Alternatives**: Using a third-party library that handles all protocols. Rejected because it might be bloated; better to implement custom lightweight wrappers around `java.net.Socket` first for raw TCP.

## Dashboard Customization (Edit Mode)

- **Decision**: Implement a drag-and-drop mechanism using Compose's `pointerInput` and `detectDragGestures`. Dashboard state will be a list of `WidgetState` objects containing X/Y coordinates.
- **Rationale**: Compose is well-suited for state-driven UI. By maintaining coordinates in the state, repositioning is simply updating the X/Y values in the StateFlow.
