# Research: Orientation Management (027)

## Decision: 2D Pager Implementation
### Rationale
To support a 2D infinite grid with discrete "pages," we will utilize a nested approach using `VerticalPager` containing a `HorizontalPager`.

- The `VerticalPager` will handle Y-axis (row-based) paging.
- Each "row" in the `VerticalPager` will contain a `HorizontalPager` for X-axis (column-based) paging.
- By synchronizing the `PagerState` (if necessary, though discrete tiles might not need strict sync), we can achieve a 2D tile-based navigation.

### Alternatives considered
- **Custom Layout with Gestures**: More flexible for "continuous" scrolling but harder to implement "snap-to-page" discrete behavior required by the spec.
- **LazyLayout**: Good for massive grids, but the `Pager` API is more idiomatic for the requested "swipe between pages" UX.

## Decision: VirtualGrid Coordinate Mapping
### Rationale
We will maintain a global coordinate system in `DashboardLayout` where each widget has a `column` and `row` (Int). 

- **Page Calculation**: 
  - `pageCol = floor(globalCol / viewportCols)`
  - `pageRow = floor(globalRow / viewportRows)`
- **Offset within Page**:
  - `localCol = globalCol % viewportCols`
  - `localRow = globalRow % viewportRows`

Negative coordinates are handled naturally by the `floor` and `%` operations (Kotlin's `%` might need adjustment for negative numbers to ensure it stays within [0, viewportSize-1]).

### Alternatives considered
- **Relative Coordinates**: Storing widgets *per page*. Rejected because orientation changes change the "page" boundaries, requiring a complex migration of all widgets every time the device rotates. Global coordinates are more stable.

## Decision: Handling Negative Coordinates in Pager
### Rationale
`Pager` typically starts at index 0. To support negative coordinates, we will:
1. Calculate the bounding box of all widgets (`minCol`, `maxCol`, `minRow`, `maxRow`).
2. Map the "0,0" Page of the Pager to the page containing the global (0,0) coordinate.
3. The Pager's `pageCount` will be dynamically calculated to cover the span from `min` to `max`.

### Alternatives considered
- **Shifting the entire grid**: Whenever a widget moves to a new negative extreme, shift all other widgets. Rejected due to unnecessary DataStore writes and complexity.

## Decision: Orientation Management
### Rationale
Use `Activity.requestedOrientation` to force `SCREEN_ORIENTATION_LANDSCAPE` or `SCREEN_ORIENTATION_PORTRAIT` when the mode is not `AUTO`.

- Persistence: Store `OrientationMode` in `DashboardRepository`.
- Lifecycle: Apply the orientation in `MainActivity` based on the observed state from the repository.

## Decision: Screenshot Testing Tool
### Rationale
Since no library is currently integrated, we will stick to standard **Compose UI Tests** (`ComposeTestRule`) for functional verification of the 2D Pager and reflow logic. If visual regressions become a major concern, we recommend adding **Roborazzi** in a future task.

## Technical Details
- **minSdk**: 24 (verified from build.gradle files).
- **Pager API**: `androidx.compose.foundation.pager.HorizontalPager` and `VerticalPager` (available in Compose 1.4+).
