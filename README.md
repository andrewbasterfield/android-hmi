# HMI Control Panel

An industrial HMI (Human-Machine Interface) and SCADA application built for Android, designed to interface with PLCs over TCP/IP. This app allows engineers to create highly customizable control panels with intuitive drag-and-drop tools and real-time monitoring capabilities.

## 🚀 Key Features

- **Customizable Dashboards**: Switch between "Run Mode" for operation and "Edit Mode" for configuration.
- **Drag-and-Drop Editor**: Reposition gauges, sliders, and buttons directly on the screen.
- **Abstracted PLC Protocols**: Support for multiple industrial protocols (Raw TCP, Modbus, etc.) through a unified interface.
- **Real-time Monitoring**: Low-latency UI updates using Kotlin StateFlow and Jetpack Compose.
- **Accessibility-First**: Built with 48dp minimum touch targets and screen-reader support as a baseline.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarative UI)
- **Architecture**: MVVM with [Unidirectional Data Flow (UDF)](https://developer.android.com/topic/architecture/ui-layer#udf)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Concurrency**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Local Persistence**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## 📜 Core Principles

As defined in the project [Constitution](.specify/memory/constitution.md):

1.  **Compose-First**: All UI is built using modern declarative toolkits.
2.  **Unidirectional Data Flow**: State flows down, events flow up—ensuring predictable UI behavior.
3.  **Accessibility by Default**: Minimum touch targets (48x48dp) and support for dynamic text scaling.
4.  **Modular Architecture**: Clear separation between the UI, business logic, and protocol implementations.

## 🏁 Getting Started

### Prerequisites

- **Android Studio**: Jellyfish or newer.
- **JDK**: Version 17+.
- **Android SDK**: API 24 (Android 7.0) minimum, API 34+ target.

### Running the Project

#### Option A: Android Studio (Recommended)
1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Sync Gradle dependencies.
4.  Run the `app` module on a physical device or emulator.

#### Option B: Command Line Only
If you prefer building without Android Studio, ensure you have **JDK 17** and a modern **Android SDK** (API 34) installed.

1.  **Configure SDK Path**:
    Create a `local.properties` file in the project root and point it to your Android SDK:
    ```properties
    sdk.dir=/path/to/your/android-sdk-linux
    ```

2.  **Accept Licenses**:
    If using fresh tools, accept the Android licenses:
    ```bash
    # Use the sdkmanager from cmdline-tools/latest/bin/
    yes | sdkmanager --licenses
    ```

3.  **Build the APK**:
    Run the Gradle wrapper. If your system default Java is not 17, prefix the command with `JAVA_HOME`:
    ```bash
    JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/ ./gradlew :app:assembleDebug
    ```

4.  **Install to Device**:
    ```bash
    adb install app/build/outputs/apk/debug/app-debug.apk
    ```

## 🛠 Troubleshooting

### `jlink executable ... does not exist`
If you see this error during a command-line build, it means you have the **JRE** installed instead of the full **JDK**. The Android build process requires tools like `jlink` and `javac`.
- **Fix (Debian/Ubuntu)**: `sudo apt install openjdk-17-jdk`

### Invalid Escape in `settings.gradle.kts`
If you encounter script compilation errors regarding `Illegal escape: '\.'`, ensure that your package filtering regexes use double-backslashes (e.g., `"com\\.android.*"`) to properly escape the literal dot in Kotlin DSL.

### Testing Locally

You can test the TCP/IP connection without a physical PLC using `ncat`:

```bash
# Start a local TCP echo server
ncat -l 9999 -k --crlf
```

In the app, connect to your machine's local IP address on port `9999`.

## 📁 Project Structure

- `app/src/main/java/com/example/hmi/`
    - `connection/`: PLC connection profiles and connection screen.
    - `dashboard/`: Dashboard rendering, Edit Mode, and widget management.
    - `widgets/`: Reusable Compose components (Buttons, Sliders, Gauges).
    - `protocol/`: The `PlcCommunicator` abstraction and implementations.
    - `data/`: Repositories and local persistence (DataStore).
- `specs/001-hmi-control-panel/`: Detailed feature specifications, implementation plans, and data models.

## 📄 Documentation

For deep technical details, refer to the specification folder:
- [Feature Specification](specs/001-hmi-control-panel/spec.md)
- [Implementation Plan](specs/001-hmi-control-panel/plan.md)
- [Research & Decisions](specs/001-hmi-control-panel/research.md)
- [Data Model](specs/001-hmi-control-panel/data-model.md)
