package com.example.hmi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hmi.protocol.PlcConnectionProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dashboard_prefs")

@Singleton
open class DashboardRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    private val DASHBOARD_KEY = stringPreferencesKey("dashboard_layout")
    private val CONNECTION_PROFILE_KEY = stringPreferencesKey("connection_profile")
    private val SAVED_PROFILES_KEY = stringPreferencesKey("saved_profiles")
    private val SAVED_LAYOUTS_KEY = stringPreferencesKey("saved_layouts")
    private val KEEP_SCREEN_ON_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("keep_screen_on")
    private val RECENT_COLORS_KEY = stringPreferencesKey("recent_colors")
    private val SYSTEM_PROFILES_KEY = stringPreferencesKey("system_profiles")
    private val ACTIVE_SYSTEM_PROFILE_ID_KEY = stringPreferencesKey("active_system_profile_id")
    private val MANUAL_CONNECTION_PROFILE_KEY = stringPreferencesKey("manual_connection_profile")
    private val MANUAL_LAYOUT_KEY = stringPreferencesKey("manual_layout")

    val systemProfilesFlow: Flow<List<SystemProfile>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[SYSTEM_PROFILES_KEY]
        val savedProfiles = if (jsonStr.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<SystemProfile>>(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        }

        val demoProfile = SystemProfile(
            id = "demo-system",
            name = "Demo System",
            connectionProfileName = "Local Demo Server",
            layoutId = "demo-layout",
            isReadOnly = true
        )

        listOf(demoProfile) + savedProfiles.filter { it.id != "demo-system" }
    }

    val activeSystemProfileIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_SYSTEM_PROFILE_ID_KEY]
    }

    suspend fun saveSystemProfile(profile: SystemProfile) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SYSTEM_PROFILES_KEY]
            val currentList: MutableList<SystemProfile> = if (currentJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    json.decodeFromString<List<SystemProfile>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            currentList.removeAll { it.id == profile.id }
            currentList.add(profile)
            preferences[SYSTEM_PROFILES_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun deleteSystemProfile(id: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SYSTEM_PROFILES_KEY]
            if (!currentJson.isNullOrEmpty()) {
                try {
                    val currentList = json.decodeFromString<List<SystemProfile>>(currentJson).toMutableList()
                    currentList.removeAll { it.id == id }
                    preferences[SYSTEM_PROFILES_KEY] = json.encodeToString(currentList)
                    
                    if (preferences[ACTIVE_SYSTEM_PROFILE_ID_KEY] == id) {
                        preferences.remove(ACTIVE_SYSTEM_PROFILE_ID_KEY)
                    }
                } catch (e: Exception) {}
            }
        }
    }

    suspend fun setActiveSystemProfileId(id: String?) {
        context.dataStore.edit { preferences ->
            if (id == null) {
                preferences.remove(ACTIVE_SYSTEM_PROFILE_ID_KEY)
            } else {
                preferences[ACTIVE_SYSTEM_PROFILE_ID_KEY] = id
            }
        }
    }

    val savedLayoutsFlow: Flow<List<DashboardLayout>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[SAVED_LAYOUTS_KEY]
        val saved = if (jsonStr.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<DashboardLayout>>(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        }

        // Always prioritize active layout data for the UI
        val activeJson = preferences[DASHBOARD_KEY]
        if (!activeJson.isNullOrEmpty()) {
            try {
                val active = json.decodeFromString<DashboardLayout>(activeJson)
                // Filter out the stale version from the library and replace with active
                val otherSaved = saved.filter { it.id != active.id }
                return@map listOf(active) + otherSaved
            } catch (e: Exception) {}
        }
        saved
    }

    suspend fun mergeLayouts(newLayouts: List<DashboardLayout>) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SAVED_LAYOUTS_KEY]
            val currentList: MutableList<DashboardLayout> = if (currentJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    json.decodeFromString<List<DashboardLayout>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            newLayouts.forEach { layout ->
                currentList.removeAll { it.id == layout.id }
                currentList.add(layout)
            }
            preferences[SAVED_LAYOUTS_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun mergeSystemProfiles(newProfiles: List<SystemProfile>) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SYSTEM_PROFILES_KEY]
            val currentList: MutableList<SystemProfile> = if (currentJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    json.decodeFromString<List<SystemProfile>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            newProfiles.forEach { profile ->
                currentList.removeAll { it.id == profile.id }
                currentList.add(profile)
            }
            preferences[SYSTEM_PROFILES_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun saveToSavedLayouts(layout: DashboardLayout) {
        mergeLayouts(listOf(layout))
    }

    suspend fun deleteLayout(layoutId: String) {
        context.dataStore.edit { preferences ->
            val systemProfilesJson = preferences[SYSTEM_PROFILES_KEY]
            if (!systemProfilesJson.isNullOrEmpty()) {
                try {
                    val profiles = json.decodeFromString<List<SystemProfile>>(systemProfilesJson)
                    if (profiles.any { it.layoutId == layoutId }) {
                        return@edit 
                    }
                } catch (e: Exception) {}
            }

            val currentJson = preferences[SAVED_LAYOUTS_KEY]
            if (!currentJson.isNullOrEmpty()) {
                try {
                    val currentList: MutableList<DashboardLayout> =
                        json.decodeFromString<List<DashboardLayout>>(currentJson).toMutableList()
                    currentList.removeAll { it.id == layoutId }
                    preferences[SAVED_LAYOUTS_KEY] = json.encodeToString(currentList)
                } catch (e: Exception) {}
            }
        }
    }

    val recentColorsFlow: Flow<List<Long>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[RECENT_COLORS_KEY]
        if (jsonStr.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<Long>>(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveRecentColors(colors: List<Long>) {
        context.dataStore.edit { preferences ->
            preferences[RECENT_COLORS_KEY] = json.encodeToString(colors)
        }
    }

    val savedProfilesFlow: Flow<List<PlcConnectionProfile>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[SAVED_PROFILES_KEY]
        val saved = if (jsonStr.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<PlcConnectionProfile>>(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        }

        val activeJson = preferences[CONNECTION_PROFILE_KEY]
        if (!activeJson.isNullOrEmpty()) {
            try {
                val active = json.decodeFromString<PlcConnectionProfile>(activeJson)
                val otherSaved = saved.filter { it.name != active.name }
                return@map listOf(active) + otherSaved
            } catch (e: Exception) {}
        }
        saved
    }

    suspend fun saveToSavedProfiles(profile: PlcConnectionProfile) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SAVED_PROFILES_KEY]
            val currentList: MutableList<PlcConnectionProfile> = if (currentJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    json.decodeFromString<List<PlcConnectionProfile>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            currentList.removeAll { it.name == profile.name }
            currentList.add(profile)

            preferences[SAVED_PROFILES_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun mergeProfiles(newProfiles: List<PlcConnectionProfile>) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[SAVED_PROFILES_KEY]
            val currentList: MutableList<PlcConnectionProfile> = if (currentJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                try {
                    json.decodeFromString<List<PlcConnectionProfile>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            newProfiles.forEach { newProfile ->
                currentList.removeAll { it.name == newProfile.name }
                currentList.add(newProfile)
            }

            preferences[SAVED_PROFILES_KEY] = json.encodeToString(currentList)
        }
    }

    suspend fun deleteFromSavedProfiles(profileName: String) {
        context.dataStore.edit { preferences ->
            val systemProfilesJson = preferences[SYSTEM_PROFILES_KEY]
            if (!systemProfilesJson.isNullOrEmpty()) {
                try {
                    val profiles = json.decodeFromString<List<SystemProfile>>(systemProfilesJson)
                    if (profiles.any { it.connectionProfileName == profileName }) {
                        return@edit 
                    }
                } catch (e: Exception) {}
            }

            val currentJson = preferences[SAVED_PROFILES_KEY]
            if (!currentJson.isNullOrEmpty()) {
                try {
                    val currentList: MutableList<PlcConnectionProfile> =
                        json.decodeFromString<List<PlcConnectionProfile>>(currentJson).toMutableList()
                    currentList.removeAll { it.name == profileName }
                    preferences[SAVED_PROFILES_KEY] = json.encodeToString(currentList)
                } catch (e: Exception) {}
            }
        }
    }

    val keepScreenOnFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_SCREEN_ON_KEY] ?: true
    }

    suspend fun saveKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON_KEY] = enabled
        }
    }

    open val dashboardLayoutFlow: Flow<DashboardLayout> = context.dataStore.data.map { preferences ->
        val activeProfileId = preferences[ACTIVE_SYSTEM_PROFILE_ID_KEY]
        val systemProfilesJson = preferences[SYSTEM_PROFILES_KEY]
        val savedLayoutsJson = preferences[SAVED_LAYOUTS_KEY]

        if (activeProfileId != null && !systemProfilesJson.isNullOrEmpty() && !savedLayoutsJson.isNullOrEmpty()) {
            try {
                val profiles = json.decodeFromString<List<SystemProfile>>(systemProfilesJson)
                val profile = profiles.find { it.id == activeProfileId }
                if (profile != null) {
                    val layouts = json.decodeFromString<List<DashboardLayout>>(savedLayoutsJson)
                    val layout = layouts.find { it.id == profile.layoutId }
                    if (layout != null) return@map layout
                }
            } catch (e: Exception) {}
        }

        val manualJson = preferences[MANUAL_LAYOUT_KEY]
        if (!manualJson.isNullOrEmpty()) {
            try {
                return@map json.decodeFromString<DashboardLayout>(manualJson)
            } catch (e: Exception) {}
        }

        val legacyJson = preferences[DASHBOARD_KEY]
        if (!legacyJson.isNullOrEmpty()) {
            try {
                return@map json.decodeFromString<DashboardLayout>(legacyJson)
            } catch (e: Exception) {}
        }

        DashboardLayout.createDemoLayout()
    }

    open suspend fun saveLayout(layout: DashboardLayout) {
        context.dataStore.edit { preferences ->
            preferences[DASHBOARD_KEY] = json.encodeToString(layout)
            preferences[MANUAL_LAYOUT_KEY] = json.encodeToString(layout)
        }
    }

    val connectionProfileFlow: Flow<PlcConnectionProfile?> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[CONNECTION_PROFILE_KEY]
        if (jsonStr.isNullOrEmpty()) {
            val ipAddress = preferences[stringPreferencesKey("ip_address")]
            val port = preferences[intPreferencesKey("port")]
            if (ipAddress != null && port != null) {
                PlcConnectionProfile(ipAddress = ipAddress, port = port)
            } else {
                PlcConnectionProfile(
                    name = "Local Demo Server",
                    ipAddress = "127.0.0.1",
                    port = 9999,
                    protocol = com.example.hmi.protocol.Protocol.RAW_TCP
                )
            }
        } else {
            try {
                json.decodeFromString<PlcConnectionProfile>(jsonStr)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveConnectionProfile(profile: PlcConnectionProfile) {
        context.dataStore.edit { preferences ->
            preferences[CONNECTION_PROFILE_KEY] = json.encodeToString(profile)
            preferences[MANUAL_CONNECTION_PROFILE_KEY] = json.encodeToString(profile)
        }
    }
}
