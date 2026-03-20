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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.widgets.ColorUtils

/**
 * A uniform container for all HMI widgets.
 * Rugged functionalism: 0dp corners, 2px bezels. No header row.
 * Includes a tactile 32x32dp Resize Handle in Edit Mode (BUG-001).
 */
@Composable
fun WidgetContainer(
    backgroundColor: Long?,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    alpha: Float = 1f,
    textColorOverride: String? = null,
    moveModifier: Modifier = Modifier, // Added to handle movement independently
    onResize: (Offset) -> Unit = {},
    onResizeEnd: () -> Unit = {},
    onEditClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val baseBg = backgroundColor?.let { ColorUtils.toColor(it) } ?: StitchTheme.tokens.surfaceContainerLow
    val bg = baseBg.copy(alpha = alpha)

    val contentColor = when (textColorOverride) {
        "BLACK" -> Color.Black
        "WHITE" -> Color.White
        else -> ColorUtils.getIndustrialContrastColor(baseBg)
    }.copy(alpha = alpha)

    val shape = RectangleShape // Strictly 0dp

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxSize().then(moveModifier), // Apply move gesture here
            color = bg,
            border = BorderStroke(2.dp, StitchTheme.tokens.statusAmber.copy(alpha = 0.5f)), // 2px Bezel
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
                                        onDragEnd = { onResizeEnd() },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            onResize(dragAmount)
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
