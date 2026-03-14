# Quickstart: Built-in Demo Server

## How to use the Demo Mode

1. **Launch the App**: Start the application on an Android device or emulator.
2. **Select Demo Mode**: On the initial "PLC Connection Profile" screen, look for the "Connect to Local Demo Server" button.
3. **Connect**: Tap the button. The app will immediately connect to its internal simulation server at `127.0.0.1:9999`.
4. **Interact**: 
   - Add a **Gauge** widget and point it to `SIM_TEMP` to see live fluctuating data.
   - Add a **Slider** and point it to `USER_LEVEL`. Observe its value persist when you navigate away and back.
   - Add a **Button** and point it to `USER_STATUS`. Pressing it should toggle the simulated boolean state.

## Testing Locally (Advanced)

If you want to verify the server's protocol manually from your development machine:
1. Ensure the app is running and your machine can reach the device/emulator.
2. Port forward the server port from the device to your localhost:
   `adb forward tcp:9999 tcp:9999`
3. Connect using `telnet` or `nc`:
   `nc 127.0.0.1 9999`
4. Send a command: `MY_TAG:42.0`
5. Observe the app's UI update (if a widget is bound to `MY_TAG`).
