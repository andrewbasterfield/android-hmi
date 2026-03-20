package com.example.hmi.widgets

import org.junit.Assert.assertEquals
import org.junit.Test

class ScaleUtilsTest {

    @Test
    fun calculateNiceStep_withVaryingTargetTicks() {
        val range = 100f
        
        // Target 2 ticks: expects step ~50 (3 ticks total: 0, 50, 100)
        assertEquals(50f, ScaleUtils.calculateNiceStep(range, 2))
        
        // Target 6 ticks (default): expects step ~20 (6 ticks total: 0, 20, 40, 60, 80, 100)
        assertEquals(20f, ScaleUtils.calculateNiceStep(range, 6))
        
        // Target 10 ticks: expects step ~10 (11 ticks total: 0, 10, ... 100)
        assertEquals(10f, ScaleUtils.calculateNiceStep(range, 10))
        
        // Target 20 ticks: expects step ~5 (21 ticks total: 0, 5, ... 100)
        assertEquals(5f, ScaleUtils.calculateNiceStep(range, 20))
    }

    @Test
    fun calculateNiceStep_smallRange() {
        val range = 1f
        
        // Target 5 ticks: expects step ~0.2
        assertEquals(0.2f, ScaleUtils.calculateNiceStep(range, 5))
    }

    @Test
    fun generateTicks_matchesExpectedCount() {
        val min = 0f
        val max = 100f
        
        // Test Target 20 -> Step 5 -> 21 ticks
        val step20 = ScaleUtils.calculateNiceStep(max - min, 20)
        val ticks20 = ScaleUtils.generateTicks(min, max, step20)
        assertEquals(21, ticks20.size)
        assertEquals(0f, ticks20.first())
        assertEquals(100f, ticks20.last())

        // Test Target 2 -> Step 50 -> 3 ticks
        val step2 = ScaleUtils.calculateNiceStep(max - min, 2)
        val ticks2 = ScaleUtils.generateTicks(min, max, step2)
        assertEquals(3, ticks2.size)
        assertEquals(0f, ticks2.first())
        assertEquals(100f, ticks2.last())
    }
}
