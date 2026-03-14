package com.example.hmi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
    private val IP_ADDRESS_KEY = stringPreferencesKey("ip_address")
    private val PORT_KEY = intPreferencesKey("port")
    private val KEEP_SCREEN_ON_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("keep_screen_on")
    private val gson = Gson()

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
        val ipAddress = preferences[IP_ADDRESS_KEY]
        val port = preferences[PORT_KEY]
        if (ipAddress != null && port != null) {
            PlcConnectionProfile(ipAddress = ipAddress, port = port)
        } else {
            null
        }
    }

    suspend fun saveConnectionProfile(profile: PlcConnectionProfile) {
        context.dataStore.edit { preferences ->
            preferences[IP_ADDRESS_KEY] = profile.ipAddress
            preferences[PORT_KEY] = profile.port
        }
    }
}
