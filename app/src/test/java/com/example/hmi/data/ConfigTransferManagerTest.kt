package com.example.hmi.data

import android.content.Context
import android.content.res.AssetManager
import com.example.hmi.protocol.PlcConnectionProfile
import com.example.hmi.protocol.Protocol
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.ByteArrayInputStream

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigTransferManagerTest {

    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
        prettyPrint = true
    }
    private lateinit var context: Context
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
        context = mock<Context> {
            on { assets } doReturn assetManager
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
}
