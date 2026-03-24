# Quickstart: Config File Transfer

This guide explains how to use the configuration transfer features in the Android HMI app.

## 1. Exporting Configuration

### Dashboard Layout
1.  Enter **Edit Mode** on the dashboard.
2.  Open **Layout Settings**.
3.  Tap **JSON Transfer (Import/Export)**.
4.  Choose **Save to File** or **Share to Cloud**.

### Connection Profiles
1.  Navigate to the **Connection Screen**.
2.  Open the **Saved Profiles** menu.
3.  Tap **Export All Profiles**.
4.  Choose your destination.

### Full Backup
1.  Go to **Dashboard Settings** (in Edit Mode).
2.  Tap **Generate Full Backup**.
3.  This saves both your layout and all profiles into a single `.json` file.

## 2. Importing Configuration

### From the App
1.  Use the **Import from File** button in the respective settings dialog.
2.  Select the JSON file.
3.  The app will strictly validate the file. If an error is found (e.g., missing tag, invalid port), the specific field will be displayed.
4.  Upon success, you will be automatically returned to the Dashboard.

### From External Apps (Google Drive, Slack, etc.)
1.  Open the JSON file in the external app.
2.  Select **Open With** or **Share to** and pick **android-ui**.
3.  The app will launch and show an import confirmation prompt.
4.  If it's a "Full Backup", you can choose whether to import only the Layout, only the Profiles, or both.

## 3. Backwards Compatibility

Files created with older versions of the app will continue to work. The app uses the `version` field in the backup to perform necessary migrations or attribute sanitization upon import.
