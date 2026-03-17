package com.example.hmi.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ShapeLogicTest {

    @Test
    fun getShapeForSize_returnsSmall_for_1x1() {
        val shape = WidgetShapes.getShapeForSize(1, 1)
        assertEquals(IndustrialShape.Small, shape)
    }

    @Test
    fun getShapeForSize_returnsStandard_for_2x1() {
        val shape = WidgetShapes.getShapeForSize(2, 1)
        assertEquals(IndustrialShape.Standard, shape)
    }

    @Test
    fun getShapeForSize_returnsStandard_for_2x2() {
        val shape = WidgetShapes.getShapeForSize(2, 2)
        assertEquals(IndustrialShape.Standard, shape)
    }
}
