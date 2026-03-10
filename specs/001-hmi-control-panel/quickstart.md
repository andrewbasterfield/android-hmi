# Quickstart: HMI Control Panel Development

## Prerequisites

- Android Studio (Latest stable)
- Java JDK 17+
- Android SDK API 34+

## Running the App

1. Clone the repository.
2. Open the `android-ui` project in Android Studio.
3. Sync Gradle dependencies.
4. Run the `app` module on an emulator or physical device.

## Testing TCP/IP Locally

To test the PLC connection without physical hardware:
1. Run a local TCP echo server on your development machine (e.g., using ncat):
   `ncat -l 9999 -k --crlf`
2. Connect your Android device/emulator to the same network as your development machine.
3. In the app, enter your machine's local IP address and port `9999`.
