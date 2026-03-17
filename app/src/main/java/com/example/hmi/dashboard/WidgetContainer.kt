package com.example.hmi.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.hmi.ui.theme.WidgetShapes
import com.example.hmi.widgets.ColorUtils

/**
 * A uniform container for all HMI widgets.
 * Provides adaptive rounded edges, a contrasting border, and hybrid contrast support.
 */
@Composable
fun WidgetContainer(
    backgroundColor: Long?,
    modifier: Modifier = Modifier,
    colSpan: Int = 1,
    rowSpan: Int = 1,
    isEditMode: Boolean = false,
    alpha: Float = 1f,
    textColorOverride: String? = null,
    onResize: (Offset) -> Unit = {},
    onResizeEnd: () -> Unit = {},
    onEditClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    // FIX: Default to theme's Primary color instead of Surface to match Buttons and ColorPicker preview
    val baseBg = backgroundColor?.let { ColorUtils.toColor(it) } ?: MaterialTheme.colorScheme.primary
    val bg = baseBg.copy(alpha = alpha)

    // US2: Use Industrial Hybrid Contrast logic, respecting manual override if present
    val baseContentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> ColorUtils.getIndustrialContrastColor(baseBg)
    }
    val contentColor = baseContentColor.copy(alpha = alpha)

    // FR-006: Subtle 1dp border (15% white)
    val borderColor = Color.White.copy(alpha = 0.15f * alpha)

    // US1: Adaptive Rounded Corners
    val shape = WidgetShapes.getShapeForSize(colSpan, rowSpan)

    // Using a Box here to allow the ResizeHandle to overlap the edges without being clipped
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape), // Ensure content is clipped to rounded corners
            color = bg,
            border = BorderStroke(1.dp, borderColor),
            shape = shape // US3: Border follows the rounded path
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                    
                    if (isEditMode) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(48.dp)
                                .padding(4.dp)
                                .zIndex(2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Edit widget",
                                modifier = Modifier.size(24.dp),
                                tint = contentColor.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // MOVED OUTSIDE: ResizeHandle is now a direct child of the outer Box
        // This prevents the container from clipping its 48dp touch target
        if (isEditMode) {
            ResizeHandle(
                onResize = onResize, 
                onResizeEnd = onResizeEnd,
                contentColor = contentColor
            )
        }
    }
}

@Composable
private fun BoxScope.ResizeHandle(
    onResize: (Offset) -> Unit,
    onResizeEnd: () -> Unit,
    contentColor: Color
) {
    // Increased invisible touch area to 48dp while keeping 24dp visual size
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = 8.dp, y = 8.dp) // Offset slightly so half the target is outside for easier "edge" grabbing
            .size(48.dp) 
            .zIndex(5f) // Top-most priority
            .semantics { contentDescription = "Resize widget" }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onResizeEnd() }
                ) { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Visual indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(contentColor.copy(alpha = 0.1f), RectangleShape)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val color = contentColor.copy(alpha = 0.5f)
                // Three diagonal lines in the corner
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.7f, size.height),
                    end = Offset(size.width, size.height * 0.7f),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.4f, size.height),
                    end = Offset(size.width, size.height * 0.4f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
