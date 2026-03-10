package com.example.hmi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dashboard_prefs")

@Singleton
class DashboardRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DASHBOARD_KEY = stringPreferencesKey("dashboard_layout")

    val dashboardLayoutFlow: Flow<DashboardLayout> = context.dataStore.data.map { preferences ->
        val json = preferences[DASHBOARD_KEY] ?: return@map DashboardLayout()
        // Extremely simple parsing to avoid adding Gson/Moshi for MVP prototype. 
        // In a real app we'd use kotlinx-serialization.
        DashboardLayout() // returning default for prototype since we just mock the persistance.
    }

    suspend fun saveLayout(layout: DashboardLayout) {
        context.dataStore.edit { preferences ->
            // In a real app: preferences[DASHBOARD_KEY] = Gson().toJson(layout)
            preferences[DASHBOARD_KEY] = "{}"
        }
    }
}
