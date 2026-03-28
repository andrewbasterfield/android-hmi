# Feature Specification: Widget Duplication

**Feature Branch**: `028-duplicate-widget`  
**Created**: 2026-03-28  
**Status**: Draft  
**Input**: User description: "it would be great to be able to duplicate an existing widget"

## Clarifications

### Session 2026-03-28

- Q: Should the duplicated widget be assigned the highest Z-Order in the dashboard? → A: Highest Z-Order: The duplicate is placed on top of all existing widgets.
- Q: Should the duplicate's label be identical to the source? → A: Identical: Duplicate the label exactly as it is on the source widget.
- Q: Should the system also automatically open the configuration dialog or shift selection for the new duplicate? → A: None: The new widget is created at the offset position but no dialog is opened and no selection state is applied.
- Q: Should the system show a temporary visual confirmation after a successful duplication? → A: None: The visible new widget at (+1, +1) is sufficient feedback.
- Q: Where should the "Duplicate" button be located? → A: Config Dialog: A button inside the widget's configuration dialog.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Single Widget Duplication (Priority: P1)

As a dashboard designer, I want to quickly create a copy of an existing widget with all its settings so I can save time when building repetitive layouts.

**Why this priority**: Core value of the feature; eliminates manual reconfiguration of similar widgets.

**Independent Test**: Can be fully tested by selecting a single widget, triggering the duplicate action, and verifying a second widget appears with identical configuration (labels, data sources, styling) but a unique ID.

**Acceptance Scenarios**:

1. **Given** a widget is configured with custom colors and a specific data source, **When** "Duplicate" is selected, **Then** a new widget is created with identical colors and data source.
2. **Given** a widget is duplicated, **When** the new widget is created, **Then** it is positioned with a +1 down and +1 right grid unit offset from the original to reveal the source widget underneath.
3. **Given** a widget is duplicated, **When** the new widget is created, **Then** it is immediately available for interaction (moving or editing) in its new position.

### Edge Cases

- **Canvas Overflow**: If a widget is duplicated near the canvas edge, the new widget is still placed with a +1, +1 offset. The system's existing canvas overflow mechanics will handle any widgets that fall outside the standard viewport.
- **Data Persistence**: Ensuring the duplicated widget is immediately saved to the underlying configuration storage.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a "Duplicate" action for a single selected widget within its configuration dialog (e.g., adjacent to the "Delete" action).
- **FR-002**: The duplicated widget MUST inherit all configuration properties from the source widget (including an identical label), such as type, size, data sources, and visual styles.
- **FR-003**: System MUST automatically assign a new, unique identifier and the highest Z-Order to the duplicated widget.
- **FR-004**: System MUST position the duplicated widget with a (1,1) grid unit offset from the source widget.
- **FR-005**: System MUST support duplicating a widget while the dashboard is in "Edit Mode".
- **FR-006**: The duplicated widget MUST be immediately persisted to the layout configuration.

### Accessibility & UI Requirements *(mandatory)*

- **A11Y-002**: Screen readers MUST announce "Widget duplicated" or similar confirmation when the action is successful.
- **A11Y-003**: All interactive elements MUST have minimum touch targets of 48x48dp.
- **UI-001**: UI MUST follow the "Clarity by Design" principle, ensuring high contrast and readability.
- **UI-003**: UI MUST prioritize essential data and use progressive disclosure to maintain low cognitive load.

### Key Entities *(include if feature involves data)*

- **WidgetConfiguration**: Represents a UI component's persistent state. Attributes include a unique `id`, `type`, position (`row`, `column`), and dimensions (`rowSpan`, `colSpan`).
- **DashboardLayout**: The collection of `WidgetConfiguration` objects that define the current dashboard arrangement.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can duplicate a widget in 2 clicks or fewer.
- **SC-002**: 100% of `WidgetConfiguration` attributes (excluding `id`) are successfully copied to the duplicate.
- **SC-003**: The duplicated widget is assigned a new, globally unique identifier (UUID) and the highest Z-Order in the current layout.
- **SC-004**: The duplicated widget is placed with a `row + 1` and `column + 1` offset from the source widget's origin.
- **SC-005**: The duplication action (from click to appearance) is completed in less than 200ms.
