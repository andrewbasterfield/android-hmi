# Tasks: Built-in Demo Server Integration

**Input**: Design documents from `specs/009-builtin-demo-server/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Verify project structure for built-in demo server integration

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Enhance `DemoPlcServer` with simulated tags and loopback logic in `app/src/main/java/com/example/hmi/protocol/DemoPlcServer.kt`
- [x] T003 Ensure `DemoPlcServer` is properly initialized and started in `app/src/main/java/com/example/hmi/HmiApplication.kt`
- [x] T004 Create unit tests for `DemoPlcServer` broadcast and simulation logic in `app/src/test/java/com/example/hmi/protocol/DemoPlcServerTest.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Instant "Play" Mode (Priority: P1) 🎯 MVP

**Goal**: Allow first-time users to connect to a functioning simulation with a single tap.

**Independent Test**: Tapping "Connect to Local Demo Server" on the connection screen immediately opens the dashboard with live data.

### Implementation for User Story 1

- [x] T005 [P] [US1] Define demo server connection constants (IP: 127.0.0.1, Port: 9999) in `app/src/main/java/com.example.hmi.protocol.PlcConnectionProfile.kt` or `ConnectionViewModel.kt`
- [x] T006 [US1] Add a dedicated "Connect to Local Demo Server" button to `app/src/main/java/com/example/hmi/connection/ConnectionScreen.kt`
- [x] T007 [US1] Verify accessibility (touch targets 48x48dp, content descriptions) for the new button in `app/src/main/java/com/example/hmi/connection/ConnectionScreen.kt`
- [x] T008 [US1] Implement `connectToDemoServer()` logic in `app/src/main/java/com/example/hmi/connection/ConnectionViewModel.kt`

**Checkpoint**: User Story 1 is functional and testable independently.

---

## Phase 4: User Story 2 - Interaction Testing (Priority: P2)

**Goal**: Verify that widgets correctly send/receive data via the built-in server without external dependencies.

**Independent Test**: Moving a slider in the app updates the internal server state and reflects on other widgets bound to the same tag.

### Implementation for User Story 2

- [x] T009 [P] [US2] Enhance `DemoPlcServer` with more complex simulation patterns (drift, toggle, echo) in `app/src/main/java/com/example/hmi/protocol/DemoPlcServer.kt`
- [x] T010 [US2] Create an instrumentation test for the "Demo Mode" end-to-end flow in `app/src/androidTest/java/com/example/hmi/connection/DemoModeTest.kt`

**Checkpoint**: Interaction testing is verified using the built-in server.

---

## Phase 5: User Story 3 - Standalone Presentation (Priority: P3)

**Goal**: Demonstrate the app's capabilities in offline environments.

**Independent Test**: App functions perfectly in Airplane Mode using the local loopback demo server.

### Implementation for User Story 3

- [x] T011 [US3] Verify connection stability and reconnection logic for the demo server in `app/src/main/java/com/example/hmi/protocol/RawTcpPlcCommunicator.kt` (ensure loopback behaves as expected)

**Checkpoint**: Standalone presentation capability is confirmed.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T012 [P] Update `README.md` with Demo Mode instructions and quickstart steps
- [x] T013 [P] Final code cleanup and refactoring in `app/src/main/java/com/example/hmi/protocol/DemoPlcServer.kt`
- [x] T014 [P] Verify `quickstart.md` steps manually on a physical device or emulator

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: BLOCKS all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational phase completion.
  - US1 (P1) is the MVP and should be completed first.
  - US2 (P2) and US3 (P3) can proceed in parallel once US1 is stable.

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories.
- **User Story 2 (P2)**: Builds upon US1's connection flow.
- **User Story 3 (P3)**: Validates the end-to-end offline capability.

### Parallel Opportunities

- T001 can run in parallel with initial research review.
- T004 (Tests) can be worked on in parallel with T002/T003 (Implementation) in Phase 2.
- Once Phase 2 is complete, UI work (T006, T007) can start in parallel with ViewModel work (T008).

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2 (Foundational) to ensure the server is running and logic is tested.
2. Complete Phase 3 (User Story 1) to provide the "one-tap" entry point.
3. **STOP and VALIDATE**: Verify the "Demo Mode" button works and shows live data.

### Incremental Delivery

1. Foundation ready.
2. Add "Connect to Demo" button (MVP!).
3. Add complex simulation patterns and E2E tests.
4. Verify offline stability.
5. Final documentation and polish.

---

## Notes

- [P] tasks = different files, no dependencies.
- [Story] label maps task to specific user story for traceability.
- All tasks include exact file paths.
- Each user story is independently completable and testable.
