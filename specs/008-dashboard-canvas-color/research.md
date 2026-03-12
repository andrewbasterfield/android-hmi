# Research: Dashboard Canvas Color

This document outlines the research and technical decisions for implementing a customizable dashboard background.

## 1. Canvas Color Representation

**Decision**: Store the `canvasColor` as a nullable `Long` (ARGB value) in the `DashboardLayout` data model.
**Rationale**:
- **Consistency**: Matches the implementation used for individual widgets.
- **Nullability**: A `null` value indicates the system should fall back to the standard `MaterialTheme.colorScheme.background`.
- **Serialization**: Easily handled by GSON and Jetpack DataStore.

## 2. Integration with Widget Containers

**Research**: How will custom canvas colors affect widget visibility?
**Decision**: Leverage the existing `WidgetContainer` logic.
**Rationale**: The `WidgetContainer` already uses `ColorUtils.getContrastColor` to determine its border color. This ensures that even if the canvas and widget have the same background, the 1dp contrasting border will maintain separation.

## 3. UI for Canvas Settings

**Decision**: Implement a `DashboardSettingsDialog` triggered by a new "Settings" icon in the Dashboard's top bar (only visible in Edit Mode).
**Rationale**:
- **Accessibility**: Keeps the `WidgetPalette` focused on adding new elements.
- **Clarity**: High-level layout settings (like canvas color or layout name) belong in a dedicated dialog.
- **Pattern**: Reuses the `ColorPicker` composable developed for widgets.

## 4. Performance

**Decision**: The canvas color will be applied directly to the root `Box` or `Scaffold` container in `DashboardScreen`.
**Rationale**: Changing a solid background color is a low-cost operation in Compose and will not trigger unnecessary recompositions of the widget tree if implemented correctly using `Modifier.background`.
