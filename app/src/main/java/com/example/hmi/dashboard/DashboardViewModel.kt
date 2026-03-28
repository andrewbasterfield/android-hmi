package com.example.hmi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.core.ui.theme.Void
import com.example.hmi.data.ConfigTransferManager
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.TransferEvent
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.di.IoDispatcher
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
import com.example.hmi.widgets.ColorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.example.hmi.core.ui.theme.HealthStatus
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository,
    private val transferManager: ConfigTransferManager,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val connectionState = plcCommunicator.connectionState

    private val _dashboardLayout = MutableStateFlow(DashboardLayout())
    val dashboardLayout: StateFlow<DashboardLayout> = _dashboardLayout.asStateFlow()

    private val _tagValues = MutableStateFlow<Map<String, Float>>(emptyMap())
    val tagValues: StateFlow<Map<String, Float>> = _tagValues.asStateFlow()

    val globalStatus: StateFlow<HealthStatus> = combine(_dashboardLayout, _tagValues) { layout, values ->
        val widgetStatuses = layout.widgets.map { widget ->
            val currentValue = values[widget.tagAddress] ?: 0f
            val zone = widget.colorZones.find { currentValue in it.startValue..it.endValue }
            when (zone?.label) {
                "CRITICAL" -> HealthStatus.CRITICAL
                "CAUTION" -> HealthStatus.CAUTION
                else -> HealthStatus.NORMAL
            }
        }

        when {
            widgetStatuses.any { it == HealthStatus.CRITICAL } -> HealthStatus.CRITICAL
            widgetStatuses.any { it == HealthStatus.CAUTION } -> HealthStatus.CAUTION
            else -> HealthStatus.NORMAL
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HealthStatus.NORMAL
    )

    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _sessionOverrides = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())
    val sessionOverrides: StateFlow<Map<String, Map<String, String>>> = _sessionOverrides.asStateFlow()

    private val _importResult = MutableSharedFlow<Result<Unit>>(replay = 0)
    val importResult: SharedFlow<Result<Unit>> = _importResult

    val transferEvents: SharedFlow<TransferEvent> = transferManager.events

    // Track active tag observation jobs to prevent duplicates and enable cleanup
    private val activeTagObservations = mutableMapOf<String, Job>()

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
                    gaugeStyle = widget.gaugeStyle ?: com.example.hmi.data.GaugeStyle.POINTER,
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
        // Skip if already observing this tag
        if (tagAddress.isBlank() || activeTagObservations[tagAddress]?.isActive == true) {
            return
        }

        val job = viewModelScope.launch(ioDispatcher) {
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
        activeTagObservations[tagAddress] = job
    }

    /**
     * Synchronizes active tag observations with the current widget list.
     * Cancels observations for tags no longer in use.
     */
    fun syncTagObservations(currentTagAddresses: Set<String>) {
        // Cancel observations for tags no longer needed
        val tagsToRemove = activeTagObservations.keys - currentTagAddresses
        tagsToRemove.forEach { tagAddress ->
            activeTagObservations[tagAddress]?.cancel()
            activeTagObservations.remove(tagAddress)
        }

        // Start observations for new tags
        currentTagAddresses.forEach { tagAddress ->
            observeTag(tagAddress)
        }
    }

    fun onButtonPress(widget: WidgetConfiguration) {
        viewModelScope.launch(ioDispatcher) {
            when (widget.interactionType) {
                com.example.hmi.data.InteractionType.MOMENTARY -> {
                    // Momentary: Send 'true' on press, NO retain
                    plcCommunicator.writeTag(widget.tagAddress, PlcValue.BooleanValue(true), shouldRetain = false)
                }
                com.example.hmi.data.InteractionType.LATCHING -> {
                    val currentVal = _tagValues.value[widget.tagAddress] ?: 0f
                    val newValue = if (currentVal > 0.5f) false else true
                    
                    // Optimistic update
                    _tagValues.value = _tagValues.value.toMutableMap().apply { 
                        put(widget.tagAddress, if (newValue) 1f else 0f) 
                    }
                    
                    plcCommunicator.writeTag(widget.tagAddress, PlcValue.BooleanValue(newValue), shouldRetain = true)
                }
                com.example.hmi.data.InteractionType.INDICATOR -> {}
            }
        }
    }

    fun onButtonRelease(widget: WidgetConfiguration) {
        if (widget.interactionType == com.example.hmi.data.InteractionType.MOMENTARY) {
            viewModelScope.launch(ioDispatcher) {
                // Momentary: Send 'false' on release, NO retain
                plcCommunicator.writeTag(widget.tagAddress, PlcValue.BooleanValue(false), shouldRetain = false)
            }
        }
    }

    fun onSliderChange(tagAddress: String, value: Float) {
        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.writeTag(tagAddress, PlcValue.FloatValue(value), shouldRetain = true)
            _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, value) }
        }
    }
    
    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }
    
    fun updateWidgetPosition(widgetId: String, column: Int, row: Int) {
        _dashboardLayout.update { layout ->
            val index = layout.widgets.indexOfFirst { it.id == widgetId }
            if (index != -1) {
                val widget = layout.widgets[index]
                val maxZOrder = layout.widgets.maxOfOrNull { it.zOrder } ?: 0
                val updatedWidgets = layout.widgets.toMutableList().apply {
                    this[index] = widget.copy(column = column, row = row, zOrder = maxZOrder + 1)
                }
                layout.copy(widgets = updatedWidgets).also { newLayout ->
                    viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
                }
            } else layout
        }
    }

    fun updateWidgetSize(widgetId: String, colSpan: Int, rowSpan: Int) {
        _dashboardLayout.update { layout ->
            val index = layout.widgets.indexOfFirst { it.id == widgetId }
            if (index != -1) {
                val widget = layout.widgets[index]
                val updatedWidgets = layout.widgets.toMutableList().apply {
                    this[index] = widget.copy(colSpan = colSpan, rowSpan = rowSpan)
                }
                layout.copy(widgets = updatedWidgets).also { newLayout ->
                    viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
                }
            } else layout
        }
    }

    fun updateWidgetConfig(updatedWidget: WidgetConfiguration) {
        _dashboardLayout.update { layout ->
            val index = layout.widgets.indexOfFirst { it.id == updatedWidget.id }
            if (index != -1) {
                val updatedWidgets = layout.widgets.toMutableList().apply {
                    this[index] = updatedWidget
                }
                layout.copy(widgets = updatedWidgets).also { newLayout ->
                    viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
                }
            } else layout
        }
    }

    fun deleteWidget(widgetId: String) {
        _dashboardLayout.update { layout ->
            layout.copy(widgets = layout.widgets.filter { it.id != widgetId }).also { newLayout ->
                viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
            }
        }
    }

    fun acknowledgeAlarm(tagAddress: String) {
        _dashboardLayout.update { layout ->
            val updatedWidgets = layout.widgets.map { widget ->
                if (widget.tagAddress == tagAddress && widget.alarmState == com.example.hmi.data.AlarmState.Unacknowledged) {
                    widget.copy(alarmState = com.example.hmi.data.AlarmState.Acknowledged)
                } else widget
            }
            if (updatedWidgets != layout.widgets) {
                layout.copy(widgets = updatedWidgets).also { newLayout ->
                    viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
                }
            } else layout
        }
    }

    fun updateDashboardSettings(name: String, canvasColor: Long?, hapticFeedbackEnabled: Boolean, orientationMode: com.example.hmi.data.OrientationMode) {
        _dashboardLayout.update { layout ->
            layout.copy(
                name = name,
                canvasColor = canvasColor,
                hapticFeedbackEnabled = hapticFeedbackEnabled,
                orientationMode = orientationMode
            ).also { newLayout ->
                viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
            }
        }
    }

    fun updateOrientationMode(mode: com.example.hmi.data.OrientationMode) {
        _dashboardLayout.update { layout ->
            layout.copy(orientationMode = mode).also { newLayout ->
                viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
            }
        }
    }

    fun addWidget(widget: WidgetConfiguration) {
        _dashboardLayout.update { layout ->
            layout.copy(widgets = layout.widgets + widget).also { newLayout ->
                viewModelScope.launch(ioDispatcher) { repository.saveLayout(newLayout) }
            }
        }
    }

    fun exportLayout(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.exportLayout(uri)
        }
    }

    fun importLayout(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.importLayout(uri)
        }
    }

    fun exportFullBackup(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.exportFullBackup(uri)
        }
    }

    fun executeImport(backup: com.example.hmi.data.FullBackupPackage, importLayout: Boolean, importProfiles: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.executeImport(backup, importLayout, importProfiles)
        }
    }

    fun shareLayout(context: android.content.Context) {
        viewModelScope.launch(ioDispatcher) {
            val layout = repository.dashboardLayoutFlow.first()
            val backup = com.example.hmi.data.FullBackupPackage(layout = layout)
            val jsonStr = json.encodeToString(backup)
            transferManager.shareConfig(context, jsonStr, "dashboard_layout.json")
        }
    }

    fun exportLayoutToJson(): String {
        return json.encodeToString(_dashboardLayout.value)
    }

    fun importLayoutFromJson(jsonStr: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val newLayout = json.decodeFromString<DashboardLayout>(jsonStr)
                if (newLayout.name.isBlank()) {
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
