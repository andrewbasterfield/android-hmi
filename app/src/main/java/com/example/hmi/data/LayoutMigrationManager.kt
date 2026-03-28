package com.example.hmi.data

import com.example.hmi.core.ui.theme.Void
import com.example.hmi.widgets.ColorUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ARCH-3.2: Dedicated manager for handling layout migrations and data sanitization.
 * Extracts logic from DashboardViewModel to improve separation of concerns.
 */
@Singleton
class LayoutMigrationManager @Inject constructor() {

    /**
     * Sanitizes a layout by ensuring all fields are non-null and handling minor
     * version-to-version adjustments that don't require a full cockpit migration.
     */
    fun ensureNonNullFields(layout: DashboardLayout?): DashboardLayout {
        if (layout == null) return DashboardLayout()
        return layout.copy(
            widgets = layout.widgets.map { widget ->
                val migratedLabelMultiplier = if (widget.fontSizeMultiplier != null && widget.fontSizeMultiplier > 0.05f && widget.labelFontSizeMultiplier == 1.0f) {
                    widget.fontSizeMultiplier
                } else {
                    widget.labelFontSizeMultiplier
                }

                val migratedMetricMultiplier = if (widget.fontSizeMultiplier != null && widget.fontSizeMultiplier > 0.05f && widget.metricFontSizeMultiplier == 1.0f) {
                    widget.fontSizeMultiplier
                } else {
                    widget.metricFontSizeMultiplier
                }

                widget.copy(
                    labelFontSizeMultiplier = migratedLabelMultiplier,
                    metricFontSizeMultiplier = migratedMetricMultiplier,
                    gaugeStyle = widget.gaugeStyle ?: GaugeStyle.POINTER,
                    alarmState = if (widget.alarmState == AlarmState.Acknowledged) {
                        AlarmState.Unacknowledged
                    } else {
                        widget.alarmState
                    }
                )
            }
        )
    }

    /**
     * FR-011: Automatically migrate existing layouts to the "Void" background
     * and sanitize colors to high-contrast Kinetic tokens.
     */
    fun migrateToKineticCockpit(layout: DashboardLayout): DashboardLayout {
        return layout.copy(
            canvasColor = Void.value.toLong(),
            isKineticCockpitMigrated = true,
            isDarkThemeMigrated = true,
            widgets = layout.widgets.map { widget ->
                val legacyColor = widget.backgroundColor?.let { ColorUtils.toColor(it) }
                val sanitized = if (legacyColor != null) {
                    ColorUtils.sanitizeColor(legacyColor).value.toLong()
                } else if (widget.type == WidgetType.BUTTON) {
                    // Force buttons to Primary identity if they had no color
                    com.example.hmi.core.ui.theme.Primary.value.toLong()
                } else {
                    // Sliders and Gauges use null to auto-follow theme background
                    null
                }

                // FR-013/RATIONALIZE: Ensure typography scale doesn't start below baseline (unless 0.0 to hide)
                val zoom = if (widget.labelFontSizeMultiplier > 0.0f && widget.labelFontSizeMultiplier < 1.0f) 1.0f else widget.labelFontSizeMultiplier
                widget.copy(
                    backgroundColor = sanitized,
                    labelFontSizeMultiplier = zoom,
                    metricFontSizeMultiplier = 1.0f
                )
            }
        )
    }
}
