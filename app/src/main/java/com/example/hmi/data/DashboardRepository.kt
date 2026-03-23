package com.example.hmi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hmi.protocol.PlcConnectionProfile
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dashboard_prefs")

@Singleton
open class DashboardRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DASHBOARD_KEY = stringPreferencesKey("dashboard_layout")
    private val CONNECTION_PROFILE_KEY = stringPreferencesKey("connection_profile")
    private val KEEP_SCREEN_ON_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("keep_screen_on")
    private val RECENT_COLORS_KEY = stringPreferencesKey("recent_colors")
    private val gson = Gson()

    val recentColorsFlow: Flow<List<Long>> = context.dataStore.data.map { preferences ->
        val json = preferences[RECENT_COLORS_KEY]
        if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val type = object : com.google.gson.reflect.TypeToken<List<Long>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveRecentColors(colors: List<Long>) {
        context.dataStore.edit { preferences ->
            preferences[RECENT_COLORS_KEY] = gson.toJson(colors)
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
        val json = preferences[DASHBOARD_KEY]
        if (json.isNullOrEmpty()) {
            DashboardLayout()
        } else {
            try {
                gson.fromJson(json, DashboardLayout::class.java)
            } catch (e: Exception) {
                DashboardLayout()
            }
        }
    }

    open suspend fun saveLayout(layout: DashboardLayout) {
        context.dataStore.edit { preferences ->
            preferences[DASHBOARD_KEY] = gson.toJson(layout)
        }
    }

    val connectionProfileFlow: Flow<PlcConnectionProfile?> = context.dataStore.data.map { preferences ->
        val json = preferences[CONNECTION_PROFILE_KEY]
        if (json.isNullOrEmpty()) {
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
                gson.fromJson(json, PlcConnectionProfile::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveConnectionProfile(profile: PlcConnectionProfile) {
        context.dataStore.edit { preferences ->
            preferences[CONNECTION_PROFILE_KEY] = gson.toJson(profile)
        }
    }
}
