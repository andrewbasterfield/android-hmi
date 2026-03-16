# Implementation Plan: Dark Mode UI Refinement

**Branch**: `012-dark-mode-ui-refinement` | **Date**: 2026-03-16 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/012-dark-mode-ui-refinement/spec.md`

## Summary
The goal is to implement a high-contrast "Dark Mode" theme for the HMI dashboard, featuring a pure black background, black widget text, a restricted high-contrast color palette, and adjustable font sizes. The visual style will be optimized for clarity, inspired by aircraft cockpit displays.

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Jetpack Compose, Hilt, Jetpack DataStore, GSON
**Storage**: Jetpack DataStore (Preferences with JSON serialization)
**Testing**: JUnit 4, Mockito-Kotlin, Espresso, Compose UI Test
**Target Platform**: Android (API 24+)
**Project Type**: Mobile App (HMI Dashboard)
**Performance Goals**: 60 FPS smooth rendering on the dashboard canvas
**Constraints**: All text MUST be black (#000000); backgrounds MUST maintain 4.5:1 contrast
**Scale/Scope**: Updating core theme and widget configuration across the dashboard

## Constitution Check

- [x] **Compose-First**: All UI changes (Theme, Dialogs, Widgets) will use Jetpack Compose.
- [x] **Unidirectional Data Flow**: State remains in `DashboardViewModel`; events flow up.
- [x] **Test-First**: Unit tests for migration logic and palette contrast; UI tests for theme verification.
- [x] **Accessibility**: Enforcing 4.5:1 contrast and supporting 48dp touch targets.
- [x] **Modular Architecture**: Applied within the `app` module following established package structures.

## Project Structure

### Documentation (this feature)

```text
specs/012-dark-mode-ui-refinement/
├── plan.md              # This file
├── research.md          # Decisions on Theme, Typography, and Palette
├── data-model.md        # Updated WidgetConfiguration
├── quickstart.md        # Verification steps
└── tasks.md             # Task breakdown (to be generated)
```

### Source Code

```text
app/src/main/java/com/example/hmi/
├── data/
│   ├── DashboardLayout.kt
│   └── WidgetConfiguration.kt    # Update for fontSizeMultiplier
├── ui/theme/                     # NEW: Custom HmiTheme and HmiPalette
├── dashboard/
│   ├── DashboardScreen.kt        # Update for black background and black text
│   ├── DashboardViewModel.kt     # Update for migration logic and font scaling
│   └── DashboardSettingsDialog.kt
├── widgets/
│   ├── ColorUtils.kt             # Update palette restrictions
│   ├── ButtonWidget.kt           # Update for black text and font scaling
│   ├── SliderWidget.kt           # Update for black text and font scaling
│   └── GaugeWidget.kt            # Update for black text and font scaling
```

**Structure Decision**: Utilizing the existing single-module Android project structure, adding a dedicated `ui/theme` package for the new design system components.
