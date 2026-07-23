package com.example.hmi.data

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import com.example.hmi.protocol.PlcConnectionProfile
import com.example.hmi.protocol.Protocol
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigTransferManagerTest {

    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        prettyPrint = true
    }
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: DashboardRepository
    private lateinit var transferManager: ConfigTransferManager
    private val schemaString = """
    {
      "${"$"}schema": "http://json-schema.org/draft-07/schema#",
      "type": "object",
      "properties": {
        "version": { "type": "integer" },
        "layout": { "type": "object" },
        "profiles": { "type": "array" }
      },
      "required": ["version"]
    }
    """.trimIndent()

    @Before
    fun setup() {
        val assetManager = mock<AssetManager> {
            on { open("schemas/full-backup.schema.json") } doReturn ByteArrayInputStream(schemaString.toByteArray())
        }
        contentResolver = mock()
        context = mock<Context> {
            on { assets } doReturn assetManager
            on { contentResolver } doReturn contentResolver
        }
        repository = mock()
        transferManager = ConfigTransferManager(context, json, repository)
    }

    @Test
    fun `validateJson returns true for valid backup`() = runTest {
        val backup = FullBackupPackage(
            version = 1,
            layout = DashboardLayout(
                id = "test-id",
                name = "Test",
                canvasColor = null,
                widgets = emptyList(),
                isDarkThemeMigrated = true,
                isKineticCockpitMigrated = true,
                hapticFeedbackEnabled = true,
                orientationMode = OrientationMode.AUTO
            ),
            profiles = emptyList()
        )
        val jsonStr = json.encodeToString(backup)
        
        val result = transferManager.validateJson(jsonStr)
        
        assertTrue(result)
    }

    @Test
    fun `validateJson returns false for invalid json`() = runTest {
        val invalidJson = "{ \"invalid\": \"json\" }"
        val result = transferManager.validateJson(invalidJson)
        assertFalse(result)
    }

    @Test
    fun `SystemProfileBundle serializes correctly`() {
        val bundle = SystemProfileBundle(
            profile = SystemProfile(name = "Test Profile", connectionProfileName = "PLC1", layoutId = "layout1"),
            layout = DashboardLayout(id = "layout1", name = "Layout 1"),
            connection = PlcConnectionProfile(name = "PLC1", ipAddress = "1.1.1.1", port = 502)
        )
        val jsonStr = json.encodeToString(bundle)
        val decoded = json.decodeFromString<SystemProfileBundle>(jsonStr)
        assertEquals(bundle, decoded)
    }

    // --- importSystemProfiles: validate-before-mutate ---

    @Test
    fun `importSystemProfiles does not mutate any state when the file has no system profiles`() = runTest {
        val backup = FullBackupPackage(
            version = 1,
            layout = DashboardLayout(id = "L1", name = "Should not be saved"),
            libraryLayouts = listOf(DashboardLayout(id = "L2", name = "Should not be merged")),
            profiles = listOf(PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)),
            systemProfiles = null
        )
        val jsonStr = json.encodeToString(backup)
        val uri = mock<Uri>()
        contentResolver.stub {
            on { openInputStream(uri) } doReturn ByteArrayInputStream(jsonStr.toByteArray())
        }

        transferManager.importSystemProfiles(uri)

        verifyNoInteractions(repository)
        val lastEvent = transferManager.events.replayCache.last()
        assertTrue(lastEvent is TransferEvent.ValidationError)
    }

    @Test
    fun `importSystemProfiles imports the profile and its dependencies when present`() = runTest {
        val systemProfile = SystemProfile(id = "SP1", name = "Prod", connectionProfileName = "PLC1", layoutId = "L1")
        val layout = DashboardLayout(id = "L1", name = "Bound Layout")
        val libraryLayouts = listOf(layout)
        val connections = listOf(PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502))
        val backup = FullBackupPackage(
            version = 1,
            layout = layout,
            libraryLayouts = libraryLayouts,
            profiles = connections,
            systemProfiles = listOf(systemProfile)
        )
        val jsonStr = json.encodeToString(backup)
        val uri = mock<Uri>()
        contentResolver.stub {
            on { openInputStream(uri) } doReturn ByteArrayInputStream(jsonStr.toByteArray())
        }

        transferManager.importSystemProfiles(uri)

        verify(repository).mergeSystemProfiles(listOf(systemProfile))
        verify(repository).saveLayout(layout)
        verify(repository).mergeLayouts(libraryLayouts)
        verify(repository).mergeProfiles(connections)
        val lastEvent = transferManager.events.replayCache.last()
        assertTrue(lastEvent is TransferEvent.Success)
    }

    // --- export: truncating writes ---

    @Test
    fun `exportLayout truncates the destination with wt mode`() = runTest {
        val layout = DashboardLayout(id = "L1", name = "Export Me")
        repository.stub {
            on { dashboardLayoutFlow } doReturn MutableStateFlow(layout)
        }
        val uri = mock<Uri>()
        val outputStream = ByteArrayOutputStream()
        contentResolver.stub {
            on { openOutputStream(uri, "wt") } doReturn outputStream
        }

        transferManager.exportLayout(uri)

        verify(contentResolver).openOutputStream(uri, "wt")
        assertTrue(outputStream.toString().contains("Export Me"))
        val lastEvent = transferManager.events.replayCache.last()
        assertTrue(lastEvent is TransferEvent.Success)
    }

    @Test
    fun `exportLayout reports failure instead of success when the stream is null`() = runTest {
        repository.stub {
            on { dashboardLayoutFlow } doReturn MutableStateFlow(DashboardLayout())
        }
        val uri = mock<Uri>()
        contentResolver.stub {
            on { openOutputStream(uri, "wt") } doReturn null
        }

        transferManager.exportLayout(uri)

        val lastEvent = transferManager.events.replayCache.last()
        assertTrue("Expected an Error event, got $lastEvent", lastEvent is TransferEvent.Error)
    }
}
