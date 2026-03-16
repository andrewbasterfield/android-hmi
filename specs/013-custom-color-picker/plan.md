# Implementation Plan: Custom Color Picker

**Branch**: `013-custom-color-picker` | **Date**: 2026-03-16 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/013-custom-color-picker/spec.md`

## Summary
The goal is to implement a comprehensive custom color selection interface for the HMI Dashboard. This includes a tabbed UI for predefined palettes, a visual spectrum picker, and direct hexadecimal entry, with a focus on ensuring text readability through automatic contrast adjustment and persisting a history of recently used colors.

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences with JSON serialization for recent colors)
**Testing**: JUnit 4, Mockito-Kotlin, Compose UI Test
**Target Platform**: Android (API 24+)
**Project Type**: Mobile App (HMI Dashboard)
**Performance Goals**: Instant UI response for color selection and preview
**Constraints**: 
- All custom colors MUST be opaque.
- Text color (Black/White) MUST be selected based on background luminance.
- Recent colors list MUST be capped at 8 and shared globally.

## Constitution Check

- [x] **Compose-First**: The new color picker, tabs, and spectrum components will all be built with Jetpack Compose.
- [x] **Unidirectional Data Flow**: State will be managed in the `DashboardViewModel` (for saving recent colors) and local UI state (for the picker dialog).
- [x] **Test-First**: Unit tests for contrast logic and recent color queueing; UI tests for hex validation.
- [x] **Accessibility**: Minimum touch targets (48x48dp) for all interactive picker elements.
- [x] **Modular Architecture**: Applied within the `app` module's established package structure.

## Project Structure

### Documentation (this feature)

```text
specs/013-custom-color-picker/
├── plan.md              # This file
├── research.md          # Decisions on UI, Contrast, and Persistence
├── data-model.md        # Updated DataStore keys and color entities
├── quickstart.md        # Verification steps
└── tasks.md             # Task breakdown (to be generated)
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── data/
│   ├── DashboardRepository.kt    # Add recent_colors persistence
├── ui/components/
│   ├── ColorPicker.kt            # NEW: Tabbed Custom Color Picker
│   ├── SpectrumPicker.kt         # NEW: Canvas-based HSV picker
│   ├── HexEntryField.kt          # NEW: Validated hex input with contrast preview
├── dashboard/
│   ├── DashboardViewModel.kt     # Logic for recent colors and global selection
│   ├── DashboardSettingsDialog.kt # Integrate new picker
│   └── WidgetPalette.kt          # Integrate new picker for widget config
```

## Planning Phase 2: Implementation Steps

1.  **Phase 1: Foundation (Data & Utils)**
    *   Update `DashboardRepository` to handle `recent_colors` persistence.
    *   Implement `ColorUtils.calculateContrast(color)` and `ColorUtils.isDark(color)`.
    *   Add unit tests for contrast and recent color logic.

2.  **Phase 2: UI Components (Custom Picker)**
    *   Create `SpectrumPicker` using Compose Canvas.
    *   Create `HexEntryField` with Material 3 `OutlinedTextField` and validation.
    *   Create `RecentColorsRow` for quick selection.

3.  **Phase 3: Integration (Dialogs & Screens)**
    *   Combine components into a tabbed `HmiColorPicker` dialog.
    *   Update `DashboardSettingsDialog` to use the new picker.
    *   Update `WidgetConfigDialog` (in `WidgetPalette.kt`) to use the new picker.

4.  **Phase 4: Polish & Refinement**
    *   Ensure all touch targets are 48x48dp.
    *   Verify content descriptions for screen readers.
    *   Final code cleanup and documentation updates.
