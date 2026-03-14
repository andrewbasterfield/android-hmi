# Quickstart: Custom Labels and Dynamic Attributes

## Testing Manual Labels
1. Launch the app and connect to the **Local Demo Server**.
2. Tap **Edit Mode** in the top bar.
3. Long-press any widget (e.g., a Gauge) to open settings.
4. Enter a **Custom Label** (e.g., "Boiler Temp") and tap Confirm.
5. Verify the widget now displays "Boiler Temp" instead of its tag address.

## Testing Protocol Attributes (via ncat)
To simulate protocol-driven updates from a terminal:

1. Start the app and connect to your development machine's IP.
2. In your terminal, use `ncat` to act as the server:
   ```bash
   ncat -l 9999 -k --crlf
   ```
3. Once the app connects, send a label update:
   ```text
   SIM_TEMP.label:Super Hot Boiler
   ```
4. Send a color update:
   ```text
   SIM_TEMP.color:#FF0000
   ```
5. Observe the widget on the Android device update its title and color in real-time.

## Conflict Resolution Check
1. Change a label manually in the app to "Manual Name".
2. Send a protocol update: `TAG.label:Remote Name`.
3. Verify the UI shows "Remote Name".
4. Restart the app.
5. Verify the UI reverts to "Manual Name" (persistent override) instead of the raw tag address.
