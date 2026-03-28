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
    private val KEEP_SCREEN_ON_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("keep_screen_on")
    private val RECENT_COLORS_KEY = stringPreferencesKey("recent_colors")

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
        if (jsonStr.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<PlcConnectionProfile>>(jsonStr)
            } catch (e: Exception) {
                emptyList()
            }
        }
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

            // Upsert: remove existing profile with same name, then add new one
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
            val currentJson = preferences[SAVED_PROFILES_KEY]
            if (!currentJson.isNullOrEmpty()) {
                try {
                    val currentList: MutableList<PlcConnectionProfile> =
                        json.decodeFromString<List<PlcConnectionProfile>>(currentJson).toMutableList()
                    currentList.removeAll { it.name == profileName }
                    preferences[SAVED_PROFILES_KEY] = json.encodeToString(currentList)
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }
        }
    }

    val keepScreenOnFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_SCREEN_ON_KEY] ?: true // Default to true
    }

    suspend fun saveKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON_KEY] = enabled
        }
    }

    open val dashboardLayoutFlow: Flow<DashboardLayout> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[DASHBOARD_KEY]
        if (jsonStr.isNullOrEmpty()) {
            DashboardLayout()
        } else {
            try {
                json.decodeFromString<DashboardLayout>(jsonStr)
            } catch (e: Exception) {
                DashboardLayout()
            }
        }
    }

    open suspend fun saveLayout(layout: DashboardLayout) {
        context.dataStore.edit { preferences ->
            preferences[DASHBOARD_KEY] = json.encodeToString(layout)
        }
    }

    val connectionProfileFlow: Flow<PlcConnectionProfile?> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[CONNECTION_PROFILE_KEY]
        if (jsonStr.isNullOrEmpty()) {
            // Migration check: if old keys exist, migrate them
            val ipAddress = preferences[stringPreferencesKey("ip_address")]
            val port = preferences[intPreferencesKey("port")]
            if (ipAddress != null && port != null) {
                PlcConnectionProfile(ipAddress = ipAddress, port = port)
            } else {
                null
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
        }
    }
}
