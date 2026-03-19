package com.example.hmi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.di.IoDispatcher
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
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

    // TagAddress -> AttributeName -> Value (transient session overrides)
    private val _sessionOverrides = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())
    val sessionOverrides: StateFlow<Map<String, Map<String, String>>> = _sessionOverrides.asStateFlow()

    private val _importResult = MutableSharedFlow<Result<Unit>>(replay = 0)
    val importResult: SharedFlow<Result<Unit>> = _importResult

    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        viewModelScope.launch(ioDispatcher) {
            repository.dashboardLayoutFlow.collect { layout ->
                // Safety check: GSON can leave non-nullable fields as null if missing in JSON
                val safeLayout = ensureNonNullFields(layout)
                
                if (!safeLayout.isDarkThemeMigrated) {
                    val migrated = migrateToDarkMode(safeLayout)
                    repository.saveLayout(migrated)
                    // The flow will emit again with the migrated layout
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
            id = layout.id ?: "",
            name = layout.name ?: "Unnamed Layout",
            hapticFeedbackEnabled = layout.hapticFeedbackEnabled, // Primitive boolean will be false if null in JSON
            widgets = (layout.widgets ?: emptyList()).map { widget ->
                widget.copy(
                    id = widget.id ?: java.util.UUID.randomUUID().toString(),
                    tagAddress = widget.tagAddress ?: "",
                    colorZones = widget.colorZones ?: emptyList()
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
                    is PlcValue.StringValue -> {
                        // Ignore string values for numeric tags
                    }
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
            currentWidgets[index] = widget.copy(column = column, row = row)
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

    private fun migrateToDarkMode(layout: DashboardLayout): DashboardLayout {
        return layout.copy(
            canvasColor = 0xFF000000uL.toLong(),
            isDarkThemeMigrated = true,
            widgets = layout.widgets.map { it.copy(fontSizeMultiplier = 1.0f) }
        )
    }

    fun exportLayoutToJson(): String {
        return gson.toJson(_dashboardLayout.value)
    }

    fun importLayoutFromJson(json: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val newLayout = gson.fromJson(json, DashboardLayout::class.java)
                if (newLayout == null || (newLayout.name?.isBlank() != false)) {
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
