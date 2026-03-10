package com.example.hmi.dashboard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.data.WidgetType
import com.example.hmi.widgets.ButtonWidget
import com.example.hmi.widgets.GaugeWidget
import com.example.hmi.widgets.SliderWidget
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val dashboardLayout by viewModel.dashboardLayout.collectAsState()
    val tagValues by viewModel.tagValues.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    LaunchedEffect(dashboardLayout.widgets) {
        dashboardLayout.widgets.forEach { widget ->
            viewModel.observeTag(widget.tagAddress)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dashboardLayout.name) },
                actions = {
                    Button(onClick = { viewModel.toggleEditMode() }) {
                        Text(if (isEditMode) "Run Mode" else "Edit Mode")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            dashboardLayout.widgets.forEach { widget ->
                val currentValue = tagValues[widget.tagAddress] ?: 0f

                var offsetX by remember { mutableStateOf(widget.x) }
                var offsetY by remember { mutableStateOf(widget.y) }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .then(
                            if (isEditMode) {
                                Modifier.pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            viewModel.updateWidgetPosition(widget.id, offsetX, offsetY)
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    when (widget.type) {
                        WidgetType.BUTTON -> {
                            ButtonWidget(
                                label = widget.tagAddress,
                                onClick = { viewModel.onButtonPress(widget.tagAddress) }
                            )
                        }
                        WidgetType.SLIDER -> {
                            SliderWidget(
                                label = widget.tagAddress,
                                value = currentValue,
                                onValueChange = { viewModel.onSliderChange(widget.tagAddress, it) },
                                valueRange = (widget.minValue ?: 0f)..(widget.maxValue ?: 100f)
                            )
                        }
                        WidgetType.GAUGE -> {
                            GaugeWidget(
                                label = widget.tagAddress,
                                value = currentValue,
                                minValue = widget.minValue ?: 0f,
                                maxValue = widget.maxValue ?: 100f
                            )
                        }
                    }
                }
            }
            
            if (isEditMode) {
                WidgetPalette(
                    onAddWidget = { viewModel.addWidget(it) },
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
                )
            }
        }
    }
}
