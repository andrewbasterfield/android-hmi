package com.example.hmi.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hmi.data.FullBackupPackage
import com.example.hmi.ui.theme.IndustrialShape

@Composable
fun ImportSelectionDialog(
    backup: FullBackupPackage,
    onDismiss: () -> Unit,
    onConfirm: (Boolean, Boolean) -> Unit
) {
    var importLayout by remember { mutableStateOf(backup.layout != null) }
    var importProfiles by remember { mutableStateOf(backup.profiles != null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Configuration") },
        shape = IndustrialShape.Standard,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select components to import from the file:")
                
                if (backup.layout != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = importLayout,
                            onCheckedChange = { importLayout = it }
                        )
                        Text("Dashboard Layout (${backup.layout.name})")
                    }
                }

                if (backup.profiles != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = importProfiles,
                            onCheckedChange = { importProfiles = it }
                        )
                        Text("Connection Profiles (${backup.profiles.size} profiles)")
                    }
                }

                if (backup.layout == null && backup.profiles == null) {
                    Text("No valid components found in this file.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(importLayout, importProfiles) },
                enabled = importLayout || importProfiles,
                shape = IndustrialShape.Standard
            ) {
                Text("Import Selected")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
