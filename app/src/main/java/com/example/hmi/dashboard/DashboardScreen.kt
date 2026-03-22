package com.example.hmi.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmi.core.ui.components.EmergencyHUD
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.widgets.ButtonWidget
import com.example.hmi.widgets.GaugeWidget
import com.example.hmi.widgets.SliderWidget
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val dashboardLayout by viewModel.dashboardLayout.collectAsState()
    val tagValues by viewModel.tagValues.collectAsState()
    val sessionOverrides by viewModel.sessionOverrides.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    var editingWidget by remember { mutableStateOf<WidgetConfiguration?>(null) }
    var showDashboardSettings by remember { mutableStateOf(false) }
    
    val draggingOffsets = remember { mutableStateMapOf<String, Offset>() }
    val resizingOffsets = remember { mutableStateMapOf<String, Offset>() }
    
    // US2 DEEP FIX: Track the starting state of a gesture to avoid feedback loops with animations
    val gestureStartStates = remember { mutableStateMapOf<String, Pair<IntOffset, IntSize>>() }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    val maxColumns by rememberUpdatedState(floor(configuration.screenWidthDp / GridSystem.CELL_SIZE.value).toInt())
    val maxRows by rememberUpdatedState(floor(configuration.screenHeightDp / GridSystem.CELL_SIZE.value).toInt())

    LaunchedEffect(dashboardLayout.widgets) {
        dashboardLayout.widgets.forEach { widget ->
            viewModel.observeTag(widget.tagAddress)
        }
    }

    // Determine Global Health Status for EmergencyHUD (FR-007)
    val globalStatus by remember(tagValues, dashboardLayout.widgets) {
        derivedStateOf {
            val widgetStatuses = dashboardLayout.widgets.map { widget ->
                val currentValue = tagValues[widget.tagAddress] ?: 0f
                val zone = widget.colorZones.find { currentValue in it.startValue..it.endValue }
                when (zone?.label) {
                    "CRITICAL" -> HealthStatus.CRITICAL
                    "CAUTION" -> HealthStatus.CAUTION
                    else -> HealthStatus.NORMAL
                }
            }
            if (widgetStatuses.any { it == HealthStatus.CRITICAL }) HealthStatus.CRITICAL
            else if (widgetStatuses.any { it == HealthStatus.CAUTION }) HealthStatus.CAUTION
            else HealthStatus.NORMAL
        }
    }

    if (editingWidget != null) {
        WidgetConfigDialog(
            initialWidget = editingWidget,
            onDismiss = { editingWidget = null },
            onConfirm = { updated ->
                viewModel.updateWidgetConfig(updated)
                editingWidget = null
            },
            onDelete = {
                editingWidget?.let { viewModel.deleteWidget(it.id) }
                editingWidget = null
            }
        )
    }

    if (showDashboardSettings) {
        DashboardSettingsDialog(
            initialName = dashboardLayout.name,
            initialCanvasColor = dashboardLayout.canvasColor,
            initialHapticEnabled = dashboardLayout.hapticFeedbackEnabled,
            onDismiss = { showDashboardSettings = false },
            onConfirm = { name, color, hapticEnabled ->
                viewModel.updateDashboardSettings(name, color, hapticEnabled)
                showDashboardSettings = false
            }
        )
    }

    // Unified math for snapping to cells
    fun calculateSnapCells(
        visualX: Float,
        visualY: Float,
        colSpan: Int,
        rowSpan: Int,
        density: Density
    ): Pair<Int, Int> {
        val col = with(density) { GridSystem.dpToCell(visualX.toDp()) }.coerceIn(0, maxColumns - colSpan)
        val row = with(density) { GridSystem.dpToCell(visualY.toDp()) }.coerceIn(0, maxRows - rowSpan)
        return col to row
    }

    fun calculateSnapSize(
        visualWidth: Float,
        visualHeight: Float,
        column: Int,
        row: Int,
        density: Density
    ): Pair<Int, Int> {
        val colSpan = with(density) { GridSystem.dpToCell(visualWidth.toDp()) }.coerceIn(1, maxColumns - column)
        val rowSpan = with(density) { GridSystem.dpToCell(visualHeight.toDp()) }.coerceIn(1, maxRows - row)
        return colSpan to rowSpan
    }

    var showAddWidgetDialog by remember { mutableStateOf(false) }

    if (showAddWidgetDialog) {
        WidgetConfigDialog(
            onDismiss = { showAddWidgetDialog = false },
            onConfirm = { widget ->
                viewModel.addWidget(widget)
                showAddWidgetDialog = false
            }
        )
    }

    EmergencyHUD(status = globalStatus) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(dashboardLayout.name) },
                    actions = {
                        if (isEditMode) {
                            Button(onClick = { showAddWidgetDialog = true }) {
                                Text("Add Widget")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { showDashboardSettings = true }) {
                                Text("Layout")
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.toggleEditMode() }) {
                            Text(if (isEditMode) "Run Mode" else "Edit Mode")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Connection")
                        }
                    }
                )
            }
        ) { paddingValues ->
            val canvasColor = dashboardLayout.canvasColor?.let { Color(it.toULong()) } ?: MaterialTheme.colorScheme.background
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(canvasColor)
            ) {
                dashboardLayout.widgets.forEach { widget ->
                    key(widget.id) {
                        val currentValue = tagValues[widget.tagAddress] ?: 0f
                        
                        // Resolve transient overrides
                        val tagOverrides = sessionOverrides[widget.tagAddress]
                        val resolvedLabel = tagOverrides?.get("label") ?: widget.customLabel ?: widget.tagAddress
                        val resolvedColorLong = tagOverrides?.get("color")?.let { 
                            com.example.hmi.widgets.ColorUtils.parseHexColor(it) 
                        } ?: widget.backgroundColor

                        val dragOffset = draggingOffsets[widget.id]
                        val resizeOffset = resizingOffsets[widget.id]
                        val isBeingDragged = dragOffset != null
                        val isBeingResized = resizeOffset != null

                        val animatableOffset = remember { 
                            Animatable(
                                IntOffset(
                                    with(density) { GridSystem.cellToDp(widget.column).toPx().roundToInt() },
                                    with(density) { GridSystem.cellToDp(widget.row).toPx().roundToInt() }
                                ),
                                IntOffset.VectorConverter
                            )
                        }

                        val animatableSize = remember {
                            Animatable(
                                IntSize(
                                    with(density) { GridSystem.cellToDp(widget.colSpan).toPx().roundToInt() },
                                    with(density) { GridSystem.cellToDp(widget.rowSpan).toPx().roundToInt() }
                                ),
                                IntSize.VectorConverter
                            )
                        }

                        LaunchedEffect(widget.column, widget.row) {
                            animatableOffset.animateTo(
                                IntOffset(
                                    with(density) { GridSystem.cellToDp(widget.column).toPx().roundToInt() },
                                    with(density) { GridSystem.cellToDp(widget.row).toPx().roundToInt() }
                                ),
                                spring(GridSystem.SNAP_DAMPING, GridSystem.SNAP_STIFFNESS)
                            )
                        }

                        LaunchedEffect(widget.colSpan, widget.rowSpan) {
                            animatableSize.animateTo(
                                IntSize(
                                    with(density) { GridSystem.cellToDp(widget.colSpan).toPx().roundToInt() },
                                    with(density) { GridSystem.cellToDp(widget.rowSpan).toPx().roundToInt() }
                                ),
                                spring(GridSystem.SNAP_DAMPING, GridSystem.SNAP_STIFFNESS)
                            )
                        }

                        // US2 DEEP FIX: Calculations must be relative to gesture START state to avoid feedback jitter
                        val visualX = if (isBeingDragged) (gestureStartStates[widget.id]?.first?.x ?: animatableOffset.value.x).toFloat() + dragOffset!!.x 
                                      else animatableOffset.value.x.toFloat()
                        val visualY = if (isBeingDragged) (gestureStartStates[widget.id]?.first?.y ?: animatableOffset.value.y).toFloat() + dragOffset!!.y 
                                      else animatableOffset.value.y.toFloat()

                        val visualWidth = if (isBeingResized) (gestureStartStates[widget.id]?.second?.width ?: animatableSize.value.width).toFloat() + resizeOffset!!.x 
                                          else animatableSize.value.width.toFloat()
                        val visualHeight = if (isBeingResized) (gestureStartStates[widget.id]?.second?.height ?: animatableSize.value.height).toFloat() + resizeOffset!!.y 
                                           else animatableSize.value.height.toFloat()

                        if (isBeingDragged) {
                            val (ghostCol, ghostRow) = calculateSnapCells(visualX, visualY, widget.colSpan, widget.rowSpan, density)
                            WidgetContainer(
                                backgroundColor = null,
                                isEditMode = true,
                                modifier = Modifier
                                    .size(
                                        width = GridSystem.cellToDp(widget.colSpan),
                                        height = GridSystem.cellToDp(widget.rowSpan)
                                    )
                                    .offset {
                                        IntOffset(
                                            with(density) { GridSystem.cellToDp(ghostCol).toPx() }.roundToInt(),
                                            with(density) { GridSystem.cellToDp(ghostRow).toPx() }.roundToInt()
                                        )
                                    }
                                    .semantics { invisibleToUser() }
                            ) {}
                        }

                        if (isBeingResized) {
                            val (ghostColSpan, ghostRowSpan) = calculateSnapSize(visualWidth, visualHeight, widget.column, widget.row, density)
                            WidgetContainer(
                                backgroundColor = null,
                                isEditMode = true,
                                modifier = Modifier
                                    .size(
                                        width = GridSystem.cellToDp(ghostColSpan),
                                        height = GridSystem.cellToDp(ghostRowSpan)
                                    )
                                    .offset {
                                        IntOffset(
                                            with(density) { GridSystem.cellToDp(widget.column).toPx().roundToInt() },
                                            with(density) { GridSystem.cellToDp(widget.row).toPx().roundToInt() }
                                        )
                                    }
                                    .semantics { invisibleToUser() }
                            ) {}
                        }

                        val currentOnDragEnd by rememberUpdatedState {
                            scope.launch {
                                val startState = gestureStartStates[widget.id]
                                val latestVisualX = (startState?.first?.x ?: animatableOffset.value.x) + (draggingOffsets[widget.id]?.x ?: 0f)
                                val latestVisualY = (startState?.first?.y ?: animatableOffset.value.y) + (draggingOffsets[widget.id]?.y ?: 0f)
                                
                                val (finalCol, finalRow) = calculateSnapCells(latestVisualX, latestVisualY, widget.colSpan, widget.rowSpan, density)
                                
                                val targetPixels = IntOffset(
                                    with(density) { GridSystem.cellToDp(finalCol).toPx().roundToInt() },
                                    with(density) { GridSystem.cellToDp(finalRow).toPx().roundToInt() }
                                )
                                
                                viewModel.updateWidgetPosition(widget.id, finalCol, finalRow)
                                animatableOffset.snapTo(targetPixels)
                                draggingOffsets.remove(widget.id)
                                gestureStartStates.remove(widget.id)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(
                                    width = with(density) { visualWidth.toDp() },
                                    height = with(density) { visualHeight.toDp() }
                                )
                                .offset {
                                    IntOffset(visualX.roundToInt(), visualY.roundToInt())
                                }
                                .zIndex(widget.zOrder.toFloat() + if (isBeingDragged || isBeingResized) 10000f else 0f)
                        ) {
                            val containerColor = resolvedColorLong ?: if (widget.type == WidgetType.BUTTON) {
                                com.example.hmi.core.ui.theme.Primary.value.toLong()
                            } else null

                            WidgetContainer(
                                backgroundColor = containerColor,
                                isEditMode = isEditMode,
                                textColorOverride = widget.textColorOverride,
                                showOutline = widget.showOutline,
                                moveModifier = if (isEditMode) {
                                    Modifier.pointerInput(widget.id) {
                                        detectDragGestures(
                                            onDragStart = { 
                                                gestureStartStates[widget.id] = animatableOffset.value to animatableSize.value
                                                draggingOffsets[widget.id] = Offset.Zero 
                                            },
                                            onDragEnd = { currentOnDragEnd() },
                                            onDragCancel = { 
                                                draggingOffsets.remove(widget.id)
                                                gestureStartStates.remove(widget.id)
                                            }
                                        ) { change, amount ->
                                            // US2 DEEP FIX: EXCLUDE BOTTOM-RIGHT 40dp FROM MOVE GESTURE
                                            val touchX = change.position.x
                                            val touchY = change.position.y
                                            val width = size.width
                                            val height = size.height
                                            
                                            if (touchX < width - 40.dp.toPx() || touchY < height - 40.dp.toPx()) {
                                                change.consume()
                                                draggingOffsets[widget.id] = (draggingOffsets[widget.id] ?: Offset.Zero) + amount
                                            }
                                        }
                                    }
                                } else Modifier,
                                onResize = { amount ->
                                    if (gestureStartStates[widget.id] == null) {
                                        gestureStartStates[widget.id] = animatableOffset.value to animatableSize.value
                                    }
                                    resizingOffsets[widget.id] = (resizingOffsets[widget.id] ?: Offset.Zero) + amount
                                },
                                onResizeEnd = {
                                    scope.launch {
                                        val startState = gestureStartStates[widget.id]
                                        val latestVisualWidth = (startState?.second?.width ?: animatableSize.value.width) + (resizingOffsets[widget.id]?.x ?: 0f)
                                        val latestVisualHeight = (startState?.second?.height ?: animatableSize.value.height) + (resizingOffsets[widget.id]?.y ?: 0f)
                                        
                                        val (finalColSpan, finalRowSpan) = calculateSnapSize(latestVisualWidth, latestVisualHeight, widget.column, widget.row, density)
                                        
                                        val targetSize = IntSize(
                                            with(density) { GridSystem.cellToDp(finalColSpan).toPx().roundToInt() },
                                            with(density) { GridSystem.cellToDp(finalRowSpan).toPx().roundToInt() }
                                        )
                                        
                                        viewModel.updateWidgetSize(widget.id, finalColSpan, finalRowSpan)
                                        animatableSize.snapTo(targetSize)
                                        resizingOffsets.remove(widget.id)
                                        gestureStartStates.remove(widget.id)
                                    }
                                },
                                onEditClick = { editingWidget = widget }
                            ) {
                                when (widget.type) {
                                    WidgetType.BUTTON -> {
                                        ButtonWidget(
                                            label = resolvedLabel,
                                            onClick = { viewModel.onButtonPress(widget.tagAddress) },
                                            backgroundColor = resolvedColorLong,
                                            textColor = widget.textColor,
                                            labelFontSizeMultiplier = widget.labelFontSizeMultiplier,
                                            hapticFeedbackEnabled = dashboardLayout.hapticFeedbackEnabled,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    WidgetType.SLIDER -> {
                                        SliderWidget(
                                            label = resolvedLabel,
                                            value = currentValue,
                                            onValueChange = { viewModel.onSliderChange(widget.tagAddress, it) },
                                            valueRange = (widget.minValue ?: 0f)..(widget.maxValue ?: 100f),
                                            backgroundColor = resolvedColorLong,
                                            labelFontSizeMultiplier = widget.labelFontSizeMultiplier,
                                            metricFontSizeMultiplier = widget.metricFontSizeMultiplier,
                                            units = widget.units,
                                            modifier = Modifier.fillMaxSize().padding(8.dp)
                                        )
                                    }
                                    WidgetType.GAUGE -> {
                                        val pulseState = when (widget.alarmState) {
                                            com.example.hmi.data.AlarmState.Normal -> com.example.hmi.core.ui.components.PulseState.NORMAL
                                            com.example.hmi.data.AlarmState.Unacknowledged -> com.example.hmi.core.ui.components.PulseState.UNACKNOWLEDGED
                                            com.example.hmi.data.AlarmState.Acknowledged -> com.example.hmi.core.ui.components.PulseState.ACKNOWLEDGED
                                        }
                                        GaugeWidget(
                                            label = resolvedLabel,
                                            value = currentValue,
                                            minValue = widget.minValue ?: 0f,
                                            maxValue = widget.maxValue ?: 100f,
                                            backgroundColor = resolvedColorLong,
                                            labelFontSizeMultiplier = widget.labelFontSizeMultiplier,
                                            metricFontSizeMultiplier = widget.metricFontSizeMultiplier,
                                            targetTicks = widget.targetTicks,
                                            arcSweep = widget.arcSweep,
                                            colorZones = widget.colorZones,
                                            needleColor = widget.needleColor,
                                            isNeedleDynamic = widget.isNeedleDynamic,
                                            units = widget.units,
                                            pulseState = pulseState,
                                            onAcknowledgeAlarm = { viewModel.acknowledgeAlarm(widget.tagAddress) },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
    }
}
