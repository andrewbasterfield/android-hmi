# HMI Control Panel

An industrial HMI (Human-Machine Interface) and SCADA application built for Android, designed to interface with PLCs over TCP/IP. This app allows engineers to create highly customizable control panels with intuitive drag-and-drop tools and real-time monitoring capabilities.

### 🛠️ Functional Ruggedization Verification

To verify the **Kinetic Cockpit** integration on physical hardware:

1. **Rugged Aesthetic**: Ensure all dashboard widgets have `0dp` corners and a `2px` thick border using the `Outline` token.
2. **Live Utility**: Verify that numerical readouts in Gauges and Sliders use **Tabular (Monospaced)** figures to prevent layout jitter during live updates.
3. **Tactile Feedback**:
    - Press any button and verify the **"Inverse Video"** state swap (Color/Black).
    - Confirm haptic feedback triggers on press (if enabled in settings).
4. **Emergency Signaling**:
    - Force a `CRITICAL` tag value (e.g., SIM_FUEL < 10%).
    - Verify the header status icon changes to an Error symbol.
    - Confirm the **Emergency HUD** pulses the screen periphery in Red (#93000A) at 2Hz.
5. **Accessibility**: Confirm that every widget has a high-contrast status icon, providing situational awareness independent of color.

## 🚀 Key Features

- **Customizable Dashboards**: Switch between "Run Mode" for operation and "Edit Mode" for configuration.
- **Drag-and-Drop Editor**: Reposition gauges, sliders, and buttons directly on the screen.
- **Abstracted PLC Protocols**: Support for multiple industrial protocols through a unified interface:
    - **Raw TCP**: Direct socket communication using `TAG:VALUE` lines.
    - **MQTT v3.1.1**: Industry-standard IoT protocol with support for JSON payloads, QoS 1 control, and Last Will and Testament (LWT) status signaling.
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
4.  **Clarity by Design**: Ambiguous-free UI, readable at a glance.
5.  **Low Cognitive Load**: Information prioritization and progressive disclosure.
6.  **No Gimmicks**: Every element and animation serves a functional purpose.
7.  **Modular Architecture**: Clear separation between the UI, business logic, and protocol implementations.

## 🖥 User Interface Guide

### 1. Connection Screen
The entry point of the application.
- **Protocol Selector**: Choose between Raw TCP and MQTT.
- **Connection Parameters**: Enter IP/Host and Port.
- **MQTT Settings**: Configure Client ID, optional Credentials, and Topic Prefix.
- **Connect**: Establishes the session based on the selected protocol.
- **Disconnect**: Safely closes the socket or MQTT session.

### 2. Dashboard: Run Mode (Default)
Once connected, the app defaults to "Run Mode", which is the live operational state.
- **Gauges**: Visually display incoming float values from the PLC in real-time.
- **Sliders**: Allow you to select a value within a range. Releasing or sliding sends a float payload to the PLC.
- **Buttons**: Send immediate boolean triggers to the PLC when pressed.

### 3. Dashboard: Edit Mode
Tap the "Edit Mode" button in the top app bar to customize the UI.
- **Drag-and-Drop**: Touch and hold any widget to freely drag it around the screen. Its new coordinates are saved instantly to the local DataStore state.
- **Add Widget**: A palette appears at the bottom of the screen. Tap "Add Widget" to select a new control type (Button, Slider, Gauge).
- **Customization**:
    - **Labels**: Override the tag address with a human-readable name.
    - **Background Color**: Choose from a high-contrast curated palette, or use the **Custom Color Picker** (Hex entry, visual spectrum, and recent colors). Text color automatically toggles between Black and White to ensure WCAG-compliant contrast.
    - **Font Scaling**: Adjust text size per widget (0.5x to 2.5x) using the "Font Size" slider.

### 4. Modern Industrial UI & Cockpit Style
The application is optimized for high-clarity industrial environments:
- **Adaptive Rounded corners**: Widgets and dialogs feature 8dp rounded corners (scaled to 4dp for 1x1 widgets) to distinguish interactive zones.
- **Pure Black Background**: Default for all dashboards to ensure high contrast and power saving.
- **Black Text Mandate**: All vibrant and light widget backgrounds use Black (#000000) text by default for a physical, tactile feel.
- **Hybrid Contrast**: System automatically switches to White text for dark backgrounds (L < 0.2) to maintain WCAG-compliant 4.5:1 contrast.
- **Subtle Borders**: 1dp borders align with rounded corner paths to ensure widget visibility on the black canvas.
- **Custom Color Picker**: Advanced tabbed interface for brand matching and safety colors.
- **Cockpit-Style Typography**: Uses optimized Roboto weights and spacing for maximum readability.
- **Automatic Migration**: Existing layouts are automatically migrated to the new dark theme on first run.

### 5. Demo Mode
For instant testing without any external dependencies:
- **Connect to Local Demo Server**: Tap this button on the Connection Screen to launch a built-in simulation at `127.0.0.1:9999`.
- **Simulated Tags**: Use `SIM_TEMP`, `SIM_PRESSURE`, or `SIM_STATUS` in your widget configurations to see live, fluctuating data immediately.
- **Dynamic Attributes (New)**: Update widget appearance remotely via the protocol using `TAG.ATTR:VALUE`:
    - `MOTOR_01.label:Main Pump` (Updates display name)
    - `MOTOR_01.color:#FF0000` (Updates background color to red)
- **JSON Import/Export (New)**: Backup or share layouts via raw JSON in Dashboard Settings.

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
    Run the Gradle wrapper:
    ```bash
    ./gradlew :app:assembleDebug
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

The project follows a modular structure to ensure build performance and clean boundaries:
- `:app`: Application entry point and navigation.
- `:core:ui`: The **Kinetic Cockpit** design system, theme tokens, and tactile industrial components (Buttons, Inputs, Cards).
- `:feature:diagnostics`: Mission-critical telemetry monitoring and peripheral emergency HUD signaling.
- `:core:protocol`: Multi-protocol backend abstractions (Raw TCP, MQTT) and dispatcher.

## 📄 Documentation

For deep technical details, refer to the specification folder:
- [Feature Specification](specs/001-hmi-control-panel/spec.md)
- [Implementation Plan](specs/001-hmi-control-panel/plan.md)
- [Research & Decisions](specs/001-hmi-control-panel/research.md)
- [Data Model](specs/001-hmi-control-panel/data-model.md)
