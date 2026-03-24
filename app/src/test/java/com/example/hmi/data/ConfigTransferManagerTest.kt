package com.example.hmi.data

import android.content.Context
import android.content.res.AssetManager
import com.example.hmi.protocol.PlcConnectionProfile
import com.example.hmi.protocol.Protocol
import com.google.gson.Gson
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

    private val gson = Gson()
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
        transferManager = ConfigTransferManager(context, gson, repository)
    }

    @Test
    fun `validateJson returns true for valid backup`() = runTest {
        val backup = FullBackupPackage(version = 1, layout = DashboardLayout(name = "Test"))
        val json = gson.toJson(backup)
        
        val result = transferManager.validateJson(json)
        
        assertTrue(result)
    }

    @Test
    fun `validateJson returns false for invalid json`() = runTest {
        val json = "{ \"invalid\": \"json\" }"
        
        val result = transferManager.validateJson(json)
        
        assertFalse(result)
    }
}
