# Quickstart: UI Refinement (Grid Layout)

This guide explains how to use the new grid-based dashboard layout.

## 1. Grid Basics

- The dashboard is divided into fixed **80dp x 80dp** cells.
- On larger devices (like tablets), you will have more rows and columns available than on phones.
- All widgets must occupy at least 1 grid cell.

## 2. Moving Widgets

1. Switch to **Edit Mode** using the top bar.
2. Press and drag a widget to a new location.
3. When you release, the widget will **snap** to the nearest grid cell boundaries.

## 3. Resizing Widgets

1. In **Edit Mode**, select a widget.
2. Drag the **resize handle** (bottom-right circle) to change its dimensions.
3. The widget will expand or shrink in 80dp increments.
4. The layout will be automatically saved once you stop dragging.

## 4. Widget Containers & Parameters

- Every widget is now wrapped in a square-edged container with a 1dp border.
- The border color automatically adjusts (light or dark) depending on the background color to ensure visibility.
- **Editing Parameters**: Click the **Settings icon** (top-right) in Edit Mode to change the Tag Address, Color, or Min/Max values.
- **Deleting**: Use the **Delete icon** inside the configuration dialog to remove a widget.

## 5. Driving Widgets over Network

1. Connect the app to your `ncat` backend.
2. Send updates using the format: `TAG_NAME:VALUE`
   - Example: `TankLevel:75.5`
   - Example: `PumpRunning:true`
3. Widgets with the matching tag address will update immediately.
