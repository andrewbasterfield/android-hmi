package com.example.hmi.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
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
            onDismiss = { showDashboardSettings = false },
            onConfirm = { name, color ->
                viewModel.updateDashboardSettings(name, color)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dashboardLayout.name) },
                actions = {
                    if (isEditMode) {
                        Button(onClick = { showDashboardSettings = true }) {
                            Icon(Icons.Default.Settings, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dashboard Settings")
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.toggleEditMode() }) {
                        Text(if (isEditMode) "Run Mode" else "Edit Mode")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onNavigateBack) {
                        Icon(Icons.Default.Link, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Connection")
                    }
                }
            )
        }
    ) { paddingValues ->
        val canvasColor = dashboardLayout.canvasColor?.let { Color(it.toInt()) } ?: MaterialTheme.colorScheme.background
        
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

                    val visualX = if (isBeingDragged) animatableOffset.value.x + dragOffset!!.x else animatableOffset.value.x.toFloat()
                    val visualY = if (isBeingDragged) animatableOffset.value.y + dragOffset!!.y else animatableOffset.value.y.toFloat()

                    val visualWidth = if (isBeingResized) animatableSize.value.width + resizeOffset!!.x else animatableSize.value.width.toFloat()
                    val visualHeight = if (isBeingResized) animatableSize.value.height + resizeOffset!!.y else animatableSize.value.height.toFloat()

                    if (isBeingDragged) {
                        val (ghostCol, ghostRow) = calculateSnapCells(visualX, visualY, widget.colSpan, widget.rowSpan, density)
                        WidgetContainer(
                            backgroundColor = widget.backgroundColor,
                            alpha = 0.3f,
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
                            backgroundColor = widget.backgroundColor,
                            alpha = 0.3f,
                            modifier = Modifier
                                .size(
                                    width = GridSystem.cellToDp(ghostColSpan),
                                    height = GridSystem.cellToDp(ghostRowSpan)
                                )
                                .offset {
                                    IntOffset(
                                        with(density) { GridSystem.cellToDp(widget.column).toPx() }.roundToInt(),
                                        with(density) { GridSystem.cellToDp(widget.row).toPx() }.roundToInt()
                                    )
                                }
                                .semantics { invisibleToUser() }
                        ) {}
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
                            .zIndex(if (isBeingDragged || isBeingResized) 1f else 0f)
                            .then(
                                if (isEditMode) {
                                    Modifier.pointerInput(widget.id) {
                                        detectDragGestures(
                                            onDragStart = { draggingOffsets[widget.id] = Offset.Zero },
                                            onDragEnd = {
                                                scope.launch {
                                                    val latestVisualX = animatableOffset.value.x + (draggingOffsets[widget.id]?.x ?: 0f)
                                                    val latestVisualY = animatableOffset.value.y + (draggingOffsets[widget.id]?.y ?: 0f)
                                                    
                                                    val (finalCol, finalRow) = calculateSnapCells(latestVisualX, latestVisualY, widget.colSpan, widget.rowSpan, density)
                                                    
                                                    // FIX: Snap directly to the final grid cell pixels immediately on release.
                                                    // This eliminates any "dead zone" where the widget is off-grid before the ViewModel updates.
                                                    val targetPixels = IntOffset(
                                                        with(density) { GridSystem.cellToDp(finalCol).toPx().roundToInt() },
                                                        with(density) { GridSystem.cellToDp(finalRow).toPx().roundToInt() }
                                                    )
                                                    
                                                    animatableOffset.snapTo(targetPixels)
                                                    draggingOffsets.remove(widget.id)
                                                    viewModel.updateWidgetPosition(widget.id, finalCol, finalRow)
                                                }
                                            },
                                            onDragCancel = { draggingOffsets.remove(widget.id) }
                                        ) { change, amount ->
                                            change.consume()
                                            draggingOffsets[widget.id] = (draggingOffsets[widget.id] ?: Offset.Zero) + amount
                                        }
                                    }
                                } else Modifier
                            )
                    ) {
                        WidgetContainer(
                            backgroundColor = resolvedColorLong,
                            isEditMode = isEditMode,
                            onResize = { amount ->
                                resizingOffsets[widget.id] = (resizingOffsets[widget.id] ?: Offset.Zero) + amount
                            },
                            onResizeEnd = {
                                scope.launch {
                                    val latestVisualWidth = animatableSize.value.width + (resizingOffsets[widget.id]?.x ?: 0f)
                                    val latestVisualHeight = animatableSize.value.height + (resizingOffsets[widget.id]?.y ?: 0f)
                                    
                                    val (finalColSpan, finalRowSpan) = calculateSnapSize(latestVisualWidth, latestVisualHeight, widget.column, widget.row, density)
                                    
                                    // FIX: Snap directly to the final grid cell size immediately on release.
                                    val targetSize = IntSize(
                                        with(density) { GridSystem.cellToDp(finalColSpan).toPx().roundToInt() },
                                        with(density) { GridSystem.cellToDp(finalRowSpan).toPx().roundToInt() }
                                    )
                                    
                                    animatableSize.snapTo(targetSize)
                                    resizingOffsets.remove(widget.id)
                                    viewModel.updateWidgetSize(widget.id, finalColSpan, finalRowSpan)
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
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                WidgetType.SLIDER -> {
                                    SliderWidget(
                                        label = resolvedLabel,
                                        value = currentValue,
                                        onValueChange = { viewModel.onSliderChange(widget.tagAddress, it) },
                                        valueRange = (widget.minValue ?: 0f)..(widget.maxValue ?: 100f),
                                        modifier = Modifier.fillMaxSize().padding(8.dp)
                                    )
                                }
                                WidgetType.GAUGE -> {
                                    GaugeWidget(
                                        label = resolvedLabel,
                                        value = currentValue,
                                        minValue = widget.minValue ?: 0f,
                                        maxValue = widget.maxValue ?: 100f,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
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
