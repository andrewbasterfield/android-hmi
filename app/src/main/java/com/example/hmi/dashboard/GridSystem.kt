package com.example.hmi.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Constants and utility functions for the DPI-aware grid system.
 */
object GridSystem {
    val CELL_SIZE = 80.dp

    // Animation constants
    const val SNAP_STIFFNESS = Spring.StiffnessLow
    const val SNAP_DAMPING = Spring.DampingRatioMediumBouncy

    /**
     * Converts a DP value to the nearest grid cell index.
     */
    fun dpToCell(dp: Dp): Int {
        return (dp.value / CELL_SIZE.value).roundToInt()
    }

    /**
     * Converts a grid cell index to its corresponding DP coordinate.
     */
    fun cellToDp(cell: Int): Dp {
        return CELL_SIZE * cell
    }

    /**
     * Snaps a DP value to the nearest grid line.
     */
    fun snapToGrid(dp: Dp): Dp {
        return cellToDp(dpToCell(dp))
    }
}
