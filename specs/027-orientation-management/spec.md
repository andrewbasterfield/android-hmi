# Feature Specification: Orientation Management

**Feature Branch**: `027-orientation-management`  
**Created**: 2026-03-27  
**Status**: Draft  
**Input**: User description: "How to deal with screen rotation in a sane way? Discuss"

## Summary

This feature provides a robust system for managing device orientation and multi-page layout swiping in an industrial HMI context. It allows users to lock the interface to specific orientations and ensures that widgets remain accessible across orientation changes by reflowing them into a paginated 2D grid. Instead of forcing widgets to fit on a single screen, the system allows widgets to span page boundaries and flow into subsequent virtual pages in any direction when the screen dimensions change.

## Clarifications

### Session 2026-03-27
- Q: Page Dimension Logic → A: Dynamic: 1 Page = 1 Viewable Screen; orientation change reflows grid into more/fewer virtual pages.
- Q: Dashboard Capacity → A: Infinite: The grid automatically extends to the right as far as the furthest widget requires.
- Q: Cross-Page Dragging → A: Edge Swiping: Dragging a widget to the screen edge triggers a page flip in Edit Mode.
- Q: Grid Collision Strategy → A: Visual Overlap: Allow overlap during orientation change; user re-arranges if desired.
- Q: Grid Expansion Directions → A: All Directions: Supports negative coordinates (Left of Page 1, Above Row 0) and extends the grid as far as the furthest widget requires in any direction.
- Q: Vertical Navigation → A: 2D Pager: The user can swipe both horizontally and vertically to navigate discrete 2D pages.

## Terminology

To simplify the implementation and design, we use two primary concepts:

- **Layout (VirtualGrid)**: The infinite 2D coordinate system where all widgets are placed. It persists across all orientation changes and supports negative coordinates.
- **Viewport (Page)**: The current viewable window onto the **Layout**. 
    - The **Viewport's** dimensions (Width x Height) are determined by the device's physical screen and current orientation.
    - **Page**: A single discrete "tile" of the Layout, exactly one Viewport wide and one Viewport high.
    - **Reflow**: The automatic process where an orientation change updates the Viewport's dimensions, which logically re-slices the Layout into a different number of Pages in both horizontal and vertical directions.

## User Scenarios & Testing

### User Story 1 - Orientation Locking (Priority: P1)

As a technician, I want to lock the HMI to Landscape mode so that the interface remains stable even if the tablet is bumped or tilted while mounted on a control panel.

**Acceptance Scenarios**:
1. **Given** the app is in "Auto" mode, **When** the user selects "Force Landscape", **Then** the UI immediately switches to landscape and stops responding to physical rotation.
2. **Given** the app is in "Force Portrait" mode, **When** the device is rebooted, **Then** the app launches directly into Portrait mode.

---

### User Story 2 - Multi-Page Swiping & Overflow (Priority: P1)

As a dashboard designer, I want my widgets to remain in their relative grid positions when I rotate the device, even if they now span across multiple "pages" of the screen.

**Acceptance Scenarios**:
1. **Given** a widget is 4 columns wide on an 8-column Landscape screen, **When** the device is rotated to a 4-column Portrait screen, **Then** the widget remains 4 columns wide but now occupies the entirety of a new virtual page (or spans two pages).
2. **Given** a widget spans a page boundary, **When** the user swipes between pages, **Then** the widget is rendered seamlessly across the transition.

---

### User Story 3 - Persistence of Settings (Priority: P2)

As a supervisor, I want the orientation and layout preferences to be saved across app sessions.

**Acceptance Scenarios**:
1. **Given** the user sets "Force Landscape", **When** the app is killed and restarted, **Then** the app starts in Landscape mode.

## Requirements

### Functional Requirements

- **FR-001**: System MUST provide three Orientation Modes: `AUTO`, `LANDSCAPE`, and `PORTRAIT`.
- **FR-002**: System MUST persist the selected Orientation Mode and layout configuration.
- **FR-003**: System MUST implement a 2D paging mechanism (supporting both horizontal and vertical swiping) where each page corresponds to the current viewable screen width/height.
- **FR-004**: System MUST allow widgets to have global grid coordinates that include negative values and extend beyond the boundaries of a single page in any direction.
- **FR-005**: System MUST render widgets that span page boundaries across both adjacent pages seamlessly.
- **FR-006**: System MUST ensure that transient UI states survive a rotation.
- **FR-007**: The VirtualGrid MUST automatically extend its boundaries in all directions (horizontal and vertical) to encompass the furthest widget edges.
- **FR-008**: System MUST support "Edge Swiping" in Edit Mode, allowing users to move widgets between pages by dragging them to any of the four screen boundaries (Left, Right, Top, Bottom).
- **FR-009**: System MUST allow visual overlap of widgets if an orientation change causes them to occupy the same grid coordinates; the user MUST be able to resolve overlaps manually in Edit Mode.

### Accessibility & UI Requirements

- **A11Y-001**: Minimum touch targets of 48x48dp.
- **UI-001**: Orientation toggle in the global settings menu.

### Key Entities

- **OrientationMode**: `AUTO`, `LANDSCAPE`, `PORTRAIT`.
- **Layout (VirtualGrid)**: A continuous two-dimensional coordinate system (Columns x Rows) that supports negative values and extends infinitely in any direction.
- **Viewport (Page)**: The current viewable window onto the Layout, logically sliced into discrete tiles based on screen dimensions.

## Success Criteria

### Measurable Outcomes

- SC-001: 100% of widgets remain accessible via swiping after any orientation change.
- SC-002: Orientation lock applied within 500ms.
- SC-003: Zero "lost widgets" after rotation.
- SC-004: Seamless rendering of boundary-spanning widgets (no flickering or clipping at the join).
