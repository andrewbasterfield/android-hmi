# Code Review Report: android-ui
**Date:** March 28, 2026
**Status:** ⚠️ Stability & Performance Risks Remaining

## 1. Executive Summary
The `android-ui` codebase has undergone significant improvements. Critical issues like coroutine leaks, non-atomic state mutations, and GSON type-safety risks have been addressed. However, **new architectural bottlenecks** and **brittle error handling** in the protocol layer have surfaced, particularly regarding industrial scale and high-frequency updates.

---

## 2. Resolved Issues ✅

| ID | Issue | Status | Resolution |
| :--- | :--- | :--- | :--- |
| **2.1** | TCP Socket Leak (Failed Connect) | **Fixed** | `RawTcpPlcCommunicator.kt` now correctly closes local sockets in `catch` blocks on failed connection attempts. |
| **2.2** | Coroutine Leak in ViewModel | **Fixed** | `DashboardViewModel.kt` tracks active observations in a map and cancels them when no longer needed. |
| **2.3** | Non-Atomic State Mutations | **Fixed** | State updates now use `MutableStateFlow.update { ... }` for thread-safe mutations. |
| **3.2** | Inefficient Canvas Math | **Fixed** | `GaugeWidget.kt` uses `remember` to cache geometry and tick calculations. |
| **4.1** | GSON Type-Safety Bypass | **Fixed** | Migrated to `kotlinx.serialization` for layouts and backups. |
| **4.2** | MQTT Subscription Redundancy | **Fixed** | `MqttPlcCommunicator.kt` implements reference-counting via `Flow` sharing. |

---

## 3. New & Remaining Critical Issues

### 3.1 UI Thread Bottleneck: Global Health Status
*   **Issue:** The `globalStatus` in `DashboardScreen.kt` is calculated using `derivedStateOf` which iterates through all widgets and their color zones on every single tag update.
*   **Impact:** At industrial frequencies (e.g., 20+ tags at 10Hz), this $O(N)$ calculation runs on the UI thread, causing significant jank and dropping frames.
*   **Recommendation:** Move health status logic to the `ViewModel` (running on `ioDispatcher`) and expose it as a single `StateFlow`.

### 3.2 Brittle Error Detection in `MqttPlcCommunicator.kt`
*   **Issue:** Distinguishing between intentional disconnections and errors relies on `cause.toString().contains("Optional.empty")`.
*   **Impact:** This is extremely fragile. Changes to the internal `toString()` implementation of the MQTT library (HiveMQ) will break the application's error handling.
*   **Recommendation:** Use the official library API (e.g., `Optional.isPresent()` or equivalent) for checking the presence of a cause.

### 3.3 Stale Socket Leak (Direct Reconnect)
*   **Issue:** While `PlcCommunicatorDispatcher` handles disconnects, `RawTcpPlcCommunicator.connect()` does not close an existing `socket` before overwriting it if it's already connected.
*   **Impact:** If the communicator is ever used directly (bypassing the dispatcher), repeated `connect()` calls will leak file descriptors.
*   **Recommendation:** Always call `disconnect()` internally at the start of `connect()`.

### 3.4 Thread Safety Risk: `activeTagObservations`
*   **Issue:** `activeTagObservations` in `DashboardViewModel` is modified from multiple coroutines without synchronization.
*   **Impact:** Potential `ConcurrentModificationException` when widgets are added/removed rapidly.
*   **Recommendation:** Wrap map operations in a `synchronized` block or use a thread-safe collection.

---

## 4. Architectural Risks

### 4.1 Missing TCP Heartbeat
*   **Issue:** `RawTcpPlcCommunicator.kt` lacks a mechanism to detect "half-open" connections (e.g., cable pull).
*   **Impact:** The app may stay in `CONNECTED` state indefinitely without receiving data.
*   **Recommendation:** Implement a simple ping/pong heartbeat or socket timeout.

### 4.2 Migration Logic in ViewModel
*   **Issue:** `migrateToKineticCockpit` is currently embedded in the `DashboardViewModel`.
*   **Impact:** Violates Separation of Concerns; makes the ViewModel harder to test and maintain.
*   **Recommendation:** Extract migration logic to a dedicated `LayoutMigrationManager`.

---
**End of Review**
