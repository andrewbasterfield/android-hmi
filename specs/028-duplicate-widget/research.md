# Research: Widget Duplication

## Decisions & Rationale

### 1. Duplication Logic Placement
- **Decision**: Implement `duplicateWidget(widgetId: String)` in `DashboardViewModel.kt`.
- **Rationale**: The ViewModel already manages widget lifecycle (add, delete, update). Centralizing duplication logic here ensures consistent state management and persistence via the repository.

### 2. UI Entry Point
- **Decision**: Add a "Duplicate" button to the `WidgetConfigDialog` footer in `WidgetPalette.kt`.
- **Rationale**: Users already open this dialog to manage widget properties. Placing "Duplicate" adjacent to "Delete" and "Save" provides a clear, high-contrast action within the established "Edit Mode" workflow.

### 3. Grid Offset Behavior
- **Decision**: Fixed (+1, +1) offset for the duplicated widget.
- **Rationale**: Ensures the original widget remains visible underneath. If this causes an overflow, the existing `GridSystem` and paging logic in `DashboardScreen.kt` already handle the visual representation.

### 4. Z-Order Management
- **Decision**: Increment the Z-Order of the duplicate to be `max(zOrder) + 1`.
- **Rationale**: Ensures the new widget is visually on top of all existing widgets, facilitating immediate interaction and movement.

### 5. UUID Generation
- **Decision**: Use `java.util.UUID.randomUUID().toString()`.
- **Rationale**: Consistent with the existing `WidgetConfiguration` default value and ensures uniqueness within the `DashboardLayout`.

## Alternatives Considered

- **Long-Press Context Menu**: Rejected due to lower discoverability and increased complexity compared to the existing configuration dialog.
- **Floating Duplicate Icon**: Rejected as it adds visual noise to the dashboard, violating the "No Gimmicks" and "Low Cognitive Load" principles.
- **Automatic Empty Space Search**: Rejected in favor of the requested (+1, +1) offset to maintain a predictable visual relationship between the original and the copy.
