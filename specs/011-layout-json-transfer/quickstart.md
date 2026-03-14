# Quickstart: JSON Import/Export

## Testing Export (Manual Backup)
1. Launch the app.
2. Tap **Edit Mode** -> **Dashboard Settings**.
3. Scroll to the **JSON Transfer** section.
4. Verify the JSON text area is populated with the current layout data.
5. Tap **Copy to Clipboard**.
6. Open a note-taking app on your device and verify you can paste the JSON string.

## Testing Import (Restore/Share)
1. Copy a valid layout JSON string (you can use one exported previously).
2. Launch the app and go to **Dashboard Settings**.
3. Clear the JSON text area and paste your new JSON string.
4. Tap **Apply Import**.
5. Verify the dashboard immediately updates to reflect the new layout (widgets, colors, name).

## Testing Error Handling
1. Paste a random, non-JSON string into the Import field.
2. Tap **Apply Import**.
3. Verify a "Malformed JSON" or similar error message appears.
4. Verify the existing dashboard remains unchanged.
