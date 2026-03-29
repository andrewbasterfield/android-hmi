# System Transfer Guide

The **System Transfer Center** allows you to backup, restore, and share your HMI configurations. Access it by tapping the **Sync/Cloud** icon in the dashboard's Edit Mode toolbar.

## Dashboard Layouts

Manage the visual arrangement and widget configurations for the current dashboard.

- **JSON Import/Export:**
  - **Copy:** Copy the raw JSON representation of your layout to the clipboard.
  - **Import:** Paste JSON into the text field and click **Import** to immediately apply the layout.
- **File Management:**
  - **Save File:** Save your layout as a `.json` file to your device storage (Android SAF).
  - **Open File:** Pick an existing `.json` file from your device to load a saved layout.
- **Sharing:**
  - **Share Layout:** Uses the Android Share Sheet to send the layout via email, messaging, or other apps.

## Connection Profiles

Manage your PLC and MQTT broker connection settings.

- **Export All:** Packages all saved connection profiles into a single file for backup or migrating to a new device.
- **Import:** Load a previously exported profile package.
- **Share Profiles:** Send all your connection configurations through the Android Share Sheet.

## Full System Backup

The **Full System Backup** is the recommended method for migrating to a new device.

- **Generate Full Backup:** Creates a single JSON package containing **everything**:
  - The current dashboard layout and all its widgets.
  - All saved connection profiles (IPs, Ports, MQTT credentials).
  - Global app preferences (Haptic feedback, keep screen on, etc.).

---

## Important Security Note

> [!WARNING]
> **Full System Backups and Profile Exports may contain sensitive information.**
> 
> While passwords are obfuscated in some displays, they are exported in plain text within the JSON files so they can be restored correctly on another device. **Treat these backup files with care and never share them publicly.**
