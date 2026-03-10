package com.example.hmi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val plcCommunicator: PlcCommunicator,
    private val repository: DashboardRepository
) : ViewModel() {

    private val _dashboardLayout = MutableStateFlow(DashboardLayout())
    val dashboardLayout: StateFlow<DashboardLayout> = _dashboardLayout.asStateFlow()

    private val _tagValues = MutableStateFlow<Map<String, Float>>(emptyMap())
    val tagValues: StateFlow<Map<String, Float>> = _tagValues.asStateFlow()
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.dashboardLayoutFlow.collect { layout ->
                // If it's a completely empty layout (first run), we could seed it, 
                // but let's just use whatever is loaded (or the default empty).
                _dashboardLayout.value = layout
            }
        }
    }

    fun observeTag(tagAddress: String) {
        viewModelScope.launch {
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
                }
            }
        }
    }

    fun onButtonPress(tagAddress: String) {
        viewModelScope.launch {
            plcCommunicator.writeTag(tagAddress, PlcValue.BooleanValue(true))
        }
    }

    fun onSliderChange(tagAddress: String, value: Float) {
        viewModelScope.launch {
            plcCommunicator.writeTag(tagAddress, PlcValue.FloatValue(value))
            _tagValues.value = _tagValues.value.toMutableMap().apply { put(tagAddress, value) }
        }
    }
    
    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }
    
    fun updateWidgetPosition(widgetId: String, newX: Float, newY: Float) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        val index = currentWidgets.indexOfFirst { it.id == widgetId }
        if (index != -1) {
            val widget = currentWidgets[index]
            currentWidgets[index] = widget.copy(x = newX, y = newY)
            val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
            _dashboardLayout.value = newLayout
            viewModelScope.launch { repository.saveLayout(newLayout) }
        }
    }
    
    fun addWidget(widget: WidgetConfiguration) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        currentWidgets.add(widget)
        val newLayout = _dashboardLayout.value.copy(widgets = currentWidgets)
        _dashboardLayout.value = newLayout
        viewModelScope.launch { repository.saveLayout(newLayout) }
    }
}
