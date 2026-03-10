package com.example.hmi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmi.data.DashboardLayout
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
    private val plcCommunicator: PlcCommunicator
) : ViewModel() {

    private val _dashboardLayout = MutableStateFlow(DashboardLayout())
    val dashboardLayout: StateFlow<DashboardLayout> = _dashboardLayout.asStateFlow()

    private val _tagValues = MutableStateFlow<Map<String, Float>>(emptyMap())
    val tagValues: StateFlow<Map<String, Float>> = _tagValues.asStateFlow()
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    init {
        // Initial mock layout
        _dashboardLayout.value = DashboardLayout(
            widgets = listOf(
                WidgetConfiguration(type = WidgetType.BUTTON, x = 10f, y = 10f, tagAddress = "Pump1_Start"),
                WidgetConfiguration(type = WidgetType.SLIDER, x = 10f, y = 100f, tagAddress = "Pump1_Speed", minValue = 0f, maxValue = 100f),
                WidgetConfiguration(type = WidgetType.GAUGE, x = 10f, y = 200f, tagAddress = "Tank_Level", minValue = 0f, maxValue = 100f)
            )
        )
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
            _dashboardLayout.value = _dashboardLayout.value.copy(widgets = currentWidgets)
        }
    }
    
    fun addWidget(widget: WidgetConfiguration) {
        val currentWidgets = _dashboardLayout.value.widgets.toMutableList()
        currentWidgets.add(widget)
        _dashboardLayout.value = _dashboardLayout.value.copy(widgets = currentWidgets)
    }
}
