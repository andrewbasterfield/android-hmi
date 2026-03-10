# Tasks: HMI Control Panel

**Input**: Design documents from `/specs/001-hmi-control-panel/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Initialize Android project with Jetpack Compose in `app/build.gradle`
- [x] T002 [P] Setup dependency injection (Hilt) base Application class in `app/src/main/java/com/example/hmi/HmiApplication.kt`
- [x] T003 [P] Configure Jetpack DataStore and Coroutines dependencies in `app/build.gradle`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T004 Create `PlcCommunicator` interface from contracts in `app/src/main/java/com/example/hmi/protocol/PlcCommunicator.kt`
- [x] T005 [P] Create `PlcValue` and `ConnectionState` models in `app/src/main/java/com/example/hmi/protocol/PlcCommunicator.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Configure and Connect to PLC (Priority: P1) 🎯 MVP

**Goal**: As an operator, I want to enter TCP/IP connection details so that the app can communicate with the target PLC.
**Independent Test**: Can be fully tested by entering an IP address and port, tapping "Connect," and verifying that the connection status changes to "Connected".

### Implementation for User Story 1

- [x] T006 [P] [US1] Create `PlcConnectionProfile` data model in `app/src/main/java/com/example/hmi/data/PlcConnectionProfile.kt`
- [x] T007 [P] [US1] Implement Raw TCP `PlcCommunicator` in `app/src/main/java/com/example/hmi/protocol/RawTcpPlcCommunicator.kt`
- [x] T008 [US1] Implement `ConnectionViewModel` in `app/src/main/java/com/example/hmi/connection/ConnectionViewModel.kt`
- [x] T009 [US1] Create Connection screen UI (Compose) in `app/src/main/java/com/example/hmi/connection/ConnectionScreen.kt`

**Checkpoint**: User Story 1 functional and testable independently.

---

## Phase 4: User Story 2 - Operate Basic Controls (Priority: P1)

**Goal**: As an operator, I want to view gauges and manipulate buttons and sliders so that I can monitor and control the industrial process.
**Independent Test**: Load a pre-configured dashboard of controls, manipulate a slider/button, and verify the expected payload is sent over the network (and gauges update).

### Implementation for User Story 2

- [x] T010 [P] [US2] Create Button Widget Composable in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`
- [x] T011 [P] [US2] Create Slider Widget Composable in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`
- [x] T012 [P] [US2] Create Gauge Widget Composable in `app/src/main/java/com/example/hmi/widgets/GaugeWidget.kt`
- [x] T013 [US2] Implement `DashboardViewModel` to handle widget interactions in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T014 [US2] Create `DashboardScreen` (Run Mode) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`

**Checkpoint**: User Story 2 functional and testable independently.

---

## Phase 5: User Story 3 - Customize Dashboard Layout (Priority: P2)

**Goal**: As an engineer, I want to easily add, move, and configure controls on the screen so that I can tailor the interface to different PLC setups without writing code.
**Independent Test**: Entirely offline, enter edit mode, drop a new gauge, save layout, restart app, and verify the gauge is still there.

### Implementation for User Story 3

- [x] T015 [P] [US3] Create `WidgetConfiguration` data model in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt`
- [x] T016 [P] [US3] Create `DashboardLayout` data model in `app/src/main/java/com/example/hmi/data/DashboardLayout.kt`
- [x] T017 [US3] Implement `DashboardRepository` using DataStore in `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [x] T018 [US3] Update `DashboardViewModel` to support Edit Mode state in `app/src/main/java/com/example/hmi/dashboard/DashboardViewModel.kt`
- [x] T019 [US3] Implement drag-and-drop mechanics in `DashboardScreen` (Edit Mode) in `app/src/main/java/com/example/hmi/dashboard/DashboardScreen.kt`
- [x] T020 [US3] Create Widget Palette UI for adding controls in `app/src/main/java/com/example/hmi/dashboard/WidgetPalette.kt`

**Checkpoint**: All user stories functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T021 Setup navigation between Connection and Dashboard screens in `app/src/main/java/com/example/hmi/MainActivity.kt`
- [x] T022 Handle connection edge cases (drops/reconnects) in `app/src/main/java/com/example/hmi/connection/ConnectionViewModel.kt`
- [x] T023 Polish UI for 48dp minimum touch targets and accessibility in all widgets

---

## Phase 7: Connection Persistence & Error Handling

**Purpose**: Fulfill FR-006 and FR-010 to persist connection parameters and handle unexpected disconnects.

- [ ] T024 Add connection profile persistence methods to `app/src/main/java/com/example/hmi/data/DashboardRepository.kt`
- [ ] T025 Update `ConnectionViewModel` to load and save `PlcConnectionProfile` on init/connect in `app/src/main/java/com/example/hmi/connection/ConnectionViewModel.kt`
- [ ] T026 Update `MainActivity.kt` NavHost to observe `ConnectionState` globally and force navigation to "connection" on `ERROR` or unexpected `DISCONNECTED` states