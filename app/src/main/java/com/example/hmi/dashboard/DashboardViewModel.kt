package com.example.hmi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.core.ui.theme.Void
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.di.IoDispatcher
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
import com.example.hmi.widgets.ColorUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dashboardLayout = MutableStateFlow(DashboardLayout())
    val dashboardLayout: StateFlow<DashboardLayout> = _dashboardLayout.asStateFlow()

    private val _tagValues = MutableStateFlow<Map<String, Float>>(emptyMap())
    val tagValues: StateFlow<Map<String, Float>> = _tagValues.asStateFlow()
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _sessionOverrides = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())
    val sessionOverrides: StateFlow<Map<String, Map<String, String>>> = _sessionOverrides.asStateFlow()

    private val _importResult = MutableSharedFlow<Result<Unit>>(replay = 0)
    val importResult: SharedFlow<Result<Unit>> = _importResult

    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        viewModelScope.launch(ioDispatcher) {
            repository.dashboardLayoutFlow.collect { layout ->
                val safeLayout = ensureNonNullFields(layout)
                
                if (!safeLayout.isKineticCockpitMigrated) {
                    val migrated = migrateToKineticCockpit(safeLayout)
                    repository.saveLayout(migrated)
                    return@collect
                }
                _dashboardLayout.value = safeLayout
            }
        }

        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.attributeUpdates.collect { (tag, attr, value) ->
                val current = _sessionOverrides.value.toMutableMap()
                val tagMap = current[tag]?.toMutableMap() ?: mutableMapOf()
                tagMap[attr] = value
                current[tag] = tagMap
                _sessionOverrides.value = current
            }
        }
    }

    private fun ensureNonNullFields(layout: DashboardLayout?): DashboardLayout {
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
                    alarmState = if (widget.alarmState == com.example.hmi.data.AlarmState.Acknowledged) {
                        com.example.hmi.data.AlarmState.Unacknowledged
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
    private fun migrateToKineticCockpit(layout: DashboardLayout): DashboardLayout {
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

    fun observeTag(tagAddress: String) {
        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.observeTag(tagAddress).collect { value ->
                when (value) {
                    is PlcValue.FloatValue -> {
                        _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, value.value) }
                    }
                    is PlcValue.IntValue -> {
                        _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, value.value.toFloat()) }
                    }
                    is PlcValue.BooleanValue -> {
                        _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, if (value.value) 1f else 0f) }
                    }
                    is PlcValue.StringValue -> {}
                }
            }
        }
    }

    fun onButtonPress(tagAddress: String) {
        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.writeTag(tagAddress, PlcValue.BooleanValue(true))
        }
    }

    fun onSliderChange(tagAddress: String, value: Float) {
        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.writeTag(tagAddress, PlcValue.FloatValue(value))
            _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, value) }
        }
    }
    
    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }
    
    fun updateWidgetPosition(widgetId: String, column: Int, row: Int) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        val index = currentWidgets.indexOfFirst { it.id == widgetId }
        if (index != -1) {
            val widget = currentWidgets[index]
            val maxZOrder = currentWidgets.maxOfOrNull { it.zOrder } ?: 0
            currentWidgets[index] = widget.copy(
                column = column,
                row = row,
                zOrder = maxZOrder + 1
            )
            val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
            _dashboardLayout.value = newLayout
            viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
        }
    }

    fun updateWidgetSize(widgetId: String, colSpan: Int, rowSpan: Int) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        val index = currentWidgets.indexOfFirst { it.id == widgetId }
        if (index != -1) {
            val widget = currentWidgets[index]
            currentWidgets[index] = widget.copy(colSpan = colSpan, rowSpan = rowSpan)
            val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
            _dashboardLayout.value = newLayout
            viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
        }
    }

    fun updateWidgetConfig(updatedWidget: WidgetConfiguration) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        val index = currentWidgets.indexOfFirst { it.id == updatedWidget.id }
        if (index != -1) {
            currentWidgets[index] = updatedWidget
            val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
            _dashboardLayout.value = newLayout
            viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
        }
    }

    fun deleteWidget(widgetId: String) {
        val currentWidgets = _dashboardLayout.value.widgets.filter { it.id != widgetId }
        val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
        _dashboardLayout.value = newLayout
        viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
    }
    
    fun acknowledgeAlarm(tagAddress: String) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        var changed = false
        for (i in currentWidgets.indices) {
            val widget = currentWidgets[i]
            if (widget.tagAddress == tagAddress && widget.alarmState == com.example.hmi.data.AlarmState.Unacknowledged) {
                currentWidgets[i] = widget.copy(alarmState = com.example.hmi.data.AlarmState.Acknowledged)
                changed = true
            }
        }
        if (changed) {
            val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
            _dashboardLayout.value = newLayout
            viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
        }
    }
    
    fun updateDashboardSettings(name: String, canvasColor: Long?, hapticFeedbackEnabled: Boolean) {
        val newLayout = _dashboardLayout.value.copy(
            name = name, 
            canvasColor = canvasColor,
            hapticFeedbackEnabled = hapticFeedbackEnabled
        )
        _dashboardLayout.value = newLayout
        viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
    }

    fun addWidget(widget: WidgetConfiguration) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        currentWidgets.add(widget)
        val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
        _dashboardLayout.value = newLayout
        viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
    }

    fun exportLayoutToJson(): String {
        return gson.toJson(_dashboardLayout.value)
    }

    fun importLayoutFromJson(json: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val newLayout = gson.fromJson(json, DashboardLayout::class.java)
                if (newLayout == null || newLayout.name.isBlank()) {
                    _importResult.emit(Result.failure(Exception("Invalid layout or name cannot be blank")))
                    return@launch
                }
                
                val safeLayout = ensureNonNullFields(newLayout)
                _dashboardLayout.value = safeLayout
                repository.saveLayout(safeLayout)
                _importResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                _importResult.emit(Result.failure(Exception("Invalid JSON format: ${e.localizedMessage}")))
            }
        }
    }
}
