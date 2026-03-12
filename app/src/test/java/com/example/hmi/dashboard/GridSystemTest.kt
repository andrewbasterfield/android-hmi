package com.example.hmi.dashboard

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class GridSystemTest {

    @Test
    fun dpToCell_convertsCorrectly() {
        assertEquals(0, GridSystem.dpToCell(0.dp))
        assertEquals(0, GridSystem.dpToCell(39.dp))
        assertEquals(1, GridSystem.dpToCell(40.dp))
        assertEquals(1, GridSystem.dpToCell(80.dp))
        assertEquals(1, GridSystem.dpToCell(119.dp))
        assertEquals(2, GridSystem.dpToCell(120.dp))
    }

    @Test
    fun cellToDp_convertsCorrectly() {
        assertEquals(0.dp, GridSystem.cellToDp(0))
        assertEquals(80.dp, GridSystem.cellToDp(1))
        assertEquals(160.dp, GridSystem.cellToDp(2))
    }

    @Test
    fun snapToGrid_snapsToNearestCell() {
        assertEquals(0.dp, GridSystem.snapToGrid(39.dp))
        assertEquals(80.dp, GridSystem.snapToGrid(40.dp))
        assertEquals(80.dp, GridSystem.snapToGrid(80.dp))
        assertEquals(160.dp, GridSystem.snapToGrid(121.dp))
    }
}
