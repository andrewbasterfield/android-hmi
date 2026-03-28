package com.example.hmi.dashboard

import kotlin.math.floor

data class GridPosition(
    val pageX: Int,
    val pageY: Int,
    val localCol: Int,
    val localRow: Int
)

data class GridBounds(
    val minPageX: Int,
    val maxPageX: Int,
    val minPageY: Int,
    val maxPageY: Int
) {
    val totalPagesX: Int get() = maxPageX - minPageX + 1
    val totalPagesY: Int get() = maxPageY - minPageY + 1

    /**
     * Expands the bounds by a fixed number of pages in all directions.
     */
    fun expand(padding: Int): GridBounds {
        return GridBounds(
            minPageX - padding,
            maxPageX + padding,
            minPageY - padding,
            maxPageY + padding
        )
    }
}

object GridReflowLogic {

    fun getPosition(
        globalCol: Int,
        globalRow: Int,
        viewportCols: Int,
        viewportRows: Int
    ): GridPosition {
        // Use floorDiv/floorMod for correct handling of negative coordinates
        val pageX = Math.floorDiv(globalCol, viewportCols)
        val pageY = Math.floorDiv(globalRow, viewportRows)
        val localCol = Math.floorMod(globalCol, viewportCols)
        val localRow = Math.floorMod(globalRow, viewportRows)
        
        return GridPosition(pageX, pageY, localCol, localRow)
    }

    @Deprecated(
        message = "No longer needed for paging - use fixed page range via GridSystem.TOTAL_PAGES instead",
        level = DeprecationLevel.WARNING
    )
    fun getBounds(
        widgets: List<com.example.hmi.data.WidgetConfiguration>,
        viewportCols: Int,
        viewportRows: Int
    ): GridBounds {
        if (widgets.isEmpty()) {
            return GridBounds(0, 0, 0, 0)
        }

        var minPX = 0
        var maxPageX = 0
        var minPY = 0
        var maxPageY = 0

        widgets.forEach { widget ->
            // Check top-left corner
            val topLeft = getPosition(widget.column, widget.row, viewportCols, viewportRows)
            // Check bottom-right corner (exclusive)
            val bottomRight = getPosition(widget.column + widget.colSpan - 1, widget.row + widget.rowSpan - 1, viewportCols, viewportRows)

            minPX = minOf(minPX, topLeft.pageX, bottomRight.pageX)
            maxPageX = maxOf(maxPageX, topLeft.pageX, bottomRight.pageX)
            minPY = minOf(minPY, topLeft.pageY, bottomRight.pageY)
            maxPageY = maxOf(maxPageY, topLeft.pageY, bottomRight.pageY)
        }

        return GridBounds(minPX, maxPageX, minPY, maxPageY)
    }
}
