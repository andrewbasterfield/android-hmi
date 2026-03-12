# Tasks: HMI Widget Customization (Refinement)

**Input**: Design documents from `/specs/002-hmi-widget-customization/`
**Prerequisites**: 001-hmi-control-panel (complete)

## Phase 1: Data Model Updates

- [ ] T027 Update `WidgetConfiguration` in `app/src/main/java/com/example/hmi/data/WidgetConfiguration.kt` to include width, height, and color fields.
- [ ] T028 Update `DashboardRepository` if necessary to handle the new fields during persistence.

---

## Phase 2: Widget Refinement

- [ ] T029 Update `ButtonWidget` to accept width, height, and color parameters in `app/src/main/java/com/example/hmi/widgets/ButtonWidget.kt`.
- [ ] T030 Update `SliderWidget` to accept width and height parameters in `app/src/main/java/com/example/hmi/widgets/SliderWidget.kt`.
- [ ] T031 Update `GaugeWidget` if necessary to maintain layout consistency (optional resizing).
- [ ] T038 Implement a new `BarChartWidget` in `app/src/main/java/com/example/hmi/widgets/BarChartWidget.kt` that supports color customization.
- [ ] T039 Add the `BarChartWidget` option to the `WidgetPalette.kt` for use in Edit Mode.

---

## Phase 3: Edit Mode UI Enhancements

- [ ] T032 Implement a "Widget Configuration" dialog/overlay in Edit Mode to allow adjusting dimensions and color.
- [ ] T033 Add resize handles or interactive resizing to the widgets in Edit Mode in `DashboardScreen.kt`.
- [ ] T034 Implement color selection UI for buttons in the configuration dialog.

---

## Phase 4: Validation & Integration

- [ ] T035 Verify that resized widgets maintain functional correctness in Run Mode.
- [ ] T036 Verify that color and dimension changes are correctly persisted in DataStore.
- [ ] T037 Ensure minimum touch target (48dp) is maintained even when widgets are resized small.
