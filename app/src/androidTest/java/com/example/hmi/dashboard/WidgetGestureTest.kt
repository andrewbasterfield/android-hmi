package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.hmi.TestActivity
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.widgets.ButtonWidget
import com.example.hmi.widgets.GaugeWidget
import com.example.hmi.core.ui.components.PulseState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import kotlin.math.roundToInt

@HiltAndroidTest
class WidgetGestureTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun draggingWidget_withButtonInside_shouldTriggerPositionUpdate() {
        var dragStarted = false
        var dragCompleted = false
        var positionUpdated = false
        val widget = WidgetConfiguration(
            id = "test-widget-button",
            type = WidgetType.BUTTON,
            column = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2,
            tagAddress = "TAG1",
            customLabel = "Drag Me Button"
        )

        composeTestRule.setContent {
            val draggingOffsets = remember { mutableStateMapOf<String, Offset>() }
            
            val isBeingDragged = draggingOffsets.containsKey(widget.id)
            val dragOffset = draggingOffsets[widget.id]
            val visualX = if (isBeingDragged) dragOffset!!.x else 0f
            val visualY = if (isBeingDragged) dragOffset!!.y else 0f

            val moveModifier = Modifier.pointerInput(widget.id) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isDownInResizeZone = down.position.x > size.width - 48.dp.toPx() && 
                                            down.position.y > size.height - 48.dp.toPx()
                    
                    if (!isDownInResizeZone) {
                        var dragObj: PointerInputChange?
                        do {
                            dragObj = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                change.consume()
                            }
                        } while (dragObj != null && !dragObj.isConsumed)

                        if (dragObj != null) {
                            dragStarted = true
                            draggingOffsets[widget.id] = Offset.Zero 
                            if (drag(dragObj.id) { change ->
                                change.consume()
                                draggingOffsets[widget.id] = draggingOffsets[widget.id]!! + change.positionChange()
                            }) {
                                dragCompleted = true
                                positionUpdated = true
                            }
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                WidgetContainer(
                    backgroundColor = null,
                    isEditMode = true,
                    modifier = Modifier
                        .size(160.dp)
                        .offset { IntOffset(visualX.roundToInt(), visualY.roundToInt()) }
                        .then(moveModifier),
                    onResize = {}
                ) {
                    ButtonWidget(
                        label = "Drag Me Button",
                        onClick = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drag Me Button").performTouchInput {
            down(center)
            moveBy(Offset(100f, 100f))
            up()
        }

        assert(dragStarted) { "Drag should have started (Button)" }
        assert(positionUpdated) { "Widget position should have been updated (Button)" }
    }

    @Test
    fun draggingWidget_withGaugeInside_shouldTriggerPositionUpdate() {
        var dragStarted = false
        var positionUpdated = false
        val widget = WidgetConfiguration(
            id = "test-widget-gauge",
            type = WidgetType.GAUGE,
            column = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2,
            tagAddress = "TAG2",
            customLabel = "Drag Me Gauge"
        )

        composeTestRule.setContent {
            val draggingOffsets = remember { mutableStateMapOf<String, Offset>() }
            
            val isBeingDragged = draggingOffsets.containsKey(widget.id)
            val dragOffset = draggingOffsets[widget.id]
            val visualX = if (isBeingDragged) dragOffset!!.x else 0f
            val visualY = if (isBeingDragged) dragOffset!!.y else 0f

            val moveModifier = Modifier.pointerInput(widget.id) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isDownInResizeZone = down.position.x > size.width - 48.dp.toPx() && 
                                            down.position.y > size.height - 48.dp.toPx()
                    
                    if (!isDownInResizeZone) {
                        var dragObj: PointerInputChange?
                        do {
                            dragObj = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                change.consume()
                            }
                        } while (dragObj != null && !dragObj.isConsumed)

                        if (dragObj != null) {
                            dragStarted = true
                            draggingOffsets[widget.id] = Offset.Zero 
                            if (drag(dragObj.id) { change ->
                                change.consume()
                                draggingOffsets[widget.id] = draggingOffsets[widget.id]!! + change.positionChange()
                            }) {
                                positionUpdated = true
                            }
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                WidgetContainer(
                    backgroundColor = null,
                    isEditMode = true,
                    modifier = Modifier
                        .size(160.dp)
                        .offset { IntOffset(visualX.roundToInt(), visualY.roundToInt()) }
                        .then(moveModifier),
                    onResize = {}
                ) {
                    GaugeWidget(
                        label = "Drag Me Gauge",
                        value = 50f,
                        minValue = 0f,
                        maxValue = 100f,
                        pulseState = PulseState.UNACKNOWLEDGED,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Drag Me Gauge").performTouchInput {
            down(center)
            moveBy(Offset(100f, 100f))
            up()
        }

        assert(dragStarted) { "Drag should have started (Gauge)" }
        assert(positionUpdated) { "Widget position should have been updated (Gauge)" }
    }

    @Test
    fun resizingWidget_shouldTriggerOnResize() {
        var resizeTriggered = false
        var dragStartedInTest = false
        val widgetId = "test-widget-resize"

        composeTestRule.setContent {
            val moveModifier = Modifier.pointerInput(widgetId) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isDownInResizeZone = down.position.x > size.width - 48.dp.toPx() && 
                                            down.position.y > size.height - 48.dp.toPx()
                    
                    if (!isDownInResizeZone) {
                        var dragObj: PointerInputChange?
                        do {
                            dragObj = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                change.consume()
                            }
                        } while (dragObj != null && !dragObj.isConsumed)

                        if (dragObj != null) {
                            dragStartedInTest = true
                            drag(dragObj.id) { change ->
                                change.consume()
                            }
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                WidgetContainer(
                    backgroundColor = null,
                    isEditMode = true,
                    modifier = Modifier
                        .size(160.dp)
                        .then(moveModifier),
                    onResize = { resizeTriggered = true }
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        // Perform swipe from bottom-right corner
        composeTestRule.onAllNodes(isRoot()).onFirst().performTouchInput {
            down(Offset(155.dp.toPx(), 155.dp.toPx()))
            moveBy(Offset(50f, 50f))
            up()
        }

        assert(resizeTriggered) { "Resize should have been triggered when dragging from the corner" }
        assert(!dragStartedInTest) { "Drag should NOT have been triggered when starting in resize zone" }
    }
}
