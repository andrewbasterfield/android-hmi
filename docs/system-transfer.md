# System Transfer Guide

The **System Transfer Center** lets you back up, restore, and share your HMI configurations. You'll find it by tapping the **Sync/Cloud** icon in the dashboard's Edit Mode toolbar.

## Dashboard Layouts

Manage the visual arrangement and widget settings for your dashboards.

- **JSON Import/Export:**
  - **Copy** -- Copy the raw JSON representation of the active layout to the clipboard.
  - **Import** -- Paste JSON into the text field and click **Import** to apply it immediately.
- **File Management:**
  - **Save File** -- Save your active layout as a `.json` file to your device storage.
  - **Open File** -- Pick an existing `.json` file from your device to load it.
- **Sharing:**
  - **Share Layout** -- Send the single layout via the Android Share Sheet.

## System Profile Bundles

A **Profile Bundle** is a standalone machine package containing a preset, its layout, and its connection technical details.

- **How to Share:**
  1. Open the **Management Hub** (Side Drawer).
  2. Locate your preset in the **Systems** list.
  3. Tap the **Share (icon)** next to the profile name.
- **Importing:**
  - When you open a profile bundle on a new device, the app automatically adds the layout and the connection profile to the recipient's library and recreates the binding.

## Full System Backup (Version 2)

This is the easiest way to migrate your entire environment to a new device.

- **Generate Full Backup** creates a single JSON package containing **everything**:
  - All saved **System Profiles** (Presets).
  - All **Dashboard Layouts** in your library.
  - All saved **Connection Profiles** (IPs, ports, MQTT credentials).
  - Global app preferences (haptic feedback, keep screen on, etc.).

### Merge Strategy
When importing a backup, the app uses an **Upsert** logic:
- If an item (Connection, Layout, or Profile) with the same identifier already exists, it is **updated** with the incoming data.
- If it doesn't exist, it is **added** to your local library.

---

## Important Security Note

> [!WARNING]
> **Full System Backups and Profile Exports may contain sensitive information.**
>
> While passwords are obfuscated in some displays, they are exported in **plain text** within the JSON files so they can be restored correctly on another device. Treat these backup files with care and never share them publicly.
