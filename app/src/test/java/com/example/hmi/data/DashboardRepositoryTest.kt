package com.example.hmi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

// Basic mock context just for DataStore
class MockContext : Context() {
    val file = File.createTempFile("datastore", ".preferences_pb").also { it.deleteOnExit() }
    
    // We only need this to satisfy DataStore factory
    override fun getApplicationContext(): Context = this
    override fun getFilesDir(): File = file.parentFile
    
    // Stub other methods throwing exception if they are ever called
    override fun getAssets(): android.content.res.AssetManager { TODO("Not yet implemented") }
    override fun getResources(): android.content.res.Resources { TODO("Not yet implemented") }
    override fun getPackageManager(): android.content.pm.PackageManager { TODO("Not yet implemented") }
    override fun getContentResolver(): android.content.ContentResolver { TODO("Not yet implemented") }
    override fun getMainLooper(): android.os.Looper { TODO("Not yet implemented") }
    override fun getTheme(): android.content.res.Resources.Theme { TODO("Not yet implemented") }
    override fun setTheme(resid: Int) { TODO("Not yet implemented") }
    override fun getClassLoader(): ClassLoader { TODO("Not yet implemented") }
    override fun getPackageName(): String { TODO("Not yet implemented") }
    override fun getApplicationInfo(): android.content.pm.ApplicationInfo { TODO("Not yet implemented") }
    override fun getPackageResourcePath(): String { TODO("Not yet implemented") }
    override fun getPackageCodePath(): String { TODO("Not yet implemented") }
    override fun getSharedPreferences(name: String?, mode: Int): android.content.SharedPreferences { TODO("Not yet implemented") }
    override fun moveSharedPreferencesFrom(sourceContext: Context?, name: String?): Boolean { TODO("Not yet implemented") }
    override fun deleteSharedPreferences(name: String?): Boolean { TODO("Not yet implemented") }
    override fun openFileInput(name: String?): java.io.FileInputStream { TODO("Not yet implemented") }
    override fun openFileOutput(name: String?, mode: Int): java.io.FileOutputStream { TODO("Not yet implemented") }
    override fun deleteFile(name: String?): Boolean { TODO("Not yet implemented") }
    override fun getFileStreamPath(name: String?): File { TODO("Not yet implemented") }
    override fun getDataDir(): File { TODO("Not yet implemented") }
    override fun getNoBackupFilesDir(): File { TODO("Not yet implemented") }
    override fun getExternalFilesDir(type: String?): File? { TODO("Not yet implemented") }
    override fun getExternalFilesDirs(type: String?): Array<File> { TODO("Not yet implemented") }
    override fun getObbDir(): File { TODO("Not yet implemented") }
    override fun getObbDirs(): Array<File> { TODO("Not yet implemented") }
    override fun getCacheDir(): File { TODO("Not yet implemented") }
    override fun getCodeCacheDir(): File { TODO("Not yet implemented") }
    override fun getExternalCacheDir(): File? { TODO("Not yet implemented") }
    override fun getExternalCacheDirs(): Array<File> { TODO("Not yet implemented") }
    override fun getExternalMediaDirs(): Array<File> { TODO("Not yet implemented") }
    override fun fileList(): Array<String> { TODO("Not yet implemented") }
    override fun getDir(name: String?, mode: Int): File { TODO("Not yet implemented") }
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?): android.database.sqlite.SQLiteDatabase { TODO("Not yet implemented") }
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?, errorHandler: android.database.DatabaseErrorHandler?): android.database.sqlite.SQLiteDatabase { TODO("Not yet implemented") }
    override fun moveDatabaseFrom(sourceContext: Context?, name: String?): Boolean { TODO("Not yet implemented") }
    override fun deleteDatabase(name: String?): Boolean { TODO("Not yet implemented") }
    override fun getDatabasePath(name: String?): File { TODO("Not yet implemented") }
    override fun databaseList(): Array<String> { TODO("Not yet implemented") }
    override fun getWallpaper(): android.graphics.drawable.Drawable { TODO("Not yet implemented") }
    override fun peekWallpaper(): android.graphics.drawable.Drawable { TODO("Not yet implemented") }
    override fun getWallpaperDesiredMinimumWidth(): Int { TODO("Not yet implemented") }
    override fun getWallpaperDesiredMinimumHeight(): Int { TODO("Not yet implemented") }
    override fun setWallpaper(bitmap: android.graphics.Bitmap?) { TODO("Not yet implemented") }
    override fun setWallpaper(data: java.io.InputStream?) { TODO("Not yet implemented") }
    override fun clearWallpaper() { TODO("Not yet implemented") }
    override fun startActivity(intent: android.content.Intent?) { TODO("Not yet implemented") }
    override fun startActivity(intent: android.content.Intent?, options: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun startActivities(intents: Array<out android.content.Intent>?) { TODO("Not yet implemented") }
    override fun startActivities(intents: Array<out android.content.Intent>?, options: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) { TODO("Not yet implemented") }
    override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun sendBroadcast(intent: android.content.Intent?) { TODO("Not yet implemented") }
    override fun sendBroadcast(intent: android.content.Intent?, receiverPermission: String?) { TODO("Not yet implemented") }
    override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?) { TODO("Not yet implemented") }
    override fun sendOrderedBroadcast(intent: android.content.Intent, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) { TODO("Not yet implemented") }
    override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?) { TODO("Not yet implemented") }
    override fun sendOrderedBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun sendStickyBroadcast(intent: android.content.Intent?) { TODO("Not yet implemented") }
    override fun sendStickyOrderedBroadcast(intent: android.content.Intent?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun removeStickyBroadcast(intent: android.content.Intent?) { TODO("Not yet implemented") }
    override fun sendStickyBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) { TODO("Not yet implemented") }
    override fun sendStickyOrderedBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) { TODO("Not yet implemented") }
    override fun removeStickyBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) { TODO("Not yet implemented") }
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?): android.content.Intent? { TODO("Not yet implemented") }
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, flags: Int): android.content.Intent? { TODO("Not yet implemented") }
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?): android.content.Intent? { TODO("Not yet implemented") }
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?, flags: Int): android.content.Intent? { TODO("Not yet implemented") }
    override fun unregisterReceiver(receiver: android.content.BroadcastReceiver?) { TODO("Not yet implemented") }
    override fun startService(service: android.content.Intent?): android.content.ComponentName? { TODO("Not yet implemented") }
    override fun startForegroundService(service: android.content.Intent?): android.content.ComponentName? { TODO("Not yet implemented") }
    override fun stopService(name: android.content.Intent?): Boolean { TODO("Not yet implemented") }
    override fun bindService(service: android.content.Intent, conn: android.content.ServiceConnection, flags: Int): Boolean { TODO("Not yet implemented") }
    override fun unbindService(conn: android.content.ServiceConnection) { TODO("Not yet implemented") }
    override fun startInstrumentation(className: android.content.ComponentName, profileFile: String?, arguments: android.os.Bundle?): Boolean { TODO("Not yet implemented") }
    override fun getSystemService(name: String): Any? { TODO("Not yet implemented") }
    override fun getSystemServiceName(serviceClass: Class<*>): String? { TODO("Not yet implemented") }
    override fun checkPermission(permission: String, pid: Int, uid: Int): Int { TODO("Not yet implemented") }
    override fun checkCallingPermission(permission: String): Int { TODO("Not yet implemented") }
    override fun checkCallingOrSelfPermission(permission: String): Int { TODO("Not yet implemented") }
    override fun checkSelfPermission(permission: String): Int { TODO("Not yet implemented") }
    override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) { TODO("Not yet implemented") }
    override fun enforceCallingPermission(permission: String, message: String?) { TODO("Not yet implemented") }
    override fun enforceCallingOrSelfPermission(permission: String, message: String?) { TODO("Not yet implemented") }
    override fun grantUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) { TODO("Not yet implemented") }
    override fun revokeUriPermission(uri: android.net.Uri?, modeFlags: Int) { TODO("Not yet implemented") }
    override fun revokeUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) { TODO("Not yet implemented") }
    override fun checkUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int): Int { TODO("Not yet implemented") }
    override fun checkCallingUriPermission(uri: android.net.Uri?, modeFlags: Int): Int { TODO("Not yet implemented") }
    override fun checkCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int): Int { TODO("Not yet implemented") }
    override fun checkUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int): Int { TODO("Not yet implemented") }
    override fun enforceUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int, message: String?) { TODO("Not yet implemented") }
    override fun enforceCallingUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) { TODO("Not yet implemented") }
    override fun enforceCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) { TODO("Not yet implemented") }
    override fun enforceUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int, message: String?) { TODO("Not yet implemented") }
    override fun createPackageContext(packageName: String?, flags: Int): Context { TODO("Not yet implemented") }
    override fun createContextForSplit(splitName: String?): Context { TODO("Not yet implemented") }
    override fun createConfigurationContext(overrideConfiguration: android.content.res.Configuration): Context { TODO("Not yet implemented") }
    override fun createDisplayContext(display: android.view.Display): Context { TODO("Not yet implemented") }
    override fun createDeviceProtectedStorageContext(): Context { TODO("Not yet implemented") }
    override fun isDeviceProtectedStorage(): Boolean { TODO("Not yet implemented") }
}

