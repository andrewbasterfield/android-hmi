package com.example.hmi.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.widgets.ColorUtils

val TrackBackgroundColorKey = SemanticsPropertyKey<Color>("TrackBackgroundColor")
var SemanticsPropertyReceiver.trackBackgroundColor by TrackBackgroundColorKey

/**
 * A uniform container for all HMI widgets.
 * Rugged functionalism: 4px corners (via Theme).
 * In Run Mode: No border (relying on color shifts per DESIGN.md).
 * In Edit Mode: 2px Amber Bezel to indicate manipulation zone.
 */
@Composable
fun WidgetContainer(
    backgroundColor: Long?,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    alpha: Float = 1f,
    textColorOverride: String? = null,
    showOutline: Boolean = false,
    moveModifier: Modifier = Modifier, // Added to handle movement independently
    onResize: (Offset) -> Unit = {},
    onResizeEnd: () -> Unit = {},
    onEditClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val baseBg = backgroundColor?.let { ColorUtils.toColor(it) }

    // FIX: Check for null/default BEFORE alpha copy to avoid Transparent becoming Black
    val containerBg = if (baseBg == null) {
        MaterialTheme.colorScheme.background
    } else {
        baseBg.copy(alpha = alpha)
    }

    // US2 FIX: Use rememberUpdatedState to prevent pointerInput restart during drag
    val currentOnResize by rememberUpdatedState(onResize)
    val currentOnResizeEnd by rememberUpdatedState(onResizeEnd)

    val contentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> ColorUtils.getIndustrialContrastColor(baseBg ?: MaterialTheme.colorScheme.background)
    }.copy(alpha = alpha)

    val shape = MaterialTheme.shapes.small

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(moveModifier)
                .semantics { trackBackgroundColor = containerBg },
            color = containerBg,
            // Border: Edit Mode uses amber, Run Mode can optionally show content-colored outline
            border = when {
                isEditMode -> BorderStroke(2.dp, StitchTheme.tokens.statusAmber.copy(alpha = 0.5f))
                showOutline -> BorderStroke(2.dp, contentColor.copy(alpha = 0.3f))
                else -> null
            },
            shape = shape
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                    
                    if (isEditMode) {
                        // Edit/Settings Button
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(40.dp)
                                .zIndex(2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Edit",
                                tint = contentColor.copy(alpha = 0.5f)
                            )
                        }

                        // Tactile Resize Handle (BUG-001, FR-012)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = { currentOnResizeEnd() },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            currentOnResize(dragAmount)
                                        }
                                    )
                                }
                                .zIndex(3f)
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 2.dp.toPx()
                                val color = contentColor.copy(alpha = 0.6f)
                                
                                // Rugged diagonal stripes pattern for the handle
                                val path = Path().apply {
                                    moveTo(size.width, size.height - 8.dp.toPx())
                                    lineTo(size.width - 8.dp.toPx(), size.height)
                                    
                                    moveTo(size.width, size.height - 16.dp.toPx())
                                    lineTo(size.width - 16.dp.toPx(), size.height)
                                    
                                    moveTo(size.width, size.height - 24.dp.toPx())
                                    lineTo(size.width - 24.dp.toPx(), size.height)
                                }
                                drawPath(
                                    path = path,
                                    color = color,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
