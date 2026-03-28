package com.example.hmi.dashboard

import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import org.junit.Assert.assertEquals
import org.junit.Test

class GridReflowLogicTest {

    @Test
    fun `getPosition maps positive coordinates correctly`() {
        val viewportCols = 8
        val viewportRows = 10

        // Page 0,0
        val pos1 = GridReflowLogic.getPosition(0, 0, viewportCols, viewportRows)
        assertEquals(0, pos1.pageX)
        assertEquals(0, pos1.pageY)
        assertEquals(0, pos1.localCol)
        assertEquals(0, pos1.localRow)

        // Page 1,0
        val pos2 = GridReflowLogic.getPosition(8, 5, viewportCols, viewportRows)
        assertEquals(1, pos2.pageX)
        assertEquals(0, pos2.pageY)
        assertEquals(0, pos2.localCol)
        assertEquals(5, pos2.localRow)

        // Page 1,1
        val pos3 = GridReflowLogic.getPosition(15, 15, viewportCols, viewportRows)
        assertEquals(1, pos3.pageX)
        assertEquals(1, pos3.pageY)
        assertEquals(7, pos3.localCol)
        assertEquals(5, pos3.localRow)
    }

    @Test
    fun `getPosition maps negative coordinates correctly`() {
        val viewportCols = 8
        val viewportRows = 10

        // Page -1,0 (Col -1 is local 7 on page -1)
        val pos1 = GridReflowLogic.getPosition(-1, 0, viewportCols, viewportRows)
        assertEquals(-1, pos1.pageX)
        assertEquals(0, pos1.pageY)
        assertEquals(7, pos1.localCol)
        assertEquals(0, pos1.localRow)

        // Page -1,-1
        val pos2 = GridReflowLogic.getPosition(-8, -10, viewportCols, viewportRows)
        assertEquals(-1, pos2.pageX)
        assertEquals(-1, pos2.pageY)
        assertEquals(0, pos2.localCol)
        assertEquals(0, pos2.localRow)

        // Page -2, -1
        val pos3 = GridReflowLogic.getPosition(-9, -1, viewportCols, viewportRows)
        assertEquals(-2, pos3.pageX)
        assertEquals(-1, pos3.pageY)
        assertEquals(7, pos3.localCol)
        assertEquals(9, pos3.localRow)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `getBounds calculates correct span for multiple widgets`() {
        val viewportCols = 8
        val viewportRows = 10

        // Create minimal WidgetConfiguration objects at specified positions
        val widgets = listOf(
            WidgetConfiguration(id = "w1", type = WidgetType.GAUGE, tagAddress = "t1", column = 0, row = 0, colSpan = 1, rowSpan = 1),
            WidgetConfiguration(id = "w2", type = WidgetType.GAUGE, tagAddress = "t2", column = 10, row = 5, colSpan = 1, rowSpan = 1),
            WidgetConfiguration(id = "w3", type = WidgetType.GAUGE, tagAddress = "t3", column = -5, row = 15, colSpan = 1, rowSpan = 1),
            WidgetConfiguration(id = "w4", type = WidgetType.GAUGE, tagAddress = "t4", column = 20, row = -5, colSpan = 1, rowSpan = 1)
        )

        val bounds = GridReflowLogic.getBounds(widgets, viewportCols, viewportRows)
        assertEquals(-1, bounds.minPageX)
        assertEquals(2, bounds.maxPageX)
        assertEquals(-1, bounds.minPageY)
        assertEquals(1, bounds.maxPageY)
        assertEquals(4, bounds.totalPagesX)
        assertEquals(3, bounds.totalPagesY)
    }
}
