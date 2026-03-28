package com.example.hmi.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.core.ui.theme.MonospaceFont
import com.example.hmi.core.ui.utils.SiFormatter
import com.example.hmi.data.WidgetOrientation

val TrackBackgroundColorKey = SemanticsPropertyKey<Color>("TrackBackgroundColor")
var SemanticsPropertyReceiver.trackBackgroundColor by TrackBackgroundColorKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidget(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    @Suppress("UNUSED_PARAMETER") backgroundColor: Long? = null, // Handled by WidgetContainer
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    units: String? = null,
    orientation: WidgetOrientation = WidgetOrientation.HORIZONTAL
) {
    if (orientation == WidgetOrientation.VERTICAL) {
        VerticalSliderWidget(
            label = label,
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            valueRange = valueRange,
            labelFontSizeMultiplier = labelFontSizeMultiplier,
            metricFontSizeMultiplier = metricFontSizeMultiplier,
            units = units
        )
    } else {
        HorizontalSliderWidget(
            label = label,
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            valueRange = valueRange,
            labelFontSizeMultiplier = labelFontSizeMultiplier,
            metricFontSizeMultiplier = metricFontSizeMultiplier,
            units = units
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorizontalSliderWidget(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    units: String? = null
) {
    // Rely on LocalContentColor provided by WidgetContainer
    val contentColor = LocalContentColor.current
    
    val metricText = SiFormatter.formatMetric(value, units)

    Box(
        modifier = modifier.padding(8.dp)
    ) {
        // Label at top
        if (labelFontSizeMultiplier > 0.0f) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize * labelFontSizeMultiplier,
                    letterSpacing = 1.25.sp
                ),
                color = contentColor.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Slider in center
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center).semantics { contentDescription = "Slider for $label" },
            colors = SliderDefaults.colors(
                thumbColor = contentColor,
                activeTrackColor = contentColor,
                inactiveTrackColor = contentColor.copy(alpha = 0.2f)
            ),
            thumb = {
                // Rugged Rectangle Thumb
                Box(
                    modifier = Modifier
                        .size(24.dp, 32.dp)
                        .background(contentColor, RectangleShape)
                )
            },
            track = { _ ->
                val layoutBgColor = MaterialTheme.colorScheme.background
                val fraction = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)

                // Track with ticks at ends
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { trackBackgroundColor = layoutBgColor },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start tick
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(16.dp)
                            .background(contentColor.copy(alpha = 0.8f), RectangleShape)
                    )

                    // Track area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                    ) {
                        // Inactive track (remaining portion) - grey like gauge arc
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(contentColor.copy(alpha = 0.3f), RectangleShape)
                        )
                        // Active track (filled portion)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .background(contentColor, RectangleShape)
                        )
                    }

                    // End tick
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(16.dp)
                            .background(contentColor.copy(alpha = 0.8f), RectangleShape)
                    )
                }
            }
        )

        // Metric row pinned to bottom
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (labelFontSizeMultiplier > 0.0f) {
                Text(
                    text = "%.0f".format(valueRange.start),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonospaceFont,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                    ),
                    color = contentColor.copy(alpha = 0.6f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (metricFontSizeMultiplier > 0.0f) {
                Text(
                    text = metricText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                    ),
                    color = contentColor
                )
            }

            if (labelFontSizeMultiplier > 0.0f) {
                Text(
                    text = "%.0f".format(valueRange.endInclusive),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonospaceFont,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                    ),
                    color = contentColor.copy(alpha = 0.6f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun VerticalSliderWidget(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    labelFontSizeMultiplier: Float = 1.0f,
    metricFontSizeMultiplier: Float = 1.0f,
    units: String? = null
) {
    val contentColor = LocalContentColor.current

    // HIGH-FREQUENCY LOCAL STATE
    var localValue by remember { mutableFloatStateOf(value) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Sync local state when external value changes (e.g. from PLC)
    // CRITICAL: We ignore external updates while dragging to prevent "fighting" and jumpiness
    LaunchedEffect(value) {
        if (!isDragging) {
            localValue = value
        }
    }

    val metricText = SiFormatter.formatMetric(localValue, units)
    var trackHeightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val thumbHeightDp = 24.dp
    val thumbHeightPx = with(density) { thumbHeightDp.toPx() }

    val range = valueRange.endInclusive - valueRange.start
    val fraction = ((localValue - valueRange.start) / range).coerceIn(0f, 1f)

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label at Top
        if (labelFontSizeMultiplier > 0.0f) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize * labelFontSizeMultiplier,
                    letterSpacing = 1.25.sp
                ),
                color = contentColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Max Label (Top of Track)
        if (labelFontSizeMultiplier > 0.0f) {
            Text(
                text = "%.0f".format(valueRange.endInclusive),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = MonospaceFont,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                ),
                color = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Vertical Track Area (Center, fills remaining space)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .width(32.dp) // Width to contain the thumb
                .semantics { contentDescription = "Slider for $label" }
                .onGloballyPositioned { coordinates ->
                    trackHeightPx = coordinates.size.height.toFloat()
                }
                .draggable(
                    state = rememberDraggableState { deltaY ->
                        // The actual travel distance is the track height minus the thumb height
                        val travelPx = trackHeightPx - thumbHeightPx
                        if (travelPx > 0) {
                            // deltaY is negative when moving UP
                            val valueDelta = -(deltaY / travelPx) * range
                            localValue = (localValue + valueDelta).coerceIn(valueRange)
                            onValueChange(localValue)
                        }
                    },
                    orientation = Orientation.Vertical,
                    onDragStarted = { isDragging = true },
                    onDragStopped = { isDragging = false }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Track Background (8dp wide)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(contentColor.copy(alpha = 0.3f), RectangleShape)
            )

            // Active Track (Filled portion from bottom)
            // We adjust the fill to match the thumb's travel center
            val travelPx = trackHeightPx - thumbHeightPx
            val activeHeightPx = (fraction * travelPx) + (thumbHeightPx / 2)
            
            Box(
                modifier = Modifier
                    .fillMaxHeight(if (trackHeightPx > 0) (activeHeightPx / trackHeightPx).coerceIn(0f, 1f) else 0f)
                    .width(8.dp)
                    .background(contentColor, RectangleShape)
                    .align(Alignment.BottomCenter)
            )

            // End Ticks (Top and Bottom)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(contentColor.copy(alpha = 0.8f), RectangleShape)
                        .align(Alignment.TopCenter)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(contentColor.copy(alpha = 0.8f), RectangleShape)
                        .align(Alignment.BottomCenter)
                )
            }

            // Thumb (32dp wide, 24dp high)
            // Positioned so the thumb stays entirely within the Box
            Box(
                modifier = Modifier
                    .offset(y = with(density) { -(fraction * (trackHeightPx - thumbHeightPx)).toDp() })
                    .size(32.dp, thumbHeightDp)
                    .background(contentColor, RectangleShape)
                    .align(Alignment.BottomCenter)
                    .semantics { contentDescription = "Vertical Slider Thumb" }
            )
        }

        // Min Label (Bottom of Track)
        if (labelFontSizeMultiplier > 0.0f) {
            Text(
                text = "%.0f".format(valueRange.start),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = MonospaceFont,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize * labelFontSizeMultiplier
                ),
                color = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        // Current Value Metric (Bottom)
        if (metricFontSizeMultiplier > 0.0f) {
            Text(
                text = metricText,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = MaterialTheme.typography.displayMedium.fontSize * metricFontSizeMultiplier
                ),
                color = contentColor
            )
        }
    }
}