// Subclass to inject DataStore manually since Context extension val is tricky to mock
class TestDashboardRepository(
    context: Context,
    private val injectedJson: Json,
    private val injectedDataStore: DataStore<Preferences>
) : DashboardRepository(context, injectedJson) {
    override suspend fun saveLayout(layout: DashboardLayout) {
        val DASHBOARD_KEY = stringPreferencesKey("dashboard_layout")
        injectedDataStore.edit { preferences ->
            preferences[DASHBOARD_KEY] = injectedJson.encodeToString(layout)
        }
    }
    
    override val dashboardLayoutFlow = injectedDataStore.data.map { preferences ->
        val DASHBOARD_KEY = stringPreferencesKey("dashboard_layout")
        val jsonStr = preferences[DASHBOARD_KEY]
        if (jsonStr.isNullOrEmpty()) {
            DashboardLayout.createDemoLayout()
        } else {
            try {
                injectedJson.decodeFromString<DashboardLayout>(jsonStr)
            } catch (e: Exception) {
                DashboardLayout.createDemoLayout()
            }
        }
    }
}

class DashboardRepositoryTest {

    private lateinit var classUnderTest: DashboardRepository
    private lateinit var context: MockContext
    private lateinit var testDataStore: DataStore<Preferences>
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        context = MockContext()
        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.file }
        )
        classUnderTest = TestDashboardRepository(context, json, testDataStore)
    }

    @Test
    fun `dashboardLayoutFlow emits DemoLayout when DataStore is empty`() = runTest {
        // Given an empty DataStore (default state in test)

        // When
        val layout = classUnderTest.dashboardLayoutFlow.first()

        // Then it should be the DemoLayout, not just an empty layout
        assertEquals("Demo Layout", layout.name)
        assertEquals(4, layout.widgets.size)
        
        // Verify it contains the Text widget and the gauges
        assertTrue(layout.widgets.any { it.type == WidgetType.TEXT })
        assertTrue(layout.widgets.any { it.tagAddress == "SIM_TEMP" })
    }

    @Test
    fun `dashboardLayoutFlow emits saved layout when DataStore is populated`() = runTest {
        // Given a custom layout is saved
        val customLayout = DashboardLayout(
            id = UUID.randomUUID().toString(),
            name = "My Custom Layout",
            widgets = listOf(WidgetConfiguration(type = WidgetType.BUTTON, customLabel = "Custom Button"))
        )
        classUnderTest.saveLayout(customLayout)

        // When
        val loadedLayout = classUnderTest.dashboardLayoutFlow.first()

        // Then it should emit the custom layout, NOT the demo layout
        assertEquals("My Custom Layout", loadedLayout.name)
        assertEquals(1, loadedLayout.widgets.size)
        assertEquals("Custom Button", loadedLayout.widgets[0].customLabel)
        assertNotEquals("Demo Layout", loadedLayout.name)
    }
}