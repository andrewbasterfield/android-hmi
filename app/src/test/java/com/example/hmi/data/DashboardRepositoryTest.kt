package com.example.hmi.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.hmi.protocol.PlcConnectionProfile
import com.example.hmi.protocol.Protocol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Unit tests for DashboardRepository covering upsert merge logic,
 * deletion protection, and system profile persistence.
 *
 * Uses a real PreferencesDataStore backed by a temp file to test
 * the actual DataStore transaction logic without Android framework.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardRepositoryTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: DashboardRepository

    // DataStore preference keys (must match production code)
    private val SAVED_LAYOUTS_KEY = stringPreferencesKey("saved_layouts")
    private val SAVED_PROFILES_KEY = stringPreferencesKey("saved_profiles")
    private val SYSTEM_PROFILES_KEY = stringPreferencesKey("system_profiles")
    private val ACTIVE_SYSTEM_PROFILE_ID_KEY = stringPreferencesKey("active_system_profile_id")

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_prefs.preferences_pb") }
        )
        repository = DashboardRepository(dataStore, json)
    }

    // --- mergeLayouts tests ---

    @Test
    fun `mergeLayouts inserts into empty store`() = testScope.runTest {
        val layout = DashboardLayout(id = "L1", name = "Layout One")

        repository.mergeLayouts(listOf(layout))

        val stored = readLayouts()
        assertEquals(1, stored.size)
        assertEquals("L1", stored[0].id)
        assertEquals("Layout One", stored[0].name)
    }

    @Test
    fun `mergeLayouts upserts existing layout by id`() = testScope.runTest {
        val original = DashboardLayout(id = "L1", name = "Original")
        repository.mergeLayouts(listOf(original))

        val updated = DashboardLayout(id = "L1", name = "Updated")
        repository.mergeLayouts(listOf(updated))

        val stored = readLayouts()
        assertEquals(1, stored.size)
        assertEquals("Updated", stored[0].name)
    }

    @Test
    fun `mergeLayouts preserves unrelated layouts`() = testScope.runTest {
        val existing = DashboardLayout(id = "L1", name = "Existing")
        repository.mergeLayouts(listOf(existing))

        val newLayout = DashboardLayout(id = "L2", name = "New")
        repository.mergeLayouts(listOf(newLayout))

        val stored = readLayouts()
        assertEquals(2, stored.size)
        assertTrue(stored.any { it.id == "L1" && it.name == "Existing" })
        assertTrue(stored.any { it.id == "L2" && it.name == "New" })
    }

    @Test
    fun `mergeLayouts handles batch upsert`() = testScope.runTest {
        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "First"),
            DashboardLayout(id = "L2", name = "Second")
        ))

        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "First Updated"),
            DashboardLayout(id = "L3", name = "Third")
        ))

        val stored = readLayouts()
        assertEquals(3, stored.size)
        assertEquals("First Updated", stored.find { it.id == "L1" }?.name)
        assertEquals("Second", stored.find { it.id == "L2" }?.name)
        assertEquals("Third", stored.find { it.id == "L3" }?.name)
    }

    // --- mergeProfiles (connection profiles) tests ---

    @Test
    fun `mergeProfiles inserts into empty store`() = testScope.runTest {
        val profile = PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)

        repository.mergeProfiles(listOf(profile))

        val stored = readConnectionProfiles()
        assertEquals(1, stored.size)
        assertEquals("PLC1", stored[0].name)
    }

    @Test
    fun `mergeProfiles upserts by name`() = testScope.runTest {
        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)
        ))

        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.2", port = 503)
        ))

        val stored = readConnectionProfiles()
        assertEquals(1, stored.size)
        assertEquals("10.0.0.2", stored[0].ipAddress)
        assertEquals(503, stored[0].port)
    }

    @Test
    fun `mergeProfiles preserves unrelated profiles`() = testScope.runTest {
        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)
        ))

        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC2", ipAddress = "10.0.0.2", port = 503)
        ))

        val stored = readConnectionProfiles()
        assertEquals(2, stored.size)
    }

    // --- mergeSystemProfiles tests ---

    @Test
    fun `mergeSystemProfiles inserts into empty store`() = testScope.runTest {
        val profile = SystemProfile(id = "SP1", name = "Production", connectionProfileName = "PLC1", layoutId = "L1")

        repository.mergeSystemProfiles(listOf(profile))

        val stored = readSystemProfiles()
        assertEquals(1, stored.size)
        assertEquals("Production", stored[0].name)
    }

    @Test
    fun `mergeSystemProfiles upserts by id`() = testScope.runTest {
        repository.mergeSystemProfiles(listOf(
            SystemProfile(id = "SP1", name = "Original", connectionProfileName = "PLC1", layoutId = "L1")
        ))

        repository.mergeSystemProfiles(listOf(
            SystemProfile(id = "SP1", name = "Updated", connectionProfileName = "PLC2", layoutId = "L2")
        ))

        val stored = readSystemProfiles()
        assertEquals(1, stored.size)
        assertEquals("Updated", stored[0].name)
        assertEquals("PLC2", stored[0].connectionProfileName)
    }

    // --- deleteLayout with protection tests ---

    @Test
    fun `deleteLayout removes unbound layout`() = testScope.runTest {
        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "Layout One"),
            DashboardLayout(id = "L2", name = "Layout Two")
        ))

        repository.deleteLayout("L1")

        val stored = readLayouts()
        assertEquals(1, stored.size)
        assertEquals("L2", stored[0].id)
    }

    @Test
    fun `deleteLayout protects layout bound to system profile`() = testScope.runTest {
        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "Protected Layout")
        ))
        repository.mergeSystemProfiles(listOf(
            SystemProfile(id = "SP1", name = "Prod", connectionProfileName = "PLC1", layoutId = "L1")
        ))

        val deleted = repository.deleteLayout("L1")

        // Layout should still exist because it's bound to SP1
        assertFalse(deleted)
        val stored = readLayouts()
        assertEquals(1, stored.size)
        assertEquals("L1", stored[0].id)
    }

    @Test
    fun `deleteLayout refuses when system profiles JSON is corrupt`() = testScope.runTest {
        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "Layout One")
        ))
        dataStore.edit { it[SYSTEM_PROFILES_KEY] = "{not valid json" }

        val deleted = repository.deleteLayout("L1")

        // Bindings are unknowable, so the delete must fail closed
        assertFalse(deleted)
        assertEquals(1, readLayouts().size)
    }

    @Test
    fun `deleteLayout allows deletion after system profile is removed`() = testScope.runTest {
        repository.mergeLayouts(listOf(
            DashboardLayout(id = "L1", name = "Previously Protected")
        ))
        repository.mergeSystemProfiles(listOf(
            SystemProfile(id = "SP1", name = "Prod", connectionProfileName = "PLC1", layoutId = "L1")
        ))

        // Remove the binding
        repository.deleteSystemProfile("SP1")

        // Now deletion should succeed
        repository.deleteLayout("L1")

        val stored = readLayouts()
        assertEquals(0, stored.size)
    }

    // --- deleteFromSavedProfiles with protection tests ---

    @Test
    fun `deleteFromSavedProfiles removes unbound connection`() = testScope.runTest {
        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502),
            PlcConnectionProfile(name = "PLC2", ipAddress = "10.0.0.2", port = 502)
        ))

        repository.deleteFromSavedProfiles("PLC1")

        val stored = readConnectionProfiles()
        assertEquals(1, stored.size)
        assertEquals("PLC2", stored[0].name)
    }

    @Test
    fun `deleteFromSavedProfiles protects connection bound to system profile`() = testScope.runTest {
        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)
        ))
        repository.mergeSystemProfiles(listOf(
            SystemProfile(id = "SP1", name = "Prod", connectionProfileName = "PLC1", layoutId = "L1")
        ))

        val deleted = repository.deleteFromSavedProfiles("PLC1")

        // Connection should still exist because it's bound to SP1
        assertFalse(deleted)
        val stored = readConnectionProfiles()
        assertEquals(1, stored.size)
        assertEquals("PLC1", stored[0].name)
    }

    @Test
    fun `deleteFromSavedProfiles refuses when system profiles JSON is corrupt`() = testScope.runTest {
        repository.mergeProfiles(listOf(
            PlcConnectionProfile(name = "PLC1", ipAddress = "10.0.0.1", port = 502)
        ))
        dataStore.edit { it[SYSTEM_PROFILES_KEY] = "{not valid json" }

        val deleted = repository.deleteFromSavedProfiles("PLC1")

        assertFalse(deleted)
        assertEquals(1, readConnectionProfiles().size)
    }

    // --- saveSystemProfile / deleteSystemProfile tests ---

    @Test
    fun `saveSystemProfile persists and can be read back`() = testScope.runTest {
        val profile = SystemProfile(id = "SP1", name = "Test", connectionProfileName = "PLC1", layoutId = "L1")

        repository.saveSystemProfile(profile)

        val stored = readSystemProfiles()
        assertEquals(1, stored.size)
        assertEquals("SP1", stored[0].id)
    }

    @Test
    fun `saveSystemProfile upserts existing profile`() = testScope.runTest {
        repository.saveSystemProfile(
            SystemProfile(id = "SP1", name = "Original", connectionProfileName = "PLC1", layoutId = "L1")
        )
        repository.saveSystemProfile(
            SystemProfile(id = "SP1", name = "Updated", connectionProfileName = "PLC2", layoutId = "L2")
        )

        val stored = readSystemProfiles()
        assertEquals(1, stored.size)
        assertEquals("Updated", stored[0].name)
    }

    @Test
    fun `deleteSystemProfile removes profile and clears active id if matching`() = testScope.runTest {
        repository.saveSystemProfile(
            SystemProfile(id = "SP1", name = "ToDelete", connectionProfileName = "PLC1", layoutId = "L1")
        )
        repository.setActiveSystemProfileId("SP1")

        repository.deleteSystemProfile("SP1")

        val stored = readSystemProfiles()
        assertEquals(0, stored.size)

        val activeId = readActiveSystemProfileId()
        assertNull(activeId)
    }

    @Test
    fun `deleteSystemProfile preserves active id if non-matching`() = testScope.runTest {
        repository.saveSystemProfile(
            SystemProfile(id = "SP1", name = "Keep", connectionProfileName = "PLC1", layoutId = "L1")
        )
        repository.saveSystemProfile(
            SystemProfile(id = "SP2", name = "Delete", connectionProfileName = "PLC2", layoutId = "L2")
        )
        repository.setActiveSystemProfileId("SP1")

        repository.deleteSystemProfile("SP2")

        val activeId = readActiveSystemProfileId()
        assertEquals("SP1", activeId)
    }

    // --- systemProfilesFlow always includes demo profile ---

    @Test
    fun `systemProfilesFlow always includes demo system profile first`() = testScope.runTest {
        // With no user profiles saved
        val profiles = repository.systemProfilesFlow.first()

        assertEquals(1, profiles.size)
        assertEquals("demo-system", profiles[0].id)
        assertEquals("Demo System", profiles[0].name)
        assertTrue(profiles[0].isReadOnly)
    }

    @Test
    fun `systemProfilesFlow prepends demo before user profiles`() = testScope.runTest {
        repository.saveSystemProfile(
            SystemProfile(id = "SP1", name = "User Profile", connectionProfileName = "PLC1", layoutId = "L1")
        )

        val profiles = repository.systemProfilesFlow.first()

        assertEquals(2, profiles.size)
        assertEquals("demo-system", profiles[0].id)
        assertEquals("SP1", profiles[1].id)
    }

    // --- Helpers ---

    private suspend fun readLayouts(): List<DashboardLayout> {
        val prefs = dataStore.data.first()
        val jsonStr = prefs[SAVED_LAYOUTS_KEY] ?: return emptyList()
        return json.decodeFromString(jsonStr)
    }

    private suspend fun readConnectionProfiles(): List<PlcConnectionProfile> {
        val prefs = dataStore.data.first()
        val jsonStr = prefs[SAVED_PROFILES_KEY] ?: return emptyList()
        return json.decodeFromString(jsonStr)
    }

    private suspend fun readSystemProfiles(): List<SystemProfile> {
        val prefs = dataStore.data.first()
        val jsonStr = prefs[SYSTEM_PROFILES_KEY] ?: return emptyList()
        return json.decodeFromString(jsonStr)
    }

    private suspend fun readActiveSystemProfileId(): String? {
        val prefs = dataStore.data.first()
        return prefs[ACTIVE_SYSTEM_PROFILE_ID_KEY]
    }
}
