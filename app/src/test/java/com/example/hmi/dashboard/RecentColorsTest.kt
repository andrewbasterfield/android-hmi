package com.example.hmi.dashboard

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentColorsTest {

    private fun updateRecentColors(current: List<Long>, newColor: Long): List<Long> {
        val list = current.toMutableList()
        list.remove(newColor) // Move to front if exists
        list.add(0, newColor)
        return list.take(8) // Limit to 8
    }

    @Test
    fun `adding new color moves it to front`() {
        val initial = listOf(1L, 2L, 3L)
        val updated = updateRecentColors(initial, 4L)
        assertEquals(listOf(4L, 1L, 2L, 3L), updated)
    }

    @Test
    fun `adding existing color moves it to front and doesn't duplicate`() {
        val initial = listOf(1L, 2L, 3L)
        val updated = updateRecentColors(initial, 2L)
        assertEquals(listOf(2L, 1L, 3L), updated)
    }

    @Test
    fun `limit is enforced at 8 colors`() {
        val initial = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)
        val updated = updateRecentColors(initial, 9L)
        assertEquals(8, updated.size)
        assertEquals(9L, updated[0])
        assertEquals(7L, updated[7])
    }
}
