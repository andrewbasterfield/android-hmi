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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        prettyPrint = true
    }

    private val plcCommunicator = mock<PlcCommunicator> {
        on { connectionState } doReturn MutableStateFlow(ConnectionState.DISCONNECTED)
        on { attributeUpdates } doReturn emptyFlow()
        on { observeTag(any(), anyOrNull()) } doReturn emptyFlow()
    }

    private lateinit var repository: DashboardRepository
    private lateinit var transferManager: com.example.hmi.data.ConfigTransferManager
    private lateinit var viewModel: DashboardViewModel
    private val layoutFlow = MutableStateFlow(DashboardLayout(name = "Initial"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = mock<DashboardRepository> {
            on { dashboardLayoutFlow } doReturn layoutFlow
            on { recentColorsFlow } doReturn MutableStateFlow(emptyList())
            on { systemProfilesFlow } doReturn MutableStateFlow(emptyList())
            on { activeSystemProfileIdFlow } doReturn MutableStateFlow(null)
            on { savedProfilesFlow } doReturn MutableStateFlow(emptyList())
            on { savedLayoutsFlow } doReturn MutableStateFlow(emptyList())
            on { connectionProfileFlow } doReturn MutableStateFlow(null)
        }

        transferManager = mock()
        val migrationManager = com.example.hmi.data.LayoutMigrationManager()
        
        // Mock saveLayout to update our flow
        repository.stub {
            onBlocking { saveLayout(any()) } doAnswer { invocation ->
                layoutFlow.value = invocation.getArgument(0)
                Unit
            }
        }

        viewModel = DashboardViewModel(plcCommunicator, repository, transferManager, migrationManager, json, testDispatcher)
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
        val layoutJson = json.encodeToString(layout)
        viewModel.importLayoutFromJson(layoutJson)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val jsonResult = viewModel.exportLayoutToJson()
        assertTrue(jsonResult.contains("Export Test"))
        assertTrue(jsonResult.contains("TEST_TAG"))
        assertTrue(jsonResult.contains("BUTTON"))
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

    @Test
    fun `duplicateWidget creates copy with offset and new ID`() = testScope.runTest {
        val sourceWidget = WidgetConfiguration(
            id = "source-id",
            type = WidgetType.BUTTON,
            column = 2,
            row = 3,
            zOrder = 5,
            tagAddress = "TAG_1"
        )
        layoutFlow.value = DashboardLayout(widgets = listOf(sourceWidget))
        runCurrent() // Ensure ViewModel collects the initial layout
        
        viewModel.duplicateWidget("source-id")
        runCurrent()

        val widgets = viewModel.dashboardLayout.value.widgets
        assertEquals(2, widgets.size)
        
        val duplicate = widgets.find { it.id != "source-id" }
        assertTrue("Duplicate should exist", duplicate != null)
        assertEquals(sourceWidget.type, duplicate?.type)
        assertEquals(sourceWidget.tagAddress, duplicate?.tagAddress)
        assertEquals(sourceWidget.column + 1, duplicate?.column)
        assertEquals(sourceWidget.row + 1, duplicate?.row)
        assertTrue("Duplicate should have higher zOrder", (duplicate?.zOrder ?: 0) > sourceWidget.zOrder)
        assertTrue("Duplicate should have new UUID", duplicate?.id?.length ?: 0 > 0 && duplicate?.id != "source-id")
    }

    @Test
    fun `addWidget sets zOrder to max plus 1`() = testScope.runTest {
        val existingWidget = WidgetConfiguration(
            id = "existing",
            zOrder = 10
        )
        layoutFlow.value = DashboardLayout(widgets = listOf(existingWidget))
        runCurrent()
        
        val newWidget = WidgetConfiguration(id = "new", type = WidgetType.GAUGE)
        viewModel.addWidget(newWidget)
        runCurrent()
        
        val widgets = viewModel.dashboardLayout.value.widgets
        assertEquals(2, widgets.size)
        val added = widgets.find { it.id == "new" }
        assertEquals(11, added?.zOrder)
    }

    @Test(timeout = 5000)
    fun `onButtonPress on a MOMENTARY widget with empty trueValues falls back instead of crashing`() = testScope.runTest {
        val widget = WidgetConfiguration(
            id = "w1",
            type = WidgetType.BUTTON,
            tagAddress = "TAG1",
            interactionType = com.example.hmi.data.InteractionType.MOMENTARY,
            trueValues = emptyList(),
            falseValues = emptyList()
        )

        viewModel.onButtonPress(widget)
        runCurrent()

        verify(plcCommunicator).writeTag("TAG1", PlcValue.StringValue("true"), false)
    }

    @Test(timeout = 5000)
    fun `onButtonRelease on a MOMENTARY widget with empty falseValues falls back instead of crashing`() = testScope.runTest {
        val widget = WidgetConfiguration(
            id = "w1",
            type = WidgetType.BUTTON,
            tagAddress = "TAG1",
            interactionType = com.example.hmi.data.InteractionType.MOMENTARY,
            trueValues = emptyList(),
            falseValues = emptyList()
        )

        viewModel.onButtonRelease(widget)
        runCurrent()

        verify(plcCommunicator).writeTag("TAG1", PlcValue.StringValue("false"), false)
    }

    @Test(timeout = 5000)
    fun `onButtonPress on a LATCHING widget with empty trueValues and falseValues falls back instead of crashing`() = testScope.runTest {
        val widget = WidgetConfiguration(
            id = "w1",
            type = WidgetType.BUTTON,
            tagAddress = "TAG1",
            interactionType = com.example.hmi.data.InteractionType.LATCHING,
            trueValues = emptyList(),
            falseValues = emptyList()
        )

        viewModel.onButtonPress(widget)
        runCurrent()

        verify(plcCommunicator).writeTag("TAG1", PlcValue.StringValue("true"), true)
    }

    @Test(timeout = 5000)
    fun `two widgets on the same tag with different json paths do not overwrite each other`() = testScope.runTest {
        val tempFlow = MutableSharedFlow<PlcValue>(replay = 1)
        val pressureFlow = MutableSharedFlow<PlcValue>(replay = 1)
        plcCommunicator.stub {
            on { observeTag("SENSOR", "temp") } doReturn tempFlow
            on { observeTag("SENSOR", "pressure") } doReturn pressureFlow
        }

        viewModel.observeTag("SENSOR", "temp")
        viewModel.observeTag("SENSOR", "pressure")
        runCurrent()

        tempFlow.emit(PlcValue.FloatValue(11f))
        runCurrent()
        pressureFlow.emit(PlcValue.FloatValue(22f))
        runCurrent()

        assertEquals(11f, viewModel.tagValues.value["SENSOR" to "temp"])
        assertEquals(22f, viewModel.tagValues.value["SENSOR" to "pressure"])
    }

    @Test(timeout = 5000)
    fun `a slow save for an older edit does not revert a faster newer edit`() = testScope.runTest {
        val widget = WidgetConfiguration(id = "w1", type = WidgetType.GAUGE, tagAddress = "T1", column = 0, row = 0, colSpan = 1, rowSpan = 1)
        layoutFlow.value = DashboardLayout(isKineticCockpitMigrated = true, isDarkThemeMigrated = true, widgets = listOf(widget))
        runCurrent()

        // The first save (from the drag) is slower than the second (from the resize
        // that follows immediately after), simulating saves completing out of the
        // order they were issued in.
        var saveCount = 0
        repository.stub {
            onBlocking { saveLayout(any()) } doSuspendableAnswer { invocation ->
                val layout = invocation.getArgument<DashboardLayout>(0)
                saveCount++
                if (saveCount == 1) {
                    delay(100)
                }
                layoutFlow.value = layout
            }
        }

        viewModel.updateWidgetPosition("w1", column = 5, row = 5)
        runCurrent() // let the save queue pick up the drag and start its (slow) save
        viewModel.updateWidgetSize("w1", colSpan = 3, rowSpan = 2)
        advanceUntilIdle() // let the slow drag save finish, then the resize save

        val result = viewModel.dashboardLayout.value.widgets.first()
        assertEquals(5, result.column)
        assertEquals(5, result.row)
        assertEquals(3, result.colSpan)
        assertEquals(2, result.rowSpan)
    }

    @Test(timeout = 10000)
    fun `concurrent tag updates from real threads do not lose writes`() = runBlocking {
        val tags = (0 until 16).map { "TAG_$it" }

        coroutineScope {
            tags.forEach { tag ->
                launch(Dispatchers.Default) {
                    repeat(100) { i ->
                        viewModel.onSliderChange(tag, null, i.toFloat())
                    }
                }
            }
        }

        val expectedKeys = tags.map { it to null }
        assertEquals(tags.size, viewModel.tagValues.value.keys.count { it in expectedKeys })
        tags.forEach { tag ->
            assertEquals(99f, viewModel.tagValues.value[tag to null])
        }
    }
}
