package com.example.hmi.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.ConfigTransferManager
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.LayoutMigrationManager
import com.example.hmi.data.TransferEvent
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.di.IoDispatcher
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.data.SystemProfile
import com.example.hmi.protocol.PlcConnectionProfile
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository,
    private val transferManager: ConfigTransferManager,
    private val migrationManager: LayoutMigrationManager,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val connectionState = plcCommunicator.connectionState

    private val _dashboardLayout = MutableStateFlow(DashboardLayout())
    val dashboardLayout: StateFlow<DashboardLayout> = _dashboardLayout.asStateFlow()

    // Keyed by (tagAddress, jsonPath) -- matching activeTagObservations -- so two
    // widgets subscribed to the same topic but different JSON paths don't overwrite
    // each other's value.
    private val _tagValues = MutableStateFlow<Map<Pair<String, String?>, Float>>(emptyMap())
    val tagValues: StateFlow<Map<Pair<String, String?>, Float>> = _tagValues.asStateFlow()

    private val _tagStringValues = MutableStateFlow<Map<Pair<String, String?>, String>>(emptyMap())
    val tagStringValues: StateFlow<Map<Pair<String, String?>, String>> = _tagStringValues.asStateFlow()

    val globalStatus: StateFlow<HealthStatus> = combine(_dashboardLayout, _tagValues) { layout, values ->
        val widgetStatuses = layout.widgets.map { widget ->
            val currentValue = values[widget.tagAddress to widget.jsonPath] ?: 0f
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

    private val _announcements = MutableSharedFlow<String>(replay = 0)
    val announcements: SharedFlow<String> = _announcements.asSharedFlow()

    val transferEvents: SharedFlow<TransferEvent> = transferManager.events

    val systemProfiles = repository.systemProfilesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeSystemProfileId = repository.activeSystemProfileIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val savedProfiles = repository.savedProfilesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedLayouts = repository.savedLayoutsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeConnection = repository.connectionProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isModified: StateFlow<Boolean> = combine(
        activeSystemProfileId,
        systemProfiles,
        activeConnection,
        dashboardLayout
    ) { activeId, profiles, conn, layout ->
        if (activeId == null) return@combine false
        val profile = profiles.find { it.id == activeId } ?: return@combine false
        
        // Check if current state differs from active preset
        profile.connectionProfileName != conn?.name || profile.layoutId != layout.id
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Track active tag observation jobs to prevent duplicates and enable cleanup
    // Key is Pair(tagAddress, jsonPath)
    private val activeTagObservations = mutableMapOf<Pair<String, String?>, Job>()

    // Layout writes are funneled through this single conflated channel so concurrent
    // edits (e.g. rapid drag-then-resize) persist in order, one at a time, instead of
    // racing as independent unordered coroutines where a slower older save can win.
    private val layoutSaveRequests = Channel<DashboardLayout>(Channel.CONFLATED)

    // The layout instance of the most recently enqueued, not-yet-completed save.
    // Cleared (by reference) once its write finishes, as long as nothing newer has
    // superseded it in the meantime. While set, the DataStore echo collector below
    // knows a fresher write for this layout id is still outstanding, so an echo of
    // an older on-disk state must be ignored rather than applied over the edit.
    @Volatile
    private var outstandingLayoutSave: DashboardLayout? = null

    private fun persistLayout(layout: DashboardLayout) {
        outstandingLayoutSave = layout
        layoutSaveRequests.trySend(layout)
    }

    init {
        viewModelScope.launch(ioDispatcher) {
            for (layout in layoutSaveRequests) {
                repository.saveLayout(layout)
                // Only clear if this was still the latest request; a newer one may
                // have already replaced it in `outstandingLayoutSave` while this
                // write was in flight, in which case it remains outstanding.
                if (outstandingLayoutSave === layout) {
                    outstandingLayoutSave = null
                }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            repository.dashboardLayoutFlow.collect { layout ->
                val safeLayout = migrationManager.ensureNonNullFields(layout)

                if (!safeLayout.isKineticCockpitMigrated) {
                    val migrated = migrationManager.migrateToKineticCockpit(safeLayout)
                    repository.saveLayout(migrated)
                    return@collect
                }

                val outstanding = outstandingLayoutSave
                if (outstanding != null && outstanding.id == safeLayout.id) {
                    // A save for this layout id hasn't finished yet, so this echo
                    // reflects on-disk state from before that write. Skip it; the
                    // write's own echo will arrive once it completes.
                    return@collect
                }
                _dashboardLayout.value = safeLayout
            }
        }
        viewModelScope.launch(ioDispatcher) {
            plcCommunicator.attributeUpdates.collect { (tag, attr, value) ->
                if (attr.equals("color", ignoreCase = true)) return@collect

                _sessionOverrides.update { current ->
                    val tagMap = (current[tag] ?: emptyMap()) + (attr to value)
                    current + (tag to tagMap)
                }
            }
        }
    }

    fun observeTag(tagAddress: String, jsonPath: String? = null) {
        if (tagAddress.isBlank()) return

        val observationKey = tagAddress to jsonPath
        synchronized(activeTagObservations) {
            // Skip if already observing this specific (topic + path) combination
            if (activeTagObservations[observationKey]?.isActive == true) {
                return
            }

            val job = viewModelScope.launch(ioDispatcher) {
                plcCommunicator.observeTag(tagAddress, jsonPath).collect { value ->
                    when (value) {
                        is PlcValue.FloatValue -> {
                            _tagValues.update { it + (observationKey to value.value) }
                        }
                        is PlcValue.IntValue -> {
                            _tagValues.update { it + (observationKey to value.value.toFloat()) }
                        }
                        is PlcValue.BooleanValue -> {
                            _tagStringValues.update { it + (observationKey to value.value.toString()) }
                            _tagValues.update { it + (observationKey to if (value.value) 1f else 0f) }
                        }
                        is PlcValue.StringValue -> {
                            _tagStringValues.update { it + (observationKey to value.value) }
                        }
                    }
                }
            }
            activeTagObservations[observationKey] = job
        }
    }

    /**
     * Synchronizes active tag observations with the current widget list.
     * Cancels observations for tags no longer in use.
     */
    fun syncTagObservations(currentTags: Set<Pair<String, String?>>) {
        synchronized(activeTagObservations) {
            // Cancel observations for tags no longer needed
            val tagsToRemove = activeTagObservations.keys - currentTags
            tagsToRemove.forEach { key ->
                activeTagObservations[key]?.cancel()
                activeTagObservations.remove(key)
            }

            // Start observations for new tags
            currentTags.forEach { (tagAddress, jsonPath) ->
                observeTag(tagAddress, jsonPath)
            }
        }
    }

    fun resolveButtonState(widget: WidgetConfiguration): Boolean {
        val key = widget.tagAddress to widget.jsonPath
        val raw = _tagStringValues.value[key]
        if (raw != null) {
            if (widget.trueValues.any { it.equals(raw, ignoreCase = true) }) return true
            if (widget.falseValues.any { it.equals(raw, ignoreCase = true) }) return false
        }
        // Fall back to numeric check
        val floatVal = _tagValues.value[key] ?: 0f
        return floatVal > 0.5f
    }

    fun onButtonPress(widget: WidgetConfiguration) {
        val writeAddr = widget.writeAddress?.takeIf { it.isNotBlank() } ?: widget.tagAddress
        val truePayload = widget.trueValues.firstOrNull() ?: "true"
        val falsePayload = widget.falseValues.firstOrNull() ?: "false"
        viewModelScope.launch(ioDispatcher) {
            when (widget.interactionType) {
                com.example.hmi.data.InteractionType.MOMENTARY -> {
                    val payload = widget.writeTemplate?.replace("${'$'}VALUE", truePayload) ?: truePayload
                    plcCommunicator.writeTag(writeAddr, PlcValue.StringValue(payload), shouldRetain = false)
                }
                com.example.hmi.data.InteractionType.LATCHING -> {
                    val isOn = resolveButtonState(widget)
                    val sendTrue = !isOn
                    val valueToWrap = if (sendTrue) truePayload else falsePayload

                    // Optimistic update
                    val key = widget.tagAddress to widget.jsonPath
                    _tagStringValues.update { it + (key to valueToWrap) }
                    _tagValues.update { it + (key to if (sendTrue) 1f else 0f) }

                    val payload = widget.writeTemplate?.replace("${'$'}VALUE", valueToWrap) ?: valueToWrap
                    plcCommunicator.writeTag(writeAddr, PlcValue.StringValue(payload), shouldRetain = true)
                }
                com.example.hmi.data.InteractionType.INDICATOR -> {}
            }
        }
    }

    fun onButtonRelease(widget: WidgetConfiguration) {
        if (widget.interactionType == com.example.hmi.data.InteractionType.MOMENTARY) {
            val writeAddr = widget.writeAddress?.takeIf { it.isNotBlank() } ?: widget.tagAddress
            val falsePayload = widget.falseValues.firstOrNull() ?: "false"
            viewModelScope.launch(ioDispatcher) {
                val payload = widget.writeTemplate?.replace("${'$'}VALUE", falsePayload) ?: falsePayload
                plcCommunicator.writeTag(writeAddr, PlcValue.StringValue(payload), shouldRetain = false)
            }
        }
    }

    fun onSliderChange(tagAddress: String, writeAddress: String?, value: Float, writeTemplate: String? = null, jsonPath: String? = null) {
        val key = tagAddress to jsonPath
        val prev = _tagValues.value[key]
        if (prev == value) return
        _tagValues.update { it + (key to value) }
        val writeAddr = writeAddress?.takeIf { it.isNotBlank() } ?: tagAddress
        viewModelScope.launch(ioDispatcher) {
            if (writeTemplate != null) {
                val formattedValue = value.toBigDecimal().stripTrailingZeros().toPlainString()
                val payload = writeTemplate.replace("${'$'}VALUE", formattedValue)
                plcCommunicator.writeTag(writeAddr, PlcValue.StringValue(payload), shouldRetain = true)
            } else {
                plcCommunicator.writeTag(writeAddr, PlcValue.FloatValue(value), shouldRetain = true)
            }
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
                    persistLayout(newLayout)
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
                    persistLayout(newLayout)
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
                    persistLayout(newLayout)
                }
            } else layout
        }
    }

    fun duplicateWidget(widgetId: String) {
        _dashboardLayout.update { layout ->
            val source = layout.widgets.find { it.id == widgetId }
            if (source != null) {
                val maxZOrder = layout.widgets.maxOfOrNull { it.zOrder } ?: 0
                val duplicate = source.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    column = source.column + 1,
                    row = source.row + 1,
                    zOrder = maxZOrder + 1
                )
                val newWidgets = layout.widgets + duplicate
                val newLayout = layout.copy(widgets = newWidgets)

                persistLayout(newLayout)
                viewModelScope.launch(ioDispatcher) { _announcements.emit("Widget duplicated") }
                newLayout
            } else {
                layout
            }
        }
    }

    fun deleteWidget(widgetId: String) {
        _dashboardLayout.update { layout ->
            layout.copy(widgets = layout.widgets.filter { it.id != widgetId }).also { newLayout ->
                persistLayout(newLayout)
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
                    persistLayout(newLayout)
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
                persistLayout(newLayout)
            }
        }
    }

    fun updateOrientationMode(mode: com.example.hmi.data.OrientationMode) {
        _dashboardLayout.update { layout ->
            layout.copy(orientationMode = mode).also { newLayout ->
                persistLayout(newLayout)
            }
        }
    }

    fun addWidget(widget: WidgetConfiguration) {
        _dashboardLayout.update { layout ->
            val maxZOrder = layout.widgets.maxOfOrNull { it.zOrder } ?: 0
            val widgetWithZOrder = widget.copy(zOrder = maxZOrder + 1)
            layout.copy(widgets = layout.widgets + widgetWithZOrder).also { newLayout ->
                persistLayout(newLayout)
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

    fun importProfiles(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.importProfiles(uri)
        }
    }

    fun importSystemProfiles(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.importSystemProfiles(uri)
        }
    }

    fun importFullBackup(uri: android.net.Uri) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.importFullBackup(uri)
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

                val safeLayout = migrationManager.ensureNonNullFields(newLayout)
                _dashboardLayout.value = safeLayout
                persistLayout(safeLayout)
                _importResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                _importResult.emit(Result.failure(Exception("Invalid JSON format: ${e.localizedMessage}")))
            }
        }
    }

    fun saveCurrentAsSystemProfile(name: String) {
        viewModelScope.launch(ioDispatcher) {
            val connection = repository.connectionProfileFlow.first()
            val layout = _dashboardLayout.value

            if (connection != null) {
                // Ensure current layout is persisted to the library
                repository.saveToSavedLayouts(layout)

                val newProfile = com.example.hmi.data.SystemProfile(
                    name = name,
                    connectionProfileName = connection.name,
                    layoutId = layout.id
                )
                repository.saveSystemProfile(newProfile)
                repository.setActiveSystemProfileId(newProfile.id)
                _announcements.emit("System Profile '$name' saved")
            } else {
                _announcements.emit("Cannot save profile: no active connection")
            }
        }
    }

    fun selectSystemProfile(profile: SystemProfile) {
        viewModelScope.launch(ioDispatcher) {
            repository.setActiveSystemProfileId(profile.id)
            
            // Resolve connection profile from library
            val savedProfiles = repository.savedProfilesFlow.first()
            val targetConnection = savedProfiles.find { it.name == profile.connectionProfileName }
            
            if (targetConnection != null) {
                // Trigger reconnection
                repository.saveConnectionProfile(targetConnection)
                plcCommunicator.connect(targetConnection)
                _announcements.emit("Switched to Profile: ${profile.name}")
            } else {
                _announcements.emit("Profile '${profile.name}' loaded but connection '${profile.connectionProfileName}' not found")
            }
        }
    }

    fun selectManualConnection(profile: PlcConnectionProfile) {
        viewModelScope.launch(ioDispatcher) {
            repository.setActiveSystemProfileId(null)
            repository.saveConnectionProfile(profile)
            plcCommunicator.connect(profile)
            _announcements.emit("Manual Connection: ${profile.name}")
        }
    }

    fun selectManualLayout(layout: DashboardLayout) {
        viewModelScope.launch(ioDispatcher) {
            repository.setActiveSystemProfileId(null)
            persistLayout(layout)
            _announcements.emit("Manual Layout: ${layout.name}")
        }
    }

    fun shareSystemProfile(profile: com.example.hmi.data.SystemProfile, context: android.content.Context) {
        viewModelScope.launch(ioDispatcher) {
            transferManager.exportSystemProfileBundle(profile, context)
        }
    }

    fun createNewLayout(name: String) {
        viewModelScope.launch(ioDispatcher) {
            val newLayout = DashboardLayout(
                id = java.util.UUID.randomUUID().toString(),
                name = name
            )
            repository.saveToSavedLayouts(newLayout)
            repository.setActiveSystemProfileId(null)
            persistLayout(newLayout)
            _announcements.emit("Created new layout: $name")
        }
    }

    fun deleteSystemProfile(id: String) {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteSystemProfile(id)
            _announcements.emit("Preset deleted")
        }
    }

    fun deleteLayout(id: String) {
        viewModelScope.launch(ioDispatcher) {
            val success = repository.deleteLayout(id)
            if (success) {
                _announcements.emit("Layout deleted")
            } else {
                _announcements.emit("Cannot delete: layout is bound to a system profile")
            }
        }
    }

    fun updateAnyLayoutSettings(id: String, name: String, canvasColor: Long?, hapticFeedbackEnabled: Boolean, orientationMode: com.example.hmi.data.OrientationMode) {
        viewModelScope.launch(ioDispatcher) {
            // If it's the active layout, use existing update method to sync state
            if (id == _dashboardLayout.value.id) {
                updateDashboardSettings(name, canvasColor, hapticFeedbackEnabled, orientationMode)
            } else {
                // Otherwise update it in the library directly
                val layouts = repository.savedLayoutsFlow.first()
                val target = layouts.find { it.id == id }
                if (target != null) {
                    val updated = target.copy(
                        name = name,
                        canvasColor = canvasColor,
                        hapticFeedbackEnabled = hapticFeedbackEnabled,
                        orientationMode = orientationMode
                    )
                    repository.saveToSavedLayouts(updated)
                    _announcements.emit("Layout '$name' updated")
                }
            }
        }
    }
}
