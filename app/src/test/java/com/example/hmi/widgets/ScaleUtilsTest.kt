package com.example.hmi.widgets

import org.junit.Assert.assertEquals
import org.junit.Test

class ScaleUtilsTest {

    @Test
    fun `calculateNiceStep returns logical intervals`() {
        // Range 0-100, target 6 ticks -> raw step 16.6 -> magnitude 10, residual 1.66 -> nice residual 2 -> step 20
        assertEquals(20f, ScaleUtils.calculateNiceStep(100f, 6))
        
        // Range 0-10, target 6 ticks -> raw step 1.66 -> magnitude 1, residual 1.66 -> nice residual 2 -> step 2
        assertEquals(2f, ScaleUtils.calculateNiceStep(10f, 6))
        
        // Range 0-1, target 6 ticks -> raw step 0.166 -> magnitude 0.1, residual 1.66 -> nice residual 2 -> step 0.2
        assertEquals(0.2f, ScaleUtils.calculateNiceStep(1f, 6))
        
        // Range 0-500, target 6 ticks -> raw step 83.3 -> magnitude 10, residual 8.33 -> nice residual 10 -> step 100
        assertEquals(100f, ScaleUtils.calculateNiceStep(500f, 6))
    }

    @Test
    fun `generateTicks handles various ranges`() {
        val step = ScaleUtils.calculateNiceStep(100f, 6) // 20
        val ticks = ScaleUtils.generateTicks(0f, 100f, step)
        assertEquals(listOf(0f, 20f, 40f, 60f, 80f, 100f), ticks)
        
        val step2 = ScaleUtils.calculateNiceStep(10f, 6) // 2
        val ticks2 = ScaleUtils.generateTicks(0f, 10f, step2)
        assertEquals(listOf(0f, 2f, 4f, 6f, 8f, 10f), ticks2)
    }
}
