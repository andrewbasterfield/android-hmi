# Quickstart: Orientation Management (027)

This guide helps you understand how to use and test the new orientation and 2D paging system.

## Using the 2D Paging System

The dashboard now operates on an infinite **VirtualGrid**. To navigate:

- **Horizontal Swipe**: Swipe left/right to move between horizontal pages.
- **Vertical Swipe**: Swipe up/down to move between vertical rows of pages.
- **Edge Swiping (Edit Mode)**: In Edit Mode, drag a widget to any edge (Top, Bottom, Left, Right) to automatically flip to the next page in that direction.

## Orientation Locking

To lock the UI to a specific orientation:

1. Open the **Dashboard Settings** menu.
2. Locate the **Orientation Mode** toggle.
3. Select between **Auto**, **Force Landscape**, or **Force Portrait**.
4. The UI will immediately switch and stay locked to the chosen mode.

## For Developers

### Adding Widgets to Specific Coordinates
Widgets are placed using global `column` and `row` coordinates. You can place a widget at `(-5, 10)` and it will be reachable by swiping left 5 units and down 10 units relative to the (0,0) origin.

### Grid Reflow
When the device rotates, the **Viewport** dimensions change. The system automatically recalculates the **Page** boundaries. For example:
- In Landscape (8 columns), Column 9 is on Page 2.
- In Portrait (4 columns), Column 9 is on Page 3.
The widget remains at Column 9; only the page it belongs to changes.

## Testing

### Unit Tests
- Run `./gradlew test` to verify the logic in `GridReflowLogic.kt`.
- These tests ensure that global coordinates are correctly mapped to local page offsets across different viewport sizes.

### UI Tests
- Run `./gradlew connectedDebugAndroidTest` to execute the Compose UI tests.
- **Key Test**: `OrientationLockTest` verifies that the UI stays locked after a physical rotation.
- **Key Test**: `CrossPageBoundaryTest` verifies that widgets spanning two pages are rendered correctly in both.
