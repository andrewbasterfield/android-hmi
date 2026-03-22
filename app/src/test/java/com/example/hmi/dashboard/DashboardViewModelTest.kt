package com.example.hmi.dashboard

import com.example.hmi.data.DashboardLayout
import com.example.hmi.data.DashboardRepository
import com.example.hmi.data.WidgetConfiguration
import com.example.hmi.data.WidgetType
import com.example.hmi.protocol.ConnectionState
import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.PlcValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val plcCommunicator = mock<PlcCommunicator> {
        on { connectionState } doReturn MutableStateFlow(ConnectionState.DISCONNECTED)
        on { attributeUpdates } doReturn emptyFlow()
        on { observeTag(any()) } doReturn emptyFlow()
    }

    private lateinit var repository: DashboardRepository
    private lateinit var viewModel: DashboardViewModel
    private val layoutFlow = MutableStateFlow(DashboardLayout(name = "Initial"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = mock<DashboardRepository> {
            on { dashboardLayoutFlow } doReturn layoutFlow
            on { recentColorsFlow } doReturn MutableStateFlow(emptyList())
        }
        
        // Mock saveLayout to update our flow
        repository.stub {
            onBlocking { saveLayout(any()) } doAnswer { invocation ->
                layoutFlow.value = invocation.getArgument(0)
                Unit
            }
        }

        viewModel = DashboardViewModel(plcCommunicator, repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test(timeout = 5000)
    fun `exportLayoutToJson returns valid JSON string`() = testScope.runTest {
        val layout = DashboardLayout(name = "Export Test", widgets = listOf(
            WidgetConfiguration(type = WidgetType.BUTTON, tagAddress = "TEST_TAG")
        ))
        layoutFlow.value = layout
        
        runCurrent()

        val json = viewModel.exportLayoutToJson()
        assertTrue(json.contains("Export Test"))
        assertTrue(json.contains("TEST_TAG"))
        assertTrue(json.contains("BUTTON"))
    }

    @Test(timeout = 5000)
    fun `importLayoutFromJson updates layout on valid JSON`() = testScope.runTest {
        val json = """
            {
              "id": "test-id",
              "name": "Imported Layout",
              "isKineticCockpitMigrated": true,
              "isDarkThemeMigrated": true,
              "hapticFeedbackEnabled": true,
              "widgets": [
                {
                  "id": "w1",
                  "type": "GAUGE",
                  "tagAddress": "GAUGE_TAG",
                  "column": 0,
                  "row": 0,
                  "colSpan": 1,
                  "rowSpan": 1,
                  "labelFontSizeMultiplier": 1.0,
                  "metricFontSizeMultiplier": 1.0,
                  "targetTicks": 6,
                  "arcSweep": 180.0,
                  "colorZones": [],
                  "isNeedleDynamic": false,
                  "alarmState": "Normal",
                  "showOutline": false,
                  "zOrder": 0
                }
              ]
            }
        """.trimIndent()

        val results = mutableListOf<Result<Unit>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.importResult.collect { results.add(it) }
        }

        viewModel.importLayoutFromJson(json)
        runCurrent()

        assertTrue(results.isNotEmpty())
        val result = results.first()
        assertTrue(result.isSuccess)
        assertEquals("Imported Layout", viewModel.dashboardLayout.value.name)
        assertEquals(1, viewModel.dashboardLayout.value.widgets.size)
        assertEquals(WidgetType.GAUGE, viewModel.dashboardLayout.value.widgets[0].type)
    }

    @Test(timeout = 5000)
    fun `importLayoutFromJson returns error on invalid JSON`() = testScope.runTest {
        val json = "invalid json"

        val results = mutableListOf<Result<Unit>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.importResult.collect { results.add(it) }
        }

        viewModel.importLayoutFromJson(json)
        runCurrent()

        assertTrue(results.isNotEmpty())
        val result = results.first()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid JSON") == true)
    }

    @Test(timeout = 5000)
    fun `importLayoutFromJson returns error on empty name`() = testScope.runTest {
        val json = """
            {
              "name": "",
              "widgets": []
            }
        """.trimIndent()

        val results = mutableListOf<Result<Unit>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.importResult.collect { results.add(it) }
        }

        viewModel.importLayoutFromJson(json)
        runCurrent()

        assertTrue(results.isNotEmpty())
        val result = results.first()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("name cannot be blank") == true)
    }
}
