# System Transfer Guide

The **System Transfer Center** lets you back up, restore, and share your HMI configurations. You'll find it by tapping the **Sync/Cloud** icon in the dashboard's Edit Mode toolbar.

## Dashboard Layouts

Manage the visual arrangement and widget settings for the current dashboard.

- **JSON Import/Export:**
  - **Copy** -- Copy the raw JSON representation of your layout to the clipboard.
  - **Import** -- Paste JSON into the text field and click **Import** to apply it immediately.
- **File Management:**
  - **Save File** -- Save your layout as a `.json` file to your device storage (via Android SAF).
  - **Open File** -- Pick an existing `.json` file from your device to load a saved layout.
- **Sharing:**
  - **Share Layout** -- Send the layout via the Android Share Sheet (email, messaging, or any other app).

## Connection Profiles

Manage your PLC and MQTT broker connection settings.

- **Export All** -- Packages all your saved connection profiles into a single file for backup or migration.
- **Import** -- Load a previously exported profile package.
- **Share Profiles** -- Send all your connection configurations through the Android Share Sheet.

## Full System Backup

This is the easiest way to migrate everything to a new device.

- **Generate Full Backup** creates a single JSON package containing **everything**:
  - Your current dashboard layout and all its widgets.
  - All saved connection profiles (IPs, ports, MQTT credentials).
  - Global app preferences (haptic feedback, keep screen on, etc.).

---

## Important Security Note

> [!WARNING]
> **Full System Backups and Profile Exports may contain sensitive information.**
>
> While passwords are obfuscated in some displays, they are exported in **plain text** within the JSON files so they can be restored correctly on another device. Treat these backup files with care and never share them publicly.
