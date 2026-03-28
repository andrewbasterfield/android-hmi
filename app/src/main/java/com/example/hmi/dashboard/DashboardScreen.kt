package com.example.hmi.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlin.math.abs
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.liveRegion
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
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.widgets.ButtonWidget
import com.example.hmi.widgets.GaugeWidget
import com.example.hmi.widgets.SliderWidget
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val dashboardLayout by viewModel.dashboardLayout.collectAsState()
    val tagValuesState = viewModel.tagValues.collectAsState()
    val sessionOverridesState = viewModel.sessionOverrides.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val globalStatus by viewModel.globalStatus.collectAsState()
    
    var srAnnouncement by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.announcements.collect { message ->
            srAnnouncement = message
        }
    }

    var editingWidget by remember { mutableStateOf<WidgetConfiguration?>(null) }
    var showDashboardSettings by remember { mutableStateOf(false) }
    var showTransferHub by remember { mutableStateOf(false) }

    if (showTransferHub) {
        val connectionViewModel: com.example.hmi.connection.ConnectionViewModel = hiltViewModel()
        SystemTransferDialog(
            dashboardViewModel = viewModel,
            connectionViewModel = connectionViewModel,
            onDismiss = { showTransferHub = false }
        )
    }
    
    val draggingOffsets = remember { mutableStateMapOf<String, Offset>() }
    val resizingOffsets = remember { mutableStateMapOf<String, Offset>() }
    val gestureStartStates = remember { mutableStateMapOf<String, Pair<IntOffset, IntSize>>() }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val viewportCols = floor(configuration.screenWidthDp / GridSystem.CELL_SIZE.value).toInt().coerceAtLeast(1)
    val viewportRows = floor(configuration.screenHeightDp / GridSystem.CELL_SIZE.value).toInt().coerceAtLeast(1)

    // Manual 2D paging - avoids nested pager state conflicts
    var currentLogicalPageX by remember { mutableIntStateOf(0) }
    var currentLogicalPageY by remember { mutableIntStateOf(0) }

    // Track swipe gesture accumulation
    var swipeAccumulatorX by remember { mutableFloatStateOf(0f) }
    var swipeAccumulatorY by remember { mutableFloatStateOf(0f) }

    // Animated page offset for smooth transitions
    val animatedPageOffsetX = remember { Animatable(0f) }
    val animatedPageOffsetY = remember { Animatable(0f) }

    // Animate page transitions
    LaunchedEffect(currentLogicalPageX, viewportCols) {
        val targetOffset = with(density) { GridSystem.cellToDp(currentLogicalPageX * viewportCols).toPx() }
        animatedPageOffsetX.animateTo(targetOffset, spring(0.8f, 300f))
    }

    LaunchedEffect(currentLogicalPageY, viewportRows) {
        val targetOffset = with(density) { GridSystem.cellToDp(currentLogicalPageY * viewportRows).toPx() }
        animatedPageOffsetY.animateTo(targetOffset, spring(0.8f, 300f))
    }

    LaunchedEffect(dashboardLayout.widgets) {
        val currentTags = dashboardLayout.widgets.map { it.tagAddress }.toSet()
        viewModel.syncTagObservations(currentTags)
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
            },
            onDuplicate = {
                editingWidget?.let { 
                    viewModel.duplicateWidget(it.id)
                }
                editingWidget = null
            }
        )
    }

    if (showDashboardSettings) {
        DashboardSettingsDialog(
            initialName = dashboardLayout.name,
            initialCanvasColor = dashboardLayout.canvasColor,
            initialHapticEnabled = dashboardLayout.hapticFeedbackEnabled,
            initialOrientationMode = dashboardLayout.orientationMode,
            onDismiss = { showDashboardSettings = false },
            onConfirm = { name, color, hapticEnabled, orientationMode ->
                viewModel.updateDashboardSettings(name, color, hapticEnabled, orientationMode)
                showDashboardSettings = false
            }
        )
    }

    var showAddWidgetDialog by remember { mutableStateOf(false) }

    if (showAddWidgetDialog) {
        WidgetConfigDialog(
            onDismiss = { showAddWidgetDialog = false },
            onConfirm = { widget ->
                // Place new widget on current page
                val col = currentLogicalPageX * viewportCols
                val row = currentLogicalPageY * viewportRows

                viewModel.addWidget(widget.copy(column = col, row = row))
                showAddWidgetDialog = false
            }
        )
    }

    EmergencyHUD(status = globalStatus) {
        // Hidden element for screen reader announcements
        if (srAnnouncement.isNotEmpty()) {
            Text(
                text = srAnnouncement,
                modifier = Modifier
                    .size(1.dp)
                    .semantics { 
                        liveRegion = LiveRegionMode.Polite 
                        invisibleToUser()
                    }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${dashboardLayout.name} [$currentLogicalPageX, $currentLogicalPageY]") },
                    actions = {
                        if (isEditMode) {
                            IconButton(onClick = { showTransferHub = true }) {
                                Icon(Icons.Default.CloudSync, "System Transfer Center")
                            }
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Connection status banner
                if (connectionState != ConnectionState.CONNECTED) {
                    val (bannerColor, bannerText) = when (connectionState) {
                        ConnectionState.RECONNECTING -> Color(0xFFFF9800) to "Reconnecting..."
                        ConnectionState.CONNECTING -> Color(0xFF2196F3) to "Connecting..."
                        ConnectionState.ERROR -> Color(0xFFF44336) to "Connection Lost"
                        ConnectionState.DISCONNECTED -> Color(0xFFF44336) to "Disconnected"
                        else -> Color(0xFFF44336) to "Not Connected"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bannerColor)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = bannerText,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Manual 2D Paging Implementation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(canvasColor)
                        .pointerInput(isEditMode, draggingOffsets.isEmpty()) {
                            // Only enable swiping when not dragging a widget
                            if (isEditMode && draggingOffsets.isNotEmpty()) return@pointerInput

                            detectDragGestures(
                                onDragStart = {
                                    swipeAccumulatorX = 0f
                                    swipeAccumulatorY = 0f
                                },
                                onDragEnd = {
                                    val viewportWidthPx = GridSystem.cellToDp(viewportCols).toPx()
                                    val viewportHeightPx = GridSystem.cellToDp(viewportRows).toPx()
                                    val thresholdX = viewportWidthPx * 0.2f
                                    val thresholdY = viewportHeightPx * 0.2f

                                    // Helper to check if any widget overlaps a given page
                                    fun pageHasWidgetOverlap(targetPageX: Int, targetPageY: Int): Boolean {
                                        val pageColStart = targetPageX * viewportCols
                                        val pageColEnd = pageColStart + viewportCols
                                        val pageRowStart = targetPageY * viewportRows
                                        val pageRowEnd = pageRowStart + viewportRows

                                        return dashboardLayout.widgets.any { widget ->
                                            val widgetColEnd = widget.column + widget.colSpan
                                            val widgetRowEnd = widget.row + widget.rowSpan
                                            widget.column < pageColEnd && widgetColEnd > pageColStart &&
                                            widget.row < pageRowEnd && widgetRowEnd > pageRowStart
                                        }
                                    }

                                    // Determine which axis had more movement
                                    if (abs(swipeAccumulatorX) > abs(swipeAccumulatorY)) {
                                        // Horizontal swipe dominates
                                        val targetPageX = when {
                                            swipeAccumulatorX > thresholdX -> currentLogicalPageX - 1
                                            swipeAccumulatorX < -thresholdX -> currentLogicalPageX + 1
                                            else -> currentLogicalPageX
                                        }
                                        if (targetPageX != currentLogicalPageX &&
                                            targetPageX >= -GridSystem.PAGE_OFFSET &&
                                            targetPageX <= GridSystem.PAGE_OFFSET &&
                                            pageHasWidgetOverlap(targetPageX, currentLogicalPageY)) {
                                            currentLogicalPageX = targetPageX
                                        }
                                    } else {
                                        // Vertical swipe dominates
                                        val targetPageY = when {
                                            swipeAccumulatorY > thresholdY -> currentLogicalPageY - 1
                                            swipeAccumulatorY < -thresholdY -> currentLogicalPageY + 1
                                            else -> currentLogicalPageY
                                        }
                                        if (targetPageY != currentLogicalPageY &&
                                            targetPageY >= -GridSystem.PAGE_OFFSET &&
                                            targetPageY <= GridSystem.PAGE_OFFSET &&
                                            pageHasWidgetOverlap(currentLogicalPageX, targetPageY)) {
                                            currentLogicalPageY = targetPageY
                                        }
                                    }

                                    swipeAccumulatorX = 0f
                                    swipeAccumulatorY = 0f
                                },
                                onDragCancel = {
                                    swipeAccumulatorX = 0f
                                    swipeAccumulatorY = 0f
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                swipeAccumulatorX += dragAmount.x
                                swipeAccumulatorY += dragAmount.y
                            }
                        }
                ) {
                    // Calculate visible area based on animated offset (for smooth transitions)
                    val viewportWidthPx = with(density) { GridSystem.cellToDp(viewportCols).toPx() }
                    val viewportHeightPx = with(density) { GridSystem.cellToDp(viewportRows).toPx() }

                    // Render widgets that intersect the visible area (based on animated offset)
                    dashboardLayout.widgets.filter { widget ->
                        val widgetLeftPx = with(density) { GridSystem.cellToDp(widget.column).toPx() }
                        val widgetTopPx = with(density) { GridSystem.cellToDp(widget.row).toPx() }
                        val widgetRightPx = with(density) { GridSystem.cellToDp(widget.column + widget.colSpan).toPx() }
                        val widgetBottomPx = with(density) { GridSystem.cellToDp(widget.row + widget.rowSpan).toPx() }

                        // Check if widget intersects the visible viewport
                        val intersectsX = widgetRightPx > animatedPageOffsetX.value &&
                                         widgetLeftPx < animatedPageOffsetX.value + viewportWidthPx
                        val intersectsY = widgetBottomPx > animatedPageOffsetY.value &&
                                         widgetTopPx < animatedPageOffsetY.value + viewportHeightPx
                        intersectsX && intersectsY
                    }.forEach { widget ->
                        key(widget.id) {
                            WidgetRenderingNode(
                                widget = widget,
                                pageX = currentLogicalPageX,
                                pageY = currentLogicalPageY,
                                animatedPageOffsetX = animatedPageOffsetX.value,
                                animatedPageOffsetY = animatedPageOffsetY.value,
                                viewportCols = viewportCols,
                                viewportRows = viewportRows,
                                tagValuesState = tagValuesState,
                                sessionOverridesState = sessionOverridesState,
                                isEditMode = isEditMode,
                                hapticEnabled = dashboardLayout.hapticFeedbackEnabled,
                                draggingOffsets = draggingOffsets,
                                resizingOffsets = resizingOffsets,
                                gestureStartStates = gestureStartStates,
                                viewModel = viewModel,
                                onEditClick = { editingWidget = it },
                                onNavigateToPage = { newPageX, newPageY ->
                                    currentLogicalPageX = newPageX
                                    currentLogicalPageY = newPageY
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun WidgetRenderingNode(
    widget: WidgetConfiguration,
    pageX: Int,
    pageY: Int,
    animatedPageOffsetX: Float,
    animatedPageOffsetY: Float,
    viewportCols: Int,
    viewportRows: Int,
    tagValuesState: State<Map<String, Float>>,
    sessionOverridesState: State<Map<String, Map<String, String>>>,
    isEditMode: Boolean,
    hapticEnabled: Boolean,
    draggingOffsets: MutableMap<String, Offset>,
    resizingOffsets: MutableMap<String, Offset>,
    gestureStartStates: MutableMap<String, Pair<IntOffset, IntSize>>,
    viewModel: DashboardViewModel,
    onEditClick: (WidgetConfiguration) -> Unit,
    onNavigateToPage: (Int, Int) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Use derivedStateOf to isolate recomposition - only recompose when THIS widget's tag value changes
    val tagAddress = widget.tagAddress.orEmpty()
    val currentValue by remember(tagAddress) {
        derivedStateOf { tagValuesState.value[tagAddress] ?: 0f }
    }
    val tagOverrides by remember(tagAddress) {
        derivedStateOf { sessionOverridesState.value[tagAddress] }
    }
    val resolvedLabel = tagOverrides?.get("label") ?: widget.customLabel ?: tagAddress
    val resolvedColorLong = tagOverrides?.get("color")?.let { 
        com.example.hmi.widgets.ColorUtils.parseHexColor(it) 
    } ?: widget.backgroundColor

    val dragOffset = draggingOffsets[widget.id]
    val resizeOffset = resizingOffsets[widget.id]
    val isBeingDragged = dragOffset != null
    val isBeingResized = resizeOffset != null

    // Use animated page offsets for smooth transitions
    val pageOffsetX = animatedPageOffsetX
    val pageOffsetY = animatedPageOffsetY

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

    val visualX = if (isBeingDragged) (gestureStartStates[widget.id]?.first?.x ?: animatableOffset.value.x).toFloat() + dragOffset!!.x 
                  else animatableOffset.value.x.toFloat()
    val visualY = if (isBeingDragged) (gestureStartStates[widget.id]?.first?.y ?: animatableOffset.value.y).toFloat() + dragOffset!!.y 
                  else animatableOffset.value.y.toFloat()

    val visualWidth = if (isBeingResized) (gestureStartStates[widget.id]?.second?.width ?: animatableSize.value.width).toFloat() + resizeOffset!!.x 
                      else animatableSize.value.width.toFloat()
    val visualHeight = if (isBeingResized) (gestureStartStates[widget.id]?.second?.height ?: animatableSize.value.width).toFloat() + resizeOffset!!.y 
                       else animatableSize.value.height.toFloat()

    // Calculate snap ghost position
    val snapCol = with(density) { GridSystem.dpToCell(visualX.toDp()) }
    val snapRow = with(density) { GridSystem.dpToCell(visualY.toDp()) }
    val snapColSpan = with(density) { GridSystem.dpToCell(visualWidth.toDp()) }.coerceAtLeast(1)
    val snapRowSpan = with(density) { GridSystem.dpToCell(visualHeight.toDp()) }.coerceAtLeast(1)

    val snapX = with(density) { GridSystem.cellToDp(snapCol).toPx() }
    val snapY = with(density) { GridSystem.cellToDp(snapRow).toPx() }
    val snapWidth = with(density) { GridSystem.cellToDp(snapColSpan).toPx() }
    val snapHeight = with(density) { GridSystem.cellToDp(snapRowSpan).toPx() }

    val currentOnDragEnd by rememberUpdatedState {

        scope.launch {
            val startState = gestureStartStates[widget.id]
            val latestVisualX = (startState?.first?.x ?: animatableOffset.value.x) + (draggingOffsets[widget.id]?.x ?: 0f)
            val latestVisualY = (startState?.first?.y ?: animatableOffset.value.y) + (draggingOffsets[widget.id]?.y ?: 0f)

            // Snap logic
            val finalCol = with(density) { GridSystem.dpToCell(latestVisualX.toDp()) }
            val finalRow = with(density) { GridSystem.dpToCell(latestVisualY.toDp()) }

            val targetPixels = IntOffset(
                with(density) { GridSystem.cellToDp(finalCol).toPx().roundToInt() },
                with(density) { GridSystem.cellToDp(finalRow).toPx().roundToInt() }
            )

            viewModel.updateWidgetPosition(widget.id, finalCol, finalRow)
            animatableOffset.snapTo(targetPixels)
            draggingOffsets.remove(widget.id)
            gestureStartStates.remove(widget.id)

            // Navigate to the page where the widget landed
            val newPageX = finalCol / viewportCols
            val newPageY = finalRow / viewportRows
            if (newPageX != pageX || newPageY != pageY) {
                onNavigateToPage(newPageX, newPageY)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(
                width = with(density) { maxOf(visualWidth, snapWidth).toDp() },
                height = with(density) { maxOf(visualHeight, snapHeight).toDp() }
            )
            .offset {
                // Outer box is positioned at the min(visual, snap) to contain both
                IntOffset(
                    (minOf(visualX, snapX) - pageOffsetX).roundToInt(),
                    (minOf(visualY, snapY) - pageOffsetY).roundToInt()
                )
            }
            .zIndex(widget.zOrder.toFloat() + if (isBeingDragged || isBeingResized) 10000f else 0f)
    ) {
        val containerColor = resolvedColorLong ?: if (widget.type == WidgetType.BUTTON) {
            com.example.hmi.core.ui.theme.Primary.value.toLong()
        } else null

        // Render snap ghost (translucent background)
        if (isBeingDragged || isBeingResized) {
            Box(
                modifier = Modifier
                    .size(
                        width = with(density) { snapWidth.toDp() },
                        height = with(density) { snapHeight.toDp() }
                    )
                    .offset {
                        IntOffset(
                            (snapX - minOf(visualX, snapX)).roundToInt(),
                            (snapY - minOf(visualY, snapY)).roundToInt()
                        )
                    }
            ) {
                WidgetContainer(
                    backgroundColor = containerColor,
                    isEditMode = true,
                    showControls = false,
                    alpha = 0.2f,
                    showOutline = true,
                    onEditClick = {},
                    onResize = {},
                    onResizeEnd = {}
                ) {}
            }
        }

        // Render active widget
        Box(
            modifier = Modifier
                .size(
                    width = with(density) { visualWidth.toDp() },
                    height = with(density) { visualHeight.toDp() }
                )
                .offset {
                    IntOffset(
                        (visualX - minOf(visualX, snapX)).roundToInt(),
                        (visualY - minOf(visualY, snapY)).roundToInt()
                    )
                }
        ) {
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
                    
                    val finalColSpan = with(density) { GridSystem.dpToCell(latestVisualWidth.toDp()) }.coerceAtLeast(1)
                    val finalRowSpan = with(density) { GridSystem.dpToCell(latestVisualHeight.toDp()) }.coerceAtLeast(1)
                    
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
            onEditClick = { onEditClick(widget) }
        ) {
            // Widget content (Button, Slider, Gauge)

            when (widget.type) {
                WidgetType.BUTTON -> {
                    ButtonWidget(
                        label = resolvedLabel,
                        onClick = { if (widget.interactionType == com.example.hmi.data.InteractionType.LATCHING) viewModel.onButtonPress(widget) },
                        onPress = { if (widget.interactionType == com.example.hmi.data.InteractionType.MOMENTARY) viewModel.onButtonPress(widget) },
                        onRelease = { if (widget.interactionType == com.example.hmi.data.InteractionType.MOMENTARY) viewModel.onButtonRelease(widget) },
                        backgroundColor = resolvedColorLong,
                        textColor = widget.textColor,
                        labelFontSizeMultiplier = widget.labelFontSizeMultiplier,
                        hapticFeedbackEnabled = hapticEnabled,
                        isChecked = if (widget.interactionType != com.example.hmi.data.InteractionType.MOMENTARY) currentValue > 0.5f else false,
                        isInverted = widget.isInverted,
                        isInteractive = widget.interactionType != com.example.hmi.data.InteractionType.INDICATOR,
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
                        pointerColor = widget.pointerColor,
                        isPointerDynamic = widget.isPointerDynamic,
                        gaugeStyle = widget.gaugeStyle,
                        units = widget.units,
                        pulseState = when (widget.alarmState) {
                            com.example.hmi.data.AlarmState.Normal -> com.example.hmi.core.ui.components.PulseState.NORMAL
                            com.example.hmi.data.AlarmState.Unacknowledged -> com.example.hmi.core.ui.components.PulseState.UNACKNOWLEDGED
                            com.example.hmi.data.AlarmState.Acknowledged -> com.example.hmi.core.ui.components.PulseState.ACKNOWLEDGED
                        },
                        onAcknowledgeAlarm = { viewModel.acknowledgeAlarm(widget.tagAddress) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
}
