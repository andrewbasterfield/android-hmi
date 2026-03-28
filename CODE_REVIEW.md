# Code Review Report: android-ui (Outstanding Issues)
**Date:** March 28, 2026
**Status:** ⚠️ Architectural Risks Remaining

## 1. Executive Summary
Following several hardening passes, all identified **critical stability bugs**, **thread-safety risks**, and **connectivity robustness issues** (TCP Heartbeat) have been resolved. The remaining items are primarily architectural refinements for long-term maintainability.

---

## 2. Resolved Issues ✅

| ID | Issue | Status | Resolution |
| :--- | :--- | :--- | :--- |
| **2.1** | Brittle MQTT Error Detection | **Fixed** | Migrated to official HiveMQ `Optional.isPresent` API. |
| **2.2** | Stale TCP Socket Leak | **Fixed** | `RawTcpPlcCommunicator.connect()` now calls `disconnect()` before opening new sockets. |
| **2.3** | Thread Safety Risk in ViewModel | **Fixed** | Wrapped `activeTagObservations` operations in `synchronized` blocks. |
| **3.1** | Missing TCP Heartbeat | **Fixed** | Implemented `soTimeout` and active write probe in `RawTcpPlcCommunicator`. |

---

## 3. Outstanding Architectural Risks

### 3.1 Embedded Migration Logic
*   **Issue:** The `migrateToKineticCockpit` logic is currently embedded directly within the `DashboardViewModel`.
*   **Impact:** Violates the Single Responsibility Principle; makes the ViewModel harder to test and maintain.
*   **Recommendation:** Extract this logic to a dedicated `LayoutMigrationManager`.

---
**End of Report**
