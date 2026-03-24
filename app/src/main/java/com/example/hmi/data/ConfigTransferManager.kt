package com.example.hmi.data

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

sealed class TransferEvent {
    data class ImportReady(val backup: FullBackupPackage) : TransferEvent()
    data class ValidationError(val message: String) : TransferEvent()
    data class Success(val message: String) : TransferEvent()
    data class Error(val message: String) : TransferEvent()
}

@Singleton
class ConfigTransferManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val repository: DashboardRepository
) {
    private val _events = MutableSharedFlow<TransferEvent>()
    val events: SharedFlow<TransferEvent> = _events.asSharedFlow()

    private val schema: Schema by lazy {
        val inputStream = context.assets.open("schemas/full-backup.schema.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val rawSchema = JSONObject(JSONTokener(jsonString))
        SchemaLoader.load(rawSchema)
    }

    suspend fun exportLayout(uri: Uri) {
        try {
            val layout = repository.dashboardLayoutFlow.first()
            val backup = FullBackupPackage(layout = layout)
            val json = gson.toJson(backup)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            _events.emit(TransferEvent.Success("Layout exported successfully"))
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Export failed: ${e.message}"))
        }
    }

    suspend fun importLayout(uri: Uri) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: throw Exception("Failed to open file")

            if (validateJson(json)) {
                val backup = gson.fromJson(json, FullBackupPackage::class.java)
                backup.layout?.let { layout ->
                    repository.saveLayout(layout)
                    _events.emit(TransferEvent.Success("Layout imported successfully"))
                } ?: run {
                    _events.emit(TransferEvent.ValidationError("File does not contain a layout"))
                }
            } else {
                _events.emit(TransferEvent.ValidationError("Invalid file format"))
            }
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Import failed: ${e.message}"))
        }
    }

    suspend fun exportProfiles(uri: Uri) {
        try {
            val profiles = repository.savedProfilesFlow.first()
            val backup = FullBackupPackage(profiles = profiles)
            val json = gson.toJson(backup)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            _events.emit(TransferEvent.Success("Profiles exported successfully"))
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Export failed: ${e.message}"))
        }
    }

    suspend fun importProfiles(uri: Uri) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: throw Exception("Failed to open file")

            if (validateJson(json)) {
                val backup = gson.fromJson(json, FullBackupPackage::class.java)
                backup.profiles?.let { profiles ->
                    repository.mergeProfiles(profiles)
                    _events.emit(TransferEvent.Success("Profiles imported successfully"))
                } ?: run {
                    _events.emit(TransferEvent.ValidationError("File does not contain profiles"))
                }
            } else {
                _events.emit(TransferEvent.ValidationError("Invalid file format"))
            }
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Import failed: ${e.message}"))
        }
    }

    fun shareConfig(context: Context, content: String, filename: String) {
        try {
            val cacheFile = java.io.File(context.cacheDir, filename)
            cacheFile.writeText(content)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "com.example.hmi.fileprovider",
                cacheFile
            )

            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share Configuration"))
        } catch (e: Exception) {
            // Emit error if needed, but this is often called from UI
        }
    }

    suspend fun exportFullBackup(uri: Uri) {
        try {
            val layout = repository.dashboardLayoutFlow.first()
            val profiles = repository.savedProfilesFlow.first()
            val backup = FullBackupPackage(
                version = CURRENT_VERSION,
                layout = layout,
                profiles = profiles
            )
            val json = gson.toJson(backup)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            _events.emit(TransferEvent.Success("Full backup exported successfully"))
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Export failed: ${e.message}"))
        }
    }

    suspend fun importFullBackup(uri: Uri) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: throw Exception("Failed to open file")

            if (validateJson(json)) {
                val backup = gson.fromJson(json, FullBackupPackage::class.java)
                
                // FR-014: Schema versioning and compatibility
                if (backup.version > CURRENT_VERSION) {
                    _events.emit(TransferEvent.Error("Backup version (${backup.version}) is newer than app version ($CURRENT_VERSION). Please update the app."))
                    return
                }

                // Emit ImportReady event to trigger Selection UI
                _events.emit(TransferEvent.ImportReady(backup))
            }
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Import failed: ${e.message}"))
        }
    }

    companion object {
        private const val CURRENT_VERSION = 1
    }

    suspend fun executeImport(backup: FullBackupPackage, importLayout: Boolean, importProfiles: Boolean) {
        try {
            if (importLayout) {
                backup.layout?.let { repository.saveLayout(it) }
            }
            if (importProfiles) {
                backup.profiles?.let { repository.mergeProfiles(it) }
            }
            _events.emit(TransferEvent.Success("Import completed successfully"))
        } catch (e: Exception) {
            _events.emit(TransferEvent.Error("Import execution failed: ${e.message}"))
        }
    }

    internal suspend fun validateJson(json: String): Boolean {
        return try {
            val jsonObject = JSONObject(json)
            schema.validate(jsonObject)
            true
        } catch (e: ValidationException) {
            val message = e.allMessages.joinToString("\n")
            _events.emit(TransferEvent.ValidationError(message))
            false
        } catch (e: Exception) {
            _events.emit(TransferEvent.ValidationError("JSON Parse Error: ${e.message}"))
            false
        }
    }
}
