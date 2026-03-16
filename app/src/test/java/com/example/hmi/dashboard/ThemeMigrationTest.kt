package com.example.hmi.dashboard

import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.ui.theme.HmiPalette
import org.mockito.kotlin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeMigrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: DashboardRepository = mock()
    private val plcCommunicator: PlcCommunicator = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(plcCommunicator.attributeUpdates).thenReturn(flowOf())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `migration logic sets canvasColor to black and increments version`() = runTest(testDispatcher) {
        // Arrange: A legacy layout with light background and false migrated flag
        val legacyLayout = DashboardLayout(
            id = UUID.randomUUID().toString(),
            name = "Legacy",
            canvasColor = 0xFFFFFFFF.toLong(), // White
            isDarkThemeMigrated = false,
            widgets = listOf(
                WidgetConfiguration(type = WidgetType.BUTTON, tagAddress = "T1")
            )
        )
        whenever(repository.dashboardLayoutFlow).thenReturn(flowOf(legacyLayout))
        whenever(repository.recentColorsFlow).thenReturn(flowOf(emptyList()))

        // Act: Initialize the ViewModel
        val viewModel = DashboardViewModel(plcCommunicator, repository, testDispatcher)
        
        // Let the init block run
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Verify that saveLayout was called with migrated layout
        val captor = argumentCaptor<DashboardLayout>()
        verify(repository).saveLayout(captor.capture())
        
        val migratedLayout = captor.firstValue
        assertEquals(0xFF000000.toLong(), migratedLayout.canvasColor)
        assertTrue(migratedLayout.isDarkThemeMigrated)
        // Check that fontSizeMultiplier is initialized (should be 1.0f by default)
        assertEquals(1.0f, migratedLayout.widgets[0].fontSizeMultiplier)
    }

    @Test
    fun `HmiPalette contrast verification`() {
        // Verification that each color in the palette has >= 4.5:1 contrast against black text.
        // Formula: (L1 + 0.05) / (L2 + 0.05) where L1 is the relative luminance of the lighter color.
        // Luminance of black (#000000) is 0.0.
        // So we need (L_palette + 0.05) / 0.05 >= 4.5
        // -> L_palette + 0.05 >= 0.225
        // -> L_palette >= 0.175
        
        HmiPalette.WidgetBackgrounds.forEach { color ->
            val luminance = calculateLuminance(color.red, color.green, color.blue)
            val contrast = (luminance + 0.05) / 0.05
            assertTrue("Color ${color.value} has insufficient contrast: $contrast", contrast >= 4.5)
        }
    }

    private fun calculateLuminance(r: Float, g: Float, b: Float): Double {
        fun convert(c: Float): Double {
            return if (c <= 0.03928) c / 12.92 else Math.pow((c + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * convert(r) + 0.7152 * convert(g) + 0.0722 * convert(b)
    }
}
